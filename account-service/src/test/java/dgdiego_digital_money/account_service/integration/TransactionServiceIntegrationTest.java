package dgdiego_digital_money.account_service.integration;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.domian.TransactionType;
import dgdiego_digital_money.account_service.entity.dto.TransactionDto;
import dgdiego_digital_money.account_service.repository.IAccountRepository;
import dgdiego_digital_money.account_service.repository.ITransactionRepository;
import dgdiego_digital_money.account_service.service.implementation.AccountService;
import dgdiego_digital_money.account_service.service.implementation.PermissionService;
import dgdiego_digital_money.account_service.service.implementation.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionServiceIntegrationTest {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ITransactionRepository transactionRepository;

    @Autowired
    private IAccountRepository accountRepository;

    @MockBean
    private PermissionService permissionService; // si es real, puede mockearse con spy

    private Account testAccount;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();

        // Crear la cuenta de prueba usando tu método real de AccountService
        Long accountId = accountService.create(1L);
        testAccount = accountService.findById(accountId); // obtenemos la entidad completa
    }

    @Test
    void testTransactionsDashboard_returnsLatestTransactions() {
        // Crear algunas transacciones
        for (int i = 0; i < 6; i++) {
            Transaction tx = new Transaction();
            tx.setAccount(testAccount);
            tx.setAmount(100.0 + i);
            tx.setType(TransactionType.DEBIT);
            tx.setDateTime(LocalDateTime.now().minusDays(i));
            transactionRepository.save(tx);
        }

        // Podés mockear permisos si querés, por ejemplo con spy
        doNothing().when(permissionService).canAccess(anyLong());

        List<Transaction> result = transactionService.transactionsDashboard(testAccount.getId());

        // Debe devolver solo las últimas 5 por limit
        assertEquals(5, result.size());
        assertTrue(result.get(0).getDateTime().isAfter(result.get(4).getDateTime()));
    }

    @Test
    void testMapToResponseDto_integration() {
        Transaction tx = new Transaction();
        tx.setAccount(testAccount);
        tx.setAmount(500.0);
        tx.setType(TransactionType.INCOME);
        tx.setDateTime(LocalDateTime.now());
        transactionRepository.save(tx);

        TransactionDto dto = transactionService.mapToResponseDto(tx);

        assertNotNull(dto);
        assertEquals(tx.getId(), dto.getId());
        assertEquals(tx.getType(), dto.getType());
        assertEquals(tx.getAmount(), dto.getAmount());
        assertEquals(tx.getAccount().getId(), dto.getAccountId());
    }
}
