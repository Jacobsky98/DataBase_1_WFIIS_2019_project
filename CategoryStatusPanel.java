import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.*;

/**
 * Panel dodawania nowych kategorii i statsów do określania pokoju
 */
class CategoryStatusPanel extends JPanel {
    private JLabel opis;
    private JButton menuButton;

    private JButton statusButton = new JButton ("Nowy status");
    private JLabel statusDesc;
    private JTextField statusField;

    private JLabel categoryDesc;
    private JTextField categoryField;
    private JLabel priceDesc;
    private JTextField priceField;
    private JButton categoryButton = new JButton ("Nowa kategoria");


    private JPanel contentPane;
    private JLabel operationStatus = new JLabel();

    private static Connection db = null;

    /**
     * Funkcja zwracająca połączenie z bazą danych do operowania na niej
     * @return - połączenie z bazą danych
     */
    public static Connection getDb()
    {
        return db;
    }

    /**
     * Konstrukor panelu dodawania nowych kategorii i statusów. Tworzy wszystkie elementy i przypisuje im domyślne wartości
     * @param panel - panel, w którym zostanie wyświetlona zawartość
     */
    public CategoryStatusPanel(JPanel panel) {
        connectToDb();
        contentPane = panel;
        opis = new JLabel("Menu dodawania nowych kategorii i statusów pokojów", SwingConstants.CENTER);
        opis.setFont(new Font("Serif", Font.PLAIN, 18));
        opis.setBounds(15, 10, 770, 60);
        add(opis);

        //adjust size and set layout
        setPreferredSize (new Dimension(800, 640));
        setLayout (null);

        JLabel statusHead = new JLabel("Status", SwingConstants.CENTER);
        statusHead.setBounds(15, 120, 370, 20);
        add(statusHead);

        JLabel categoryHead = new JLabel("Kategoria", SwingConstants.CENTER);
        categoryHead.setBounds(415, 120, 370, 20);
        add(categoryHead);

        statusDesc = new JLabel("Nazwa");
        statusDesc.setBounds(115, 170, 170, 20);
        add(statusDesc);
        statusField = new JTextField();
        statusField.setBounds(115, 200, 170, 30);
        add(statusField);

        statusButton.setBounds(115, 250, 170, 40);
        statusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    PreparedStatement insertUser = MenuPanel.getDb().prepareStatement("INSERT INTO room_status (status) VALUES (?)");

                    insertUser.setString(1, statusField.getText().substring(0, 1).toUpperCase() + statusField.getText().substring(1).toLowerCase());

                    insertUser.executeUpdate();
                    insertUser.close();
                    operationStatus.setText("Dodano nowy status.");
                } catch (Exception ser) {
                    System.out.println(ser.getMessage());
//                    ser.printStackTrace();
                    operationStatus.setText(ser.getMessage());
                }
            }
        });
        add(statusButton);

        categoryDesc = new JLabel("Nazwa");
        categoryDesc.setBounds(515, 170, 170, 20);
        add(categoryDesc);
        categoryField = new JTextField();
        categoryField.setBounds(515, 200, 170, 30);
        add(categoryField);

        priceDesc = new JLabel("Cena za osobę");
        priceDesc.setBounds(515, 250, 170, 20);
        add(priceDesc);
        priceField = new JTextField();
        priceField.setBounds(515, 280, 170, 30);
        add(priceField);

        categoryButton.setBounds(515, 330, 170, 40);
        categoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    PreparedStatement insertUser = MenuPanel.getDb().prepareStatement("INSERT INTO room_category (category, price_for_person) VALUES (?, ?)");

                    insertUser.setString(1, categoryField.getText().substring(0, 1).toUpperCase() + categoryField.getText().substring(1).toLowerCase());
                    insertUser.setDouble(2, Double.parseDouble(String.valueOf(priceField.getText())));

                    insertUser.executeUpdate();
                    insertUser.close();
                    operationStatus.setText("Dodano nową kateorie.");
                } catch (Exception ser) {
                    System.out.println(ser.getMessage());
//                    ser.printStackTrace();
                    operationStatus.setText(ser.getMessage());
                }
            }
        });
        add(categoryButton);

        operationStatus.setBounds(25, 550, 770, 40);
        operationStatus.setFont(new Font("Serif", Font.PLAIN, 15));
        add(operationStatus);



        menuButton = new JButton("Powrót do menu");
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
    }

    /**
     * Funckja ustanawiająca połączenie z bazą danych
     */
    public void connectToDb()
    {
        try {
            db = DriverManager.getConnection("jdbc:postgresql://pascal.fis.agh.edu.pl:5432/?currentSchema=projekt",
                    "u7salamon", "7salamon");
        } catch (SQLException ser) {
            System.out.println("Brak polaczenia z baza danych, wydruk logu sledzenia i koniec.");
            //ser.printStackTrace();
            //System.exit(1);
        }
    }
}

