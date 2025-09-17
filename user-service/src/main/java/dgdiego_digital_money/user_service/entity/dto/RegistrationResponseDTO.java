package dgdiego_digital_money.user_service.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponseDTO {

    private Long Id;
    private String name;
    private String lastname;
    private String email;
    private String dni;
    private String phone;
    private String alias;
    private String cvu;
}
