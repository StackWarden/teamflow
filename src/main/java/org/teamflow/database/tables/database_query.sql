CREATE TABLE IF NOT EXISTS  `User` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `username` VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS  `Project` (
                           `id` INT AUTO_INCREMENT PRIMARY KEY,
                           `name` VARCHAR(255) NOT NULL UNIQUE,
                           `description` TEXT
);

CREATE TABLE IF NOT EXISTS  `Role` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `role_name` VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS  `User_Project` (
                                `id` INT AUTO_INCREMENT PRIMARY KEY,
                                `user_id` INT NOT NULL,
                                `project_id` INT NOT NULL ,
                                `role_id` INT NOT NULL,
                                FOREIGN KEY (`user_id`) REFERENCES `User`(`id`),
                                FOREIGN KEY (`project_id`) REFERENCES `Project`(`id`) ON DELETE CASCADE,
                                FOREIGN KEY (`role_id`) REFERENCES `Role`(`id`)
);

CREATE TABLE IF NOT EXISTS  `Epic` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `project_id` INT NOT NULL,
                        `title` VARCHAR(255) NOT NULL,
                        FOREIGN KEY (`project_id`) REFERENCES `Project`(`id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS  `UserStory` (
                             `id` INT AUTO_INCREMENT PRIMARY KEY,
                             `epic_id` INT NOT NULL,
                             `description` TEXT,
                             FOREIGN KEY (`epic_id`) REFERENCES `Epic`(`id`)
);

CREATE TABLE IF NOT EXISTS  `Task` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `title` VARCHAR(255) NOT NULL,
                        `status` VARCHAR(50) NOT NULL,
                        `story_id` INT,
                        FOREIGN KEY (`story_id`) REFERENCES `UserStory`(`id`)
);

CREATE TABLE IF NOT EXISTS  `User_Task` (
                             `user_id` INT NOT NULL,
                             `task_id` INT NOT NULL,
                             PRIMARY KEY (`user_id`, `task_id`),
                             FOREIGN KEY (`user_id`) REFERENCES `User`(`id`),
                             FOREIGN KEY (`task_id`) REFERENCES `Task`(`id`)
);

CREATE TABLE IF NOT EXISTS  `Sprint` (
                          `id` INT AUTO_INCREMENT PRIMARY KEY,
                          `project_id` INT NOT NULL,
                          `start_date` DATE NOT NULL,
                          `end_date` DATE NOT NULL,
                          FOREIGN KEY (`project_id`) REFERENCES `Project`(`id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS  `Sprint_Task` (
                               `sprint_id` INT NOT NULL,
                               `task_id` INT NOT NULL,
                               PRIMARY KEY (`sprint_id`, `task_id`),
                               FOREIGN KEY (`sprint_id`) REFERENCES `Sprint`(`id`),
                               FOREIGN KEY (`task_id`) REFERENCES `Task`(`id`)
);

CREATE TABLE IF NOT EXISTS  `Chatroom` (
                            `id` INT AUTO_INCREMENT PRIMARY KEY,
                            `name` VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS  `Message` (
                           `id` INT AUTO_INCREMENT PRIMARY KEY,
                           `chatroom_id` INT NOT NULL,
                           `user_id` INT NOT NULL,
                           `content` TEXT,
                           `timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (`chatroom_id`) REFERENCES `Chatroom`(`id`),
                           FOREIGN KEY (`user_id`) REFERENCES `User`(`id`)
);

CREATE TABLE IF NOT EXISTS  `Epic_Chatroom` (
                                 `chatroom_id` INT NOT NULL,
                                 `epic_id` INT NOT NULL,
                                 PRIMARY KEY (`chatroom_id`, `epic_id`),
                                 FOREIGN KEY (`chatroom_id`) REFERENCES `Chatroom`(`id`),
                                 FOREIGN KEY (`epic_id`) REFERENCES `Epic`(`id`)
);

CREATE TABLE IF NOT EXISTS  `Story_Chatroom` (
                                  `chatroom_id` INT NOT NULL,
                                  `story_id` INT NOT NULL,
                                  PRIMARY KEY (`chatroom_id`, `story_id`),
                                  FOREIGN KEY (`chatroom_id`) REFERENCES `Chatroom`(`id`),
                                  FOREIGN KEY (`story_id`) REFERENCES `UserStory`(`id`)
);

CREATE TABLE IF NOT EXISTS  `Task_Chatroom` (
                                 `chatroom_id` INT NOT NULL,
                                 `task_id` INT NOT NULL,
                                 PRIMARY KEY (`chatroom_id`, `task_id`),
                                 FOREIGN KEY (`chatroom_id`) REFERENCES `Chatroom`(`id`),
                                 FOREIGN KEY (`task_id`) REFERENCES `Task`(`id`)
);

CREATE TABLE IF NOT EXISTS  `Sprint_Chatroom` (
                                   `chatroom_id` INT NOT NULL,
                                   `sprint_id` INT NOT NULL,
                                   PRIMARY KEY (`chatroom_id`, `sprint_id`),
                                   FOREIGN KEY (`chatroom_id`) REFERENCES `Chatroom`(`id`),
                                   FOREIGN KEY (`sprint_id`) REFERENCES `Sprint`(`id`)
);

