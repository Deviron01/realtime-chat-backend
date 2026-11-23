package com.deviron.RealTimeChat.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
@Setter
@Getter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    private LocalDateTime sentAt;
    private LocalDateTime editedAt;

    @Column(nullable = false)
    private boolean deleted = false;

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY)
    private List<MessageReceipt> receipts = new ArrayList<>();

    @PrePersist
    protected   void onSend() {
        sentAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onEdit() {
        editedAt = LocalDateTime.now();
    }
}
