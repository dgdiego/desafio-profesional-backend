package dgdiego_digital_money.user_service.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String lastname;

    @NotNull
    @Email
    private String email;

    @Schema(
            description = "Contrseña del usuario",
            type = "string",
            example = "MiUsuario123"
    )
    private String password;

    @NotNull
    private String dni;

    @NotNull
    private String phone;
}
