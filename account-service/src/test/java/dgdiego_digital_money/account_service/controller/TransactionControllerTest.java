package dgdiego_digital_money.account_service.controller;

import com.netflix.discovery.DiscoveryClient;
import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.domian.TransactionType;
import dgdiego_digital_money.account_service.repository.ITransactionRepository;
import dgdiego_digital_money.account_service.service.implementation.AccountService;
import dgdiego_digital_money.account_service.service.implementation.PermissionService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;



@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
@AutoConfigureMockMvc(addFilters = false)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ITransactionRepository transactionRepository;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private DiscoveryClient discoveryClient;

    private Long accountId;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();

        // crear cuenta de prueba
        try {
            accountId = accountService.create(1L);
        } catch (IllegalArgumentException ex) {
            accountId = accountService.findByUserId(1L).getId();
        }

        // mockear permisos para que no falle
        doNothing().when(permissionService).canAccess(1L);

        // crear algunas transacciones
        for (int i = 0; i < 6; i++) {
            Transaction tx = new Transaction();
            tx.setAccount(accountService.findById(accountId));
            tx.setAmount(100.0 + i);
            tx.setType(TransactionType.DEBIT);
            tx.setDateTime(LocalDateTime.now().minusDays(i));
            transactionRepository.save(tx);
        }
    }

    @Test
    void testTransactionsDashboardEndpoint() throws Exception {
        mockMvc.perform(get("/accounts/{accountId}/transactions", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5))) // devuelve solo las últimas 5
                .andExpect(jsonPath("$[0].type", is("DEBIT")))
                .andExpect(jsonPath("$[0].amount", notNullValue()))
                .andExpect(jsonPath("$[0].accountId", is(accountId.intValue())));
    }
}
