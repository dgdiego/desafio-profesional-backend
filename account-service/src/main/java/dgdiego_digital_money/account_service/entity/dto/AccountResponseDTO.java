package dgdiego_digital_money.account_service.entity.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponseDTO {

    private Long id;
    private Long userId;
    private Double balance;
    private String alias;
    private String cvu;
}
