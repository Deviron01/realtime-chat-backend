package com.deviron.RealTimeChat.DTO.Auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDTO {
    private String accessToken;
    private String tokenType ;
    private Long userId;
    private String username;
    private String email;
}
