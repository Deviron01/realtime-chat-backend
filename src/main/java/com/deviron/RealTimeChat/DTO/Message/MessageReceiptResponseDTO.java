package com.deviron.RealTimeChat.DTO.Message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageReceiptResponseDTO {

    private Long messageId;
    private Long conversationId;
    private Long userId;
    private String username;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
}