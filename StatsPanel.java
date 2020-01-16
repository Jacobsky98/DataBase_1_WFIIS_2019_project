import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.Result;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;
import java.util.ArrayList;

class StatsPanel extends JPanel {

    private JButton menuButton;
    private JPanel contentPane;
    private String [] roomsColumns = {"Numer pokoju", "Kategoria", "Liczba miejsc", "Dodatkowe łóżka", "Liczba rezerwacji", "Średnia liczba osób"};
    private String [] clientsColumns = {"Imię", "Nazwisko", "Miejscowość", "Kod pocztowy", "Liczba rezerwacji"};


    private JTable roomsTable;
    static private DefaultTableModel roomsModel = new DefaultTableModel();
    private JScrollPane roomsScrollPane;

    private JTable clientsTable;
    static private DefaultTableModel clientsModel = new DefaultTableModel();
    private JScrollPane clientsScrollPane;




    public StatsPanel(JPanel menuPanel) {

        menuButton = new JButton("Powrót do menu");
        contentPane = menuPanel;

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



        roomsTable = new JTable(roomsModel);
        roomsTable.setFont(new Font("Times New Roman", Font.PLAIN, 13));

        roomsScrollPane = new JScrollPane(roomsTable);
        roomsScrollPane.setBounds(15,50,770,225);

        for(int i = 0; i < roomsColumns.length; i++)
            roomsModel.addColumn(roomsColumns[i]);

        fillRoomsTable();
        add(roomsScrollPane, BorderLayout.CENTER);


        clientsTable = new JTable(clientsModel);
        clientsTable.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        clientsScrollPane = new JScrollPane(clientsTable);
        clientsScrollPane.setBounds(15,325,770,225);

        for(int i = 0; i < clientsColumns.length; i++)
            clientsModel.addColumn(clientsColumns[i]);

        fillClientsTable();
        add(clientsScrollPane, BorderLayout.CENTER);
    }

    static void fillRoomsTable()
    {
        for (int i = roomsModel.getRowCount() - 1; i >= 0; i--) {
            roomsModel.removeRow(i);
        }
        ResultSet result = null;
        PreparedStatement select = null;

        try {
            select = MenuPanel.getDb().prepareStatement("select * FROM najpopularniejszePokoje");
            result = select.executeQuery();

            while (result.next()) {
                Vector<String> tableText = new Vector<String>();
                tableText.add(result.getString("room_nr"));
                tableText.add(result.getString("category"));
                tableText.add(result.getString("capacity"));
                tableText.add(result.getString("extra_beds"));
                tableText.add(result.getString("rezerwacjecount"));
                tableText.add(result.getString("sredniaosob"));
                roomsModel.addRow(tableText);
            }
            result.close();
            select.close();
        } catch (Exception ser){
            System.out.println("Panel statystyk - najlepsze pokoje - blad wczytywania wierszy tabeli");
        }

    }


    static void fillClientsTable()
    {
        for (int i = clientsModel.getRowCount() - 1; i >= 0; i--) {
            clientsModel.removeRow(i);
        }
        ResultSet result = null;
        PreparedStatement select = null;

        try {
            select = MenuPanel.getDb().prepareStatement("select * FROM najlepszyKlient");
            result = select.executeQuery();

            while (result.next()) {
                Vector<String> tableText = new Vector<String>();
                tableText.add(result.getString("fname"));
                tableText.add(result.getString("lname"));
                tableText.add(result.getString("city"));
                tableText.add(result.getString("postcode"));
                tableText.add(result.getString("ilosc"));
                clientsModel.addRow(tableText);
            }
            result.close();
            select.close();
        } catch (Exception ser){
            System.out.println("Panel statystyk - najlepsi klienci - blad wczytywania wierszy tabeli");
        }

    }


}