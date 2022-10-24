package com.example.demo.user;

import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findUserByName(String username);
    User findUserByEmail(String email);
}
