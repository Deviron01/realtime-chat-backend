package com.deviron.RealTimeChat.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "message_receipts",
    uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "user_id"})
)
@Setter
@Getter
@NoArgsConstructor
public class MessageReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
}
