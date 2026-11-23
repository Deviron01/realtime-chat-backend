
package com.deviron.RealTimeChat.Service.IMPL;

import com.deviron.RealTimeChat.DTO.Message.*;
import com.deviron.RealTimeChat.Entity.*;
import com.deviron.RealTimeChat.Repository.*;
import com.deviron.RealTimeChat.Service.MessageService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.deviron.RealTimeChat.Repository.MessageReceiptRepo;
import com.deviron.RealTimeChat.Entity.ConversationMembers;

import com.deviron.RealTimeChat.Repository.ConversationMenmbersRepository;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageReceiptRepo messageReceiptRepo;
    private final MessageRepo messageRepository;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationMenmbersRepository memberRepository;

    public MessageServiceImpl(
            MessageRepo messageRepository,
            UserRepository userRepository,
            ConversationRepository conversationRepository,
            ConversationMenmbersRepository memberRepository,
            MessageReceiptRepo messageReceiptRepo
    ) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
        this.memberRepository = memberRepository;
        this.messageReceiptRepo = messageReceiptRepo;
    }

    @Override
    @Transactional
    public MessageResponseDTO sendMessage(
            String senderEmail,
            SendMessageDTO request
    ) {

        // 1. Find the sender
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        // 2. Find the conversation
        Conversation conversation =
                conversationRepository.findById(request.getConversationId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Conversation not found"));

        // 3. Verify conversation membership
        boolean isMember =
                memberRepository.existsByConversationIdAndUserId(
                        conversation.getId(),
                        sender.getId()
                );

        if (!isMember) {
            throw new AccessDeniedException(
                    "You are not a member of this conversation");
        }

        // 4. Create the message
        Message message = new Message();

        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(request.getContent());

        // 5. Save the message
        Message savedMessage = messageRepository.save(message);

// Get all members of this conversation
        List<ConversationMembers> members =
                memberRepository.findByConversationId(conversation.getId());

// Create a receipt for every recipient except the sender
        for (ConversationMembers member : members) {

            if (!member.getUser().getId().equals(sender.getId())) {

                MessageReceipt receipt = new MessageReceipt();

                receipt.setMessage(savedMessage);
                receipt.setUser(member.getUser());

                messageReceiptRepo.save(receipt);
            }
        }

        // 6. Convert saved message into DTO
        return new MessageResponseDTO(
                savedMessage.getId(),
                conversation.getId(),
                sender.getId(),
                sender.getUsername(),
                savedMessage.getContent(),
                savedMessage.getSentAt(),
                savedMessage.getEditedAt(),
                savedMessage.isDeleted()
        );
    }
    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getConversationMessages(
            String userEmail,
            Long conversationId) {

        // Find the requesting user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Check whether the user belongs to this conversation
        boolean isMember =
                memberRepository
                        .existsByConversationIdAndUserId(
                                conversationId,
                                user.getId()
                        );

        if (!isMember) {
            throw new AccessDeniedException(
                    "You are not a member of this conversation"
            );
        }

        // Retrieve messages, excluding deleted messages
        List<Message> messages =
                messageRepository
                        .findByConversationIdAndDeletedFalseOrderBySentAtAsc(
                                conversationId
                        );

        // Convert entities into response DTOs
        return messages.stream()
                .map(message -> new MessageResponseDTO(
                        message.getId(),
                        message.getConversation().getId(),
                        message.getSender().getId(),
                        message.getSender().getUsername(),
                        message.isDeleted()
                                ? "This message was deleted"
                                : message.getContent(),
                        message.getSentAt(),
                        message.getEditedAt(),
                        message.isDeleted()
                ))
                .toList();
    }
    @Override
    @Transactional
    public MessageResponseDTO editMessage(
            String userEmail,
            Long messageId,
            UpdateMessageDTO request) {

        // Find the message
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new RuntimeException("Message not found"));

        // Verify the sender
        if (!message.getSender().getEmail().equals(userEmail)) {
            throw new AccessDeniedException(
                    "You can only edit your own messages");
        }

        // Prevent editing deleted messages
        if (message.isDeleted()) {
            throw new IllegalStateException(
                    "Deleted messages cannot be edited");
        }

        // Update message content
        message.setContent(request.getContent());

        Message updatedMessage = messageRepository.save(message);

        // Return updated message
        return new MessageResponseDTO(
                updatedMessage.getId(),
                updatedMessage.getConversation().getId(),
                updatedMessage.getSender().getId(),
                updatedMessage.getSender().getUsername(),
                updatedMessage.getContent(),
                updatedMessage.getSentAt(),
                updatedMessage.getEditedAt(),
                updatedMessage.isDeleted()
        );
    }
    @Override
    @Transactional
    public MessageResponseDTO deleteMessage(String userEmail, Long messageId) {

        // Find the message
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new RuntimeException("Message not found"));

        // Verify the sender
        if (!message.getSender().getEmail().equals(userEmail)) {
            throw new AccessDeniedException(
                    "You can only delete your own messages");
        }

        // Mark message as deleted
        message.setDeleted(true);
        message.setContent("This message was deleted");

        Message deletedMessage = messageRepository.save(message);

        return new MessageResponseDTO(
                deletedMessage.getId(),
                deletedMessage.getConversation().getId(),
                deletedMessage.getSender().getId(),
                deletedMessage.getSender().getUsername(),
                deletedMessage.getContent(),
                deletedMessage.getSentAt(),
                deletedMessage.getEditedAt(),
                deletedMessage.isDeleted()
        );
    }
    @Override
    @Transactional
    public MessageReceiptResponseDTO markMessageAsDelivered(
            String userEmail,
            Long messageId) {

        User recipient = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new RuntimeException("Message not found"));

        MessageReceipt receipt =
                messageReceiptRepo.findByMessageIdAndUserId(
                        messageId,
                        recipient.getId()
                ).orElseThrow(() ->
                        new AccessDeniedException(
                                "You are not a recipient of this message"));

        if (receipt.getDeliveredAt() == null) {
            receipt.setDeliveredAt(
                    java.time.LocalDateTime.now()
            );
        }

        MessageReceipt savedReceipt =
                messageReceiptRepo.save(receipt);

        return new MessageReceiptResponseDTO(
                message.getId(),
                message.getConversation().getId(),
                recipient.getId(),
                recipient.getUsername(),
                savedReceipt.getDeliveredAt(),
                savedReceipt.getReadAt()
        );
    }
    @Override
    @Transactional
    public MessageReceiptResponseDTO markMessageAsRead(
            String userEmail,
            Long messageId) {

        User recipient = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        MessageReceipt receipt = messageReceiptRepo
                .findByMessageIdAndUserId(messageId, recipient.getId())
                .orElseThrow(() -> new AccessDeniedException(
                        "You are not a recipient of this message"
                ));

        // Reading a message also means it has been delivered.
        if (receipt.getDeliveredAt() == null) {
            receipt.setDeliveredAt(java.time.LocalDateTime.now());
        }

        // Only set the read time once.
        if (receipt.getReadAt() == null) {
            receipt.setReadAt(java.time.LocalDateTime.now());
        }

        MessageReceipt savedReceipt = messageReceiptRepo.save(receipt);

        return new MessageReceiptResponseDTO(
                message.getId(),
                message.getConversation().getId(),
                recipient.getId(),
                recipient.getUsername(),
                savedReceipt.getDeliveredAt(),
                savedReceipt.getReadAt()
        );
    }
}
