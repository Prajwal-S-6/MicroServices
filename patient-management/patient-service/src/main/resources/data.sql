CREATE TABLE IF NOT EXISTS Patient (
    id UUID PRIMARY KEY, -- UUID as CHAR(36)
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE, -- Email must be unique
    date_of_birth DATE NOT NULL,
    registered_date DATE NOT NULL
);

-- Insert 15 dummy records with random UUIDs into the Patient table
INSERT INTO Patient (id, first_name, last_name, email, date_of_birth, registered_date)
VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 'John', 'Doe', 'john.doe@example.com', '1980-05-15', '2025-05-01'),
    ('b41f0847-689a-4a24-b724-8f7b41a35428', 'Jane', 'Smith', 'jane.smith@example.com', '1992-07-20', '2025-05-01'),
    ('d0f6c233-e209-463d-b4c4-81c18e5c55b3', 'Emily', 'Davis', 'emily.davis@example.com', '1985-11-30', '2025-05-01'),
    ('0fe6b74e-91b2-471b-a557-12e0d01b1d29', 'Michael', 'Johnson', 'michael.johnson@example.com', '1978-02-10', '2025-05-01'),
    ('918519bb-9033-4961-b2a9-5eae27634c97', 'Sarah', 'Brown', 'sarah.brown@example.com', '1990-04-05', '2025-05-01'),
    ('6f1b2f63-083d-4ad1-b1ff-67435b0c7f50', 'David', 'Taylor', 'david.taylor@example.com', '1988-12-25', '2025-05-01'),
    ('8b20aeb8-93f1-423f-bb56-dcf911adf0b6', 'Laura', 'Martinez', 'laura.martinez@example.com', '1995-08-30', '2025-05-01'),
    ('f251ca00-7029-4b97-b8f7-c350ef5d755f', 'James', 'Anderson', 'james.anderson@example.com', '1983-11-12', '2025-05-01'),
    ('890d8316-9418-4c34-bb23-cd3cf0b8b7fc', 'Emily', 'Thomas', 'emily.thomas@example.com', '1987-03-22', '2025-05-01'),
    ('27644c92-3eb1-4a99-b939-0a7b6e3e6b56', 'Daniel', 'Jackson', 'daniel.jackson@example.com', '1993-09-17', '2025-05-01'),
    ('de29a6d9-b0f1-4d30-b3b3-bef53fc720a4', 'Samantha', 'White', 'samantha.white@example.com', '1994-05-02', '2025-05-01'),
    ('4497846e-bb6c-4f7f-897f-d71c4a12f93f', 'Kevin', 'Harris', 'kevin.harris@example.com', '1982-07-10', '2025-05-01'),
    ('0ec1425e-dada-4b3d-b3a3-e6e1c51004bc', 'Olivia', 'Clark', 'olivia.clark@example.com', '1991-06-25', '2025-05-01'),
    ('fe6e9c07-cb3f-4964-b707-fbe4993bcb9a', 'Joshua', 'Lewis', 'joshua.lewis@example.com', '1984-09-13', '2025-05-01'),
    ('39d9ab76-b876-4a7b-bf8d-31d90a594db1', 'Sophia', 'Walker', 'sophia.walker@example.com', '1989-01-30', '2025-05-01');
