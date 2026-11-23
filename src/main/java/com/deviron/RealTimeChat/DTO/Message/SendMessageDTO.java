package com.deviron.RealTimeChat.DTO.Message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class SendMessageDTO {
    @NotNull(message = "Conversation ID is required")
    private Long conversationId;
    @NotNull(message = "Sender ID is required")
    @Size(max = 5000, message = "Message content is too long")
    private String content;
}
