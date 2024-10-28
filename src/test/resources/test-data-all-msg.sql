INSERT INTO osttra_message (id, content, read, recipient, timestamp, sender)
VALUES (nextval('message_seq'), 'Remember about meeting tomorrow at 14:30.', true,
        '49368723-db5a-4232-a44e-71b3c54b8b83','2024-10-27 18:34:00.113942', 'System') ON CONFLICT  DO NOTHING;
INSERT INTO osttra_message (id, content, read, recipient, timestamp, sender)
VALUES (nextval('message_seq'), 'Remember about meeting tomorrow at 15:30.', false,
        '49368723-db5a-4232-a44e-71b3c54b8b83','2024-10-27 18:35:00.113942', 'System') ON CONFLICT  DO NOTHING;