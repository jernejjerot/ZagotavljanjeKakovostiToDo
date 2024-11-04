
 /*CREATE DATABASE ToDoList;*/
/*DROP USER 'JakaCvikl'@'localhost';
DROP USER 'readonly'@'localhost'; 

CREATE USER 'readonly'@'localhost' IDENTIFIED BY 'geslo';
GRANT SELECT ON *.* TO 'readonly'@'localhost';

CREATE USER 'JakaCvikl'@'localhost' IDENTIFIED BY '123123';
GRANT SELECT, INSERT, UPDATE, DELETE ON ToDoList.* TO 'JakaCvikl'@'localhost';
*/

DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS responsibilities;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
CREATE TABLE responsibilities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL
);

CREATE TABLE tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    responsibility_id INT NOT NULL,
    task_name VARCHAR(100) NOT NULL,
    description TEXT,
    due_date DATE,
    is_completed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (responsibility_id) REFERENCES responsibilities(id) ON DELETE CASCADE
);

INSERT INTO user (name, surname, email, password)
VALUES ('John', 'Doe', 'john.doe@example.com', 'password123'),
       ('Jane', 'Smith', 'jane.smith@example.com', 'password456');
       
       INSERT INTO responsibilities (type)
VALUES ('School'), ('Work'), ('Home');

INSERT INTO tasks (user_id, responsibility_id, task_name, description, due_date, is_completed)
VALUES (1, 1, 'Finish homework', 'Complete math and science homework', '2024-10-22', FALSE),
       (2, 2, 'Work meeting', 'Attend project meeting with the team', '2024-10-21', FALSE),
       (1, 3, 'Clean house', 'Do some cleaning before the weekend', '2024-10-20', TRUE);