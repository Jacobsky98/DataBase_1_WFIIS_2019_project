import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.sql.*;

/**
 * Panel logowania. Wyświetlane są w nim wszystkie informacje i komórki powalające użytkownikowi na l
 */
class LoginPanel extends JPanel {
    private JLabel opis;
    private JButton loginButton = new JButton ("Zaloguj");
    private JLabel loginDesc;
    private JTextField loginField;
    private JLabel passwordDesc;
    private JPasswordField passwordField;
    private JPanel contentPane;
    private JLabel operationStatus = new JLabel();

    boolean logIn = false;
    private static Connection db = null;


    public static Connection getDb()
    {
        return db;
    }

    /**
     * Konstrukor panelu logowania. Tworzy wszystkie elementy i przypisuje im domyślne wartości
     * @param panel - panel, w którym zostanie wyświetlona zawartość
     */
    public LoginPanel(JPanel panel) {
        connectToDb();
        contentPane = panel;
        opis = new JLabel("Zaloguj się, aby kontynuować", SwingConstants.CENTER);
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

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    login();
                }
            }
        });

        loginField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    login();
                }
            }
        });

        operationStatus.setBounds(25, 600, 770, 40);
        operationStatus.setFont(new Font("Serif", Font.PLAIN, 15));
        operationStatus.setText("Domyślnie (login: admin, hasło: admin)");
        add(operationStatus);

        loginButton.setBounds(260, 340, 250, 70);
        loginButton.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        add (loginButton);
    }

    /**
     * Funkcja, która ustawnia połączenie z schematem projekt z bazy danych jdbc:postgresql://pascal.fis.agh.edu.pl:5432/?currentSchema=projekt.
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

    /**
     * Funkcja sprawdzająca poprawność wprowadzonych danych logowania
     */
    void login(){
        try {
            System.out.println(getDb());
            PreparedStatement selectLog = getDb().prepareStatement("SELECT password FROM loginData WHERE login='"+String.valueOf(loginField.getText())+"'");
            ResultSet resultLog = selectLog.executeQuery();
            while (resultLog.next()) {
                if(resultLog.getString("password").equals(String.valueOf(passwordField.getPassword()))) {
                    resultLog.close();
                    selectLog.close();
                    logIn = true;
                    loginField.setText("");
                    passwordField.setText("");
                    break;
                }
            }
        }catch(Exception ser){
            ser.printStackTrace();
        }
        if (logIn) {
            CardLayout cardLayout = (CardLayout) contentPane.getLayout();
            cardLayout.next(contentPane);
            loginField.setText("");
            passwordField.setText("");
            operationStatus.setText("Domyślnie (login: admin, hasło: admin)");
            logIn = false;
        }
        else{
            operationStatus.setText("Niepoprawne dane logowania. Domyślnie (login: admin, hasło: admin)");
        }
    }
}

