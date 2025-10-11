package dgdiego_digital_money.account_service.entity.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestInitDTO {

    private Long userId;
    private String alias;
    private String cvu;
}
