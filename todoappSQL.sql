-- Drop existing users (if they exist)
DROP USER IF EXISTS 'readonly'@'localhost';
DROP USER IF EXISTS 'root'@'localhost';

-- Create the database
DROP DATABASE IF EXISTS ToDoList;
CREATE DATABASE ToDoList;
USE ToDoList;

-- Create the `readonly` user with minimal privileges
CREATE USER 'readonly'@'localhost' IDENTIFIED BY 'geslo';
GRANT SELECT ON ToDoList.* TO 'readonly'@'localhost';

-- Create the `root` user with full privileges
CREATE USER 'root'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;

-- Flush privileges to ensure changes are applied
FLUSH PRIVILEGES;

-- Create the tables
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS taskType;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- BIGINT namest INT
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    admin BOOLEAN NOT NULL
);

CREATE TABLE taskType (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- BIGINT namest INT
    type VARCHAR(50) NOT NULL
);

CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- BIGINT namest INT
    user_id BIGINT NOT NULL,  -- BIGINT namest INT
    task_type_id BIGINT NOT NULL, -- Updated column name  -- BIGINT namest INT
    task_name VARCHAR(100) NOT NULL,
    description TEXT,
    due_datetime DATETIME, -- Combined date and time
    picture VARCHAR(255),
    is_completed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (task_type_id) REFERENCES taskType(id) ON DELETE CASCADE -- Updated reference
);

-- Insert sample data
INSERT INTO user (name, surname, email, password, admin)
VALUES 
    ('John', 'Doe', 'john.doe@example.com', 'password123', false),
    ('Jane', 'Smith', 'jane.smith@example.com', 'password456', false),
    ('Admin', 'User', 'admin@example.com', 'admin123', TRUE);

INSERT INTO taskType (type)
VALUES ('School'), ('Work'), ('Home');

INSERT INTO tasks (user_id, task_type_id, task_name, description, due_datetime, picture, is_completed)
VALUES 
    (1, 1, 'Finish homework', 'Complete math and science homework', '2024-10-22 18:40:00', '/uploads/homework.jpg', FALSE),
    (2, 2, 'Work meeting', 'Attend project meeting with the team', '2024-10-21 14:00:00', '/uploads/meeting.jpg', FALSE),
    (3, 3, 'Clean house', 'Do some cleaning before the weekend', '2024-10-20 09:30:00', '/uploads/cleaning.jpg', FALSE);

-- Verify data
SELECT * FROM user;
SELECT * FROM taskType;
SELECT * FROM todolist.tasks;

-- Find tasks with missing taskName or taskType
SELECT * FROM todolist.tasks WHERE task_name IS NULL OR task_type_id IS NULL;

-- Example: Fix invalid rows manually or remove them if they are not needed
DELETE FROM todolist.tasks WHERE task_name IS NULL OR task_type_id IS NULL;

-- Update user_id in tasks table to BIGINT
-- ALTER TABLE tasks MODIFY COLUMN user_id BIGINT;

-- Update task_type_id in tasks table to BIGINT
-- ALTER TABLE tasks MODIFY COLUMN task_type_id BIGINT;

DESCRIBE tasks;
DESCRIBE user;
DESCRIBE taskType;
