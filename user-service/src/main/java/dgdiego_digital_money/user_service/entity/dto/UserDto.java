package dgdiego_digital_money.user_service.entity.dto;

import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long Id;
    private String name;
    private String lastname;
    private String email;
    private String dni;
    private String phone;
    private String password;

    private List<RolDto> roles;
}
