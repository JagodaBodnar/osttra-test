INSERT INTO osttra_user
values ('00bded6f-96b0-432c-abfe-d85d1129ae29',
        'oscar.enman@gmail.com',
        'Oscar',
        'Ernman'),
       ('b0bb1a3d-c07c-4a07-8826-8c8c6e4fbc93',
        'philip.petersson@gmail.com',
            'Philip',
        'Petersson'),
       ('042484f0-be2f-49b1-a741-6962ef991718',
        'emma.eriksson@gmail.com',
        'Emma',
        'Eriksson') ON CONFLICT  do nothing ;