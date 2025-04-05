package com.example.coursemanagement.Service;

import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Respository.UserRespository;

public class UserService {
    private final UserRespository userRepository = new UserRespository(); // Tạo repository
    public boolean isValidPassword(String email,String password){
        if(userRepository.loginUser(email,password) != null){
            return true;
        }
        return false;
    }
    public User getUserProfile(String email){
        return  userRepository.getUserByEmail(email);
    }
    public boolean modifierUser(User user){
       return userRepository.updateInforUser(user);
    }

}
