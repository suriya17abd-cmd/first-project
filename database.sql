DROP DATABASE IF EXISTS qrid_campus;
CREATE DATABASE qrid_campus CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qrid_campus;

CREATE TABLE users (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 name VARCHAR(100) NOT NULL,
 email VARCHAR(120) NOT NULL UNIQUE,
 password_hash VARCHAR(100) NOT NULL,
 role ENUM('ADMIN','SECURITY','STAFF') NOT NULL,
 department VARCHAR(100),
 status ENUM('ACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 INDEX idx_users_role_status(role,status)
) ENGINE=InnoDB;

CREATE TABLE identities (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 identity_no VARCHAR(40) NOT NULL UNIQUE,
 full_name VARCHAR(120) NOT NULL,
 email VARCHAR(120) NOT NULL,
 person_type ENUM('STUDENT','STAFF') NOT NULL,
 department VARCHAR(100) NOT NULL,
 course VARCHAR(120),
 photo LONGBLOB,
 photo_mime VARCHAR(50),
 status ENUM('ACTIVE','SUSPENDED','GRADUATED') NOT NULL DEFAULT 'ACTIVE',
 valid_from DATE NOT NULL,
 valid_until DATE NOT NULL,
 qr_token VARCHAR(64) NOT NULL UNIQUE,
 version INT NOT NULL DEFAULT 1,
 created_by BIGINT NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 CONSTRAINT fk_identity_creator FOREIGN KEY(created_by) REFERENCES users(id),
 INDEX idx_identity_name(full_name), INDEX idx_identity_department(department), INDEX idx_identity_status(status)
) ENGINE=InnoDB;

CREATE TABLE generated_cards (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 identity_id BIGINT NOT NULL,
 version INT NOT NULL,
 pdf_data LONGBLOB NOT NULL,
 generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_card_identity FOREIGN KEY(identity_id) REFERENCES identities(id) ON DELETE CASCADE,
 UNIQUE KEY uq_card_version(identity_id,version)
) ENGINE=InnoDB;

CREATE TABLE scan_logs (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 identity_id BIGINT NULL,
 verified_by BIGINT NULL,
 gate_location VARCHAR(100) NOT NULL,
 result ENUM('VALID','SUSPENDED','GRADUATED','EXPIRED','NOT_FOUND') NOT NULL,
 ip_address VARCHAR(45),
 scanned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_scan_identity FOREIGN KEY(identity_id) REFERENCES identities(id) ON DELETE SET NULL,
 CONSTRAINT fk_scan_verifier FOREIGN KEY(verified_by) REFERENCES users(id) ON DELETE SET NULL,
 INDEX idx_scan_time(scanned_at), INDEX idx_scan_result(result), INDEX idx_scan_identity(identity_id)
) ENGINE=InnoDB;

CREATE TABLE audit_logs (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 user_id BIGINT NULL,
 action VARCHAR(60) NOT NULL,
 entity_type VARCHAR(40),
 entity_id VARCHAR(64),
 details VARCHAR(500),
 ip_address VARCHAR(45),
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_audit_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE SET NULL,
 INDEX idx_audit_time(created_at), INDEX idx_audit_action(action)
) ENGINE=InnoDB;

CREATE TABLE notifications (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 user_id BIGINT NOT NULL,
 title VARCHAR(160) NOT NULL,
 message VARCHAR(500) NOT NULL,
 is_read BOOLEAN NOT NULL DEFAULT FALSE,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_notification_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
 INDEX idx_notification_user(user_id,is_read)
) ENGINE=InnoDB;

CREATE TABLE contact_messages (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 name VARCHAR(100) NOT NULL,
 email VARCHAR(120) NOT NULL,
 subject VARCHAR(160),
 message TEXT NOT NULL,
 status ENUM('NEW','READ','CLOSED') NOT NULL DEFAULT 'NEW',
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO users(name,email,password_hash,role,department,status) VALUES
('System Administrator','admin@qrid.local','$2a$12$pPcbSP52c3ML7nUJ.7Io2uXIsatKkb0n2vpPX2SKWwxiWlO1YcZ1y','ADMIN','Administration','ACTIVE'),
('Main Gate Officer','security@qrid.local','$2a$12$X.AtaLK7vngnYMnjiXW20.GqQESDEtGQBWDg97yhPjgNWcGk7/b7G','SECURITY','Campus Security','ACTIVE'),
('CSE Department Staff','staff@qrid.local','$2a$12$2zz1Pl6TjEWveHevNnVk.Okk.QDl4p7SqpGrDNq60j70rFGLUnTg6','STAFF','Computer Science','ACTIVE');

INSERT INTO identities(identity_no,full_name,email,person_type,department,course,status,valid_from,valid_until,qr_token,created_by) VALUES
('STU/CSE/2026/001','Ananya Ravi','ananya@example.edu','STUDENT','Computer Science','B.Sc Computer Science','ACTIVE','2026-06-01','2029-05-31','sample-active-token-001',1),
('STU/COM/2026/014','Arjun Kumar','arjun@example.edu','STUDENT','Commerce','B.Com','SUSPENDED','2026-06-01','2029-05-31','sample-suspended-token-014',1),
('EMP/2026/008','Dr. Meena S','meena@example.edu','STAFF','Computer Science','Faculty','ACTIVE','2026-01-01','2030-12-31','sample-staff-token-008',1);

INSERT INTO notifications(user_id,title,message) VALUES
(1,'System ready','QRID Campus database and default accounts were created.'),
(2,'Scanner assigned','You can verify IDs from the Security Scanner page.'),
(3,'Department access','You can manage identities for Computer Science only.');
