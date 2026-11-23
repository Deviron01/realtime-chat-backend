package com.deviron.RealTimeChat.DTO.Conversation;

import com.deviron.RealTimeChat.Enum.ConversationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ConversationResponseDTO {

    private Long id;

    private String name;

    private ConversationType type;

    private LocalDateTime createdAt;
}