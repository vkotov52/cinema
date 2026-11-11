CREATE TABLE hall (
    hall_code VARCHAR(10) PRIMARY KEY CHECK (hall_code <> ''),
    name VARCHAR(50) NOT NULL UNIQUE CHECK (name <> ''),
    rows_count INT NOT NULL CHECK (rows_count BETWEEN 1 AND 50),
    seats_per_row INT NOT NULL CHECK (seats_per_row BETWEEN 1 AND 60)
);

CREATE TABLE genre (
    genre_code VARCHAR(20) PRIMARY KEY CHECK (genre_code <> ''),
    name VARCHAR(50) NOT NULL UNIQUE CHECK (name <> '')
);

CREATE TABLE film (
    film_code VARCHAR(20) PRIMARY KEY CHECK (film_code <> ''),
    title VARCHAR(200) NOT NULL CHECK (title <> ''),
    duration_min INT NOT NULL CHECK (duration_min BETWEEN 1 AND 600),
    age_rating VARCHAR(3) NOT NULL CHECK (age_rating IN ('0+', '6+', '12+', '16+', '18+'))
);

CREATE TABLE film_genre (
    film_code VARCHAR(20) NOT NULL REFERENCES film(film_code) ON DELETE CASCADE,
    genre_code VARCHAR(20) NOT NULL REFERENCES genre(genre_code) ON DELETE RESTRICT,
    PRIMARY KEY (film_code, genre_code)
);

CREATE TABLE film_session (
    session_code VARCHAR(30) PRIMARY KEY
        CHECK (session_code ~ '^[A-Z0-9]+-[0-9]{8}-[0-9]{4}$'),
    hall_code VARCHAR(10) NOT NULL REFERENCES hall(hall_code),
    film_code VARCHAR(20) NOT NULL REFERENCES film(film_code),
    start_time TIMESTAMP NOT NULL,
    base_price NUMERIC(8,2) NOT NULL CHECK (base_price >= 0),
    UNIQUE (hall_code, start_time)
);

CREATE TABLE user_account (
    login VARCHAR(30) PRIMARY KEY
        CHECK (login <> '' AND login NOT LIKE '% %'),
    password_hash VARCHAR(100) NOT NULL,
    last_name VARCHAR(50) NOT NULL CHECK (last_name <> ''),
    first_name VARCHAR(50) NOT NULL CHECK (first_name <> ''),
    email VARCHAR(100) NOT NULL UNIQUE CHECK (email LIKE '%@%'),
    role VARCHAR(10) NOT NULL DEFAULT 'CUSTOMER'
        CHECK (role IN ('ADMIN', 'MANAGER', 'CUSTOMER'))
);

CREATE TABLE ticket (
    ticket_number VARCHAR(12) PRIMARY KEY
        CHECK (ticket_number ~ '^TKT-[0-9]{6}$'),
    session_code VARCHAR(30) NOT NULL REFERENCES film_session(session_code) ON DELETE CASCADE,
    row_num INT NOT NULL CHECK (row_num > 0),
    seat_num INT NOT NULL CHECK (seat_num > 0),
    status VARCHAR(10) NOT NULL DEFAULT 'AVAILABLE'
        CHECK (status IN ('AVAILABLE', 'RESERVED', 'SOLD')),
    price NUMERIC(8,2) NOT NULL CHECK (price >= 0),
    customer_login VARCHAR(30) REFERENCES user_account(login),
    sold_at TIMESTAMP,
    UNIQUE (session_code, row_num, seat_num),
    CHECK (
        (status = 'AVAILABLE' AND customer_login IS NULL AND sold_at IS NULL)
        OR
        (status = 'RESERVED' AND customer_login IS NOT NULL AND sold_at IS NULL)
        OR
        (status = 'SOLD' AND customer_login IS NOT NULL AND sold_at IS NOT NULL)
    )
);