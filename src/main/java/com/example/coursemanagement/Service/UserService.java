package com.example.coursemanagement.Service;

import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Repository.UserRepository;

public class UserService {

    private static int currentInstructorId; // Lưu ID của giảng viên hiện tại
    private static User currentUser; // Lưu thông tin người dùng hiện tại (Admin, Giảng viên, Học viên)

    // Hàm này gọi khi người dùng đăng nhập (bao gồm Admin, Giảng viên, Học viên)
    public static void setCurrentUser(User user) {
        currentUser = user;

        // Nếu là giảng viên, lưu ID giảng viên
        if (user.getRoleId() == 2) {
            currentInstructorId = user.getUserId();
        }
    }

    // Hàm này sẽ trả về thông tin người dùng hiện tại
    public static User getCurrentUser() {
        return currentUser;
    }

    // Hàm này sẽ trả về ID giảng viên hiện tại
    public static int getCurrentInstructorId() {
        return currentInstructorId;
    }

    private final UserRepository userRepository = new UserRepository(); // Tạo repository

    // Kiểm tra thông tin đăng nhập
    public boolean isValidPassword(String email, String password) {
        if (userRepository.loginUser(email, password) != null) {
            return true;
        }
        return false;
    }

    // Lấy thông tin hồ sơ người dùng
    public User getUserProfile(String email) {
        return userRepository.getUserByEmail(email);
    }

    // Phương thức sửa thông tin người dùng (nếu có)
    public boolean modifierUser(User user) {
        // return userRepository.updateInforUser(user); // Đang tạm thời chưa thực hiện update
        return false;
    }

    // Các phương thức có thể thêm vào nếu cần để kiểm tra quyền truy cập
    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRoleId() == 1;
    }

    public static boolean isInstructor() {
        return currentUser != null && currentUser.getRoleId() == 2;
    }

    public static boolean isStudent() {
        return currentUser != null && currentUser.getRoleId() == 3;
    }
}
