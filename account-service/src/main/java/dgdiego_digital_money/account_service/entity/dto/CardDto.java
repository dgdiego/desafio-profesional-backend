package dgdiego_digital_money.account_service.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.entity.domian.CardType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDto {

    private Long id;
    private String number;
    private CardType type;
    private LocalDate expirationDate;
    private Long accountId;

}
