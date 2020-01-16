import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.Result;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.ArrayList;

class ShowReservationClient extends JPanel {

    private JLabel description;
    private JLabel sortByText;
    private String [] sortByOptions = {"Data wejścia", "Data wyjścia", "Rachunek", "Niezapłacone", "Numer pokoju"};
    static private JComboBox sortBy;
    private JButton menuButton;
    private JPanel contentPane;
    private JTable roomsTable;
    static private DefaultTableModel model;
    private JScrollPane scrollPane;

    // add or change data room
    private JLabel selectToDeleteDesc;
    static private JComboBox selectToDelete;
    private JButton deleteReservationButton;


    private JButton showRoomReservations;
    static int client_id = -1;
    static int [] reservation_ids = null;
    static int [] payment_ids = null;

    private JLabel howMuchToPayDesc;
    static private JComboBox howMuchToPaySelectId = new JComboBox();
    private JTextField howMuchToPayField;
    private JButton howMuchToPayButton;
    private JLabel operationStatus = new JLabel();



    public ShowReservationClient(JPanel menuPanel) {

        menuButton = new JButton("Powrót do menu");
        contentPane = menuPanel;


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
//        model.addColumn("Imię");
//        model.addColumn("Nazwisko");
//        model.addColumn("Telefon");
//        model.addColumn("Dorości");
//        model.addColumn("Dzieci");
        model.addColumn("Data wejścia");
        model.addColumn("Data wyjścia");
        model.addColumn("Data złożenia");
        model.addColumn("Zapłacono");
        model.addColumn("Do zapłaty");
        model.addColumn("Pokój");

        fillRoomsTable();
        add(scrollPane, BorderLayout.CENTER);


        ArrayList<String> roomIdsArray = new ArrayList<String>();
        roomIdsArray.add("Nic");
        try{
            PreparedStatement select = MenuPanel.getDb().prepareStatement("SELECT reservation_id FROM reservation");
            ResultSet result = select.executeQuery();
            while(result.next())
                roomIdsArray.add(result.getString("reservation_id"));
            result.close();
            select.close();
        } catch(Exception ser){
            System.out.println("Panel klienta - blad wczytywania tabeli");
//            ser.printStackTrace();
            return;
        }
        String [] roomID = new String[roomIdsArray.size()];
        roomIdsArray.toArray(roomID);

        howMuchToPayDesc = new JLabel("Wybierz numer rezerwacji do dopłaty i wpisz kwotę");
        howMuchToPayDesc.setBounds(25, 370, 400, 20);
        add(howMuchToPayDesc);

        howMuchToPaySelectId.setBounds(25, 400, 100, 30);
        add(howMuchToPaySelectId);

        howMuchToPayField = new JTextField();
        howMuchToPayField.setBounds(130, 400, 100, 30);
        add(howMuchToPayField);

        operationStatus.setBounds(15,550,770,20);
        add(operationStatus);

        howMuchToPayButton = new JButton("Dopłać");
        howMuchToPayButton.setBounds(245, 400, 100, 30);
        howMuchToPayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(Double.parseDouble(String.valueOf(howMuchToPayField.getText())) > 0)
                {
                    try {
                        double iWantPay = Double.parseDouble(String.valueOf(howMuchToPayField.getText()));
                        PreparedStatement selectPayment = MenuPanel.getDb().prepareStatement("SELECT * FROM payment WHERE payment_id="+payment_ids[howMuchToPaySelectId.getSelectedIndex()], ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet resultPayment = selectPayment.executeQuery();

                        while(resultPayment.next()) {
                            resultPayment.updateInt("payment_id", resultPayment.getInt("payment_id"));
                            resultPayment.updateObject("already_paid", iWantPay+resultPayment.getDouble("already_paid"), java.sql.Types.NUMERIC);
                            resultPayment.updateObject("total", resultPayment.getDouble("total"), java.sql.Types.NUMERIC);
                            resultPayment.updateRow();
                        }
                        resultPayment.close();
                        selectPayment.close();

                        DecimalFormat df2 = new DecimalFormat("#.##");
                        operationStatus.setText("Zapłacono " +df2.format(iWantPay) + "zł");

                    } catch (Exception ser){
                        System.out.println("Panel rezerwacji klienta - blad zapisywania danych do tabeli payment");
//                        ser.printStackTrace();
                    }
                    fillRoomsTable();
                }else{
                    operationStatus.setText("Nieprawidłowa kwota płatności. Spróbuj jeszcze raz.");
                }

            }
        });
        add(howMuchToPayButton);


        selectToDeleteDesc = new JLabel("Wybierz ID rezerwacji do usunięcia");
        selectToDeleteDesc.setBounds(25, 450, 700, 20);
        add(selectToDeleteDesc);
        selectToDelete = new JComboBox(roomID);
        selectToDelete.setBounds(25, 470, 200, 30);
        add(selectToDelete);

        showRoomReservations = new JButton("Powrót do klientów");
        showRoomReservations.setBounds(500, 465, 285, 35);
        add(showRoomReservations);
        showRoomReservations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CardLayout cardLayout = (CardLayout) contentPane.getLayout();
                cardLayout.first(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
            }
        });

        deleteReservationButton = new JButton("Usuń rezerwacje");
        deleteReservationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    if(selectToDelete.getSelectedItem().equals("Nic") == false)
                    {
                        PreparedStatement deleteRes = MenuPanel.getDb().prepareStatement("DELETE FROM reservation where reservation_id=?");
                        int idToDelete = Integer.parseInt(String.valueOf(selectToDelete.getSelectedItem()));
                        deleteRes.setInt(1, idToDelete);
                        deleteRes.executeUpdate();
                        deleteRes.close();
                        fillRoomsTable();
                        MenuPanel.updateRoomsStatus();


                    }
                } catch (Exception ser){
                    System.out.println("Panel rezerwacji pokoi klienta - blad usuwania z tabeli");
                    ser.getMessage();
//                    ser.printStackTrace();
                }
            }
        });
        deleteReservationButton.setBounds(235, 470, 250, 30);
        add(deleteReservationButton);

        try{
            int room_nr = 0;// tu jest cos zle
            PreparedStatement selectNr = MenuPanel.getDb().prepareStatement("SELECT reservation_id FROM reservation WHERE client_id="+client_id);
            ResultSet resultNr = selectNr.executeQuery();
            while(resultNr.next())
                room_nr = resultNr.getInt("room_nr");
            resultNr.close();
            selectNr.close();
            description = new JLabel("Rezerwacje klienta ");// + room_nr);
            description.setBounds(35, 10, 300, 25);
            add(description);
        }catch(Exception ser){
//            ser.printStackTrace();
        }

    }



    public static void fillRoomsTable()
    {
        System.out.println("Room id to show " + client_id);
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        ResultSet resultRes = null;
        PreparedStatement select = null;

        int chosen = sortBy.getSelectedIndex();
        try {
            switch(chosen)
            {
                case 0:
                    select = MenuPanel.getDb().prepareStatement("SELECT * FROM showReservationClient("+client_id+") ORDER BY check_in DESC");
                    resultRes = select.executeQuery();
                    break;
                case 1:
                    select = MenuPanel.getDb().prepareStatement("SELECT * FROM showReservationClient("+client_id+") ORDER BY check_out DESC");
                    resultRes = select.executeQuery();
                    break;
                case 2:
                    select = MenuPanel.getDb().prepareStatement("SELECT * FROM showReservationClient("+client_id+") ORDER BY total DESC");
                    resultRes = select.executeQuery();
                    break;
                case 3:
                    select = MenuPanel.getDb().prepareStatement("SELECT * FROM showReservationClient("+client_id+") ORDER BY to_pay DESC");
                    resultRes = select.executeQuery();
                    break;
                case 4:
                    select = MenuPanel.getDb().prepareStatement("SELECT * FROM showReservationClient("+client_id+") ORDER BY room_nr");
                    resultRes = select.executeQuery();
                    break;
                default: break;
            }


            ArrayList<Integer> res_id = new ArrayList<Integer>();
            ArrayList<Integer> pay_id = new ArrayList<Integer>();
            while (resultRes.next()) {
                Vector<String> tableText = new Vector<String>();
                tableText.add(resultRes.getString("reservation_id"));
                res_id.add(Integer.parseInt(String.valueOf(resultRes.getString("reservation_id"))));
                pay_id.add(Integer.parseInt(String.valueOf(resultRes.getString("payment_id"))));
//                tableText.add(resultRes.getString("fname"));
//                tableText.add(resultRes.getString("lname"));
//                tableText.add(resultRes.getString("phone"));
//                tableText.add(resultRes.getString("adults"));
//                tableText.add(resultRes.getString("children"));
                tableText.add(resultRes.getString("check_in"));
                tableText.add(resultRes.getString("check_out"));
                tableText.add(resultRes.getString("date_placed"));
                tableText.add(resultRes.getString("already_paid"));
                tableText.add(resultRes.getString("to_pay"));
                tableText.add(resultRes.getString("room_nr"));
                model.addRow(tableText);
            }
            resultRes.close();
            select.close();

            reservation_ids = new int[res_id.size()];
            payment_ids = new int[pay_id.size()];
            howMuchToPaySelectId.removeAllItems();
            selectToDelete.removeAllItems();
            for(int i = 0; i < reservation_ids.length; i++)
            {
                reservation_ids[i] = res_id.get(i);
                howMuchToPaySelectId.addItem(reservation_ids[i]);
                selectToDelete.addItem(reservation_ids[i]);
            }
            for(int i = 0; i < payment_ids.length; i++)
            {
                payment_ids[i] = pay_id.get(i);
            }

        } catch (Exception ser){
            System.out.println("Rezerwacje klienta - blad wczytywania wierszy tabeli");
//            ser.printStackTrace();
        }

    }
}