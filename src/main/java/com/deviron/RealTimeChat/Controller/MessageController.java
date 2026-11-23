
package com.deviron.RealTimeChat.Controller;

import com.deviron.RealTimeChat.DTO.Message.MessageResponseDTO;
import com.deviron.RealTimeChat.DTO.Message.UpdateMessageDTO;
import com.deviron.RealTimeChat.Service.MessageService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(
            Authentication authentication,
            @PathVariable Long id) {

        MessageResponseDTO response = messageService.deleteMessage(
                authentication.getName(),
                id
        );

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + response.getConversationId(),
                response
        );

        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> editMessage(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateMessageDTO request) {

        MessageResponseDTO response = messageService.editMessage(
                authentication.getName(),
                id,
                request
        );

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + response.getConversationId(),
                response
        );

        return ResponseEntity.ok(response);
    }
}
