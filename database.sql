-- HEALTH TRACKER - DATABASE + DỮ LIỆU MẪU
 
CREATE DATABASE IF NOT EXISTS health_tracker CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE health_tracker;
 
-- TẠO CÁC BẢNG
 
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    birth_date DATE,
    gender ENUM('Nam', 'Nữ'),
    height_cm FLOAT,
    created_at DATETIME DEFAULT NOW()
);
 
CREATE TABLE weight_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    weight_kg FLOAT,
    bmi FLOAT,
    log_date DATE,
    note VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
 
CREATE TABLE water_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    amount_ml INT,
    log_time DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
 
CREATE TABLE sleep_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    sleep_time DATETIME,
    wake_time DATETIME,
    quality ENUM('Tệ', 'Bình thường', 'Tốt', 'Rất tốt'),
    note VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
 
CREATE TABLE goals (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    goal_type VARCHAR(50),
    target_value FLOAT,
    deadline DATE,
    status ENUM('Đang thực hiện', 'Đạt được', 'Thất bại'),
    FOREIGN KEY (user_id) REFERENCES users(id)
);