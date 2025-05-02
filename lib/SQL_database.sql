-- 1. Tạo Database
CREATE DATABASE IT_Course_Management;
GO
USE IT_Course_Management;
GO

-- 2. Roles
CREATE TABLE Roles (
    role_id INT PRIMARY KEY IDENTITY(1,1),
    role_name NVARCHAR(50) NOT NULL UNIQUE,
    description NVARCHAR(MAX)
);

-- 3. Permissions
CREATE TABLE Permissions (
    permission_id INT PRIMARY KEY IDENTITY(1,1),
    permission_name NVARCHAR(100) NOT NULL UNIQUE
);

-- 4. Role_Permissions
CREATE TABLE Role_Permissions (
    role_id INT,
    permission_id INT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES Roles(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES Permissions(permission_id) ON DELETE CASCADE
);

-- 5. Users
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

-- 6. User_Permissions
CREATE TABLE User_Permissions (
    user_id INT,
    permission_id INT,
    PRIMARY KEY (user_id, permission_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES Permissions(permission_id) ON DELETE CASCADE
);

-- 7. Instructors
CREATE TABLE Instructors (
    instructor_id INT PRIMARY KEY,
    specialty NVARCHAR(100),
    degree NVARCHAR(100),
    years_of_experience INT DEFAULT 0 CHECK(years_of_experience >= 0),
    FOREIGN KEY (instructor_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- 8. Students
CREATE TABLE Students (
    student_id INT PRIMARY KEY,
    class NVARCHAR(50),
    enrollment_year INT DEFAULT 0 CHECK(enrollment_year >= 0),
    gpa FLOAT DEFAULT 0 CHECK (gpa >= 0 AND gpa <= 4),
    FOREIGN KEY (student_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- 9. Categories
CREATE TABLE Categories (
    category_id INT PRIMARY KEY IDENTITY(1,1),
    category_name NVARCHAR(100) UNIQUE NOT NULL,
    description NVARCHAR(MAX)
);

-- 10. Courses
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

-- 11. Course_Instructors (nhiều giảng viên dạy 1 khóa)
CREATE TABLE Course_Instructors (
    id INT PRIMARY KEY IDENTITY(1,1),
    course_id INT,
    user_id INT,
    FOREIGN KEY (course_id) REFERENCES Courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    UNIQUE (course_id, user_id)
);

-- 12. Cart
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

-- 13. Enrollments
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

-- 14. Orders (MỚI)
CREATE TABLE Orders (
    order_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    status NVARCHAR(10) CHECK (status IN ('Pending', 'Paid', 'Failed')) DEFAULT 'Pending',
    created_at DATETIME DEFAULT GETDATE(),
    payment_method NVARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- 15. Order_Details (MỚI)
CREATE TABLE Order_Details (
    order_detail_id INT PRIMARY KEY IDENTITY(1,1),
    order_id INT NOT NULL,
    enrollments_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (enrollments_id) REFERENCES Enrollments(enrollments_id) ON DELETE NO ACTION
);

-- 16. Payments (CHỈNH SỬA)
CREATE TABLE Payments (
    payments_id INT PRIMARY KEY IDENTITY(1,1),
    order_id INT UNIQUE,
    amount DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
    method NVARCHAR(50) CHECK (method IN ('VNPay', 'Momo', 'Bank Transfer', 'Cash')) NOT NULL,
    status NVARCHAR(10) CHECK (status IN ('Success', 'Pending', 'Failed')) NOT NULL DEFAULT 'Pending',
    payment_date DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE
);
select * from Enrollments

--17. bảng Assignments
CREATE TABLE Assignments (
    assignment_id INT PRIMARY KEY IDENTITY(1,1),
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    file_path NVARCHAR(500), -- đường dẫn file bài tập
	file_name NVARCHAR(200),
    course_id INT NOT NULL,
    teacher_id INT NULL, -- sửa thành NULLABLE
    due_date DATETIME,
	completed BIT DEFAULT 0,  -- 0 là chưa hoàn thành, 1 là đã hoàn thành
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (course_id) REFERENCES Courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES Users(user_id) ON DELETE SET NULL
);


--18 bảng Submissions 
CREATE TABLE Submissions (
    submission_id INT PRIMARY KEY IDENTITY(1,1),
    assignment_id INT NOT NULL,
    student_id INT NOT NULL, -- user_id của học viên
    file_path NVARCHAR(500), -- đường dẫn file học viên nộp
    submitted_at DATETIME DEFAULT GETDATE(),
    grade FLOAT CHECK (grade >= 0 AND grade <= 10), -- có thể NULL nếu chưa chấm
    feedback NVARCHAR(MAX), -- nhận xét của giảng viên
    FOREIGN KEY (assignment_id) REFERENCES Assignments(assignment_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    UNIQUE (assignment_id, student_id) -- 1 học viên chỉ nộp 1 lần/1 bài
);

-- 17. Logs
CREATE TABLE Logs (
    log_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT,
    action NVARCHAR(255),
    action_time DATETIME2 DEFAULT SYSDATETIME(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL
);


-- insert từng Procedure
-- Stored Procedure để lấy danh sách khóa học có phân trang
CREATE PROCEDURE GetPaginatedCourses
    @PageNumber INT = 1,            -- Số trang hiện tại
    @PageSize INT = 8,              -- Số lượng mục trên mỗi trang
    @TotalRecords INT OUTPUT        -- Tổng số bản ghi
AS
BEGIN
    SET NOCOUNT ON;

    -- Tính tổng số bản ghi
    SELECT @TotalRecords = COUNT(*)
    FROM Courses
    WHERE is_deleted = 0;

    -- Lấy danh sách khóa học theo trang
    SELECT 
                    c.course_id, 
                    c.course_name, 
                    c.description AS course_description, 
                    c.fee,
                    c.start_date, 
                    c.end_date,
                    c.create_date AS course_create_date,
                    c.course_thumbnail, 
                    c.is_deleted, 

                -- Instructor Info
                    i.specialty,
                    i.degree, 
                    i.years_of_experience, 

                -- User Info (người giảng dạy)
                    u.user_id, 
                    u.full_name AS instructor_name,
                    u.email AS instructor_email,
                    u.role_id, 
                    u.phonenumber, 
                    u.create_date, 

                -- Category Info
                    cat.category_id, 
                    cat.category_name,
                    cat.description AS category_description -- // ← Không có dấu phẩy ở cuối dòng này

    FROM Courses c
    LEFT JOIN Instructors i ON c.instructor_id = i.instructor_id
    LEFT JOIN Users u ON i.instructor_id = u.user_id
    LEFT JOIN Categories cat ON c.category_id = cat.category_id
    WHERE c.is_deleted = 0
    ORDER BY c.create_date DESC
    OFFSET (@PageNumber - 1) * @PageSize ROWS
    FETCH NEXT @PageSize ROWS ONLY;
END


-- Stored Procedure để tìm kiếm khóa học có phân trang
CREATE PROCEDURE SearchPaginatedCourses
    @SearchQuery NVARCHAR(100),     -- Từ khóa tìm kiếm
    @PageNumber INT = 1,            -- Số trang hiện tại
    @PageSize INT = 8,              -- Số lượng mục trên mỗi trang
    @TotalRecords INT OUTPUT        -- Tổng số bản ghi
AS
BEGIN
    SET NOCOUNT ON;

    -- Tính tổng số bản ghi phù hợp với từ khóa tìm kiếm
    SELECT @TotalRecords = COUNT(*)
    FROM Courses c
    WHERE c.is_deleted = 0 AND 
          (c.course_name LIKE '%' + @SearchQuery + '%' OR c.description LIKE '%' + @SearchQuery + '%');

    -- Lấy danh sách khóa học theo trang và từ khóa
    SELECT 
                    c.course_id, 
                    c.course_name, 
                    c.description AS course_description, 
                    c.fee,
                    c.start_date, 
                    c.end_date,
                    c.create_date AS course_create_date,
                    c.course_thumbnail, 
                    c.is_deleted, 

                -- Instructor Info
                    i.specialty,
                    i.degree, 
                    i.years_of_experience, 

                -- User Info (người giảng dạy)
                    u.user_id, 
                    u.full_name AS instructor_name,
                    u.email AS instructor_email,
                    u.role_id, 
                    u.phonenumber, 
                    u.create_date, 

                -- Category Info
                    cat.category_id, 
                    cat.category_name,
                    cat.description AS category_description -- // ← Không có dấu phẩy ở cuối dòng này
    FROM Courses c
    LEFT JOIN Instructors i ON c.instructor_id = i.instructor_id
    LEFT JOIN Users u ON i.instructor_id = u.user_id
    LEFT JOIN Categories cat ON c.category_id = cat.category_id
    WHERE c.is_deleted = 0 AND 
          (c.course_name LIKE '%' + @SearchQuery + '%' OR c.description LIKE '%' + @SearchQuery + '%')
    ORDER BY c.create_date DESC
    OFFSET (@PageNumber - 1) * @PageSize ROWS
    FETCH NEXT @PageSize ROWS ONLY;
END


-- Stored Procedure để lọc khóa học theo danh mục có phân trang
CREATE PROCEDURE FilterPaginatedCoursesByCategory
    @CategoryId INT,                -- ID danh mục
    @PageNumber INT = 1,            -- Số trang hiện tại
    @PageSize INT = 8,              -- Số lượng mục trên mỗi trang
    @TotalRecords INT OUTPUT        -- Tổng số bản ghi
AS
BEGIN
    SET NOCOUNT ON;

    -- Tính tổng số bản ghi theo danh mục
    SELECT @TotalRecords = COUNT(*)
    FROM Courses c
    WHERE c.is_deleted = 0 AND c.category_id = @CategoryId;

    -- Lấy danh sách khóa học theo trang và danh mục
    SELECT 
                    c.course_id, 
                    c.course_name, 
                    c.description AS course_description, 
                    c.fee,
                    c.start_date, 
                    c.end_date,
                    c.create_date AS course_create_date,
                    c.course_thumbnail, 
                    c.is_deleted, 

                -- Instructor Info
                    i.specialty,
                    i.degree, 
                    i.years_of_experience, 

                -- User Info (người giảng dạy)
                    u.user_id, 
                    u.full_name AS instructor_name,
                    u.email AS instructor_email,
                    u.role_id, 
                    u.phonenumber, 
                    u.create_date, 

                -- Category Info
                    cat.category_id, 
                    cat.category_name,
                    cat.description AS category_description -- // ← Không có dấu phẩy ở cuối dòng này
    FROM Courses c
    LEFT JOIN Instructors i ON c.instructor_id = i.instructor_id
    LEFT JOIN Users u ON i.instructor_id = u.user_id
    LEFT JOIN Categories cat ON c.category_id = cat.category_id
    WHERE c.is_deleted = 0 AND c.category_id = @CategoryId
    ORDER BY c.create_date DESC
    OFFSET (@PageNumber - 1) * @PageSize ROWS
    FETCH NEXT @PageSize ROWS ONLY;
END


-- Xóa các bảng có khóa ngoại trước
-- Xóa các bảng cũ nếu tồn tại (đưa lên đầu)
DROP TABLE IF EXISTS Logs;
DROP TABLE IF EXISTS Submissions;
DROP TABLE IF EXISTS Assignments;
DROP TABLE IF EXISTS Payments;
DROP TABLE IF EXISTS Order_Details;
DROP TABLE IF EXISTS Orders;
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

select * from Users
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


SELECT FORMAT(p.payment_date, 'yyyy-MM-dd') AS month, SUM(p.amount) AS revenue 
                FROM Payments p 
                WHERE p.status = 'Success' 
                GROUP BY FORMAT(p.payment_date, 'yyyy-MM-dd') 
                ORDER BY month ASC
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
-- Thêm danh mục mở rộng cho bảng Categories
INSERT INTO Categories(category_name, description)
VALUES 
(N'Lập trình', N'Các khóa học về lập trình'),
(N'Thiết kế', N'Các khóa học về thiết kế'),
(N'Trí tuệ nhân tạo', N'Khóa học về AI, Machine Learning, Deep Learning'),
(N'Phân tích dữ liệu', N'Học xử lý và trực quan hóa dữ liệu'),
(N'Quản trị mạng', N'Các khóa học về bảo mật và mạng máy tính'),
(N'Kinh doanh', N'Khóa học kỹ năng kinh doanh, khởi nghiệp, marketing'),
(N'Ngoại ngữ', N'Học tiếng Anh, Nhật, Hàn, Trung...'),
(N'Tin học văn phòng', N'Học Word, Excel, PowerPoint chuyên sâu'),
(N'Phát triển web', N'HTML, CSS, JavaScript, React, Node.js,...'),
(N'Cơ sở dữ liệu', N'MySQL, SQL Server, MongoDB,...'),
(N'Điện tử - Viễn thông', N'Khóa học về mạch điện, truyền dẫn tín hiệu'),
(N'Toán - Lý - Hóa', N'Bồi dưỡng kiến thức phổ thông và nâng cao');

INSERT INTO Courses (course_name, description, instructor_id, category_id, start_date, end_date, fee, course_thumbnail, is_deleted)
VALUES 
(N'Java core', N'Khóa học Java core cơ bản cho người mới', 2, 10, '2025-05-02', '2025-05-31', 150000.00, 'Images/java-core.jpg', 0),
(N'HTML và CSS', N'Khóa học HTML và CSS', 2, 9, '2025-05-02', '2025-05-31', 200000.00, 'Images/html-css.jpg', 0),
(N'ReactJs', N'Khóa học Reactjs và phát triển web', 3, 9, '2025-05-02', '2025-05-31', 1000000.00, 'Images/reacte.jpg', 0),
(N'C++', N'Khóa học C++ và Phân tích dữ liệu', 3, 4, '2025-05-02', '2025-05-31', 300000.00, 'Images/c++.jpg', 0),
(N'Pyhon', N'Khóa học Pyhon', 3, 3, '2025-05-02', '2025-05-31', 350000.00, 'Images/python.jpg', 0),
(N'Trí tuệ nhân tạo', N'Khóa học trí tuệ nhân tạo', 2, 3, '2025-05-02', '2025-05-30', 500000.00, 'Images/ai.jpg', 0);

