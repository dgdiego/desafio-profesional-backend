package dgdiego_digital_money.account_service.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDTO {

    private Long id;
    @NotNull
    private String alias;
}
