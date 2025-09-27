package dgdiego_digital_money.account_service.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
