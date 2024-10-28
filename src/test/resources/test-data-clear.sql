ALTER TABLE osttra_message
    DROP CONSTRAINT fkn7hpgy39k7v447bxa8dxao0q2;

ALTER TABLE osttra_message
    ADD CONSTRAINT fkn7hpgy39k7v447bxa8dxao0q2
        FOREIGN KEY (recipient) REFERENCES osttra_user(id)
            ON DELETE CASCADE;

DELETE FROM osttra_user;


