package dgdiego_digital_money.account_service.integration;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.exceptions.ResourceNotFoundException;
import dgdiego_digital_money.account_service.repository.IAccountRepository;
import dgdiego_digital_money.account_service.service.implementation.AccountService;
import dgdiego_digital_money.account_service.service.implementation.PermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class AccountServiceIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private IAccountRepository accountRepository;

    @MockBean
    private PermissionService permissionService; // 🔹 mock en test de integración

    @Test
    void create_ShouldPersistAccount_WhenUserHasNoAccount() {
        Long userId = 1L;

        Long accountId = accountService.create(userId);

        Account saved = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("No se guardó la cuenta"));

        assertEquals(userId, saved.getUserId());
        assertEquals(0.0, saved.getBalance());
    }

    @Test
    void findByUserId_ShouldReturnAccount_WhenExists() {
        Account account = Account.builder().userId(2L).balance(500.0).build();
        accountRepository.save(account);

        Account result = accountService.findByUserId(2L);

        assertEquals(500.0, result.getBalance());
    }

    @Test
    void findByUserId_ShouldThrowException_WhenNotExists() {
        assertThrows(ResourceNotFoundException.class,
                () -> accountService.findByUserId(99L));
    }

    @Test
    void getBalance_ShouldReturnBalance_WhenPermissionGranted() {
        Account account = Account.builder().userId(3L).balance(750.0).build();
        accountRepository.save(account);

        doNothing().when(permissionService).canAccess(3L);

        Double balance = accountService.getBalance(account.getId());

        assertEquals(750.0, balance);
    }

    @Test
    void getBalance_ShouldThrowException_WhenAccountNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> accountService.getBalance(999L));
    }
}
