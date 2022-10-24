package com.example.demo.Controllers;

import com.example.demo.SecurityEvents.Event;
import com.example.demo.SecurityEvents.EventService;
import com.example.demo.securityConfig.pCheck;
import com.example.demo.user.User;
import com.example.demo.user.UserService;
import com.example.demo.user.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
public class UserRestController {

    @Autowired
    UserService userService;

    @Autowired
    EventService eventService;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("api/auth/signup")
    public ResponseEntity Signup(@RequestBody User user){

        String message = "User exist!";

        System.out.println("\n\nREGISTRATION\n");
        System.out.println(user.getName());
        System.out.println(user.getLastname());

        System.out.println(user.getEmail());
        System.out.println(user.getPassword() + "\n********************");


        if(user.valid()) {
            user.setEmail(user.getEmail().toLowerCase());
            if(uniqueEmail(user.getEmail()) && pCheck.isValid(user.getPassword())){
                System.out.println("SAVED!!!\n");
                user.setPassword(encoder.encode(user.getPassword()));
                long i = userService.getCount();
                if(i == 0)
                    user.addRole("ROLE_ADMINISTRATOR");
                else
                    user.addRole("ROLE_USER");
                userService.save(user);
                User savedUser = userService.findByEmail(user.getEmail());
                savedUser.setId(savedUser.getNumber().getNumber());
                userService.save(savedUser);

                Event event = new Event("CREATE_USER", "Anonymous", savedUser.getEmail(), "/api/auth/signup");
                eventService.save(event);

                return new ResponseEntity(savedUser, HttpStatus.OK);
            }
            else if (user.getPassword().length() < 12){
                message = "The password length must be at least 12 chars!";
            }
            else if (!pCheck.isValid(user.getPassword()))
                message = "The password is in the hacker's database!";
        }
        return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/auth/signup", "message", message), HttpStatus.BAD_REQUEST);
    }


    @PostMapping("api/auth/changepass")
    public ResponseEntity ChangePassword(@RequestBody Map<String, String> password, Authentication auth){

        String message;

        User user = userService.findByEmail(auth.getName());

        if (password.get("new_password") == null || password.get("new_password").length() < 12) {
            message = "Password length must be 12 chars minimum!";
            return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/auth/changepass", "message", message), HttpStatus.BAD_REQUEST);
        }
        else if (encoder.matches(password.get("new_password"), user.getPassword())) {
            message = "The passwords must be different!";
            return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/auth/changepass", "message", message), HttpStatus.BAD_REQUEST);
        }
        else if (!pCheck.isValid(password.get("new_password"))) {
            message = "The password is in the hacker's database!";
            return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/auth/changepass", "message", message), HttpStatus.BAD_REQUEST);
        }
        else {
            user.setPassword(encoder.encode(password.get("new_password")));
            userService.save(user);
            Event event = new Event("CHANGE_PASSWORD", user.getEmail(), user.getEmail(), "/api/auth/changepass");
            eventService.save(event);
            return new ResponseEntity(Map.of("email", user.getEmail(), "status", "The password has been updated successfully"), HttpStatus.OK);
        }
    }


    private boolean uniqueEmail(String email) {
        return !userService.exists(email);
    }

}
