package com.deviron.RealTimeChat.Controller;

import com.deviron.RealTimeChat.DTO.Auth.AuthResponseDTO;
import com.deviron.RealTimeChat.DTO.Auth.LoginRequestDTO;
import com.deviron.RealTimeChat.DTO.Auth.RegisterRequestDTO;
import com.deviron.RealTimeChat.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }
    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request){
        AuthResponseDTO response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    //Login an existing user
    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request
            ){
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
