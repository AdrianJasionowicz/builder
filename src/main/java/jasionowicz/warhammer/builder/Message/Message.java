package jasionowicz.warhammer.builder.Message;

import jakarta.persistence.*;
import jasionowicz.warhammer.builder.ChatUser.ChatUser;
import jasionowicz.warhammer.builder.Conversation.Conversation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime timestamp;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private ChatUser author;
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;


}
