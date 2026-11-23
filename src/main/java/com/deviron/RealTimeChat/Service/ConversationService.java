package com.deviron.RealTimeChat.Service;

import com.deviron.RealTimeChat.DTO.Conversation.ConversationMemberDTO;
import com.deviron.RealTimeChat.DTO.Conversation.ConversationResponseDTO;
import com.deviron.RealTimeChat.DTO.Conversation.CreateGroupDTO;
import com.deviron.RealTimeChat.Entity.Conversation;

import java.util.List;

public interface ConversationService {


    ConversationResponseDTO createPrivateConversation(
            String currentUserEmail,
            Long recipientId
    );
    ConversationResponseDTO createGroupConversation(
            String currentUserEmail,
            CreateGroupDTO request
    );
    List<ConversationResponseDTO> getUserConversations(String email);
    ConversationResponseDTO getConversationDetails(
            String email,
            Long conversationId
    );

    List<ConversationMemberDTO> getConversationMembers(
            String email,
            Long conversationId
    );
}