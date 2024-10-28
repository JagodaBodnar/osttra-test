CREATE TABLE IF NOT EXISTS osttra_message
(
    id        BIGSERIAL PRIMARY KEY,
    content   VARCHAR(255) NOT NULL,
    read      BOOLEAN      DEFAULT FALSE,
    recipient UUID         NOT NULL,
    timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    sender    VARCHAR(255) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS message_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS osttra_user
(
    id         UUID PRIMARY KEY,
    email      VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255),
    last_name  VARCHAR(255)
);

INSERT INTO osttra_user (id, email, first_name, last_name)
VALUES ('49368723-db5a-4232-a44e-71b3c54b8b83', 'Henrik', 'Nordin', 'henrik.nordin@gmail.com') ON CONFLICT  DO NOTHING;

INSERT INTO osttra_message (id, content, read, recipient, timestamp, sender)
VALUES (nextval('message_seq'), 'Remember about meeting tomorrow at 13:30.', false,
        '49368723-db5a-4232-a44e-71b3c54b8b83','2024-10-27 18:33:00.113942', 'System') ON CONFLICT  DO NOTHING;