-- creating `role_authorization` table

CREATE TABLE IF NOT EXISTS `app_user_role` (
  `app_user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`app_user_id`,`role_id`),
  CONSTRAINT `app_user_role_app_user_id_fk` FOREIGN KEY (`app_user_id`) REFERENCES `app_user` (`app_user_id`),
  CONSTRAINT `app_user_role_role_id_fk` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`)
);