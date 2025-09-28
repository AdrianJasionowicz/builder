package jasionowicz.warhammer.builder.ChatUser;

import jakarta.persistence.*;
import jasionowicz.warhammer.builder.Conversation.Conversation;
import jasionowicz.warhammer.builder.LoginUser.LoginUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "chat_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nickname")
})
public class ChatUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nickname;
    @OneToOne
    @JoinColumn(name = "login_user_id", referencedColumnName = "id", unique = true)
    private LoginUser loginUser;
    @ManyToMany(mappedBy = "participants")
    private List<Conversation> conversations = new ArrayList<>();




}
