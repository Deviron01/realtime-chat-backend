package com.deviron.RealTimeChat.Repository;

import com.deviron.RealTimeChat.Entity.MessageReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageReceiptRepo extends JpaRepository<MessageReceipt, Long> {
    List<MessageReceipt> findByMessageId(Long messageId);
    List<MessageReceipt> findByUserId(Long userId);
    Optional<MessageReceipt> findByMessageIdAndUserId(Long messageId, Long userId);
    boolean existsByMessageIdAndUserId(Long messageId, Long userId);
}
