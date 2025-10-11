package dgdiego_digital_money.account_service.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.entity.domian.Card;
import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.domian.TransactionType;
import dgdiego_digital_money.account_service.entity.dto.CardDepositDto;
import dgdiego_digital_money.account_service.entity.dto.TransactionDto;
import dgdiego_digital_money.account_service.entity.dto.TransferenceCreateDto;
import dgdiego_digital_money.account_service.exceptions.InsufficientFundsException;
import dgdiego_digital_money.account_service.exceptions.ResourceNotFoundException;
import dgdiego_digital_money.account_service.repository.ITransactionRepository;
import dgdiego_digital_money.account_service.service.implementation.AccountService;
import dgdiego_digital_money.account_service.service.implementation.CardService;
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
    private CardService cardService;

    @Mock
    private ITransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -----------------------------
    // ✅ transactionsDashboard()
    // -----------------------------
    @Test
    void testTransactionsDashboard_returnsTransactions() {
        Long accountId = 1L;
        Account mockAccount = new Account();
        mockAccount.setId(accountId);
        mockAccount.setUserId(10L);

        Transaction tx = new Transaction();
        tx.setId(100L);
        tx.setAmount(50.0);
        tx.setType(TransactionType.DEBIT);
        tx.setDateTime(LocalDateTime.now());
        tx.setAccount(mockAccount);

        when(accountService.findById(accountId)).thenReturn(mockAccount);
        doNothing().when(permissionService).canAccess(mockAccount.getUserId());
        when(transactionRepository.findByAccountIdOrderByDateTimeDesc(eq(accountId), any(Pageable.class)))
                .thenReturn(Collections.singletonList(tx));

        List<Transaction> result = transactionService.transactionsDashboard(accountId);

        assertEquals(1, result.size());
        assertEquals(tx.getId(), result.get(0).getId());
        verify(accountService).findById(accountId);
        verify(permissionService).canAccess(mockAccount.getUserId());
        verify(transactionRepository).findByAccountIdOrderByDateTimeDesc(eq(accountId), any(Pageable.class));
    }

    // -----------------------------
    // ✅ getById() - éxito
    // -----------------------------
    @Test
    void testGetById_success() {
        Long accountId = 1L;
        Long transactionId = 2L;

        Account mockAccount = new Account();
        mockAccount.setId(accountId);
        mockAccount.setUserId(10L);

        Transaction tx = new Transaction();
        tx.setId(transactionId);
        tx.setAccount(mockAccount);

        when(accountService.findById(accountId)).thenReturn(mockAccount);
        doNothing().when(permissionService).canAccess(mockAccount.getUserId());
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(tx));

        Transaction result = transactionService.getById(accountId, transactionId);

        assertEquals(transactionId, result.getId());
        verify(permissionService).canAccess(mockAccount.getUserId());
    }

    // -----------------------------
    // ❌ getById() - transacción no pertenece a cuenta
    // -----------------------------
    @Test
    void testGetById_transactionDoesNotBelongToAccount_throwsException() {
        Long accountId = 1L;
        Long transactionId = 2L;

        Account mockAccount = new Account();
        mockAccount.setId(accountId);
        mockAccount.setUserId(10L);

        Account otherAccount = new Account();
        otherAccount.setId(999L);

        Transaction tx = new Transaction();
        tx.setId(transactionId);
        tx.setAccount(otherAccount);

        when(accountService.findById(accountId)).thenReturn(mockAccount);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(tx));

        assertThrows(IllegalArgumentException.class,
                () -> transactionService.getById(accountId, transactionId));
    }

    // -----------------------------
    // ❌ findById() - no encontrada
    // -----------------------------
    @Test
    void testFindById_notFound_throwsException() {
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.findById(999L));
    }

    // -----------------------------
    // ✅ listAllByAccount()
    // -----------------------------
    @Test
    void testListAllByAccount_success() {
        Long accountId = 1L;
        Account mockAccount = new Account();
        mockAccount.setId(accountId);
        mockAccount.setUserId(5L);

        when(accountService.findById(accountId)).thenReturn(mockAccount);
        doNothing().when(permissionService).canAccess(mockAccount.getUserId());
        when(transactionRepository.findByAccountIdOrderByDateTimeDesc(accountId))
                .thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.listAllByAccount(accountId);

        assertNotNull(result);
        verify(accountService).findById(accountId);
        verify(permissionService).canAccess(mockAccount.getUserId());
    }

    // -----------------------------
    // ✅ createDepositWithCard() - éxito
    // -----------------------------
    @Test
    void testCreateDepositWithCard_success() {
        Long accountId = 1L;
        Account account = new Account();
        account.setId(accountId);
        account.setUserId(10L);
        account.setBalance(100.0);

        Card card = new Card();
        card.setId(10L);
        card.setAccountId(accountId);

        CardDepositDto dto = new CardDepositDto();
        dto.setCardId(card.getId());
        dto.setAmount(50.0);
        dto.setDetail("Recarga");

        when(accountService.findById(accountId)).thenReturn(account);
        doNothing().when(permissionService).canAccess(account.getUserId());
        when(cardService.findById(dto.getCardId())).thenReturn(card);

        transactionService.createDepositWithCard(accountId, dto);

        assertEquals(150.0, account.getBalance());
        verify(transactionRepository).save(any(Transaction.class));
        verify(accountService).updateBalance(account);
    }

    // -----------------------------
    // ❌ createDepositWithCard() - tarjeta no pertenece
    // -----------------------------
    @Test
    void testCreateDepositWithCard_cardNotBelongToAccount_throwsException() {
        Long accountId = 1L;
        Account account = new Account();
        account.setId(accountId);
        account.setUserId(10L);

        Card card = new Card();
        card.setId(2L);
        card.setAccountId(999L); // otra cuenta

        CardDepositDto dto = new CardDepositDto();
        dto.setCardId(card.getId());
        dto.setAmount(100.0);

        when(accountService.findById(accountId)).thenReturn(account);
        when(cardService.findById(dto.getCardId())).thenReturn(card);

        assertThrows(IllegalArgumentException.class,
                () -> transactionService.createDepositWithCard(accountId, dto));
    }

    // -----------------------------
    // ❌ createDepositWithCard() - monto <= 0
    // -----------------------------
    @Test
    void testCreateDepositWithCard_invalidAmount_throwsException() {
        Long accountId = 1L;
        Account account = new Account();
        account.setId(accountId);
        account.setUserId(10L);

        Card card = new Card();
        card.setId(2L);
        card.setAccountId(accountId);

        CardDepositDto dto = new CardDepositDto();
        dto.setCardId(card.getId());
        dto.setAmount(0.0);

        when(accountService.findById(accountId)).thenReturn(account);
        when(cardService.findById(dto.getCardId())).thenReturn(card);

        assertThrows(IllegalArgumentException.class,
                () -> transactionService.createDepositWithCard(accountId, dto));
    }

    // -----------------------------
    // ✅ mapToResponseDto()
    // -----------------------------
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

    @Test
    void createTransference_ShouldCreateTransactions_WhenValid() {
        // Arrange
        Long accountId = 1L;

        Account account = Account.builder()
                .id(accountId)
                .userId(10L)
                .balance(1000.0)
                .build();

        Account originAccount = Account.builder()
                .id(accountId)
                .balance(1000.0)
                .build();

        Account destinationAccount = Account.builder()
                .id(2L)
                .balance(500.0)
                .build();

        TransferenceCreateDto requestDto = TransferenceCreateDto.builder()
                .amount(200.0)
                .origin("origin-param")
                .destination("destination-param")
                .detail("Pago de prueba")
                .build();

        // Mockear búsquedas de cuentas y permisos
        when(accountService.findById(accountId)).thenReturn(account);
        doNothing().when(permissionService).canAccess(account.getUserId());

        when(accountService.findByParam("origin-param")).thenReturn(originAccount);
        when(accountService.findByParam("destination-param")).thenReturn(destinationAccount);

        when(accountService.updateBalance(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        transactionService.createTransference(accountId, requestDto);

        // Assert
        // Verificar que balances se actualizaron
        assertEquals(800.0, originAccount.getBalance());
        assertEquals(700.0, destinationAccount.getBalance());

        // Verificar que updateBalance se llamó con ambas cuentas
        verify(accountService).updateBalance(originAccount);
        verify(accountService).updateBalance(destinationAccount);

        // Verificar que se guardaron dos transacciones
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void createTransference_ShouldThrowException_WhenAmountInvalid() {
        Long accountId = 1L;
        Account account = Account.builder().id(accountId).userId(10L).balance(1000.0).build();

        TransferenceCreateDto dto = TransferenceCreateDto.builder()
                .amount(0.0)
                .origin("origin")
                .destination("destination")
                .detail("Detalle")
                .build();

        when(accountService.findById(accountId)).thenReturn(account);
        doNothing().when(permissionService).canAccess(account.getUserId());

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.createTransference(accountId, dto));
    }

    @Test
    void createTransference_ShouldThrowException_WhenInsufficientFunds() {
        Long accountId = 1L;
        Account account = Account.builder().id(accountId).userId(10L).balance(100.0).build();
        Account originAccount = Account.builder().id(accountId).balance(100.0).build();
        Account destinationAccount = Account.builder().id(2L).balance(50.0).build();

        TransferenceCreateDto dto = TransferenceCreateDto.builder()
                .amount(200.0)
                .origin("origin")
                .destination("destination")
                .detail("Detalle")
                .build();

        when(accountService.findById(accountId)).thenReturn(account);
        doNothing().when(permissionService).canAccess(account.getUserId());
        when(accountService.findByParam("origin")).thenReturn(originAccount);
        when(accountService.findByParam("destination")).thenReturn(destinationAccount);

        assertThrows(InsufficientFundsException.class, () ->
                transactionService.createTransference(accountId, dto));
    }

    @Test
    void createTransference_ShouldThrowException_WhenOriginMismatch() {
        Long accountId = 1L;
        Account account = Account.builder().id(accountId).userId(10L).balance(1000.0).build();
        Account originAccount = Account.builder().id(99L).balance(1000.0).build();
        Account destinationAccount = Account.builder().id(2L).balance(500.0).build();

        TransferenceCreateDto dto = TransferenceCreateDto.builder()
                .amount(200.0)
                .origin("origin")
                .destination("destination")
                .detail("Detalle")
                .build();

        when(accountService.findById(accountId)).thenReturn(account);
        doNothing().when(permissionService).canAccess(account.getUserId());
        when(accountService.findByParam("origin")).thenReturn(originAccount);
        when(accountService.findByParam("destination")).thenReturn(destinationAccount);

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.createTransference(accountId, dto));
    }

    @Test
    void testGetLastRecipientsByAccount() {
        Long accountId = 1L;
        Long userId = 10L;

        // Mock de la cuenta
        Account account = new Account();
        account.setId(accountId);
        account.setUserId(userId);

        // Mock de la lista de destinatarios
        Account recipient1 = new Account();
        recipient1.setId(2L);

        Account recipient2 = new Account();
        recipient2.setId(3L);

        List<Account> recipients = Arrays.asList(recipient1, recipient2);

        // Definición de comportamiento de mocks
        when(accountService.findById(accountId)).thenReturn(account);
        doNothing().when(permissionService).canAccess(userId);
        when(transactionRepository.findLastRecipientsByAccountId(eq(accountId), any(Pageable.class)))
                .thenReturn(recipients);

        // Llamada al método a testear
        List<Account> result = transactionService.getLastRecipientsByAccount(accountId);

        // Verificaciones
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(recipient1.getId(), result.get(0).getId());
        assertEquals(recipient2.getId(), result.get(1).getId());

        // Verificar que se llamaron los métodos del mock
        verify(accountService).findById(accountId);
        verify(permissionService).canAccess(userId);
        verify(transactionRepository).findLastRecipientsByAccountId(eq(accountId), any(Pageable.class));
    }
}

