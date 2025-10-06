package dgdiego_digital_money.account_service.integration;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.domian.TransactionType;
import dgdiego_digital_money.account_service.entity.dto.CardDepositDto;
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
    private PermissionService permissionService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();

        // Crea la cuenta con el método real de AccountService
        Long accountId = accountService.create(1L);
        testAccount = accountService.findById(accountId);

        // Mockea los permisos (no queremos restricciones en tests)
        doNothing().when(permissionService).canAccess(anyLong());
    }

    // -----------------------------
    // ✅ transactionsDashboard()
    // -----------------------------
    @Test
    void testTransactionsDashboard_returnsLatestTransactions() {
        // Crear 6 transacciones (solo deberían devolverse 5)
        for (int i = 0; i < 6; i++) {
            Transaction tx = new Transaction();
            tx.setAccount(testAccount);
            tx.setAmount(100.0 + i);
            tx.setType(TransactionType.DEBIT);
            tx.setDateTime(LocalDateTime.now().minusDays(i));
            transactionRepository.save(tx);
        }

        List<Transaction> result = transactionService.transactionsDashboard(testAccount.getId());

        assertNotNull(result);
        assertEquals(5, result.size(), "Debe devolver solo las 5 últimas transacciones");

        // Verificar que están ordenadas descendentemente por fecha
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getDateTime().isAfter(result.get(i + 1).getDateTime()),
                    "Las transacciones deben estar en orden descendente por fecha");
        }
    }

    // -----------------------------
    // ✅ mapToResponseDto()
    // -----------------------------
    @Test
    void testMapToResponseDto_integration() {
        Transaction tx = new Transaction();
        tx.setAccount(testAccount);
        tx.setAmount(500.0);
        tx.setType(TransactionType.INCOME);
        tx.setDateTime(LocalDateTime.now());
        tx.setDetail("Ingreso por transferencia");
        transactionRepository.save(tx);

        TransactionDto dto = transactionService.mapToResponseDto(tx);

        assertNotNull(dto);
        assertEquals(tx.getId(), dto.getId());
        assertEquals(tx.getType(), dto.getType());
        assertEquals(tx.getAmount(), dto.getAmount());
        assertEquals(testAccount.getId(), dto.getAccountId());
        assertEquals("Ingreso por transferencia", dto.getDetail());
    }

    // -----------------------------
    // ✅ listAllByAccount()
    // -----------------------------
    @Test
    void testListAllByAccount_returnsAll() {
        Transaction tx1 = new Transaction();
        tx1.setAccount(testAccount);
        tx1.setAmount(100.0);
        tx1.setType(TransactionType.INCOME);
        tx1.setDateTime(LocalDateTime.now());
        transactionRepository.save(tx1);

        Transaction tx2 = new Transaction();
        tx2.setAccount(testAccount);
        tx2.setAmount(200.0);
        tx2.setType(TransactionType.DEBIT);
        tx2.setDateTime(LocalDateTime.now().minusHours(1));
        transactionRepository.save(tx2);

        List<Transaction> result = transactionService.listAllByAccount(testAccount.getId());

        assertEquals(2, result.size(), "Debe devolver todas las transacciones asociadas a la cuenta");
        assertTrue(result.get(0).getDateTime().isAfter(result.get(1).getDateTime()));
    }

    // -----------------------------
    // ❌ createDepositWithCard() — cuando el monto es inválido
    // -----------------------------
    @Test
    void testCreateDepositWithCard_invalidAmount_throwsException() {
        CardDepositDto dto = new CardDepositDto();
        dto.setAmount(0.0); // monto inválido

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.createDepositWithCard(testAccount.getId(), dto));
    }
}
