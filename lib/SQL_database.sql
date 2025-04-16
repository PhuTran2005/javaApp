CREATE DATABASE IT_Course_Management;
GO
USE IT_Course_Management;
GO

-- 1. Bảng Roles
CREATE TABLE Roles (
    role_id INT PRIMARY KEY IDENTITY(1,1),
    role_name NVARCHAR(50) NOT NULL UNIQUE,
    description NVARCHAR(MAX)
);

-- 2. Bảng Permissions
CREATE TABLE Permissions (
    permission_id INT PRIMARY KEY IDENTITY(1,1),
    permission_name NVARCHAR(100) NOT NULL UNIQUE
);

-- 3. Gán quyền cho Role
CREATE TABLE Role_Permissions (
    role_id INT,
    permission_id INT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES Roles(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES Permissions(permission_id) ON DELETE CASCADE
);

-- 4. Bảng Users
CREATE TABLE Users (
    user_id INT PRIMARY KEY IDENTITY(1,1),
    email NVARCHAR(100) NOT NULL UNIQUE,
    password_hash NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    role_id INT,
    phonenumber NVARCHAR(20),
    create_date DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (role_id) REFERENCES Roles(role_id) ON DELETE SET NULL
);

-- 5. Giao quyền cá nhân
CREATE TABLE User_Permissions (
    user_id INT,
    permission_id INT,
    PRIMARY KEY (user_id, permission_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES Permissions(permission_id) ON DELETE CASCADE
);

-- 6. Bảng Instructors
CREATE TABLE Instructors (
    instructor_id INT PRIMARY KEY,
    specialty NVARCHAR(100),
    degree NVARCHAR(100),
    years_of_experience INT DEFAULT 0 CHECK(years_of_experience >= 0),
    FOREIGN KEY (instructor_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- 7. Bảng Students
CREATE TABLE Students (
    student_id INT PRIMARY KEY,
    class NVARCHAR(50),
    enrollment_year INT DEFAULT 0 CHECK(enrollment_year >= 0),
    gpa FLOAT DEFAULT 0 CHECK (gpa >= 0 AND gpa <= 4),
    FOREIGN KEY (student_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- 8. Bảng Categories
CREATE TABLE Categories (
    category_id INT PRIMARY KEY IDENTITY(1,1),
    category_name NVARCHAR(100) UNIQUE NOT NULL,
    description NVARCHAR(MAX)
);

-- 9. Bảng Courses
CREATE TABLE Courses (
    course_id INT PRIMARY KEY IDENTITY(1,1),
    course_name NVARCHAR(100) NOT NULL,
    description NVARCHAR(MAX),
    instructor_id INT,
    category_id INT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    fee DECIMAL(10, 2),
    course_thumbnail VARCHAR(MAX) DEFAULT 'Images/logo.png',
    is_deleted BIT DEFAULT 0,
    create_date DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (instructor_id) REFERENCES Instructors(instructor_id) ON DELETE SET NULL,
    FOREIGN KEY (category_id) REFERENCES Categories(category_id) ON DELETE SET NULL
);

-- 10. Bảng Course_Instructors (nhiều GV dạy 1 khóa)
CREATE TABLE Course_Instructors (
    id INT PRIMARY KEY IDENTITY(1,1),
    course_id INT,
    user_id INT,
    FOREIGN KEY (course_id) REFERENCES Courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    UNIQUE (course_id, user_id)
);

-- 11. Bảng Enrollments
CREATE TABLE Enrollments (
    enrollments_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    course_id INT NOT NULL,
    enrollment_date DATETIME DEFAULT GETDATE(),
    payment_status NVARCHAR(10) CHECK (payment_status IN ('PAID', 'UNPAID')) NOT NULL DEFAULT 'UNPAID',
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES Courses(course_id) ON DELETE CASCADE,
    UNIQUE (user_id, course_id)
);

-- 12. Bảng Payments
CREATE TABLE Payments (
    payments_id INT PRIMARY KEY IDENTITY(1,1),
    enrollments_id INT UNIQUE,
    amount DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
    method NVARCHAR(50) CHECK (method IN ('VNPay', 'Momo', 'Bank Transfer', 'Cash')) NOT NULL,
    status NVARCHAR(10) CHECK (status IN ('Success', 'Pending', 'Failed')) NOT NULL DEFAULT 'Pending',
    payment_date DATETIME,
    FOREIGN KEY (enrollments_id) REFERENCES Enrollments(enrollments_id) ON DELETE CASCADE
);

-- 13. Bảng Cart
CREATE TABLE Cart (
    cart_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    course_id INT NOT NULL,
    quantity INT DEFAULT 1,
    added_at DATETIME DEFAULT GETDATE(),
    status VARCHAR(10) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CHECKOUT')),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES Courses(course_id) ON DELETE CASCADE
);

-- 14. Bảng Logs
CREATE TABLE Logs (
    log_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT,
    action NVARCHAR(255),
    action_time DATETIME2 DEFAULT SYSDATETIME(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL
);



-- Xóa các bảng có khóa ngoại trước
-- Xóa các bảng cũ nếu tồn tại (đưa lên đầu)
DROP TABLE IF EXISTS Logs;
DROP TABLE IF EXISTS Payments;
DROP TABLE IF EXISTS Enrollments;
DROP TABLE IF EXISTS Course_Instructors;
DROP TABLE IF EXISTS Cart;
DROP TABLE IF EXISTS Courses;
DROP TABLE IF EXISTS Categories;
DROP TABLE IF EXISTS User_Permissions;
DROP TABLE IF EXISTS Students;
DROP TABLE IF EXISTS Instructors;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Role_Permissions;
DROP TABLE IF EXISTS Permissions;
DROP TABLE IF EXISTS Roles;



-- Thêm người dùng với quyền ADMIN
INSERT INTO Users (full_name, password_hash, role_id, email, phonenumber)
VALUES 
('admin', '$2a$10$Y0L8WUUWemHJhCJUG4J17uoUrcePpEA8RgMs1bOgFhNauW5HK5qUe', 1, 'admin@gmail.com', '0123456789');

-- Roles
INSERT INTO Roles(role_name, description)
VALUES 
('Admin', N'Quản trị hệ thống'),
('Instructor', N'Giảng viên'),
('Student', N'Học viên');

-- Permissions
INSERT INTO Permissions(permission_name)
VALUES 
('create_course'), ('edit_course'), ('delete_course'),
('enroll_course'), ('view_course'), ('grade_student');

-- Gán quyền cho Role
INSERT INTO Role_Permissions(role_id, permission_id)
VALUES 
(1, 1), (1, 2), (1, 3),  -- Admin có quyền tạo, sửa, xoá khóa học
(2, 1), (2, 2), (2, 6),  -- Instructor có thể tạo, sửa khóa học và chấm điểm
(3, 4), (3, 5);          -- Student có quyền đăng ký và xem khóa học

-- Users (mật khẩu giả lập, hash ở đây là chỉ để mẫu)
INSERT INTO Users(email, password_hash, full_name, role_id, phonenumber)
VALUES 
('admin@example.com', '$2a$10$Y0L8WUUWemHJhCJUG4J17uoUrcePpEA8RgMs1bOgFhNauW5HK5qUe', N'Nguyễn Quản Trị', 1, '0123456789'),
('gv1@example.com', '$2a$10$Y0L8WUUWemHJhCJUG4J17uoUrcePpEA8RgMs1bOgFhNauW5HK5qUe', N'Trần Văn A', 2, '0912345678'),
('gv2@example.com', '$2a$10$Y0L8WUUWemHJhCJUG4J17uoUrcePpEA8RgMs1bOgFhNauW5HK5qUe', N'Lê Thị B', 2, '0987654321'),
('sv1@example.com', '$2a$10$Y0L8WUUWemHJhCJUG4J17uoUrcePpEA8RgMs1bOgFhNauW5HK5qUe', N'Phạm Học Viên 1', 3, '0901122334'),
('sv2@example.com', '$2a$10$Y0L8WUUWemHJhCJUG4J17uoUrcePpEA8RgMs1bOgFhNauW5HK5qUe', N'Ngô Học Viên 2', 3, '0902233445');

-- Instructors
INSERT INTO Instructors(instructor_id, specialty, degree, years_of_experience)
VALUES 
(2, N'Lập trình Java', N'Thạc sĩ', 5),
(3, N'Thiết kế Web', N'Kỹ sư', 3);

-- Students
INSERT INTO Students(student_id, class, enrollment_year, gpa)
VALUES 
(4, 'IT01', 2022, 3.5),
(5, 'IT02', 2023, 3.8);

-- Categories
INSERT INTO Categories(category_name, description)
VALUES 
(N'Lập trình', N'Các khóa học về lập trình'),
(N'Thiết kế', N'Các khóa học về thiết kế');

-- Courses
INSERT INTO Courses(course_name, description, instructor_id, category_id, start_date, fee, end_date)
VALUES
(N'Java Cơ Bản', N'Học Java từ đầu', 2, 1, '2025-05-01', 1000000, '2025-08-01'),
(N'Thiết kế Web', N'HTML/CSS/JS cơ bản', 3, 2, '2025-05-10', 1200000, '2025-08-10');

-- Course_Instructors
INSERT INTO Course_Instructors(course_id, user_id)
VALUES
(1, 2), (2, 3);

-- Enrollments
INSERT INTO Enrollments(user_id, course_id, payment_status)
VALUES
(4, 1, 'PAID'),
(5, 2, 'UNPAID');

-- Payments
INSERT INTO Payments(enrollments_id, amount, method, status, payment_date)
VALUES
(1, 1000000, 'VNPay', 'Success', GETDATE());

-- Cart
INSERT INTO Cart(user_id, course_id, quantity, status)
VALUES
(5, 1, 1, 'PENDING');

-- Logs
INSERT INTO Logs(user_id, action)
VALUES
(1, N'Thêm khóa học mới: Java Cơ Bản'),
(4, N'Đăng ký khóa học Java Cơ Bản'),
(5, N'Thêm khóa học vào giỏ hàng: Java Cơ Bản');
select * from Cart
