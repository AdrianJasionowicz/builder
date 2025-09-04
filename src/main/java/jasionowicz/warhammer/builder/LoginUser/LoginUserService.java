package jasionowicz.warhammer.builder.LoginUser;

import jasionowicz.warhammer.builder.LoginUser.model.LoginRequest;
import jasionowicz.warhammer.builder.LoginUser.model.RegisterRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUserService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private LoginUserRepository loginUserRepository;

    public LoginUserService(LoginUserRepository loginUserRepository, PasswordEncoder passwordEncoder) {
        this.loginUserRepository = loginUserRepository;
        this.passwordEncoder = passwordEncoder;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loginUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public void setUserEmail(String email, String password, Authentication authentication) {
        String username = authentication.getName();
        LoginUser loginUser = loginUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        String encodedPassword = passwordEncoder.encode(password);
        if (encodedPassword.equals(loginUser.getPassword())) {
            loginUser.setEmail(email);
        } else {
            throw new RuntimeException("Wrong password");
        }
    }


    public void setUserPassword(String password, String newPassword, Authentication authentication) {
        String username = authentication.getName();
        LoginUser loginUser = loginUserRepository.findByUsername(username).orElse(null);
        String encodedPassword = passwordEncoder.encode(password);
        if (encodedPassword.equals(loginUser.getPassword())) {
            loginUser.setPassword(newPassword);
        } else {
            throw new RuntimeException("Wrong password");
        }
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

}
