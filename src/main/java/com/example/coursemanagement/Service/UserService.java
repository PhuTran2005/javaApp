package com.example.coursemanagement.Service;

import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Respository.UserRespository;

public class UserService {
    public User getUserProfile(String email){
        return  UserRespository.getUserByEmail(email);
    }
    public boolean modifierUser(User user){
       return UserRespository.updateUser(user);
    }

}
