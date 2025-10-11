package dgdiego_digital_money.account_service.controller;

import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.dto.AccountRequestDTO;
import dgdiego_digital_money.account_service.entity.dto.AccountRequestInitDTO;
import dgdiego_digital_money.account_service.entity.dto.AccountResponseDTO;
import dgdiego_digital_money.account_service.entity.dto.TransactionDto;
import dgdiego_digital_money.account_service.service.implementation.AccountService;
import dgdiego_digital_money.account_service.service.implementation.TransactionService;
import io.swagger.v3.oas.annotations.Hidden;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/accounts")
@Tag(name = "Cuentas", description = "Operaciones relacionadas con administración de cuentas")

public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping(path = "/create")
    @Operation(summary = "Crear cuenta", description = "Crear cuenta en la aplicación")
    @Hidden
    public ResponseEntity<Long> register(@RequestBody AccountRequestInitDTO data) {
        return  ResponseEntity.ok(accountService.create(data));
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Obtener", description = "Obtener datos de la cuenta")
    @Parameter(
            name = "Authorization",
            in = ParameterIn.HEADER,
            required = true,
            description = "JWT Bearer token",
            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
    )
    public ResponseEntity<AccountResponseDTO> balanceDashboard(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.mapToResponseDto(accountService.findById(id)));
    }

    @PatchMapping(path = "/{id}")
    @Operation(summary = "Actualizar", description = "Actualizar el alias de la cuenta")
    @Parameter(
            name = "Authorization",
            in = ParameterIn.HEADER,
            required = true,
            description = "JWT Bearer token",
            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
    )
    public ResponseEntity<AccountResponseDTO> update(@PathVariable Long id, @Valid @RequestBody AccountRequestDTO requestDto) {
        requestDto.setId(id);
        return ResponseEntity.ok(
                accountService.mapToResponseDto(
                        accountService.update(
                                accountService.mapToEntity(requestDto))));
    }
}
