package com.deviron.RealTimeChat.Repository;

import com.deviron.RealTimeChat.Entity.Conversation;
import com.deviron.RealTimeChat.Enum.ConversationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByType(ConversationType type);

}
