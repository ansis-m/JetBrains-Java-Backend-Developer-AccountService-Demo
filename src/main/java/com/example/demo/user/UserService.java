package com.example.demo.user;

import com.example.demo.user.User;

import java.util.List;

public interface UserService {


    List<User> getAll();
    void save(User user);
    User findByEmail(String email);
    void deleteByEmail(String email);
    boolean exists(String email);
    User findUserByUsername(String username);
    void deleteAll();

    long getCount();
}
