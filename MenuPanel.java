import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


class MenuPanel extends JPanel {
    private JLabel opis;
    private JButton reservationButton;
    private JButton roomsButton;
    private JButton clientsButton;
    private JButton registerUser;
    private JButton logoutUser;
    private JButton statistics;
    private JPanel contentPane;
    private static Connection db = null;

    public static Connection getDb()
    {
        return db;
    }

    public MenuPanel(JPanel panel) {
        connectToDb();
        contentPane = panel;
        opis = new JLabel("Menu zarządzania hotelem", SwingConstants.CENTER);
        opis.setBounds(15, 10, 770, 25);
        add(opis);
        reservationButton = new JButton ("Rezerwacja");
        roomsButton = new JButton ("Pokoje");
        clientsButton = new JButton ("Goście");
        registerUser = new JButton ("Nowy użytkownik");
        logoutUser = new JButton ("Wyloguj");
        statistics = new JButton ("Statystyki");

        //adjust size and set layout
        setPreferredSize (new Dimension(800, 640));
        setLayout (null);

        //set component bounds (only needed by Absolute Positioning)
//        How.setBounds (245, 50, 60, 25);
//        jcomp2.setBounds (35, 30, 185, 50);
//        jcomp3.setBounds (250, 30, 60, 20);
        reservationButton.setLocation(100, 100);
        reservationButton.setSize(600, 120);
        roomsButton.setLocation(100, 260);
        roomsButton.setSize(600, 120);
        clientsButton.setLocation(100, 420);
        clientsButton.setSize(600, 120);
        registerUser.setBounds(15, 45, 370,35);
        logoutUser.setBounds(415, 45, 370,35);
        statistics.setBounds(100, 600, 600,30);

        reservationButton.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                CardLayout cardLayout = (CardLayout) contentPane.getLayout();
                cardLayout.next(contentPane);
            }
        });

        roomsButton.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                CardLayout cardLayout = (CardLayout) contentPane.getLayout();
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
            }
        });

        clientsButton.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                CardLayout cardLayout = (CardLayout) contentPane.getLayout();
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
            }
        });

        registerUser.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                CardLayout cardLayout = (CardLayout) contentPane.getLayout();
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
            }
        });


        statistics.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                StatsPanel.fillRoomsTable();
                StatsPanel.fillClientsTable();
                CardLayout cardLayout = (CardLayout) contentPane.getLayout();
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
            }
        });


        logoutUser.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                CardLayout cardLayout = (CardLayout) contentPane.getLayout();
                cardLayout.first(contentPane);
            }
        });

        add (reservationButton);
        add (roomsButton);
        add (clientsButton);
        add (registerUser);
        add (logoutUser);
        add (statistics);
    }
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

