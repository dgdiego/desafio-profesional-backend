package dgdiego_digital_money.account_service.entity.dto;

import dgdiego_digital_money.account_service.entity.domian.CardType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferenceCreateDto {

    @NotNull
    private Double amount;

    @NotNull
    private String detail;

    @NotNull
    private String origin;

    @NotNull
    private String destination;



}
