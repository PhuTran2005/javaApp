package com.example.coursemanagement.Service;

import com.example.coursemanagement.Models.Cart;
import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Repository.CartRepository;

import java.util.List;

public class CartService {
    private final CartRepository cartRepository = new CartRepository();

    public boolean addToCart(int userId, int courseId) {
        return cartRepository.addToCart(userId, courseId);
    }

    public boolean deleteCartItem(int cartId) {

        return cartRepository.removeFromCart(cartId);
    }

    public boolean isExistedInCart(int userId, int courseId) {

        return cartRepository.isExistInCart(userId, courseId);
    }

    public List<Cart> getAllCartOfUser(int userId) {
        return cartRepository.getCartByUser(userId);

    }

    public int getCartSize(int userId) {

        return cartRepository.getCartSize(userId);
    }

}
