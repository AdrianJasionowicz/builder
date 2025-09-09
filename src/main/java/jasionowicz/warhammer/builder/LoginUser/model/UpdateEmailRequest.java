package jasionowicz.warhammer.builder.LoginUser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmailRequest {
    private String email;
    private String password;
}
