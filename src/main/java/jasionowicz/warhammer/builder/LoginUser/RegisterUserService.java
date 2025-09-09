package jasionowicz.warhammer.builder.LoginUser;

import jasionowicz.warhammer.builder.LoginUser.model.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserService {

    private final LoginUserRepository loginUserRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserService(LoginUserRepository loginUserRepository, PasswordEncoder passwordEncoder) {
        this.loginUserRepository = loginUserRepository;
        this.passwordEncoder = passwordEncoder;
    }



    public void makeNewUser(RegisterRequest request) {
        LoginUser newUser = new LoginUser();
        newUser.setUsername(request.getUsername());
        String password = passwordEncoder.encode(request.getPassword());
        newUser.setPassword(password);
        newUser.setEmail(request.getEmail());
        newUser.setRole(Role.ROLE_USER);
        loginUserRepository.save(newUser);
    }
}
