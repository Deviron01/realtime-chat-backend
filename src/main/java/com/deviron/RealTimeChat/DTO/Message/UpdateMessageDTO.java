package com.deviron.RealTimeChat.DTO.Message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMessageDTO {

    @NotBlank(message = "Message content cannot be empty")
    @Size(max = 5000, message = "Message cannot exceed 5000 characters")
    private String content;
}