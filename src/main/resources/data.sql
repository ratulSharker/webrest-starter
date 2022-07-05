-- MySQL
INSERT IGNORE INTO app_user (app_user_id, name, email, mobile, password, app_user_type, created_at, updated_at)
VALUES (1, "John Doe", "john@gmail.com", "+880123456789", "2bb80d537b1da3e38bd30361aa855686bde0eacd7162fef6a25fe97bf527a25b", "ADMIN", CURDATE(), CURDATE());