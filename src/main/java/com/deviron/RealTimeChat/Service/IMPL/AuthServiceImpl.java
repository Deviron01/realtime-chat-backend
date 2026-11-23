package com.deviron.RealTimeChat.Service.IMPL;

import com.deviron.RealTimeChat.DTO.Auth.AuthResponseDTO;
import com.deviron.RealTimeChat.DTO.Auth.LoginRequestDTO;
import com.deviron.RealTimeChat.DTO.Auth.RegisterRequestDTO;
import com.deviron.RealTimeChat.Entity.User;
import com.deviron.RealTimeChat.Enum.UserRole;
import com.deviron.RealTimeChat.Repository.UserRepository;
import com.deviron.RealTimeChat.Security.JwtService;
import com.deviron.RealTimeChat.Service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository =userRepository;

    }
    @Override
    public AuthResponseDTO register(RegisterRequestDTO request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw  new IllegalArgumentException("Email already registered");
        };
        if(userRepository.existsByUsername(request.getUsername())){
            throw new IllegalArgumentException("Username is already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // hash Password
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        // default user
        user.setRole(UserRole.USER);
        User saveUser = userRepository.save(user);

        String token = jwtService.generateToken(saveUser);

        return new AuthResponseDTO(
                token,
                "Bearer",
                saveUser.getId(),
                saveUser.getUsername(),
                saveUser.getEmail()
        );

    }
    @Override
    public AuthResponseDTO login(LoginRequestDTO requst){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requst.getEmail(),
                        requst.getPassword()
                )
        );

        User user =userRepository.findByEmail(requst.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtService.generateToken(user);
        return  new AuthResponseDTO(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }


}
