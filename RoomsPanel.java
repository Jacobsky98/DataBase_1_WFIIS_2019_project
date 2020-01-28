import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;
import java.util.ArrayList;

/**
 * Panel pokazujący dane wszystkich pokoi. Umożliwia tworzenie nowych pokoi lub modyfikowanie ich danych
 */
class RoomsPanel extends JPanel {

    private JLabel description;
    private JLabel sortByText;
    private String [] sortByOptions = {"Kategoria", "Status", "Liczba miejsc", "Ilość dodatkowych łóżek", "Numer pokoju"};
    private JComboBox sortBy;
    private JButton menuButton;
    private JPanel contentPane;
    private JTable roomsTable;
    private DefaultTableModel model;
    private JScrollPane scrollPane;

    // add or change data room
    private JLabel addOrChangeRoomDataDesc;
    private JLabel selectCategoryDesc;
    private JComboBox selectCategory;
    private JLabel selectStatusDesc;
    private JComboBox selectStatus;
    private JLabel setCapacityDesc;
    private JTextField setCapacity;
    private JLabel setExtraBedsDesc;
    private JTextField setExtraBeds;
    private JLabel selectForKidsDesc;
    private JComboBox selectForKids;
    private JLabel selectRoomNrDesc;
    private JComboBox selectRoomNr;
    private JLabel setRoomNrDesc;
    private JTextField setRoomNr;
    private JButton createRoomButton;
    private JButton modifyRoomButton;
    private JButton showRoomReservations;


