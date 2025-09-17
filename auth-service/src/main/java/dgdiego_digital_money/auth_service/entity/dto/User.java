package dgdiego_digital_money.auth_service.entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class User {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String dni;
    private String phone;
    private String alias;
    private String cvu;
    private String password;

    List<Role> roles;
}
