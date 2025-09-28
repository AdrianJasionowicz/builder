package jasionowicz.warhammer.builder.ChatUser;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {

    public Optional<ChatUser> findById(Long id);
    public ChatUser findByNickname(String nickname);
}
