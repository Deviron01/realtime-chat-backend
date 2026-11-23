package com.deviron.RealTimeChat.Service;


import com.deviron.RealTimeChat.DTO.User.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO getCurrentUser(String email);
    List<UserResponseDTO> getAllUsers();
    List<UserResponseDTO> searchUsers(String username);
}
