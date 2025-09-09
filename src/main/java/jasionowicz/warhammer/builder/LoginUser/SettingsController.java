package jasionowicz.warhammer.builder.LoginUser;

import org.springframework.web.bind.annotation.RequestBody;
import jasionowicz.warhammer.builder.LoginUser.model.UpdateEmailRequest;
import jasionowicz.warhammer.builder.LoginUser.model.UpdatePasswordRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @PostMapping("/user/setEmail")
    public ResponseEntity<?> setEmail(@RequestBody UpdateEmailRequest request, Authentication authentication) {

        settingsService.setUserEmail(request.getEmail(), request.getPassword(), authentication);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/setPassword")
    public ResponseEntity<?> setPassword(@RequestBody UpdatePasswordRequest request, Authentication authentication) {
        settingsService.setUserPassword(request.getPassword(), request.getNewPassword(), authentication);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/getUserInfo")
    public ResponseEntity<LoginUserPreview> getUserInfo(Authentication authentication) {
        LoginUserPreview loginUserPreview = settingsService.getUserInfo(authentication);
        return ResponseEntity.ok().body(loginUserPreview);
    }
}