    /**
     * Konstrukor panelu pokoi. Tworzy wszystkie elementy i przypisuje im domyślne wartości
     * @param panel - panel, w którym zostanie wyświetlona zawartość
     */
    public RoomsPanel(JPanel menuPanel) {

        menuButton = new JButton("Powrót do menu");
        contentPane = menuPanel;
        description = new JLabel("Panel pokoi", SwingConstants.CENTER);
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

        model.addColumn("Kategoria");
        model.addColumn("Status");
        model.addColumn("Liczba miejsc");
        model.addColumn("Dodatkowe łóżka");
        model.addColumn("Dla dzieci");
        model.addColumn("Numer pokoju");

        fillRoomsTable();
        add(scrollPane, BorderLayout.CENTER);

        addOrChangeRoomDataDesc = new JLabel("Dodaj lub zmień dane pokoju");
        addOrChangeRoomDataDesc.setBounds(15, 370, 270, 20);
        add(addOrChangeRoomDataDesc);
        selectCategoryDesc = new JLabel("Kategoria");
        selectCategoryDesc.setBounds(25, 390, 100, 20);
        add(selectCategoryDesc);
        ArrayList<String> roomCategories = new ArrayList<String>();
        ArrayList<String> roomStatuses = new ArrayList<String>();
        ArrayList<String> roomNumbers = new ArrayList<String>();
        roomNumbers.add("Nowy");
        try{
            PreparedStatement select = MenuPanel.getDb().prepareStatement("SELECT category FROM room_category");
            ResultSet result = select.executeQuery();
            while(result.next())
                roomCategories.add(result.getString("category"));
            result.close();
            select.close();

            select = MenuPanel.getDb().prepareStatement("SELECT status FROM room_status");
            result = select.executeQuery();
            while(result.next())
                roomStatuses.add(result.getString("status"));
            result.close();
            select.close();

            select = MenuPanel.getDb().prepareStatement("SELECT room_nr FROM room");
            result = select.executeQuery();
            while(result.next())
                roomNumbers.add(result.getString("room_nr"));
            result.close();
            select.close();

        } catch(Exception ser){
            System.out.println("Panel pokoi - blad wczytywania tabeli");
            return;
        }
        String [] roomCat = new String[roomCategories.size()];
        roomCategories.toArray(roomCat);
        String [] roomStat = new String[roomStatuses.size()];
        roomStatuses.toArray(roomStat);
        String [] roomNr = new String[roomNumbers.size()];
        roomNumbers.toArray(roomNr);
        selectCategory = new JComboBox(roomCat);
        selectCategory.setBounds(25, 410, 100, 30);
        add(selectCategory);
        selectStatusDesc = new JLabel("Status");
        selectStatusDesc.setBounds(130, 390, 100, 20);
        add(selectStatusDesc);
        selectStatus = new JComboBox(roomStat);
        selectStatus.setBounds(130, 410, 100, 30);
        add(selectStatus);
        setCapacityDesc = new JLabel("Ilość miejsc");
        setCapacityDesc.setBounds(235, 390, 100, 20);
        add(setCapacityDesc);
        setCapacity = new JTextField();
        setCapacity.setBounds(235, 410, 100, 30);
        add(setCapacity);
        setExtraBedsDesc = new JLabel("Dodatkowe łóżka");
        setExtraBedsDesc.setBounds(340, 390, 150, 20);
        add(setExtraBedsDesc);
        setExtraBeds = new JTextField();
        setExtraBeds.setBounds(340, 410, 150, 30);
        add(setExtraBeds);
        selectForKidsDesc = new JLabel("Z dziećmi?");
        selectForKidsDesc.setBounds(495, 390, 100, 20);
        add(selectForKidsDesc);
        String [] kidsOptions = {"Tak", "Nie"};
        selectForKids = new JComboBox(kidsOptions);
        selectForKids.setBounds(495, 410, 100, 30);
        add(selectForKids);

        setRoomNrDesc = new JLabel("Numer");
        setRoomNrDesc.setBounds(600, 390, 100, 20);
        add(setRoomNrDesc);
        setRoomNr = new JTextField();
        setRoomNr.setBounds(600, 410, 100, 30);
        add(setRoomNr);


        selectRoomNrDesc = new JLabel("Wybierz nr pokoju do modyfikacji lub wyświetlenia rezerwacji");
        selectRoomNrDesc.setBounds(25, 450, 700, 20);
        add(selectRoomNrDesc);
        selectRoomNr = new JComboBox(roomNr);
        selectRoomNr.setBounds(25, 470, 200, 30);
        selectRoomNr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fillTextFields();
            }
        });
        add(selectRoomNr);

        showRoomReservations = new JButton("Pokaż rezerwacje");
        showRoomReservations.setBounds(500, 465, 285, 35);
        add(showRoomReservations);
        showRoomReservations.addActionListener(new ActionListener() {
            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                reservationFrame = new ReservationFrame("room", Integer.parseInt(String.valueOf(selectRoomNr.getSelectedItem())));
//            }
            public void actionPerformed(ActionEvent e)
            {
                ShowReservation.room_id = Integer.parseInt(String.valueOf(selectRoomNr.getSelectedItem()));
                ShowReservation.fillRoomsTable();
                CardLayout cardLayout = (CardLayout) contentPane.getLayout();
                cardLayout.first(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
            }
        });

        createRoomButton = new JButton("Stwórz nowy pokój");
        createRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean forKidsBool = false;
                if(String.valueOf(selectForKids.getSelectedItem()).equals("Tak"))
                    forKidsBool = true;

                try {
                    PreparedStatement updateRoom = MenuPanel.getDb().prepareStatement("INSERT INTO room (category_id, status_id, capacity, extra_beds, for_kids, room_nr) VALUES (?, ?, ?, ?, ?, ?)" );

                    updateRoom.setInt(1, selectCategory.getSelectedIndex()+1);
                    updateRoom.setInt(2, selectStatus.getSelectedIndex()+1);
                    updateRoom.setInt(3, Integer.parseInt(setCapacity.getText()));
                    updateRoom.setInt(4, Integer.parseInt(setExtraBeds.getText()));
                    updateRoom.setBoolean(5, forKidsBool);
                    updateRoom.setInt(6, Integer.parseInt(setRoomNr.getText()));
                    updateRoom.executeUpdate();
                    updateRoom.close();
                    fillRoomsTable();

                    selectRoomNr.removeAllItems();
                    selectRoomNr.addItem("Nowy");
                    PreparedStatement selectNr = MenuPanel.getDb().prepareStatement("SELECT room_nr FROM room");
                    ResultSet resultNr = selectNr.executeQuery();
                    while(resultNr.next())
                        selectRoomNr.addItem(resultNr.getString("room_nr"));
                    resultNr.close();
                    selectNr.close();

                } catch (Exception ser){
                    System.out.println("Panel pokoi - blad zapisywania danych do tabeli room");
                }
            }
        });
        createRoomButton.setBounds(470, 510, 250, 60);
        add(createRoomButton);

        modifyRoomButton = new JButton("Modyfikuj istniejący pokój");
        modifyRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean forKidsBool = false;
                if(String.valueOf(selectForKids.getSelectedItem()).equals("Tak"))
                    forKidsBool = true;

                try {
                    PreparedStatement selectMod = MenuPanel.getDb().prepareStatement("SELECT * FROM room", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet resultMod = selectMod.executeQuery();
                    while(resultMod.next())
                    {
                        if(resultMod.getString("room_nr").equals(String.valueOf(selectRoomNr.getSelectedItem())))
                        {
                            resultMod.updateInt("category_id", selectCategory.getSelectedIndex()+1);
                            resultMod.updateInt("status_id", selectStatus.getSelectedIndex()+1);
                            resultMod.updateInt("capacity", Integer.parseInt(setCapacity.getText()));
                            resultMod.updateInt("extra_beds", Integer.parseInt(setExtraBeds.getText()));
                            resultMod.updateBoolean("for_kids", forKidsBool);
                            resultMod.updateInt("room_nr", Integer.parseInt(setRoomNr.getText()));
                            resultMod.updateRow();
                            resultMod.close();
                            selectMod.close();
                            fillRoomsTable();

                            selectRoomNr.removeAllItems();
                            selectRoomNr.addItem("Nowy");
                            PreparedStatement selectNr = MenuPanel.getDb().prepareStatement("SELECT room_nr FROM room");
                            ResultSet resultNr = selectNr.executeQuery();
                            while(resultNr.next())
                                selectRoomNr.addItem(resultNr.getString("room_nr"));
                            resultNr.close();
                            selectNr.close();
                            break;
                        }
                    }

                    fillRoomsTable();
                    resultMod.close();
                    selectMod.close();

                } catch (Exception ser){
                    System.out.println("Panel pokoi - blad zapisywania danych do tabeli room");
//                    ser.printStackTrace();
                }
            }
        });
        modifyRoomButton.setBounds(200, 510, 250, 60);
        add(modifyRoomButton);


    }

    /**
     * Uzupełnia pola tekstowe i pole wyboru danymi pokojów, które zostali wybrani do modyfikacji
     */
    void fillTextFields()
    {
        String cl_idS = String.valueOf(selectRoomNr.getSelectedItem());
        if(cl_idS.equals("Nowy"))
        {
            selectCategory.setSelectedIndex(0);
            selectStatus.setSelectedIndex(0);
            setCapacity.setText("");
            setExtraBeds.setText("");
            selectForKids.setSelectedIndex(0);
            setRoomNr.setText("");
        }
        else
        {
            ResultSet result = null;
            PreparedStatement select = null;
            try{
                select = MenuPanel.getDb().prepareStatement("SELECT * FROM room");
                result = select.executeQuery();

                while (result.next()) {
                    if(result.getString("room_nr").equals(cl_idS)) {
                        selectCategory.setSelectedIndex(result.getInt("category_id")-1);
                        selectStatus.setSelectedIndex(result.getInt("status_id")-1);
                        setCapacity.setText(result.getString("capacity"));
                        setExtraBeds.setText(result.getString("extra_beds"));
                        if(result.getBoolean("for_kids"))
                            selectForKids.setSelectedIndex(0);
                        else
                            selectForKids.setSelectedIndex(1);
                        setRoomNr.setText(result.getString("room_nr"));
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
     * Uzupełnia pole wyboru statusu i kategorii z wszystkich dostępnych dla pokoju.
     */
    void fillCategoryStatusNumber(){
        ArrayList<String> roomCategories = new ArrayList<String>();
        ArrayList<String> roomStatuses = new ArrayList<String>();
        ArrayList<String> roomNumbers = new ArrayList<String>();
        roomNumbers.add("Nowy");
        try{
            PreparedStatement select = MenuPanel.getDb().prepareStatement("SELECT category FROM room_category");
            ResultSet result = select.executeQuery();
            while(result.next())
                roomCategories.add(result.getString("category"));
            result.close();
            select.close();

            select = MenuPanel.getDb().prepareStatement("SELECT status FROM room_status");
            result = select.executeQuery();
            while(result.next())
                roomStatuses.add(result.getString("status"));
            result.close();
            select.close();

            select = MenuPanel.getDb().prepareStatement("SELECT room_nr FROM room");
            result = select.executeQuery();
            while(result.next())
                roomNumbers.add(result.getString("room_nr"));
            result.close();
            select.close();

        } catch(Exception ser){
            System.out.println("Panel pokoi - blad wczytywania tabeli");
            return;
        }

        String [] roomCat = new String[roomCategories.size()];
        roomCategories.toArray(roomCat);
        String [] roomStat = new String[roomStatuses.size()];
        roomStatuses.toArray(roomStat);
        String [] roomNr = new String[roomNumbers.size()];
        roomNumbers.toArray(roomNr);
        selectCategory.removeAllItems();
        selectStatus.removeAllItems();
        for(String x : roomCat){
            selectCategory.addItem(x);
        }
        for(String x : roomStat){
            selectStatus.addItem(x);
        }
    }

    /**
     * Uzupełnia tablice wyświetlająca wszystkie dostępne pokoje. Umożliwia sortowanie według określonej przez użytkownika kolejności
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
                    select = MenuPanel.getDb().prepareStatement("select * FROM showRoomsWithDesc ORDER BY category");
                    result = select.executeQuery();
                    break;
                case 1:
                    select = MenuPanel.getDb().prepareStatement("select * FROM showRoomsWithDesc ORDER BY status");
                    result = select.executeQuery();
                    break;
                case 2:
                    select = MenuPanel.getDb().prepareStatement("select * FROM showRoomsWithDesc ORDER BY capacity");
                    result = select.executeQuery();
                    break;
                case 3:
                    select = MenuPanel.getDb().prepareStatement("select * FROM showRoomsWithDesc ORDER BY extra_beds");
                    result = select.executeQuery();
                    break;
                case 4:
                    select = MenuPanel.getDb().prepareStatement("select * FROM showRoomsWithDesc ORDER BY room_nr");
                    result = select.executeQuery();
                    break;
                default: break;
            }

            while (result.next()) {
                Vector<String> tableText = new Vector<String>();
                tableText.add(result.getString("category"));
                tableText.add(result.getString("status"));
                tableText.add(result.getString("capacity"));
                tableText.add(result.getString("extra_beds"));
                if(result.getString("for_kids").equals("t"))
                    tableText.add("Tak");
                else tableText.add("Nie");
                tableText.add(result.getString("room_nr"));
                model.addRow(tableText);
            }
            result.close();
            select.close();
        } catch (Exception ser){
            System.out.println("Panel pokoi - blad wczytywania wierszy tabeli");
        }

    }
}