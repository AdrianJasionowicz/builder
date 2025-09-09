package jasionowicz.warhammer.builder.LoginUser.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {
    private String password;
    private String newPassword;

}
