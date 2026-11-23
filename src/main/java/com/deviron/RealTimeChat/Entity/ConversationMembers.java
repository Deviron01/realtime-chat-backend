package com.deviron.RealTimeChat.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "conversation_members",
    uniqueConstraints = @UniqueConstraint(columnNames = {"conversation_id", "user_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class ConversationMembers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    private LocalDateTime joinedAt;

    @PrePersist
    protected void onJoin() {
        joinedAt = LocalDateTime.now();
    }
}
