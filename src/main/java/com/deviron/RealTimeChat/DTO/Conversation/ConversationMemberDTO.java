package com.deviron.RealTimeChat.DTO.Conversation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConversationMemberDTO {

    private Long id;
    private String username;
    private String email;
}