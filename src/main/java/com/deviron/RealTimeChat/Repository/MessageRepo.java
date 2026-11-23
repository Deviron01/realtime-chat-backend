package com.deviron.RealTimeChat.Repository;

import com.deviron.RealTimeChat.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findByConversationOrderBySentAtAsc(Long conversationId);
    List<Message> findBySenderId(Long senderId);
    List<Message> findByConversationIdAndDeletedFalseOrderBySentAtAsc(Long conversationId);


}
