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
public class RegistrationRequestDTO {
    @NotNull
    private String name;

    @NotNull
    private String lastname;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Pattern(regexp = "^(?=.*[a-z])(?=.*?[0-9]).{8,}$")
    private String password;

    @NotNull
    private String dni;

    @NotNull
    private String phone;
}
