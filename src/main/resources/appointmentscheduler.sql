CREATE DATABASE  IF NOT EXISTS `officehoursportal`;
USE `officehoursportal`;

CREATE TABLE IF NOT EXISTS `roles` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(50) DEFAULT NULL,
    PRIMARY KEY (`id`)
    )
    ENGINE=InnoDB
    AUTO_INCREMENT=1
    DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `users` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL,
    `password` char(80) NOT NULL,
    `first_name` varchar(50),
    `last_name` varchar(50),
    `email` varchar(50),
    `mobile` varchar(50),
    `street` varchar(50),
    `city` varchar(50),
    `postcode` varchar(50),
    PRIMARY KEY (`id`)
    )
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `users_roles` (
    `user_id` int(11) NOT NULL,
    `role_id` int(11) NOT NULL,
    PRIMARY KEY (`user_id`,`role_id`),
    KEY `FK_ROLE_idx` (`role_id`),

    CONSTRAINT `FK_users_user` FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION ON UPDATE NO ACTION,

    CONSTRAINT `FK_roles_role` FOREIGN KEY (`role_id`)
    REFERENCES `roles` (`id`)
    ON DELETE NO ACTION ON UPDATE NO ACTION
    )
    ENGINE=InnoDB
    DEFAULT CHARSET=utf8;
SET FOREIGN_KEY_CHECKS = 1;


