package dgdiego_digital_money.account_service.entity.dto;

import dgdiego_digital_money.account_service.entity.domian.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private Long id;
    private Double amount;
    private LocalDateTime dateTime;
    private TransactionType type;
    private Long accountId;
}
