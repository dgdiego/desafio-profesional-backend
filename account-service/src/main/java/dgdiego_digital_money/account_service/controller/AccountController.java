package dgdiego_digital_money.account_service.controller;

import dgdiego_digital_money.account_service.entity.domian.Transaction;
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
    public ResponseEntity<Long> register(@RequestBody Long userId) {
        return  ResponseEntity.ok(accountService.create(userId));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Map<String,Double>> balanceDashboard(@PathVariable Long id) {
        Double balance = accountService.getBalance(id);

        Map<String, Double> response = new HashMap<>();
        response.put("balance", balance);

        return ResponseEntity.ok(response);
    }
}
