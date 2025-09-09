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
    private LoginUserRepository loginUserRepository;

    public LoginUserService(LoginUserRepository loginUserRepository) {
        this.loginUserRepository = loginUserRepository;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loginUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }






}
