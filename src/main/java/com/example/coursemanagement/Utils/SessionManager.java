package com.example.coursemanagement.Utils;

import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Service.CartService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser; // Lưu thông tin người dùng hiện tại
    private final CartService cartService = new CartService();
    private int cartSize = 0;

    public int getCartSize() {
        return cartSize;
    }

    public void setCartSize() {
        if (currentUser != null) {
            this.cartSize = cartService.getCartSize(currentUser.getUserId());
        }
    }

    private final List<Consumer<User>> listeners = new ArrayList<>(); // Danh sách listener

    private SessionManager() {
    } // 🔥 Đảm bảo Singleton bằng cách để constructor private

    // Thêm phương thức để đăng ký listener
    public void addListener(Consumer<User> listener) {
        listeners.add(listener);
    }

    // Thêm phương thức để xóa listener (đề phòng rò rỉ bộ nhớ)
    public void removeListener(Consumer<User> listener) {
        listeners.remove(listener);
    }

    // Khi setUser() được gọi, tất cả listener sẽ nhận thông báo
    public void setUser(User newUser) {
        if (newUser == null) return; // Không cập nhật nếu newUser là null

        boolean isSameUser = (currentUser != null && currentUser.getUserEmail().equals(newUser.getUserEmail()));
        if (isSameUser) return; // Không làm gì nếu user không đổi

        this.currentUser = newUser; // Cập nhật user mới

        // 🔥 Thông báo tất cả listener rằng user đã thay đổi
        for (Consumer<User> listener : new ArrayList<>(listeners)) {
            listener.accept(newUser);
        }
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public User getUser() {
        return currentUser;
    }

    public int getCurrentStudentId() {
        return 1;
    }
}
