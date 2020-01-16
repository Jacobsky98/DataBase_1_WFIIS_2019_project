import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class myGUI {
    private JPanel contentPane;
    private MenuPanel menuPanel;
    private LoginPanel loginPanel;
    private ReservationPanel reservationPanel;
    public static RoomsPanel roomsPanel;
    private ClientsPanel clientsPanel;
    private RegisterPanel registerPanel;
    private StatsPanel statsPanel;
    private CategoryStatusPanel categoryStatusPanel;
    private static ShowReservation showReseravtion = null;
    private static ShowReservationClient showReseravtionClient = null;

    private void displayMenu()
    {
        JFrame menu = new JFrame("Hotel - Jakub Salamon");
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new CardLayout());

        menuPanel = new MenuPanel(contentPane);
        loginPanel = new LoginPanel(contentPane);
        reservationPanel = new ReservationPanel(contentPane);
        roomsPanel = new RoomsPanel(contentPane);
        clientsPanel = new ClientsPanel(contentPane);
        showReseravtion = new ShowReservation(contentPane);
        showReseravtionClient = new ShowReservationClient(contentPane);
        registerPanel = new RegisterPanel(contentPane);
        statsPanel = new StatsPanel(contentPane);
        categoryStatusPanel = new CategoryStatusPanel(contentPane);
        contentPane.add(loginPanel, "Hotel - panel logowania");
        contentPane.add(menuPanel, "Hotel - menu rezerwacji");
        contentPane.add(reservationPanel, "Hotel - rezerwacja");
        contentPane.add(roomsPanel, "Hotel - pokoje");
        contentPane.add(clientsPanel, "Hotel - goscie");
        contentPane.add(showReseravtion, "Hotel - rezerwacje pokoju");
        contentPane.add(showReseravtionClient, "Hotel - rezerwacje pokoju danego klienta");
        contentPane.add(registerPanel, "Hotel - rejestracja nowego użytkownika sytemu");
        contentPane.add(statsPanel, "Hotel - statystyki");
        contentPane.add(categoryStatusPanel, "Hotel - kategoria, status dodawanie");
        menu.setContentPane(contentPane);
        menu.pack();
        menu.setLocationByPlatform(true);
        menu.setVisible(true);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new myGUI().displayMenu();
            }
        });
    }
}
