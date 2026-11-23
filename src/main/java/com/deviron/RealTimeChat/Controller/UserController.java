package com.deviron.RealTimeChat.Controller;

import com.deviron.RealTimeChat.DTO.User.UserResponseDTO;
import com.deviron.RealTimeChat.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(
            Authentication authentication) {

        String email = authentication.getName();

        UserResponseDTO user =
                userService.getCurrentUser(email);

        return ResponseEntity.ok(user);
    }
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        List<UserResponseDTO> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }
    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> searchUsers(
            @RequestParam String username) {

        List<UserResponseDTO> users =
                userService.searchUsers(username);

        return ResponseEntity.ok(users);
    }
}