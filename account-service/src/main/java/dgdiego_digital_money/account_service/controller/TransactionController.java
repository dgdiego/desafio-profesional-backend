package dgdiego_digital_money.account_service.controller;

import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.dto.TransactionDto;
import dgdiego_digital_money.account_service.service.implementation.AccountService;
import dgdiego_digital_money.account_service.service.implementation.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<List<TransactionDto>> transactionsDashboard(@PathVariable Long accountId) {
        List<Transaction> list = transactionService.transactionsDashboard(accountId);
        List<TransactionDto> listDto = new ArrayList<>();

        for(Transaction transaction : list){
            listDto.add(transactionService.mapToResponseDto(transaction));
        }

        return ResponseEntity.ok(listDto);
    }
}
