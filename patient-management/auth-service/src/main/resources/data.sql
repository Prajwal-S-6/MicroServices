CREATE TABLE IF NOT EXISTS user_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

---- Insert the user if no existing user with the same id or email exists
INSERT INTO user_details(id, email, password, role)
SELECT 10001, 'test@test.com', '$2b$12$7hoRZfJrRKD2nIm2vHLs7OBETy.LWenXXMLKf99W8M4PUwO6KB7fu', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM user_details WHERE id = 10001 OR email = 'test@test.com');

