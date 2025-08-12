package jasionowicz.warhammer.builder.LoginUser;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jasionowicz.warhammer.builder.Config.JwtUtil;
import jasionowicz.warhammer.builder.LoginUser.model.LoginRequest;
import jasionowicz.warhammer.builder.LoginUser.model.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginUserController {

    private LoginUserService loginUserService;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private RegisterUserService registerUserService;

    public LoginUserController(LoginUserService loginUserService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, RegisterUserService registerUserService) {
        this.loginUserService = loginUserService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.registerUserService = registerUserService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtil.generateToken(request.getUsername());

            ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(false)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(86400)
                    .build();

            response.setHeader("Set-Cookie", cookie.toString());

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Błędny login lub hasło");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(authentication.getName());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        registerUserService.makeNewUser(request);
        return ResponseEntity.ok().build();
    }

}
