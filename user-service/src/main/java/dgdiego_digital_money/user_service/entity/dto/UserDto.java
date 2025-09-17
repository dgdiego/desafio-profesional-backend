package dgdiego_digital_money.user_service.entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long Id;
    private String name;
    private String lastname;
    private String email;
    private String dni;
    private String phone;
    private String alias;
    private String cvu;
    private String password;

    private List<RolDto> roles;
}
