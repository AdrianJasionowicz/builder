package jasionowicz.warhammer.builder.LoginUser;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    private final LoginUserRepository loginUserRepository;
    private final PasswordEncoder passwordEncoder;

    public SettingsService(LoginUserRepository loginUserRepository, PasswordEncoder passwordEncoder) {
        this.loginUserRepository = loginUserRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public LoginUserPreview getUserInfo(Authentication authentication) {
        String username = authentication.getName();
        LoginUser loginUser = loginUserRepository.findByUsername(username).orElse(null);
        LoginUserPreview loginUserPreview = new LoginUserPreview();
        loginUserPreview.setId(loginUser.getId());
        loginUserPreview.setUsername(loginUser.getUsername());
        loginUserPreview.setEmail(loginUser.getEmail());
        return loginUserPreview;
    }

    public void setUserEmail(String email, String password, Authentication authentication) {
        String username = authentication.getName();

        LoginUser loginUser = loginUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (passwordEncoder.matches(password, loginUser.getPassword())) {
            loginUser.setEmail(email);
            loginUserRepository.save(loginUser);
        } else {
            throw new RuntimeException("Wrong password");
        }

    }


    public void setUserPassword(String password, String newPassword, Authentication authentication) {
        String username = authentication.getName();
        LoginUser loginUser = loginUserRepository.findByUsername(username).orElse(null);

        if (passwordEncoder.matches(password, loginUser.getPassword())) {
            loginUser.setPassword(newPassword);
            loginUserRepository.save(loginUser);
        } else {
            throw new RuntimeException("Wrong password");
        }
    }
}
