package dgdiego_digital_money.account_service.service;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.entity.dto.AccountRequestInitDTO;
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
        // given
        AccountRequestInitDTO request = new AccountRequestInitDTO();
        request.setUserId(1L);
        request.setAlias("mi.alias");
        request.setCvu("1234567890123456789012");

        when(accountRepository.findByUserId(request.getUserId())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            acc.setId(10L);
            return acc;
        });

        // when
        Long result = accountService.create(request);

        // then
        assertEquals(10L, result);
        verify(accountRepository).findByUserId(request.getUserId());
        verify(accountRepository).save(any(Account.class));
    }


    @Test
    void create_ShouldThrowException_WhenAccountAlreadyExists() {
        // given
        AccountRequestInitDTO request = new AccountRequestInitDTO();
        request.setUserId(1L);
        request.setAlias("mi.alias");
        request.setCvu("1234567890123456789012");

        Account existing = Account.builder()
                .id(5L)
                .userId(request.getUserId())
                .balance(100.0)
                .build();

        when(accountRepository.findByUserId(request.getUserId()))
                .thenReturn(Optional.of(existing));

        // when / then
        assertThrows(IllegalArgumentException.class, () -> accountService.create(request));

        verify(accountRepository).findByUserId(request.getUserId());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void getBalance_ShouldThrowException_WhenAccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.findById(1L));
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

