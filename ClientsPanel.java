import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.Result;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;
import java.util.ArrayList;

/**
 * Panel pokazujący wszystkich klientów. Pozwala na modyfikacje danych klientów, dodanie nowych klientów lub wyświetlenie wszystkich rezerwacji danego klienta
 */
class ClientsPanel extends JPanel {

    private JLabel description;
    private JLabel sortByText;
    private String [] sortByOptions = {"ID", "Imię", "Nazwisko", "Miejscowość", "Kod pocztowy", "Ilość rezerwacji"};
    private JComboBox sortBy;
    private JButton menuButton;
    private JPanel contentPane;
    private JTable roomsTable;
    private DefaultTableModel model;
    private JScrollPane scrollPane;

    // add or change data room
    private JLabel addOrChangeRoomDataDesc;
    private JLabel fnameDesc;
    private JTextField fnameField;
    private JLabel lnameDesc;
    private JTextField lnameField;
    private  JLabel city;
    private JTextField cityField;
    private JLabel streetDesc;
    private JTextField streetField;
    private JLabel streetNrDesc;
    private JTextField streetNrField;
    private JLabel postcodeDesc;
    private JTextField postcodeField;
    private JLabel phoneDesc;
    private JTextField phoneField;
    private JLabel emailDesc;
    private JTextField emailField;

    private JLabel selectClientIdDesc;
    private JComboBox selectClientId;
    private JButton createClientButton;
    private JButton modifyClientButton;
    private JButton showClientReservations;


