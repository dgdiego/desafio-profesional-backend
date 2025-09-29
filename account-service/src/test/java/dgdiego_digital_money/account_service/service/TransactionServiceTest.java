package dgdiego_digital_money.account_service.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.domian.TransactionType;
import dgdiego_digital_money.account_service.entity.dto.TransactionDto;
import dgdiego_digital_money.account_service.repository.ITransactionRepository;
import dgdiego_digital_money.account_service.service.implementation.AccountService;
import dgdiego_digital_money.account_service.service.implementation.PermissionService;
import dgdiego_digital_money.account_service.service.implementation.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Pageable;

public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private AccountService accountService;

    @Mock
    private PermissionService permissionService;

    @Mock
    private ITransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTransactionsDashboard_returnsTransactions() {
        Long accountId = 1L;

        Account mockAccount = new Account();
        mockAccount.setId(accountId);
        mockAccount.setUserId(10L);

        Transaction tx1 = new Transaction();
        tx1.setId(100L);
        tx1.setAmount(50.0);
        tx1.setType(TransactionType.DEBIT);
        tx1.setDateTime(LocalDateTime.now());
        tx1.setAccount(mockAccount);

        List<Transaction> mockTransactions = Arrays.asList(tx1);

        when(accountService.findById(accountId)).thenReturn(mockAccount);
        doNothing().when(permissionService).canAccess(mockAccount.getUserId());
        when(transactionRepository.findByAccountIdOrderByDateTimeDesc(eq(accountId), any(Pageable.class)))
                .thenReturn(mockTransactions);

        List<Transaction> result = transactionService.transactionsDashboard(accountId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(tx1.getId(), result.get(0).getId());

        verify(accountService).findById(accountId);
        verify(permissionService).canAccess(mockAccount.getUserId());
        verify(transactionRepository).findByAccountIdOrderByDateTimeDesc(eq(accountId), any(Pageable.class));
    }

    @Test
    void testMapToResponseDto_returnsDto() {
        Account mockAccount = new Account();
        mockAccount.setId(1L);

        Transaction tx = new Transaction();
        tx.setId(100L);
        tx.setType(TransactionType.INCOME);
        tx.setAmount(20.0);
        tx.setDateTime(LocalDateTime.now());
        tx.setAccount(mockAccount);

        TransactionDto dto = transactionService.mapToResponseDto(tx);

        assertNotNull(dto);
        assertEquals(tx.getId(), dto.getId());
        assertEquals(tx.getType(), dto.getType());
        assertEquals(tx.getAmount(), dto.getAmount());
        assertEquals(tx.getAccount().getId(), dto.getAccountId());
    }

    @Test
    void testMapToResponseDto_nullTransaction_returnsNull() {
        TransactionDto dto = transactionService.mapToResponseDto(null);
        assertNull(dto);
    }
}

