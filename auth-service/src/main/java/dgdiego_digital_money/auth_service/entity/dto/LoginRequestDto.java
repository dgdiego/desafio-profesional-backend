package dgdiego_digital_money.auth_service.entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LoginRequestDto {
    private String email;
    private String password;
}
