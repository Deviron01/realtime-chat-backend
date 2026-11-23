package com.deviron.RealTimeChat.DTO.Message;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageReceiptRequestDTO {

    @NotNull(message = "Message ID is required")
    private Long messageId;
}