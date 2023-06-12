-- Creating a default user
INSERT IGNORE INTO app_user (app_user_id, name, email, mobile, password, created_at, updated_at)
VALUES (1, "John Doe", "john@gmail.com", "+880123456789", "2bb80d537b1da3e38bd30361aa855686bde0eacd7162fef6a25fe97bf527a25b", CURDATE(), CURDATE());

-- Creating a super admin role
INSERT IGNORE INTO role (role_id, name, active) VALUES (1, 'Super Admin', 1);

-- Association between super admin role and authorization features
INSERT IGNORE INTO role_authorization (action, feature, role_id) VALUES ('LISTING', 'ROLE', 1);
INSERT IGNORE INTO role_authorization (action, feature, role_id) VALUES ('CREATE', 'ROLE', 1);
INSERT IGNORE INTO role_authorization (action, feature, role_id) VALUES ('UPDATE', 'ROLE', 1);

INSERT IGNORE INTO role_authorization (action, feature, role_id) VALUES ('CREATE', 'USER', 1);
INSERT IGNORE INTO role_authorization (action, feature, role_id) VALUES ('VIEW', 'USER', 1);
INSERT IGNORE INTO role_authorization (action, feature, role_id) VALUES ('UPDATE', 'USER', 1);
INSERT IGNORE INTO role_authorization (action, feature, role_id) VALUES ('LISTING', 'USER', 1);
INSERT IGNORE INTO role_authorization (action, feature, role_id) VALUES ('DELETE', 'USER', 1);

-- Association between role and user
INSERT IGNORE INTO app_user_role (app_user_id, role_id) VALUES (1, 1);