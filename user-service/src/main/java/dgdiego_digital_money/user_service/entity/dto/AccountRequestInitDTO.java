package dgdiego_digital_money.user_service.entity.dto;

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
