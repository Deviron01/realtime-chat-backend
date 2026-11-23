package com.deviron.RealTimeChat.Controller;

import com.deviron.RealTimeChat.DTO.Conversation.ConversationMemberDTO;
import com.deviron.RealTimeChat.DTO.Conversation.ConversationResponseDTO;
import com.deviron.RealTimeChat.DTO.Conversation.CreateGroupDTO;
import com.deviron.RealTimeChat.DTO.Message.MessageResponseDTO;
import com.deviron.RealTimeChat.Service.ConversationService;
import com.deviron.RealTimeChat.Service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.deviron.RealTimeChat.DTO.Message.MessageResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.Authentication;
import java.util.List;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final MessageService messageService;

    public ConversationController(
            ConversationService conversationService,
            MessageService messageService) {

        this.conversationService = conversationService;
        this.messageService = messageService;
    }

    @PostMapping("/private/{userId}")
    public ResponseEntity<ConversationResponseDTO> createPrivateConversation(
            @PathVariable Long userId,
            Authentication authentication) {

        ConversationResponseDTO conversation =
                conversationService.createPrivateConversation(
                        authentication.getName(),
                        userId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(conversation);
    }
    @PostMapping("/group")
    public ResponseEntity<ConversationResponseDTO> createGroupConversation(
            @Valid @RequestBody CreateGroupDTO request,
            Authentication authentication) {

        ConversationResponseDTO conversation =
                conversationService.createGroupConversation(
                        authentication.getName(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(conversation);
    }
    @GetMapping
    public ResponseEntity<List<ConversationResponseDTO>> getUserConversations(
            Authentication authentication) {

        List<ConversationResponseDTO> conversations =
                conversationService.getUserConversations(
                        authentication.getName()
                );

        return ResponseEntity.ok(conversations);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponseDTO> getConversationDetails(
            @PathVariable Long id,
            Authentication authentication) {

        ConversationResponseDTO conversation =
                conversationService.getConversationDetails(
                        authentication.getName(), id
                );

        return ResponseEntity.ok(conversation);
    }
    @GetMapping("/{id}/members")
    public ResponseEntity<List<ConversationMemberDTO>> getConversationMembers(
            @PathVariable Long id,
            Authentication authentication) {

        List<ConversationMemberDTO> members =
                conversationService.getConversationMembers(
                        authentication.getName(), id
                );

        return ResponseEntity.ok(members);
    }
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageResponseDTO>> getConversationMessages(
            Authentication authentication,
            @PathVariable Long id) {

        List<MessageResponseDTO> messages =
                messageService.getConversationMessages(
                        authentication.getName(),
                        id
                );

        return ResponseEntity.ok(messages);
    }
}