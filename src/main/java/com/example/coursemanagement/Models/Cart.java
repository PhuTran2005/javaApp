package com.example.coursemanagement.Models;

import java.time.LocalDateTime;

public class Cart {
    private int cartId;
    private int userId;
    private int courseId;
    private int quantity;
    private LocalDateTime addedAt;
    private String status;

    // Constructor
    public Cart(int cartId, int userId, int courseId, int quantity, LocalDateTime addedAt, String status) {
        this.cartId = cartId;
        this.userId = userId;
        this.courseId = courseId;
        this.quantity = quantity;
        this.addedAt = addedAt;
        this.status = status;
    }

    // Getters and Setters
    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
