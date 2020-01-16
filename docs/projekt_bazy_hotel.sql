CREATE TABLE room_category (
  category_id SERIAL PRIMARY KEY,
  category VARCHAR(32) NOT NULL,
  price_for_person NUMERIC(7,2) NOT NULL
);

/* Uzupelnianie tabeli room_category */
INSERT INTO room_category(category, price_for_person) VALUES ('Ekonomiczny', 40.00);
INSERT INTO room_category(category, price_for_person) VALUES ('Standard', 60.00);
INSERT INTO room_category(category, price_for_person) VALUES ('Superior', 100.00);
INSERT INTO room_category(category, price_for_person) VALUES ('Deluxe', 180.00);



CREATE TABLE room_status (
  status_id SERIAL PRIMARY KEY,
  status VARCHAR(32) NOT NULL
);

/* Uzupelnianie tabeli room_status */
INSERT INTO room_status(status) VALUES ('Wolny');
INSERT INTO room_status(status) VALUES ('Zajęty');
INSERT INTO room_status(status) VALUES ('Do sprzątania');

CREATE TABLE payment (
  payment_id SERIAL PRIMARY KEY,
  already_paid NUMERIC(7,2),
  total NUMERIC(7,2)
);

INSERT INTO payment(payment_id, already_paid, total) VALUES (1, 240.00, 240.00);
INSERT INTO payment(payment_id, already_paid, total) VALUES (2, 7425.00, 7425.00);
INSERT INTO payment(payment_id, already_paid, total) VALUES (3, 1400.00, 1400.00);
INSERT INTO payment(payment_id, already_paid, total) VALUES (4, 1540.00, 1540.00);
INSERT INTO payment(payment_id, already_paid, total) VALUES (5, 1080.00, 1080.00);
INSERT INTO payment(payment_id, already_paid, total) VALUES (6, 360.00, 360.00);
INSERT INTO payment(payment_id, already_paid, total) VALUES (7, 2550.00, 2550.00);
INSERT INTO payment(payment_id, already_paid, total) VALUES (8, 1000.00, 3600.00);



CREATE TABLE room (
  room_id SERIAL PRIMARY KEY,
  category_id INTEGER NOT NULL REFERENCES room_category(category_id),
  status_id INTEGER NOT NULL REFERENCES room_status(status_id),
  capacity INTEGER,
  extra_beds INTEGER,
  for_kids BOOLEAN,
  room_nr INTEGER
);

/* Uzupelnianie tabeli room */
INSERT INTO room(category_id, status_id, capacity, extra_beds, for_kids, room_nr) VALUES (2, 1, 2, 0, false, 1); /* standard, wolny */
INSERT INTO room(category_id, status_id, capacity, extra_beds, for_kids, room_nr) VALUES (2, 1, 2, 0, false, 2); /* standard, wolny */
INSERT INTO room(category_id, status_id, capacity, extra_beds, for_kids, room_nr) VALUES (1, 3, 3, 1, true, 3);  /* ekonomiczny, do sprzatania */
INSERT INTO room(category_id, status_id, capacity, extra_beds, for_kids, room_nr) VALUES (3, 3, 3, 2, true, 4);  /* superior, zajety */
INSERT INTO room(category_id, status_id, capacity, extra_beds, for_kids, room_nr) VALUES (4, 2, 3, 0, true, 5);  /* deluxe, zajety */


CREATE TABLE client (
  client_id SERIAL PRIMARY KEY,
  fname VARCHAR(32) NOT NULL,
  lname VARCHAR(32) NOT NULL,
  city VARCHAR(32),
  street VARCHAR(32),
  street_nr INTEGER,
  postcode CHAR(10),
  email VARCHAR(32),
  phone VARCHAR(32) NOT NULL
);



