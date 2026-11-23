
package com.deviron.RealTimeChat.Controller;

import com.deviron.RealTimeChat.DTO.Message.MessageReceiptRequestDTO;
import com.deviron.RealTimeChat.DTO.Message.MessageReceiptResponseDTO;
import com.deviron.RealTimeChat.DTO.Message.MessageResponseDTO;
import com.deviron.RealTimeChat.DTO.Message.SendMessageDTO;
import com.deviron.RealTimeChat.Service.MessageService;

import jakarta.validation.Valid;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(
            MessageService messageService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(
            @Valid @Payload SendMessageDTO request,
            Principal principal
    ) {

        // Identify the authenticated sender
        String senderEmail = principal.getName();

        // Save the message
        MessageResponseDTO response =
                messageService.sendMessage(senderEmail, request);

        // Broadcast to everyone subscribed to this conversation
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + response.getConversationId(),
                response
        );
    }
    @MessageMapping("/chat.delivered")
    public void markAsDelivered(
            @Valid @Payload MessageReceiptRequestDTO request,
            Principal principal) {

        MessageReceiptResponseDTO response =
                messageService.markMessageAsDelivered(
                        principal.getName(),
                        request.getMessageId()
                );

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + response.getConversationId(),
                response
        );
    }
    @MessageMapping("/chat.read")
    public void markAsRead(
            @Valid @Payload MessageReceiptRequestDTO request,
            Principal principal) {

        MessageReceiptResponseDTO response =
                messageService.markMessageAsRead(
                        principal.getName(),
                        request.getMessageId()
                );

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + response.getConversationId(),
                response
        );
    }
}
