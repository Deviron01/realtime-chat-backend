package com.deviron.RealTimeChat.Service.IMPL;

import com.deviron.RealTimeChat.DTO.User.UserResponseDTO;
import com.deviron.RealTimeChat.Entity.User;
import com.deviron.RealTimeChat.Repository.UserRepository;
import com.deviron.RealTimeChat.Service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO getCurrentUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
    @Override
    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getCreatedAt()
                ))
                .toList();
    }
    @Override
    public List<UserResponseDTO> searchUsers(String username) {

        return userRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getCreatedAt()
                ))
                .toList();
    }
}