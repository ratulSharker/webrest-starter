-- creating `role` table
CREATE TABLE IF NOT EXISTS `role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name_uk` (`name`)
);

-- creating `role_authorization` table
CREATE TABLE IF NOT EXISTS `role_authorization` (
  `action` varchar(255) NOT NULL,
  `feature` varchar(255) NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`action`,`feature`,`role_id`),
  CONSTRAINT `role_authorization_role_id_fk` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`)
);