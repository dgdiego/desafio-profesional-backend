package dgdiego_digital_money.account_service.controller;

import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.dto.TransactionDto;
import dgdiego_digital_money.account_service.service.implementation.AccountService;
import dgdiego_digital_money.account_service.service.implementation.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/accounts/{accountId}/transactions")
@Tag(name = "Transacciones", description = "Operaciones relacionadas con administración de transacciones")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    @Operation(summary = "Transacciones Dashboard", description = "Obtener las últimas 5 transacciones de la cuenta")
    @Parameter(
            name = "Authorization",
            in = ParameterIn.HEADER,
            required = true,
            description = "JWT Bearer token",
            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
    )
    public ResponseEntity<List<TransactionDto>> transactionsDashboard(@PathVariable Long accountId) {
        List<Transaction> list = transactionService.transactionsDashboard(accountId);
        List<TransactionDto> listDto = new ArrayList<>();

        for(Transaction transaction : list){
            listDto.add(transactionService.mapToResponseDto(transaction));
        }

        return ResponseEntity.ok(listDto);
    }

    @GetMapping("/activity")
    @Operation(summary = "Actividad de transacciones", description = "Buscar todas las transacciones de la cuenta")
    @Parameter(
            name = "Authorization",
            in = ParameterIn.HEADER,
            required = true,
            description = "JWT Bearer token",
            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
    )
    public ResponseEntity<List<TransactionDto>> listAllByAccount(@PathVariable Long accountId) {
        List<Transaction> list = transactionService.listAllByAccount(accountId);
        List<TransactionDto> listDto = new ArrayList<>();

        for(Transaction transaction : list){
            listDto.add(transactionService.mapToResponseDto(transaction));
        }

        return ResponseEntity.ok(listDto);
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Detalle transacción", description = "Obtener el detalle de una transacción a través de su ID")
    @Parameter(
            name = "Authorization",
            in = ParameterIn.HEADER,
            required = true,
            description = "JWT Bearer token",
            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
    )
    public ResponseEntity<TransactionDto> get(@PathVariable Long accountId, @PathVariable Long transactionId, @RequestHeader("X-Gateway-Auth") String gatewayHeader) {
        log.info(gatewayHeader);
        return ResponseEntity.ok(transactionService.mapToResponseDto(transactionService.getById(accountId,transactionId)));
    }
}
