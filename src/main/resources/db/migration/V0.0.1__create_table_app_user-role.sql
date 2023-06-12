-- creating `app_user` table
CREATE TABLE IF NOT EXISTS `app_user` (
  `app_user_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `dob` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `profile_picture_path` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`app_user_id`),
  UNIQUE KEY `app_user_email_uk` (`email`),
  UNIQUE KEY `app_user_mobile_uk` (`mobile`)
);
