package dgdiego_digital_money.account_service.controller;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.dto.AccountResponseDTO;
import dgdiego_digital_money.account_service.entity.dto.CardDepositDto;
import dgdiego_digital_money.account_service.entity.dto.TransactionDto;
import dgdiego_digital_money.account_service.entity.dto.TransferenceCreateDto;
import dgdiego_digital_money.account_service.service.implementation.AccountService;
import dgdiego_digital_money.account_service.service.implementation.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/accounts/{accountId}")
@Tag(name = "Transferencias", description = "Operaciones relacionadas con administración de transferencias y depósitos")
public class TransferenceController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/deposits")
    @Operation(summary = "Depósito con tarjeta", description = "Acreditar dinero a la billetera desde una tarjeta")
    @Parameter(
            name = "Authorization",
            in = ParameterIn.HEADER,
            required = true,
            description = "JWT Bearer token",
            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
    )
    public ResponseEntity<?> saveDeposit(@PathVariable Long accountId, @Valid @RequestBody CardDepositDto requestDto) {
        transactionService.createDepositWithCard(accountId,requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transferences")
    @Operation(summary = "Transferencia entre cuentas", description = "Transferir dinero desde mi billetera a otra cuenta de la aplicación")
    @Parameter(
            name = "Authorization",
            in = ParameterIn.HEADER,
            required = true,
            description = "JWT Bearer token",
            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
    )
    public ResponseEntity<?> saveTransference(@PathVariable Long accountId, @Valid @RequestBody TransferenceCreateDto requestDto) {
        transactionService.createTransference(accountId,requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/transferences")
    @Operation(summary = "Últimos destinatarios", description = "Obtener los últimos destinatarios a los que se les transfirió dinero")
    @Parameter(
            name = "Authorization",
            in = ParameterIn.HEADER,
            required = true,
            description = "JWT Bearer token",
            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
    )
    public ResponseEntity<?> findLastRecipients(@PathVariable Long accountId) {
        List<Account> list = transactionService.getLastRecipientsByAccount(accountId);
        List<AccountResponseDTO> listDto = new ArrayList<>();

        for(Account account : list){
            account.setBalance(null);
            listDto.add(accountService.mapToResponseDto(account));
        }

        return ResponseEntity.ok(listDto);
    }
}