/* Uzupelnianie tabeli client */
INSERT INTO client(fname, lname, city, street, street_nr, postcode, email, phone) VALUES ('Daria', 'Kowalska', 'Łódź', 'Aleksandrowska', 44, '91-151', 'DrugiSokolowski@rhyta.com', '48663366007'); /* id 1 */
INSERT INTO client(fname, lname, city, street, street_nr, postcode, email, phone) VALUES ('Weronika', 'Maciejewska', 'Mysłowice', 'Dębowska', 21, '41-404', 'WeronikaMaciejewska@rhyta.com', '48782346382');
INSERT INTO client(fname, lname, city, street, street_nr, postcode, email, phone) VALUES ('Paweł', 'Wiśniewski', 'Warszawa', 'Białobrzeska', 110, '02-365', 'PawelWisniewski@teleworm.us', '48603885015');
INSERT INTO client(fname, lname, city, street, street_nr, postcode, email, phone) VALUES ('Zofia', 'Woźniak', 'Białystok', 'Ułańska', 28, '15-340', 'ZosiaWozniak@armyspy.com', '48795537570');
INSERT INTO client(fname, lname, city, street, street_nr, postcode, email, phone) VALUES ('Marcin', 'Wieczorek', 'Poznań', 'Rzeczna', 36, '61-013', 'MarcinWieczorek@dayrep.com', '48511550523');
INSERT INTO client(fname, lname, city, street, street_nr, postcode, email, phone) VALUES ('Grzegorz', 'Grabowski', 'Gdańsk', 'Hoża', 69, '80-628', 'GrzegorzGrabowski@jourrapide.com', '48698513442');
INSERT INTO client(fname, lname, city, street, street_nr, postcode, email, phone) VALUES ('Bartosz', 'Dudek', 'Wrocław', 'Tulipanowa', 77, '52-221', 'BartoszDudek@armyspy.com', '48794751727');  /* id 7 */




CREATE TABLE reservation (
  reservation_id SERIAL PRIMARY KEY,
  room_id INTEGER NOT NULL REFERENCES room(room_id), 
  client_id  INTEGER NOT NULL REFERENCES client(client_id), 
  payment_id  INTEGER NOT NULL REFERENCES payment(payment_id), 
  adults INTEGER,
  children INTEGER,
  check_in DATE,
  check_out DATE,
  date_placed DATE
);

/* Uzupelnianie tabeli reservation */
INSERT INTO reservation(room_id, client_id, payment_id, adults, children, check_in, check_out, date_placed) VALUES (1, 1, 1, 2, 0, '6-11-2019', '8-11-2019', '24-10-2019');
INSERT INTO reservation(room_id, client_id, payment_id, adults, children, check_in, check_out, date_placed) VALUES (5, 2, 2, 2, 1, '7-11-2019', '12-11-2019', '27-10-2019');
INSERT INTO reservation(room_id, client_id, payment_id, adults, children, check_in, check_out, date_placed) VALUES (3, 3, 3, 2, 2, '20-11-2019', '30-11-2019', '4-11-2019');
INSERT INTO reservation(room_id, client_id, payment_id, adults, children, check_in, check_out, date_placed) VALUES (1, 4, 4, 2, 0, '4-12-2019', '15-12-2019', '6-11-2019');
INSERT INTO reservation(room_id, client_id, payment_id, adults, children, check_in, check_out, date_placed) VALUES (2, 5, 5, 2, 0, '5-12-2019', '14-12-2019', '7-11-2019');
INSERT INTO reservation(room_id, client_id, payment_id, adults, children, check_in, check_out, date_placed) VALUES (1, 2, 6, 2, 0, '30-12-2019', '2-01-2020', '15-12-2019'); 
INSERT INTO reservation(room_id, client_id, payment_id, adults, children, check_in, check_out, date_placed) VALUES (4, 6, 7, 2, 3, '11-01-2020', '17-01-2020', '9-01-2020'); 
INSERT INTO reservation(room_id, client_id, payment_id, adults, children, check_in, check_out, date_placed) VALUES (5, 7, 8, 2, 0, '10-01-2020', '18-01-2020', '10-01-2020'); 



CREATE TABLE loginData (
  login_id SERIAL PRIMARY KEY,
  login VARCHAR(32) NOT NULL,
  password VARCHAR(32) NOT NULL
);

/* Uzupelnianie tabeli room_category */
INSERT INTO loginData(login, password) VALUES ('admin', 'admin');
