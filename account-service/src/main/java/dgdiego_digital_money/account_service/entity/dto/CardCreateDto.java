package dgdiego_digital_money.account_service.entity.dto;

import dgdiego_digital_money.account_service.entity.domian.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardCreateDto {

    private String number;
    private CardType type;
    private LocalDate expirationDate;

}
