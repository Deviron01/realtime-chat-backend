package com.deviron.RealTimeChat.Service;

import com.deviron.RealTimeChat.DTO.Message.MessageReceiptResponseDTO;
import com.deviron.RealTimeChat.DTO.Message.MessageResponseDTO;
import com.deviron.RealTimeChat.DTO.Message.SendMessageDTO;
import com.deviron.RealTimeChat.DTO.Message.UpdateMessageDTO;

import java.util.List;

public interface MessageService {

    MessageResponseDTO sendMessage(
            String senderEmail,
            SendMessageDTO request
    );
    List<MessageResponseDTO> getConversationMessages(
            String userEmail,
            Long conversationId
    );
    MessageResponseDTO editMessage(
            String userEmail,
            Long messageId,
            UpdateMessageDTO request
    );

    MessageResponseDTO deleteMessage(
            String userEmail,
            Long messageId
    );
    MessageReceiptResponseDTO markMessageAsDelivered(
            String userEmail,
            Long messageId
    );
    MessageReceiptResponseDTO markMessageAsRead(
            String userEmail,
            Long messageId
    );

}