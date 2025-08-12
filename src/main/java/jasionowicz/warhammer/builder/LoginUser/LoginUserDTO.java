package jasionowicz.warhammer.builder.LoginUser;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jasionowicz.warhammer.builder.Army.Army;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LoginUserDTO {


    private Long id;
    private String username;
    private String password;
    private String email;
    private Role role;
    private List<Army> armies = new ArrayList<>();




}
