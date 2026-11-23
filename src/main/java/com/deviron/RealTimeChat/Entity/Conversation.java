package com.deviron.RealTimeChat.Entity;

import com.deviron.RealTimeChat.Enum.ConversationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "conversations")
@Setter
@Getter
@NoArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationType type;

    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY)

    private List<ConversationMembers> members = new ArrayList<>();
    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
