import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.Result;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;
import java.util.ArrayList;

class ShowReservation extends JPanel {

    private JLabel description;
    private JLabel sortByText;
    private String [] sortByOptions = {"Data wejścia", "Data wyjścia", "Rachunek", "Niezapłacone", "Nazwisko"};
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
    static int room_id = -1;



    public ShowReservation(JPanel menuPanel) {

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
        model.addColumn("Imię");
        model.addColumn("Nazwisko");
        model.addColumn("Telefon");
        model.addColumn("Dorości");
        model.addColumn("Dzieci");
        model.addColumn("Data wejścia");
        model.addColumn("Data wyjścia");
        model.addColumn("Data złożenia");
        model.addColumn("Zapłacono");
        model.addColumn("Do zapłaty");

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
            ser.printStackTrace();
            return;
        }
        String [] roomID = new String[roomIdsArray.size()];
        roomIdsArray.toArray(roomID);
/*
        selectToDeleteDesc = new JLabel("Wybierz ID rezerwacji do usunięcia");
        selectToDeleteDesc.setBounds(25, 450, 700, 20);
        add(selectToDeleteDesc);
        selectToDelete = new JComboBox(roomID);
        selectToDelete.setBounds(25, 470, 200, 30);
        add(selectToDelete);
*/
        showRoomReservations = new JButton("Powrót do pokoi");
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
            }
        });
/*
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

                        selectToDelete.removeAllItems();
                        PreparedStatement selectNr = MenuPanel.getDb().prepareStatement("SELECT reservation_id FROM reservation");
                        ResultSet resultNr = selectNr.executeQuery();
                        while(resultNr.next())
                            selectToDelete.addItem(resultNr.getString("reservation_id"));
                        resultNr.close();
                        selectNr.close();
                    }
                } catch (Exception ser){
                    System.out.println("Panel rezerwacji pokoi - blad usuwania z tabeli");
                    ser.getMessage();
                    ser.printStackTrace();
                }
            }
        });
        deleteReservationButton.setBounds(470, 510, 250, 60);
        add(deleteReservationButton);
*/
        try{
            int room_nr = 0;
            PreparedStatement selectNr = MenuPanel.getDb().prepareStatement("SELECT room_nr FROM room WHERE room_id="+room_id);
            ResultSet resultNr = selectNr.executeQuery();
            while(resultNr.next())
                room_nr = resultNr.getInt("room_nr");
            resultNr.close();
            selectNr.close();
            description = new JLabel("Rezerwacje pokoju ");// + room_nr);
            description.setBounds(35, 10, 300, 25);
            add(description);
        }catch(Exception ser){
            ser.printStackTrace();
        }

    }



    public static void fillRoomsTable()
    {
        System.out.println("Room id to show " + room_id);
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
                    select = MenuPanel.getDb().prepareStatement("SELECT * FROM showReservationRoom("+room_id+") ORDER BY check_in DESC");
                    resultRes = select.executeQuery();
                    break;
                case 1:
                    select = MenuPanel.getDb().prepareStatement("SELECT * FROM showReservationRoom("+room_id+") ORDER BY check_out DESC");
                    resultRes = select.executeQuery();
                    break;
                case 2:
                    select = MenuPanel.getDb().prepareStatement("SELECT * FROM showReservationRoom("+room_id+") ORDER BY total DESC");
                    resultRes = select.executeQuery();
                    break;
                case 3:
                    select = MenuPanel.getDb().prepareStatement("SELECT * FROM showReservationRoom("+room_id+") ORDER BY to_pay DESC");
                    resultRes = select.executeQuery();
                    break;
                case 4:
                    select = MenuPanel.getDb().prepareStatement("SELECT * FROM showReservationRoom("+room_id+") ORDER BY lname");
                    resultRes = select.executeQuery();
                    break;
                default: break;
            }

            while (resultRes.next()) {
                Vector<String> tableText = new Vector<String>();
//                selectToDelete.addItem(resultRes.getString("reservation_id"));
                tableText.add(resultRes.getString("reservation_id"));
                tableText.add(resultRes.getString("fname"));
                tableText.add(resultRes.getString("lname"));
                tableText.add(resultRes.getString("phone"));
                tableText.add(resultRes.getString("adults"));
                tableText.add(resultRes.getString("children"));
                tableText.add(resultRes.getString("check_in"));
                tableText.add(resultRes.getString("check_out"));
                tableText.add(resultRes.getString("date_placed"));
                tableText.add(resultRes.getString("already_paid"));
                tableText.add(resultRes.getString("to_pay"));
                model.addRow(tableText);
            }
            resultRes.close();
            select.close();
        } catch (Exception ser){
            System.out.println("Panel hisotrii rezerwacji pokoi - blad wczytywania wierszy tabeli");
            ser.printStackTrace();
        }

    }
}