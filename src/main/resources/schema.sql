CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_name varchar(100) NOT NULL,
    email varchar(254) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    item_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    item_name varchar(100) NOT NULL,
    description varchar(320) NOT NULL,
    available boolean NOT NULL,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT fk_items_to_users
        FOREIGN KEY(owner_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status varchar(10),
    CONSTRAINT fk_bookings_to_items
        FOREIGN KEY (item_id)
        REFERENCES items(item_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_bookings_to_users
        FOREIGN KEY (booker_id)
        REFERENCES users(user_id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS requests (
    request_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description varchar(320) NOT NULL,
    requester_id BIGINT NOT NULL,
    create_date  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_requests_to_users
        FOREIGN KEY (requester_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    comment_text varchar(500) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    create_date  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_comments_to_items
        FOREIGN KEY (item_id)
            REFERENCES items(item_id)
            ON DELETE CASCADE,
    CONSTRAINT fk_comments_to_users
        FOREIGN KEY (author_id)
            REFERENCES users(user_id)
            ON DELETE SET NULL
);