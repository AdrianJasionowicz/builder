package jasionowicz.warhammer.builder.LoginUser;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SettingsController {

    private LoginUserService loginUserService;

    public SettingsController(LoginUserService loginUserService) {
        this.loginUserService = loginUserService;
    }

    @PostMapping("/user/setEmail/")
    public ResponseEntity<?> setEmail(@RequestParam  String email,@RequestParam String password, Authentication authentication) {
        loginUserService.setUserEmail(email,password,authentication);

        return ResponseEntity.ok().build();
    }
    @PostMapping("/user/setPassword")
    public ResponseEntity<?> setPassword(@RequestParam String password,@RequestParam String newPassword, Authentication authentication) {
        loginUserService.setUserPassword(password,newPassword,authentication);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/getUserInfo")
    public ResponseEntity<LoginUserPreview> getUserInfo( Authentication authentication) {
       LoginUserPreview loginUserPreview = loginUserService.getUserInfo(authentication);
       return ResponseEntity.ok().body(loginUserPreview);
    }

}
