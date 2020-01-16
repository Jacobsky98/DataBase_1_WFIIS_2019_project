import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.Result;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.ArrayList;

class ReservationPanel extends JPanel {

    private JLabel description;
    private JLabel sortByText;
    private String [] sortByOptions = {"Kategoria", "Liczba miejsc", "Ilość dodatkowych łóżek", "Numer pokoju"};
    private JComboBox sortBy;
    private JButton menuButton;
    private JPanel contentPane;
    private JTable roomsTable;
    private DefaultTableModel model;
    private JScrollPane scrollPane;
    private JButton newClientButton;
    private Date todayDate = new Date(System.currentTimeMillis());

    // add or change data room
    private JLabel addOrChangeRoomDataDesc;
    private JLabel selectCategoryDesc;
    private JComboBox selectCategory = null;
    private JLabel checkInDesc;
    private JTextField checkInField = null;
    private JLabel checkOutDesc;
    private JTextField checkOutField = null;
    private JLabel adultsDesc;
    private JTextField adultsField = null;
    private JLabel childrenDesc;
    private JTextField childrenField = null;
    private JButton findRooms;

    String [] clientsTable = new String[] {};
    String [] roomTable = new String[] {};
    boolean reservationPlaced = false;
    int [] client_ids = null;
    int [] room_ids = null;
    int categoryId = -1;
    int paymentId = -1;
    String categoryId_name = "";
    int howManyPeople = -1;
    double price = 0.0;
    String date_in = "";
    String date_out = "";
    boolean foundRooms = false;
    boolean haveKids = false;
    private JLabel selectClientDesc;
    private JComboBox selectClient;
    private JLabel selectRoomNrDesc;
    private JComboBox selectRoomNr;
    private JButton placeReservation;

    private JLabel howMuchToPayDesc;
    private JTextField howMuchToPay;
    private JButton payForReservation;

    private JLabel operationStatus = new JLabel();



