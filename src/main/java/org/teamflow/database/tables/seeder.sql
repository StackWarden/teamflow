-- Insert roles
INSERT INTO Role (role_name) VALUES
                                 ('Developer'),
                                 ('Product Owner'),
                                 ('Scrum Master');

-- Insert users
INSERT INTO User (username) VALUES
                                ('alice'),
                                ('bob'),
                                ('carol');

-- Insert projects
INSERT INTO Project (name, description) VALUES
                                            ('TeamFlow Web App', 'A collaborative task and sprint management tool'),
                                            ('TeamFlow Mobile App', 'The mobile version of TeamFlow');

-- Link users to projects with roles
INSERT INTO User_Project (user_id, project_id, role_id) VALUES
                                                            (1, 1, 1), -- alice → Web App → Developer
                                                            (2, 1, 2), -- bob → Web App → Product Owner
                                                            (3, 1, 3), -- carol → Web App → Scrum Master
                                                            (1, 2, 1); -- alice → Mobile App → Developer

-- Insert epics
INSERT INTO Epic (project_id, title) VALUES
                                         (1, 'User Registration Flow'),
                                         (1, 'Task Management Module');

-- Insert user stories
INSERT INTO UserStory (epic_id, description) VALUES
                                                 (1, 'As a user, I want to sign up with email and password'),
                                                 (1, 'As a user, I want to reset my password'),
                                                 (2, 'As a user, I want to create and assign tasks');

-- Insert tasks
INSERT INTO Task (title, status, story_id) VALUES
                                               ('Create signup form', 'todo', 1),
                                               ('Implement password reset email', 'in_progress', 2),
                                               ('Task creation UI', 'done', 3);

-- Assign tasks to users
INSERT INTO User_Task (user_id, task_id) VALUES
                                             (1, 1), -- alice → signup form
                                             (2, 2), -- bob → reset email
                                             (3, 3); -- carol → task UI

-- Insert a sprint
INSERT INTO Sprint (project_id, start_date, end_date) VALUES
    (1, '2024-04-01', '2024-04-14');

-- Assign tasks to the sprint
INSERT INTO Sprint_Task (sprint_id, task_id) VALUES
                                                 (1, 1),
                                                 (1, 2),
                                                 (1, 3);

-- Create chatrooms
INSERT INTO Chatroom (name) VALUES
                                ('Registration Discussion'),
                                ('Task Module Standup');
                                ('Story chatroom 1'),
                                ('Sprint chatroom 1');

-- Messages in chatrooms
INSERT INTO Message (chatroom_id, user_id, content, timestamp) VALUES
                                                                   (1, 1, 'Working on the registration page today', NOW()),
                                                                   (2, 3, 'Task module is now complete!', NOW());

-- Connect chatrooms to entities
INSERT INTO Epic_Chatroom (chatroom_id, epic_id) VALUES
    (1, 1);

INSERT INTO Task_Chatroom (chatroom_id, task_id) VALUES
    (2, 3);

-- Connect chatrooms to entities
INSERT INTO Story_Chatroom (chatroom_id, story_id) VALUES
    (3, 1);

INSERT INTO Sprint_Chatroom (chatroom_id, sprint_id) VALUES
    (4, 1);
