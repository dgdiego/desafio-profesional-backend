package dgdiego_digital_money.account_service.integration;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.domian.TransactionType;
import dgdiego_digital_money.account_service.entity.dto.AccountRequestInitDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private Long createTestAccount(Long userId) {
        AccountRequestInitDTO request = new AccountRequestInitDTO();
        request.setUserId(userId);
        request.setAlias("alias." + userId);
        request.setCvu(String.format("%022d", userId));
        return accountService.create(request);
    }

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();

        // Crea la cuenta con el método real de AccountService
        Long accountId = createTestAccount(1L);
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

    @Test
    void testGetLastRecipientsByAccount_inMemory_withoutService() {
        // Crear 6 cuentas destinatarias
        List<Account> recipients = new ArrayList<>();
        for (long userId = 2; userId <= 7; userId++) {
            Long accountId = createTestAccount(userId);
            Account recipient = accountService.findById(accountId);
            recipients.add(recipient);

            // Crear transacciones de testAccount hacia cada destinatario
            Transaction tx = new Transaction();
            tx.setAccount(testAccount);         // cuenta origen
            tx.setRelatedAccount(recipient);    // cuenta destino
            tx.setAmount(100.0 + userId);
            tx.setType(TransactionType.DEBIT);
            tx.setDateTime(LocalDateTime.now().minusDays(7 - userId));
            transactionRepository.save(tx);
        }

        // Traer todas las transacciones de la base
        List<Transaction> allTx = transactionRepository.findAll();

        // Filtrar solo las transacciones de testAccount con relatedAccount no nulo
        Map<Long, LocalDateTime> lastTxPerRecipient = allTx.stream()
                .filter(t -> t.getAccount().getId().equals(testAccount.getId()) && t.getRelatedAccount() != null)
                .collect(Collectors.toMap(
                        t -> t.getRelatedAccount().getId(),
                        Transaction::getDateTime,
                        (d1, d2) -> d1.isAfter(d2) ? d1 : d2 // tomar la fecha más reciente
                ));

        // Ordenar por fecha descendente y tomar los últimos 5 destinatarios
        List<Long> lastFiveRecipientIds = lastTxPerRecipient.entrySet().stream()
                .sorted(Map.Entry.<Long, LocalDateTime>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        // Obtener los Account reales
        List<Account> lastFiveRecipients = lastFiveRecipientIds.stream()
                .map(accountService::findById)
                .toList();

        // Verificaciones
        assertNotNull(lastFiveRecipients);
        assertEquals(5, lastFiveRecipients.size(), "Debe devolver solo los 5 últimos destinatarios");

        // Verificar que los IDs estén ordenados correctamente
        List<Long> actualIds = lastFiveRecipients.stream().map(Account::getId).toList();
        assertEquals(lastFiveRecipientIds, actualIds, "Los destinatarios deben coincidir con los más recientes");
    }



}