    public ReservationPanel(JPanel menuPanel) {

        menuButton = new JButton("Powrót do menu");
        contentPane = menuPanel;
        description = new JLabel("Panel rezerwacji", SwingConstants.CENTER);
        description.setBounds(335, 10, 200, 25);
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
                selectClientDesc.setVisible(false);
                selectClient.setVisible(false);
                selectRoomNrDesc.setVisible(false);
                selectRoomNr.setVisible(false);
                placeReservation.setVisible(false);
                newClientButton.setVisible(false);
                howMuchToPayDesc.setVisible(false);
                howMuchToPay.setVisible(false);
                payForReservation.setVisible(false);
            }
        });
        add(menuButton);
        operationStatus.setBounds(15,550,770,20);
        add(operationStatus);

        model = new DefaultTableModel();
        roomsTable = new JTable(model);
        roomsTable.setFont(new Font("Times New Roman", Font.PLAIN, 13));

        scrollPane = new JScrollPane(roomsTable);
        scrollPane.setBounds(15,50,770,180);

        model.addColumn("Kategoria");
        model.addColumn("Liczba miejsc");
        model.addColumn("Dodatkowe łóżka");
        model.addColumn("Dla dzieci");
        model.addColumn("Numer pokoju");

        fillRoomsTable();
        add(scrollPane, BorderLayout.CENTER);

        addOrChangeRoomDataDesc = new JLabel("Dodaj rezerwacje: (format dat yyyy-mm-dd)");
        addOrChangeRoomDataDesc.setBounds(15, 200, 270, 20);
        add(addOrChangeRoomDataDesc);
        selectCategoryDesc = new JLabel("Kategoria");
        selectCategoryDesc.setBounds(25, 230, 150, 20);
        add(selectCategoryDesc);
        ArrayList<String> roomCategories = new ArrayList<String>();
        try{
            PreparedStatement select = MenuPanel.getDb().prepareStatement("SELECT category FROM room_category");
            ResultSet result = select.executeQuery();
            while(result.next())
                roomCategories.add(result.getString("category"));
            result.close();
            select.close();
        } catch(Exception ser){
            System.out.println("Panel pokoi - blad wczytywania tabeli");
            ser.printStackTrace();
        }
        String [] roomCat = new String[roomCategories.size()];
        roomCategories.toArray(roomCat);
        selectCategory = new JComboBox(roomCat);
        selectCategory.setBounds(25, 260, 150, 30);
        selectCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                categoryId = selectCategory.getSelectedIndex()+1;
            }
        });
        add(selectCategory);

        checkInDesc = new JLabel("Od");
        checkInDesc.setBounds(180, 230, 100, 20);
        add(checkInDesc);
        checkInField = new JTextField(String.valueOf(todayDate));
        checkInField.setBounds(180, 260, 100, 30);
        add(checkInField);

        checkOutDesc = new JLabel("Do");
        checkOutDesc.setBounds(285, 230, 100, 20);
        add(checkOutDesc);
        checkOutField = new JTextField();
        checkOutField.setBounds(285, 260, 100, 30);
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        cal.add(Calendar.DAY_OF_YEAR,7);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        java.sql.Date sql7days = new java.sql.Date(cal.getTimeInMillis());
        checkOutField.setText(String.valueOf(sql7days));
        add(checkOutField);

        adultsDesc = new JLabel("Dorośli");
        adultsDesc.setBounds(390, 230, 75, 20);
        add(adultsDesc);
        adultsField = new JTextField();
        adultsField.setBounds(390, 260, 75, 30);
        adultsField.setText("2");
        add(adultsField);

        childrenDesc = new JLabel("Dzieci");
        childrenDesc.setBounds(470, 230, 75, 20);
        add(childrenDesc);
        childrenField = new JTextField();
        childrenField.setBounds(470, 260, 75, 30);
        childrenField.setText("0");
        add(childrenField);

        findRooms = new JButton("Szukaj");
        findRooms.setBounds(550, 230, 235, 60);
        findRooms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                categoryId = selectCategory.getSelectedIndex()+1;
                categoryId_name = String.valueOf(selectCategory.getSelectedItem());
                date_in = checkInField.getText();
                date_out = checkOutField.getText();
                reservationPlaced = false;
                howManyPeople = Integer.parseInt(String.valueOf(adultsField.getText())) + Integer.parseInt(String.valueOf(childrenField.getText()));
                haveKids = false;
                foundRooms = true;
                if(Integer.parseInt(String.valueOf(childrenField.getText())) > 0)
                    haveKids = true;
                fillRoomsTable();
                addClientsToComboBox();
                if(room_ids.length>0) {
                    selectClientDesc.setVisible(true);
                    selectClient.setVisible(true);
                    selectRoomNrDesc.setVisible(true);
                    selectRoomNr.setVisible(true);
                    placeReservation.setVisible(true);
                    newClientButton.setVisible(true);
                }
                else
                {
                    operationStatus.setText("Brak miejsc w podanym terminie.");
                }
            }
        });
        add(findRooms);

        // choose client
        selectClientDesc = new JLabel("Wybierz klienta");
        selectClientDesc.setBounds(25, 310, 750, 20);
        add(selectClientDesc);
        selectClient = new JComboBox(clientsTable);
        selectClient.setBounds(25, 340, 750, 30);
        selectClient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // dodac obsluge pokoi
            }
        });
        add(selectClient);

        selectRoomNrDesc = new JLabel("Wybierz nr pokoju");
        selectRoomNrDesc.setBounds(25, 380, 150, 20);
        add(selectRoomNrDesc);
        selectRoomNr = new JComboBox(roomTable);
        selectRoomNr.setBounds(25, 410, 150, 30);
        selectRoomNr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
        add(selectRoomNr);

        newClientButton = new JButton("Nowy klient");
        newClientButton.setBounds(185, 380, 200, 60);
        newClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CardLayout cardLayout = (CardLayout) contentPane.getLayout();
                cardLayout.first(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                cardLayout.next(contentPane);
                selectClientDesc.setVisible(false);
                selectClient.setVisible(false);
                selectRoomNrDesc.setVisible(false);
                selectRoomNr.setVisible(false);
                placeReservation.setVisible(false);
                newClientButton.setVisible(false);
                howMuchToPayDesc.setVisible(false);
                howMuchToPay.setVisible(false);
                payForReservation.setVisible(false);
            }
        });
        add(newClientButton);



        placeReservation = new JButton("Rezerwuj");
        placeReservation.setBounds(395, 380, 250, 60);
        placeReservation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                placeReservation.setVisible(false);
                    if(reservationPlaced == false)
                    {
                        price = 0.0;
                        if((Integer.parseInt(String.valueOf(adultsField.getText())) > 0) && (Integer.parseInt(String.valueOf(childrenField.getText())) >= 0)) {
                            try {
                                PreparedStatement selectPr = MenuPanel.getDb().prepareStatement("SELECT price_for_person FROM room_category WHERE category_id=" + categoryId);
                                ResultSet resultPr = selectPr.executeQuery();
                                while (resultPr.next())
                                    price = resultPr.getDouble("price_for_person");
                                resultPr.close();
                                selectPr.close();
                                price = price * Integer.parseInt(String.valueOf(adultsField.getText())) + 0.75 * price * Integer.parseInt(String.valueOf(childrenField.getText()));
                            } catch (Exception ser) {
                                ser.printStackTrace();
                            }
                            try {

                                System.out.println("room_id " + room_ids[selectRoomNr.getSelectedIndex()]);
                                System.out.println("client_id " + client_ids[selectClient.getSelectedIndex()]);

                                PreparedStatement selectMod = MenuPanel.getDb().prepareStatement("SELECT payment_id FROM payment ORDER BY payment_id DESC LIMIT 1", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                ResultSet resultMod = selectMod.executeQuery();
                                while (resultMod.next()) {
                                    paymentId = resultMod.getInt("payment_id") + 1;
                                }

                                PreparedStatement createPayment = MenuPanel.getDb().prepareStatement("INSERT INTO payment (payment_id, already_paid, total) VALUES (?, ?, ?)");

                                createPayment.setInt(1, paymentId);
                                createPayment.setObject(2, 0.0, java.sql.Types.NUMERIC);
                                ;
                                createPayment.setObject(3, price, java.sql.Types.NUMERIC);
                                createPayment.executeUpdate();
                                createPayment.close();

                                System.out.println("Adults " + adultsField.getText());
                                System.out.println("Children " + childrenField.getText());
                                System.out.println("check_in " + checkInField.getText());
                                System.out.println("check_out " + checkOutField.getText());
                                System.out.println("date_placed " + todayDate);
                            } catch (Exception ser) {
                                ser.printStackTrace();
                            }
                    }

                    try{
                        PreparedStatement createReservation = MenuPanel.getDb().prepareStatement("INSERT INTO reservation(room_id, client_id, payment_id, adults, children, check_in, check_out, date_placed) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

                        createReservation.setInt(1, room_ids[selectRoomNr.getSelectedIndex()]);
                        createReservation.setInt(2, client_ids[selectClient.getSelectedIndex()]);
                        createReservation.setInt(3, paymentId);
                        createReservation.setInt(4, Integer.parseInt(String.valueOf(adultsField.getText())));
                        createReservation.setInt(5, Integer.parseInt(String.valueOf(childrenField.getText())));
                        createReservation.setDate(6, java.sql.Date.valueOf(checkInField.getText()));
                        createReservation.setDate(7, java.sql.Date.valueOf(checkOutField.getText()));
                        createReservation.setDate(8, todayDate);
                        createReservation.executeUpdate();
                        createReservation.close();

                        DecimalFormat df2 = new DecimalFormat("#.##");
                        operationStatus.setText("Przyjęto rezerwacje. Do zapłaty " + df2.format(price) + "zł");
                        howMuchToPayDesc.setVisible(true);
                        howMuchToPay.setVisible(true);
                        payForReservation.setVisible(true);
                        MenuPanel.updateRoomsStatus();
                    } catch (Exception ser) {
                        ser.printStackTrace();
                    }
                }
                else{
                    operationStatus.setText("Błędne dane. Rezerwacja nie została przyjęta.");
                }
            }
        });
        add(placeReservation);

        howMuchToPayDesc = new JLabel("Ile chcesz zapłacić teraz?");
        howMuchToPayDesc.setBounds(25, 450, 200, 20);
        add(howMuchToPayDesc);
        howMuchToPay = new JTextField();
        howMuchToPay.setBounds(25, 480, 200, 30);
        add(howMuchToPay);

        payForReservation = new JButton("Zapłać");
        payForReservation.setBounds(240, 450, 120, 60);
        payForReservation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if(Double.parseDouble(String.valueOf(howMuchToPay.getText())) > 0) {


                    payForReservation.setVisible(false);
                    try {
                        double iWantPay = Double.parseDouble(String.valueOf(howMuchToPay.getText()));
                        PreparedStatement selectPayment = MenuPanel.getDb().prepareStatement("SELECT * FROM payment WHERE payment_id=" + paymentId, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet resultPayment = selectPayment.executeQuery();

                        while (resultPayment.next()) {
                            resultPayment.updateInt("payment_id", resultPayment.getInt("payment_id"));
                            resultPayment.updateObject("already_paid", iWantPay, java.sql.Types.NUMERIC);
                            resultPayment.updateObject("total", resultPayment.getDouble("total"), java.sql.Types.NUMERIC);
                            resultPayment.updateRow();
                        }
                        resultPayment.close();
                        selectPayment.close();
                        DecimalFormat df2 = new DecimalFormat("#.##");
                        if (price - iWantPay > 0)
                            operationStatus.setText("Zapłacono " + df2.format(iWantPay) + ". :)");
                        else {
                            operationStatus.setText("Zapłacono " + df2.format(iWantPay) + ". Pozostało kwota do zapłaty: " + String.valueOf(df2.format(price - iWantPay)));
                        }

                    } catch (Exception ser) {
                        System.out.println("Panel pokoi - blad zapisywania danych do tabeli room");
                        ser.printStackTrace();
                    }
                }
                else{
                    operationStatus.setText("Nieprawidłowa kwota płatności. Spróbuj jeszcze raz.");
                }
            }
        });
        add(payForReservation);



        selectClientDesc.setVisible(false);
        selectClient.setVisible(false);
        selectRoomNrDesc.setVisible(false);
        selectRoomNr.setVisible(false);
        placeReservation.setVisible(false);
        newClientButton.setVisible(false);
        howMuchToPayDesc.setVisible(false);
        howMuchToPay.setVisible(false);
        payForReservation.setVisible(false);



    }

    void addClientsToComboBox()
    {
        ResultSet result = null;
        PreparedStatement select = null;
        ArrayList<String> categoryList = new ArrayList<String>();
        ArrayList<Integer> clientIDs = new ArrayList<Integer>();
        try{
            select = MenuPanel.getDb().prepareStatement("SELECT * FROM client");
            result = select.executeQuery();

            while (result.next()) {
                categoryList.add(result.getString("fname") +", "+ result.getString("lname") +", "+ result.getString("city") +", "+ result.getString("street") +", "+ result.getString("street_nr") +", "+ result.getString("postcode").substring(0, 6) +", "+ result.getString("phone") +", "+ result.getString("email"));
                clientIDs.add(result.getInt("client_id"));
            }
            result.close();
            select.close();

            clientsTable = new String[categoryList.size()];
            client_ids = new int[clientIDs.size()];
            categoryList.toArray(clientsTable);
            selectClient.removeAllItems();
            //System.out.println("Tablica string");
            for(int i = 0; i < clientsTable.length; i++) {
                selectClient.addItem(clientsTable[i]);
            }
            for(int i = 0; i < client_ids.length; i++)
            {
                client_ids[i] = clientIDs.get(i);
                //System.out.println(client_ids[i]);
            }

            //System.out.println("na koniec length " +  clientsTable.length);
        } catch (Exception ser){
            System.out.println("Panel rezerwacji - blad wczytywania wierszy tabeli client");
            ser.printStackTrace();
        }
    }

    void addRoomsToComboBox()
    {

    }

    void fillRoomsTable()
    {
        System.out.println("categoryId = " +categoryId+"\n"+date_in+"\n"+date_out+"\n"+howManyPeople+"\n"+haveKids);

        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        ResultSet result = null;
        PreparedStatement select = null;

        int chosen = sortBy.getSelectedIndex();
        try {
            if(foundRooms) {
                switch (chosen)
                {
                    case 0:
                        if(haveKids){
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+" AND category_id="+categoryId+" AND for_kids='true') EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY category_id DESC");
                        }
                        else{
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+" AND category_id="+categoryId+") EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY category_id DESC");
                        }
                        result = select.executeQuery();
                        break;
                    case 1:
                        if(haveKids){
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+" AND category_id="+categoryId+" AND for_kids='true') EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY capacity DESC");
                        }
                        else{
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+" AND category_id="+categoryId+") EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY category_id DESC");
                        }
                        result = select.executeQuery();
                        break;
                    case 2:
                        if(haveKids){
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+" AND category_id="+categoryId+" AND for_kids='true') EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY extra_beds DESC");
                        }
                        else{
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+" AND category_id="+categoryId+") EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY extra_beds DESC");
                        }
                        result = select.executeQuery();
                        break;
                    case 3:
                        if(haveKids){
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+" AND category_id="+categoryId+" AND for_kids='true') EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY for_kids DESC");
                        }
                        else{
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+" AND category_id="+categoryId+") EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY for_kids DESC");
                        }
                        result = select.executeQuery();
                        break;
                    case 4:
                        if(haveKids){
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+" AND category_id="+categoryId+" AND for_kids='true') EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY room_nr DESC");
                        }
                        else{
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+" AND category_id="+categoryId+") EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY room_nr DESC");
                        }
                        result = select.executeQuery();
                        break;
                    default: break;
                }
            }
            else {
                switch (chosen)
                {
                    case 0:
                        if(haveKids){
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+" AND for_kids='true') EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY category_id DESC");
                        }
                        else{
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+") EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY category_id DESC");
                        }
                        result = select.executeQuery();
                        break;
                    case 1:
                        if(haveKids){
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+" AND for_kids='true') EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY capacity DESC");
                        }
                        else{
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+") EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY category_id DESC");
                        }
                        result = select.executeQuery();
                        break;
                    case 2:
                        if(haveKids){
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+" AND for_kids='true') EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY extra_beds DESC");
                        }
                        else{
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+") EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY extra_beds DESC");
                        }
                        result = select.executeQuery();
                        break;
                    case 3:
                        if(haveKids){
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+" AND for_kids='true') EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY for_kids DESC");
                        }
                        else{
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+") EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY for_kids DESC");
                        }
                        result = select.executeQuery();
                        break;
                    case 4:
                        if(haveKids){
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+" AND for_kids='true') EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY room_nr DESC");
                        }
                        else{
                            select = MenuPanel.getDb().prepareStatement("SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>="+howManyPeople+") EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (('"+date_in+"' < check_out AND '"+date_in+"' >= check_in) OR ('"+date_out+"'>check_in AND '"+date_out+"'<=check_out ) OR ('"+date_in+"'<=check_in AND '"+date_out+"' >= check_out))) ORDER BY room_nr DESC");
                        }
                        result = select.executeQuery();
                        break;
                    default: break;
                }
            }

            ArrayList<String> roomNrs = new ArrayList<String>();
            ArrayList<Integer> roomIds = new ArrayList<Integer>();
            while (result.next()) {
                Vector<String> tableText = new Vector<String>();
                tableText.add(String.valueOf(selectCategory.getItemAt(result.getInt("category_id")-1)));
                //tableText.add(result.getString("category_id"));
                tableText.add(result.getString("capacity"));
                tableText.add(result.getString("extra_beds"));
                if(result.getString("for_kids").equals("t"))
                    tableText.add("Tak");
                else tableText.add("Nie");
                tableText.add(result.getString("room_nr"));
                model.addRow(tableText);

                roomNrs.add(result.getString("room_nr"));
                roomIds.add(Integer.parseInt(String.valueOf(result.getString("room_id"))));
            }
            result.close();
            select.close();
//            if(foundRooms && (roomIds.size() < 1))
//            {
//                operationStatus.setText("Nie znaleniono pokoi dla danej kategorii");
//                foundRooms = false;
//                fillRoomsTable();
//                return;
//            }

            selectRoomNr.removeAllItems();
            roomTable = new String[roomNrs.size()];
            roomNrs.toArray(roomTable);
            for(int i = 0; i < roomTable.length; i++)
            {
                selectRoomNr.addItem(roomTable[i]);
            }

            room_ids = new int[roomIds.size()];
            for(int i = 0; i < room_ids.length; i++)
            {
                room_ids[i] = roomIds.get(i);
            }
        } catch (Exception ser){
            System.out.println("Panel rezerwacji - blad wczytywania wierszy tabeli");
            ser.printStackTrace();
        }

    }


}