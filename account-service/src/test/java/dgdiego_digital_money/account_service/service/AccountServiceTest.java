package dgdiego_digital_money.account_service.service;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.exceptions.ResourceNotFoundException;
import dgdiego_digital_money.account_service.repository.IAccountRepository;

import dgdiego_digital_money.account_service.service.implementation.AccountService;
import dgdiego_digital_money.account_service.service.implementation.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private IAccountRepository accountRepository;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void create_ShouldCreateNewAccount_WhenUserHasNoAccount() {
        Long userId = 1L;

        when(accountRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            acc.setId(10L);
            return acc;
        });

        Long result = accountService.create(userId);

        assertEquals(10L, result);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void create_ShouldThrowException_WhenAccountAlreadyExists() {
        Long userId = 1L;
        Account existing = Account.builder().id(5L).userId(userId).balance(100.0).build();

        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> accountService.create(userId));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void getBalance_ShouldReturnBalance_WhenAccountExistsAndPermissionGranted() {
        Long accountId = 1L;
        Account account = Account.builder().id(accountId).userId(2L).balance(50.0).build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Double result = accountService.getBalance(accountId);

        assertEquals(50.0, result);
        verify(permissionService).canAccess(2L);
    }

    @Test
    void getBalance_ShouldThrowException_WhenAccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.getBalance(1L));
    }

    @Test
    void findById_ShouldReturnAccount_WhenExists() {
        Account account = Account.builder().id(1L).userId(2L).balance(10.0).build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Account result = accountService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_ShouldThrowException_WhenNotExists() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.findById(1L));
    }

    @Test
    void findByUserId_ShouldReturnAccount_WhenExists() {
        Long userId = 2L;
        Account account = Account.builder().id(1L).userId(userId).balance(15.0).build();

        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(account));

        Account result = accountService.findByUserId(userId);

        assertEquals(userId, result.getUserId());
    }

    @Test
    void findByUserId_ShouldThrowException_WhenNotExists() {
        when(accountRepository.findByUserId(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.findByUserId(2L));
    }
}