    /**
     * Konstrukor panelu klientów. Tworzy wszystkie elementy i przypisuje im domyślne wartości
     * @param panel - panel, w którym zostanie wyświetlona zawartość
     */
    public ClientsPanel(JPanel menuPanel) {

        menuButton = new JButton("Powrót do menu");
        contentPane = menuPanel;
        description = new JLabel("Panel gości", SwingConstants.CENTER);
        description.setBounds(335, 10, 100, 25);
        add(description);

        sortByText = new JLabel("Sortuj po", SwingConstants.CENTER);
        sortByText.setBounds(535, 20, 70, 25);
        add(sortByText);

        sortBy = new JComboBox(sortByOptions);
        sortBy.setBounds(610,20, 175,25);
        sortBy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fillRoomsTable();
            }
        });
        add(sortBy);

        //adjust size and set layout
        setPreferredSize (new Dimension(800, 640));
        setLayout (null);

        menuButton.setLocation(15, 585);
        menuButton.setSize(770, 40);
        menuButton.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                CardLayout cardLayout = (CardLayout) contentPane.getLayout();
                cardLayout.first(contentPane);
                cardLayout.next(contentPane);
            }
        });
        add(menuButton);

        model = new DefaultTableModel();
        roomsTable = new JTable(model);
        roomsTable.setFont(new Font("Times New Roman", Font.PLAIN, 13));

        scrollPane = new JScrollPane(roomsTable);
        scrollPane.setBounds(15,50,770,300);

        model.addColumn("ID");
        model.addColumn("Imię");
        model.addColumn("Nazwisko");
        model.addColumn("Miejscowość");
        model.addColumn("Ulica");
        model.addColumn("Numer");
        model.addColumn("Kod pocztowy");
        model.addColumn("Numer tel");
        model.addColumn("Email");
        model.addColumn("Rezerwacje");

        fillRoomsTable();
        add(scrollPane, BorderLayout.CENTER);

        addOrChangeRoomDataDesc = new JLabel("Dodaj lub zmień dane klienta");
        addOrChangeRoomDataDesc.setBounds(15, 370, 270, 20);
        add(addOrChangeRoomDataDesc);
        fnameDesc = new JLabel("Imię");
        fnameDesc.setBounds(25, 390, 100, 20);
        add(fnameDesc);
        fnameField = new JTextField();
        fnameField.setBounds(25, 410, 100, 30);
        add(fnameField);

        lnameDesc = new JLabel("Nazwisko");
        lnameDesc.setBounds(130, 390, 100, 20);
        add(lnameDesc);
        lnameField = new JTextField();
        lnameField.setBounds(130, 410, 100, 30);
        add(lnameField);

        city = new JLabel("Miejscowość");
        city.setBounds(235, 390, 100, 20);
        add(city);
        cityField = new JTextField();
        cityField.setBounds(235, 410, 100, 30);
        add(cityField);

        streetDesc = new JLabel("Ulica");
        streetDesc.setBounds(340, 390, 150, 20);
        add(streetDesc);
        streetField = new JTextField();
        streetField.setBounds(340, 410, 150, 30);
        add(streetField);

        streetNrDesc = new JLabel("Numer");
        streetNrDesc.setBounds(495, 390, 100, 20);
        add(streetNrDesc);
        streetNrField = new JTextField();
        streetNrField.setBounds(495, 410, 100, 30);
        add(streetNrField);

        postcodeDesc = new JLabel("Kod pocztowy");
        postcodeDesc.setBounds(600, 390, 100, 20);
        add(postcodeDesc);
        postcodeField = new JTextField();
        postcodeField.setBounds(600, 410, 100, 30);
        add(postcodeField);

        phoneDesc = new JLabel("Telefon");
        phoneDesc.setBounds(15, 510, 150, 20);
        add(phoneDesc);
        phoneField = new JTextField();
        phoneField.setBounds(15, 535, 150, 30);
        add(phoneField);

        emailDesc = new JLabel("Email");
        emailDesc.setBounds(180, 510, 150, 20);
        add(emailDesc);
        emailField = new JTextField();
        emailField.setBounds(180, 535, 200, 30);
        add(emailField);

        ArrayList<String> clientIds = new ArrayList<String>();
        clientIds.add("Nowy");
        try{
            PreparedStatement selectid = MenuPanel.getDb().prepareStatement("SELECT client_id FROM client");
            ResultSet resultid = selectid.executeQuery();
            while(resultid.next())
                clientIds.add(resultid.getString("client_id"));
            resultid.close();
            selectid.close();
        } catch(Exception ser){
            System.out.println("Panel klienta - blad wczytywania tabeli ID");
//            ser.printStackTrace();
            return;
        }
        String [] clientIdsString = new String[clientIds.size()];
        clientIds.toArray(clientIdsString);
        selectClientIdDesc = new JLabel("Wybierz nr klienta do modyfikacji lub wyświetlenia rezerwacji");
        selectClientIdDesc.setBounds(25, 450, 700, 20);
        add(selectClientIdDesc);
        selectClientId = new JComboBox(clientIdsString);
        selectClientId.setBounds(25, 470, 200, 30);
        selectClientId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fillTextFields();
            }
        });
        add(selectClientId);

        showClientReservations = new JButton("Pokaż rezerwacje");
        showClientReservations.setBounds(500, 465, 285, 35);
        add(showClientReservations);
        showClientReservations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ShowReservationClient.client_id = Integer.parseInt(String.valueOf(selectClientId.getSelectedItem()));
                System.out.println("Wysylam client_id="+Integer.parseInt(String.valueOf(selectClientId.getSelectedItem())));
                ShowReservationClient.fillRoomsTable();
                CardLayout cardLayout = (CardLayout) contentPane.getLayout();

                cardLayout.first(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
            }
        });

        createClientButton = new JButton("Dodaj klienta");
        createClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    PreparedStatement updateRoom = MenuPanel.getDb().prepareStatement("INSERT INTO client (fname, lname, city, street, street_nr, postcode, phone, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?)" );

                    updateRoom.setString(1, fnameField.getText());
                    updateRoom.setString(2, lnameField.getText());
                    updateRoom.setString(3, cityField.getText());
                    updateRoom.setString(4, streetField.getText());
                    updateRoom.setInt(5, Integer.parseInt(String.valueOf(streetNrField.getText())));
                    updateRoom.setString(6, postcodeField.getText());
                    updateRoom.setString(7, phoneField.getText());
                    updateRoom.setString(8, emailField.getText());

                    updateRoom.executeUpdate();
                    updateRoom.close();
                    fillRoomsTable();

                    selectClientId.removeAllItems();
                    selectClientId.addItem("Nowy");
                    PreparedStatement selectNr = MenuPanel.getDb().prepareStatement("SELECT client_id FROM client");
                    ResultSet resultNr = selectNr.executeQuery();
                    while(resultNr.next())
                        selectClientId.addItem(resultNr.getString("client_id"));
                    resultNr.close();
                    selectNr.close();

                } catch (Exception ser){
                    System.out.println("Panel klienta - blad zapisywania danych do tabeli client");
//                    ser.printStackTrace();
                }
            }
        });
        createClientButton.setBounds(600, 510, 180, 60);
        add(createClientButton);

        modifyClientButton = new JButton("Zmień dane klienta");
        modifyClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                try {
                    PreparedStatement selectMod = MenuPanel.getDb().prepareStatement("SELECT * FROM client", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet resultMod = selectMod.executeQuery();
                    while(resultMod.next())
                    {
                        if(resultMod.getString("client_id").equals(String.valueOf(selectClientId.getSelectedItem())))
                        {
                            resultMod.updateString("fname", fnameField.getText());
                            resultMod.updateString("lname", lnameField.getText());
                            resultMod.updateString("city", cityField.getText());
                            resultMod.updateString("street", streetField.getText());
                            resultMod.updateInt("street_nr", Integer.parseInt(String.valueOf(streetNrField.getText())));
                            resultMod.updateString("postcode", postcodeField.getText());
                            resultMod.updateString("phone", phoneField.getText());
                            resultMod.updateString("email", emailField.getText());
                            resultMod.updateRow();
                            resultMod.close();
                            selectMod.close();
                            fillRoomsTable();

                            selectClientId.removeAllItems();
                            selectClientId.addItem("Nowy");
                            PreparedStatement selectNr = MenuPanel.getDb().prepareStatement("SELECT client_id FROM client");
                            ResultSet resultNr = selectNr.executeQuery();
                            while(resultNr.next())
                                selectClientId.addItem(resultNr.getString("client_id"));
                            resultNr.close();
                            selectNr.close();
                            break;
                        }
                    }

                    fillRoomsTable();
                    resultMod.close();
                    selectMod.close();

                } catch (Exception ser){
                    System.out.println("Panel klienta - blad zapisywania danych do tabeli client");
//                    ser.printStackTrace();
                }
            }
        });
        modifyClientButton.setBounds(400, 510, 180, 60);
        add(modifyClientButton);


    }

    /**
     * Uzupełnia pola tekstowe i pole wyboru danymi klientów, którzy zostali wybrani do modyfikacji lub wyświetlenia rezerwacji
     */
    void fillTextFields()
    {
        String cl_idS = String.valueOf(selectClientId.getSelectedItem());
        if(cl_idS.equals("Nowy"))
        {
            fnameField.setText("");
            lnameField.setText("");
            cityField.setText("");
            streetField.setText("");
            streetNrField.setText("");
            postcodeField.setText("");
            phoneField.setText("");
            emailField.setText("");
        }
        else
        {
            ResultSet result = null;
            PreparedStatement select = null;
            try{
                select = MenuPanel.getDb().prepareStatement("select * from showclientswithreservationscount");
                result = select.executeQuery();

                while (result.next()) {
                    if(result.getString("client_id").equals(cl_idS)) {
                        fnameField.setText(result.getString("fname"));
                        lnameField.setText(result.getString("lname"));
                        cityField.setText(result.getString("city"));
                        streetField.setText(result.getString("street"));
                        streetNrField.setText(result.getString("street_nr"));
                        postcodeField.setText(result.getString("postcode"));
                        phoneField.setText(result.getString("phone"));
                        emailField.setText(result.getString("email"));
                        break;
                    }
                }
                result.close();
                select.close();
            } catch (Exception ser){
                System.out.println("Panel klienta - blad wczytywania wierszy tabeli");
//                ser.printStackTrace();
            }

        }

    }

    /**
     * Uzupełnia tabele na górze panelu danymi klientów. Umożliwia sortowanie według określonej przez użytkownika kolejności
     */
    void fillRoomsTable()
    {
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        ResultSet result = null;
        PreparedStatement select = null;

        int chosen = sortBy.getSelectedIndex();
        try {
            switch(chosen)
            {
                case 0:
                    select = MenuPanel.getDb().prepareStatement("select * from showclientswithreservationscount ORDER BY client_id");
                    result = select.executeQuery();
                    break;
                case 1:
                    select = MenuPanel.getDb().prepareStatement("select * from showclientswithreservationscount ORDER BY fname");
                    result = select.executeQuery();
                    break;
                case 2:
                    select = MenuPanel.getDb().prepareStatement("select * from showclientswithreservationscount ORDER BY lname");
                    result = select.executeQuery();
                    break;
                case 3:
                    select = MenuPanel.getDb().prepareStatement("select * from showclientswithreservationscount ORDER BY city");
                    result = select.executeQuery();
                    break;
                case 4:
                    select = MenuPanel.getDb().prepareStatement("select * from showclientswithreservationscount ORDER BY postcode");
                    result = select.executeQuery();
                    break;
                case 5:
                    select = MenuPanel.getDb().prepareStatement("select * from showclientswithreservationscount ORDER BY rescount DESC");
                    result = select.executeQuery();
                    break;
                default: break;
            }

            while (result.next()) {
                Vector<String> tableText = new Vector<String>();
                tableText.add(result.getString("client_id"));
                tableText.add(result.getString("fname"));
                tableText.add(result.getString("lname"));
                tableText.add(result.getString("city"));
                tableText.add(result.getString("street"));
                tableText.add(result.getString("street_nr"));
                tableText.add(result.getString("postcode"));
                tableText.add(result.getString("phone"));
                tableText.add(result.getString("email"));
                tableText.add(result.getString("rescount"));
                model.addRow(tableText);
            }
            result.close();
            select.close();
        } catch (Exception ser){
            System.out.println("Panel klienta - blad wczytywania wierszy tabeli");
//            ser.printStackTrace();
        }

    }
}