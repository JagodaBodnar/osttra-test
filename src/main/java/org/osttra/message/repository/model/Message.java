package org.osttra.message.repository.model;

import jakarta.persistence.*;
import lombok.*;
import org.osttra.user.repository.model.User;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="osttra_message")
public class Message {
    private static final String SEQUENCE = "message_seq";
    @Id
    @GeneratedValue(generator = SEQUENCE, strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = SEQUENCE, sequenceName = SEQUENCE, allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name="recipient", referencedColumnName = "id")
    private User recipient;

    private String sender = "System";

    private boolean read = false;

    private LocalDateTime timestamp;

    public Message(String content, User recipient) {
        this.content = content;
        this.recipient = recipient;
        this.timestamp = LocalDateTime.now();
    }
}
