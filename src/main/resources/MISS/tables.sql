/**
  used to create subscription tabl;
 */
CREATE TABLE `subscription` (
  `id` int NOT NULL AUTO_INCREMENT,
  `subscription_id` varchar(60) DEFAULT NULL,
  `endpoint` varchar(80) NOT NULL,
  `status` tinyint(1) DEFAULT '1',
  `protocol` varchar(20) NOT NULL,
  `create_date` timestamp NULL DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `topic_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `subscription_id` (`subscription_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/**
  used to create user table
 */

CREATE TABLE `user` (
    `id` int NOT NULL AUTO_INCREMENT,
    `username` varchar(40) NOT NULL,
    `password` varchar(100) NOT NULL,
    `secret` varchar(16) DEFAULT NULL,
    `enabled` tinyint(1) NOT NULL,
    `additional_security` tinyint(1) NOT NULL,
    `role_name` varchar(40) DEFAULT NULL,
    `detail` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/**
  used to create topic table
 */
CREATE TABLE `topic` (
     `id` int NOT NULL AUTO_INCREMENT,
     `topic_name` varchar(100) NOT NULL,
     `topic_type` varchar(20) DEFAULT NULL,
     `topic_arn` varchar(200) NOT NULL,
     `create_date` timestamp NULL DEFAULT NULL,
     `is_deleted` tinyint(1) DEFAULT '0',
     PRIMARY KEY (`id`),
     UNIQUE KEY `topic_name` (`topic_name`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;

/**
    used to create user_login_log table
 */

CREATE TABLE `user_login_log` (
      `log_id` int NOT NULL AUTO_INCREMENT,
      `username` varchar(40) NOT NULL,
      `login_time` timestamp NOT NULL,
      PRIMARY KEY (`log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;