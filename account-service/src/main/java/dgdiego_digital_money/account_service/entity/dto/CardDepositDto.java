package dgdiego_digital_money.account_service.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDepositDto {

    @NotNull
    private Long cardId;

    @NotNull
    private Double amount;

    @NotNull
    private String detail;





}
