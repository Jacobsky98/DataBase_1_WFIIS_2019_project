
CREATE FUNCTION room_id(INTEGER)
returns INTEGER AS $$ 
  SELECT room_id FROM room WHERE room_nr=$1;
$$
LANGUAGE SQL;




CREATE VIEW showClientsWithReservationsCount AS 
SELECT cl.client_id, cl.fname, cl.lname, cl.city, cl.street, cl.street_nr, cl.postcode, cl.phone, cl.email, (SELECT count(*) FROM reservation r where r.client_id=cl.client_id) as rescount FROM client cl ORDER BY rescount DESC;

CREATE VIEW showRoomsWithDesc AS
SELECT rc.category, rs.status, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM (room r INNER JOIN room_category rc ON r.category_id=rc.category_id ) INNER JOIN room_status rs ON r.status_id=rs.status_id;






CREATE OR REPLACE FUNCTION showReservationRoom(INTEGER)
RETURNS TABLE(
  reservation_id INTEGER, 
  fname VARCHAR, 
  lname VARCHAR, 
  phone VARCHAR, 
  adults INTEGER, 
  children INTEGER, 
  check_in DATE, 
  check_out DATE, 
  date_placed DATE, 
  already_paid NUMERIC(7,2),
  to_pay NUMERIC(7,2)
) AS $$
  SELECT r.reservation_id, cl.fname, cl.lname, cl.phone, r.adults, r.children, r.check_in, r.check_out, r.date_placed, p.already_paid, p.total-p.already_paid as to_pay FROM (reservation r INNER JOIN client cl ON cl.client_id=r.client_id ) INNER JOIN payment p ON p.payment_id=r.payment_id WHERE room_id=$1;
$$
LANGUAGE SQL;


CREATE OR REPLACE FUNCTION showReservationClient(INTEGER)
RETURNS TABLE(
  reservation_id INTEGER, 
  fname VARCHAR, 
  lname VARCHAR, 
  phone VARCHAR, 
  adults INTEGER, 
  children INTEGER, 
  check_in DATE, 
  check_out DATE, 
  date_placed DATE, 
  already_paid NUMERIC(7,2), 
  to_pay NUMERIC(7,2), 
  room_nr INTEGER, 
  payment_id INTEGER
) AS $$
  SELECT r.reservation_id, cl.fname, cl.lname, cl.phone, r.adults, r.children, r.check_in, r.check_out, r.date_placed, p.already_paid, p.total-p.already_paid as to_pay, room.room_nr, r.payment_id  FROM ((reservation r INNER JOIN client cl ON cl.client_id=r.client_id ) INNER JOIN payment p ON p.payment_id=r.payment_id), room WHERE (room.room_id=r.room_id AND r.client_id=$1);
$$
LANGUAGE SQL;




CREATE OR REPLACE FUNCTION findRoomForClient (howManyPeople INTEGER, categoryId INTEGER, forKids BOOLEAN, date_in DATE, date_out DATE)
RETURNS TABLE (
  room_id INTEGER,
  category_id INTEGER,
  capacity INTEGER,
  extra_beds INTEGER,
  for_kids BOOLEAN,
  room_nr INTEGER
)
AS $$

    IF $3=true THEN 
      SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>=$1 AND category_id=$2 AND (for_kids=true OR ) EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (($4 < check_out AND $4 >= check_in) OR ($5 >check_in AND $5 <=check_out ) OR ($4<=check_in AND $5 >= check_out)));
    
    ELSE
      SELECT r.room_id, r.category_id, r.capacity, r.extra_beds, r.for_kids, r.room_nr FROM room r WHERE ((r.capacity+r.extra_beds)>=$1 AND category_id=$2) EXCEPT SELECT rr.room_id, rr.category_id, rr.capacity, rr.extra_beds, rr.for_kids, rr.room_nr FROM room rr WHERE room_id IN (SELECT room_id FROM reservation WHERE (($4 < check_out AND $4 >= check_in) OR ($5 >check_in AND $5 <=check_out ) OR ($4<=check_in AND $5 >= check_out)));
    END IF;
$$
LANGUAGE SQL;


CREATE OR REPLACE FUNCTION validateNewUser()
    RETURNS TRIGGER
    LANGUAGE plpgsql
    AS $$
    DECLARE
      sameLogin VARCHAR;
    BEGIN
    SELECT login INTO sameLogin FROM loginData WHERE login=NEW.login;
    IF LENGTH(NEW.login) = 0 THEN
        RAISE EXCEPTION 'Brak loginu.';
    END IF;

    
    IF NEW.login = sameLogin THEN
        RAISE EXCEPTION 'Taki uzytkownik juz istnieje';
    END IF;
    
    IF LENGTH(NEW.password) = 0 THEN
        RAISE EXCEPTION 'Brak hasla.';
    END IF;

    RETURN NEW;                                                          
    END;
    $$;


CREATE TRIGGER validateNewUserTrigger 
    BEFORE INSERT OR UPDATE  ON loginData
    FOR EACH ROW EXECUTE PROCEDURE validateNewUser(); 

CREATE OR REPLACE VIEW najpopularniejszePokoje AS
  SELECT r.room_nr, rc.category, r.capacity, r.extra_beds, COUNT(*) as rezerwacjeCount, ROUND(AVG(adults+children),0) AS sredniaOsob  FROM ((room r INNER JOIN reservation res ON r.room_id=res.room_id) INNER JOIN room_category rc ON r.category_id=rc.category_id )GROUP BY res.room_id, r.room_nr, rc.category, r.capacity, r.extra_beds ORDER BY rezerwacjeCount DESC; 

CREATE OR REPLACE VIEW najlepszyKlient AS
  SELECT cl.fname, cl.lname, cl.city, cl.postcode, COUNT(*) as ilosc FROM client cl INNER JOIN reservation res ON cl.client_id=res.client_id GROUP BY res.client_id, cl.fname, cl.lname, cl.city, cl.postcode ORDER BY ilosc DESC;




CREATE OR REPLACE FUNCTION updateRoomsStatus()
RETURNS text AS $$
DECLARE
  todayDate DATE;
  i INTEGER := 0;
  n INTEGER;
begin
  SELECT CURRENT_DATE into todayDate;
  SELECT COUNT(*) into n FROM reservation;

  LOOP 
    EXIT WHEN i>=n;
    i := i+1;
    UPDATE
  END LOOP;
end;
$$
LANGUAGE 'plpgsql';


