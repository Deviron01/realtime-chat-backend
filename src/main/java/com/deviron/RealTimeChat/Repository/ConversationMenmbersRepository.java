package com.deviron.RealTimeChat.Repository;

import com.deviron.RealTimeChat.Entity.ConversationMembers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationMenmbersRepository extends JpaRepository<ConversationMembers, Long> {
    List<ConversationMembers> findByConversationId(Long conversationId);
    List<ConversationMembers> findByUserId(Long userId);
    Optional<ConversationMembers> findByConversationIdAndUserId(Long conversationId, Long userId);
    boolean existsByConversationIdAndUserId(Long conversationId, Long userId);
}