CREATE TABLE IF NOT EXISTS `courses` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(256),
    `duration` INT(11),
    `credits` INT(11),
    `editable` BOOLEAN,
    `target` VARCHAR(256),
    `description` TEXT,
    PRIMARY KEY (`id`)
    )
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `reports` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `number` VARCHAR(256),
    `status` VARCHAR(256),
    `total_credits` DECIMAL(10, 2),
    `issued` DATETIME,
    PRIMARY KEY (`id`)
    )
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `meetings` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `start` DATETIME,
    `end` DATETIME,
    `canceled_at` DATETIME,
    `status` VARCHAR(20),
    `id_canceler` INT(11),
    `id_instructor` INT(11),
    `id_student` INT(11),
    `id_course` INT(11),
    `id_report` INT(11),
    PRIMARY KEY (`id`),
    KEY `id_canceler` (`id_canceler`),
    KEY `id_instructor` (`id_instructor`),
    KEY `id_student` (`id_student`),
    KEY `id_course` (`id_course`),
    KEY `id_report` (`id_report`),
    CONSTRAINT `meetings_users_canceler` FOREIGN KEY (`id_canceler`) REFERENCES `users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CONSTRAINT `meetings_users_students` FOREIGN KEY (`id_student`) REFERENCES `users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CONSTRAINT `meetings_courses` FOREIGN KEY (`id_course`) REFERENCES `courses` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CONSTRAINT `meetings_users_instructors` FOREIGN KEY (`id_instructor`) REFERENCES `users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CONSTRAINT `meetings_reports` FOREIGN KEY (`id_report`) REFERENCES `reports` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE

    )
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8;



CREATE TABLE IF NOT EXISTS `courses_instructors` (
    `id_user` INT(11) NOT NULL,
    `id_course` INT(11) NOT NULL,
    PRIMARY KEY (`id_user`, `id_course`),
    KEY `id_course` (`id_course`),
    CONSTRAINT `courses_instructors_users_instructors` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CONSTRAINT `courses_instructors_courses` FOREIGN KEY (`id_course`) REFERENCES `courses` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    )
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `working_plans` (
    `id_instructor` int(11) NOT NULL,
    `monday` TEXT,
    `tuesday` TEXT,
    `wednesday` TEXT,
    `thursday` TEXT,
    `friday` TEXT,
    `saturday` TEXT,
    `sunday` TEXT,

    PRIMARY KEY (`id_instructor`),
    KEY `id_instructor` (`id_instructor`),

    CONSTRAINT `FK_meetings_instructor` FOREIGN KEY (`id_instructor`)
    REFERENCES `users` (`id`)

    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    )
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `messages` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `created_at` DATETIME,
    `message` TEXT,
    `id_author` INT(11),
    `id_meeting` INT(11),
    PRIMARY KEY (`id`),
    KEY `id_author` (`id_author`),
    KEY `id_meeting` (`id_meeting`),

    CONSTRAINT `FK_notes_author` FOREIGN KEY (`id_author`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,

    CONSTRAINT `FK_notes_meeting` FOREIGN KEY (`id_meeting`)
    REFERENCES `meetings` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    )
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8;



CREATE TABLE IF NOT EXISTS `grad_students` (
    `id_student` int(11) NOT NULL,
    `cid_number` VARCHAR(256),
    `univ_name` VARCHAR(256),
    PRIMARY KEY (`id_student`),
    KEY `id_student` (`id_student`),
    CONSTRAINT `FK_grad_student_user` FOREIGN KEY (`id_student`)
    REFERENCES `users` (`id`)
    )
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `instructors` (
    `id_instructor` int(11) NOT NULL,
    PRIMARY KEY (`id_instructor`),
    KEY `id_instructor` (`id_instructor`),
    CONSTRAINT `FK_instructor_user` FOREIGN KEY (`id_instructor`)
    REFERENCES `users` (`id`)
    )
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS  `undergrad_students` (
    `id_student` int(11) NOT NULL,
    PRIMARY KEY (`id_student`),
    KEY `id_student` (`id_student`),
    CONSTRAINT `FK_undergrad_student_user` FOREIGN KEY (`id_student`)
    REFERENCES `users` (`id`)
    )
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `students` (
    `id_student` int(11) NOT NULL,
    PRIMARY KEY (`id_student`),
    KEY `id_student` (`id_student`),

    CONSTRAINT `FK_student_user` FOREIGN KEY (`id_student`)
    REFERENCES `users` (`id`)
    )
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `notifications` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(256),
    `message` TEXT,
    `created_at` DATETIME,
    `url` VARCHAR(256),
    `is_read` BOOLEAN,
    `id_user` INT(11),
    PRIMARY KEY (`id`),
    KEY `id_user` (`id_user`),

    CONSTRAINT `FK_notification_user` FOREIGN KEY (`id_user`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    )
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `exchanges` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `exchange_status` VARCHAR(256),
    `id_meeting_requestor` INT(11),
    `id_meeting_requested` INT(11),
    PRIMARY KEY (`id`),
    KEY `id_meeting_requestor` (`id_meeting_requestor`),
    KEY `id_meeting_requested` (`id_meeting_requested`),
    CONSTRAINT `FK_exchange_meeting_requestor` FOREIGN KEY (`id_meeting_requestor`)
    REFERENCES `meetings` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `FK_exchange_meeting_requested` FOREIGN KEY (`id_meeting_requested`)
    REFERENCES `meetings` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    )
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8;

-- INSERT available roles
INSERT INTO `roles` (id,name) VALUES
(1,'ROLE_ADMIN'),
(2,'ROLE_INSTRUCTOR'),
(3,'ROLE_STUDENT'),
(4,'ROLE_STUDENT_GRAD'),
(5,'ROLE_STUDENT_UNDERGRAD');
INSERT INTO `users` (id, username, password)
VALUES (1, 'admin', '$2a$10$EqKcp1WFKVQISheBxkQJoOqFbsWDzGJXRz/tjkGq85IZKJJ1IipYi');
INSERT INTO `users_roles` (user_id, role_id)
VALUES (1, 1);
INSERT INTO `users` (id, username, password)
VALUES (2, 'instructor', '$2a$10$EqKcp1WFKVQISheBxkQJoOqFbsWDzGJXRz/tjkGq85IZKJJ1IipYi');
INSERT INTO `instructors` (id_instructor)
VALUES (2);
INSERT INTO `users_roles` (user_id, role_id)
VALUES (2, 2);
INSERT INTO `users` (id, username, password)
VALUES (3, 'undergrad', '$2a$10$EqKcp1WFKVQISheBxkQJoOqFbsWDzGJXRz/tjkGq85IZKJJ1IipYi');
INSERT INTO `students` (id_student)
VALUES (3);
INSERT INTO `undergrad_students` (id_student)
VALUES (3);
INSERT INTO `users_roles` (user_id, role_id)
VALUES (3, 3);
INSERT INTO `users_roles` (user_id, role_id)
VALUES (3, 5);
INSERT INTO `users` (id, username, password)
VALUES (4, 'graduate', '$2a$10$EqKcp1WFKVQISheBxkQJoOqFbsWDzGJXRz/tjkGq85IZKJJ1IipYi');
INSERT INTO `students` (id_student)
VALUES (4);
INSERT INTO `grad_students` (id_student, cid_number, univ_name)
VALUES (4, '123456789', 'Northeastern University');
INSERT INTO `users_roles` (user_id, role_id)
VALUES (4, 3);
INSERT INTO `users_roles` (user_id, role_id)
VALUES (4, 4);

INSERT INTO `courses` (id, name, duration, credits, editable, target, description)
VALUES (1, 'CS5610', 60, 4, true, 'undergrad',
        'Web Development Courses by Jose Annunziato');

INSERT INTO courses_instructors
VALUES (2, 1);
INSERT INTO working_plans
VALUES (2,
        '{"workingHours":{"start":[6,0],"end":[18,0]},"breaks":[],"timePeroidsWithBreaksExcluded":[{"start":[6,0],"end":[18,0]}]}',
        '{"workingHours":{"start":[6,0],"end":[18,0]},"breaks":[],"timePeroidsWithBreaksExcluded":[{"start":[6,0],"end":[18,0]}]}',
        '{"workingHours":{"start":[6,0],"end":[18,0]},"breaks":[],"timePeroidsWithBreaksExcluded":[{"start":[6,0],"end":[18,0]}]}',
        '{"workingHours":{"start":[6,0],"end":[18,0]},"breaks":[],"timePeroidsWithBreaksExcluded":[{"start":[6,0],"end":[18,0]}]}',
        '{"workingHours":{"start":[6,0],"end":[18,0]},"breaks":[],"timePeroidsWithBreaksExcluded":[{"start":[6,0],"end":[18,0]}]}',
        '{"workingHours":{"start":[6,0],"end":[18,0]},"breaks":[],"timePeroidsWithBreaksExcluded":[{"start":[6,0],"end":[18,0]}]}',
        '{"workingHours":{"start":[6,0],"end":[18,0]},"breaks":[],"timePeroidsWithBreaksExcluded":[{"start":[6,0],"end":[18,0]}]}');

