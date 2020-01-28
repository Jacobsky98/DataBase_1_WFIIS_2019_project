import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * Panel rejestracji nowego użytkownika systemu. Jest dostępny tylko po zalogowaniu się istniejącego już użytkownika. Zapobiega to tworzeniu niezautoryzowanych użytkoników.
 */
class RegisterPanel extends JPanel {
    private JLabel opis;
    private JButton loginButton = new JButton ("Zarejestruj");
    private JButton menuButton;
    private JLabel loginDesc;
    private JTextField loginField;
    private JLabel passwordDesc;
    private JPasswordField passwordField;
    private JPanel contentPane;
    private JLabel operationStatus = new JLabel();

    boolean logIn = false;
    private static Connection db = null;

    /**
     *
     * @return - połączenie z bazą danych
     */
    public static Connection getDb()
    {
        return db;
    }

    /**
     * Konstrukor panelu rejestracji nowego użytkownika systemu. Tworzy wszystkie elementy i przypisuje im domyślne wartości
     * @param panel - panel, w którym zostanie wyświetlona zawartość
     */
    public RegisterPanel(JPanel panel) {
        connectToDb();
        contentPane = panel;
        opis = new JLabel("Rejestracja nowego użytkownika", SwingConstants.CENTER);
        opis.setFont(new Font("Serif", Font.PLAIN, 18));
        opis.setBounds(15, 10, 770, 60);
        add(opis);

        //adjust size and set layout
        setPreferredSize (new Dimension(800, 640));
        setLayout (null);

        loginDesc = new JLabel("Login");
        loginDesc.setBounds(260, 150, 250, 20);
        add(loginDesc);
        loginField = new JTextField();
        loginField.setBounds(260, 180, 250, 50);
        add(loginField);

        passwordDesc = new JLabel("Hasło");
        passwordDesc.setBounds(260, 250, 250, 20);
        add(passwordDesc);
        passwordField = new JPasswordField();
        passwordField.setBounds(260, 280, 250, 50);
        passwordField.setEchoChar('*');
        add(passwordField);

        operationStatus.setBounds(25, 550, 770, 40);
        operationStatus.setFont(new Font("Serif", Font.PLAIN, 15));
        add(operationStatus);


        loginButton.setBounds(260, 340, 250, 70);
        loginButton.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                String loginData = loginField.getText();
                if(loginData.length() >= 1)
                {
                    try {
                        PreparedStatement insertUser = MenuPanel.getDb().prepareStatement("INSERT INTO loginData (login, password) VALUES (?, ?)");

                        insertUser.setString(1, loginField.getText());
                        insertUser.setString(2, String.valueOf(passwordField.getPassword()));

                        insertUser.executeUpdate();
                        insertUser.close();
                        logIn = true;

                    } catch (Exception ser) {
                        System.out.println(ser.getMessage());
                        ser.printStackTrace();
                    }

                    if (logIn) {
    //                    CardLayout cardLayout = (CardLayout) contentPane.getLayout();
    //                    cardLayout.first(contentPane);
                        operationStatus.setText("Zarejestrowano uzytkownika " + String.valueOf(loginField.getText()));
                        loginField.setText("");
                        passwordField.setText("");
                        logIn = false;
                    } else {
                        operationStatus.setText("Niepoprawne dane. Spróbuj jeszcze raz");
                    }
                }
            }
        });

        add (loginButton);

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
     * Tworzy połączenie z bazą danych
     */
    public void connectToDb()
    {
        try {
            db = DriverManager.getConnection("jdbc:postgresql://pascal.fis.agh.edu.pl:5432/?currentSchema=projekt",
                    "u7salamon", "7salamon");
        } catch (SQLException ser) {
            System.out.println("Brak polaczenia z baza danych, wydruk logu sledzenia i koniec.");
            ser.printStackTrace();
            //System.exit(1);
        }
    }
}

