package com.deviron.RealTimeChat.Service;

import com.deviron.RealTimeChat.DTO.Auth.AuthResponseDTO;
import com.deviron.RealTimeChat.DTO.Auth.LoginRequestDTO;
import com.deviron.RealTimeChat.DTO.Auth.RegisterRequestDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO registerRequestDTO);
    AuthResponseDTO login(LoginRequestDTO loginRequestDTO);
}
