package com.deviron.RealTimeChat.DTO.Message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageResponseDTO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderUsername;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
    private boolean deleted;
}
