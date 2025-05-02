CREATE TABLE IF NOT EXISTS Patient (
    id UUID PRIMARY KEY, -- UUID as CHAR(36)
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE, -- Email must be unique
    address VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    registered_date DATE NOT NULL
);

INSERT INTO Patient (id, first_name, last_name, email, address, date_of_birth, registered_date)
VALUES
    ('a1f8f93a-4b5a-47b8-805e-19e9ef8ad01b', 'John', 'Doe', 'john.doe@example.com', '123 Elm Street', '1985-06-15', '2025-05-01'),
    ('b2a5e174-2fce-4e3d-b6e2-dbc58e8d5c39', 'Jane', 'Smith', 'jane.smith@example.com', '456 Oak Avenue', '1990-08-22', '2025-05-01'),
    ('c3cf46be-0a72-442e-8e28-4c4100e3cdd9', 'Michael', 'Johnson', 'michael.johnson@example.com', '789 Maple Drive', '1978-03-10', '2025-05-01'),
    ('d4e713f1-df33-41a4-8c68-90950a2be701', 'Emily', 'Davis', 'emily.davis@example.com', '321 Pine Lane', '1995-11-30', '2025-05-01'),
    ('e5f931ca-c181-4f15-81d9-96f8b3cf1aab', 'Daniel', 'Brown', 'daniel.brown@example.com', '654 Cedar Road', '1982-02-25', '2025-05-01'),
    ('f65c5ae4-7bd6-4c1f-9eb9-0a25eac14de6', 'Olivia', 'Taylor', 'olivia.taylor@example.com', '78 Willow Way', '1991-04-14', '2025-05-01'),
    ('07df2df3-9e20-41ea-a8f7-88167ac8b409', 'James', 'Wilson', 'james.wilson@example.com', '900 Birch Blvd', '1988-09-03', '2025-05-01'),
    ('180af607-bf9f-40cb-bfd6-4fbc4468771f', 'Sophia', 'Martinez', 'sophia.martinez@example.com', '245 Redwood Rd', '1993-07-08', '2025-05-01'),
    ('29f3eb84-fda6-4b01-83a3-e68d39a8d264', 'Liam', 'Garcia', 'liam.garcia@example.com', '522 Spruce Ct', '1996-05-17', '2025-05-01'),
    ('3a794d4a-dfc7-4784-9cb5-f4507b2f6702', 'Isabella', 'Lee', 'isabella.lee@example.com', '188 Magnolia Dr', '1994-12-01', '2025-05-01'),
    ('4b904e2f-4e8c-4f14-b4a5-d8b4db6b8a7f', 'William', 'Clark', 'william.clark@example.com', '333 Poplar Path', '1986-10-23', '2025-05-01'),
    ('5cba7c63-b93e-49ff-a032-3ffec2b69e43', 'Ava', 'Lewis', 'ava.lewis@example.com', '678 Fir Forest', '1997-03-05', '2025-05-01'),
    ('6d38ef12-10c2-423e-aee2-6dc2a159e60c', 'Noah', 'Hall', 'noah.hall@example.com', '901 Aspen Alley', '1983-01-19', '2025-05-01'),
    ('7efb9c8b-4b83-4ff5-bd15-f4b9894d15b2', 'Mia', 'Young', 'mia.young@example.com', '102 Hemlock Hill', '1998-06-29', '2025-05-01'),
    ('8f15c5e6-7e44-4f4c-85e1-c2de88b7b12f', 'Ethan', 'King', 'ethan.king@example.com', '77 Maple View', '1981-11-11', '2025-05-01');
