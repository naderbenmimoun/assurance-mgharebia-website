package com.example.Assurance.Controlleur;


import com.example.Assurance.Entity.User;
import com.example.Assurance.Service.JwtUtil;
import com.example.Assurance.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private JwtUtil jwtUtil;    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User savedUser = userService.addUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = Optional.ofNullable(userService.getUserById(id));
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }/*
    @RequestMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String token) {
        System.out.println("Token reçu : " + token);

        String username = jwtUtil.extractUsername(token.substring(7)); // Suppression du "Bearer "
        User userProfile = userService.getUserProfile(username);
        return ResponseEntity.ok(userProfile);
    }*/
  @RequestMapping("/profile")
  public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String token) {
    try {
      // Assuming you have a JWT utility class to extract the username from the token
      String username = jwtUtil.extractUsername(token.substring(7)); // Remove 'Bearer ' prefix
      User userProfile = userService.getUserProfile(username);  // Retrieve the user from the database
      return ResponseEntity.ok(userProfile);
    } catch (Exception e) {
      // Handle any errors (e.g., invalid token)
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }


}
