package dgdiego_digital_money.account_service.controller;

import com.netflix.discovery.DiscoveryClient;
import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.domian.TransactionType;
import dgdiego_digital_money.account_service.entity.dto.AccountRequestInitDTO;
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

        try {
            AccountRequestInitDTO request = new AccountRequestInitDTO();
            request.setUserId(1L);
            request.setAlias("test.alias");
            request.setCvu("1234567890123456789012");

            accountId = accountService.create(request);
        } catch (IllegalArgumentException ex) {
            accountId = accountService.findByUserId(1L).getId();
        }

        doNothing().when(permissionService).canAccess(1L);

        for (int i = 0; i < 6; i++) {
            Transaction tx = new Transaction();
            tx.setAccount(accountService.findById(accountId)); // devuelve la entidad completa si ahora findById retorna DTO, ajustar si es necesario
            tx.setAmount(100.0 + i);
            tx.setType(TransactionType.DEBIT);
            tx.setDateTime(LocalDateTime.now().minusDays(i));
            tx.setDetail("Transacción de prueba " + i);
            transactionRepository.save(tx);
        }
    }

    @Test
    void testTransactionsDashboardEndpoint_returnsLastFiveTransactions() throws Exception {
        mockMvc.perform(get("/accounts/{accountId}/transactions", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].type", is("DEBIT")))
                .andExpect(jsonPath("$[0].amount", notNullValue()))
                .andExpect(jsonPath("$[0].accountId", is(accountId.intValue())));
    }

    @Test
    void testListAllByAccount_returnsAllTransactions() throws Exception {
        mockMvc.perform(get("/accounts/{accountId}/transactions/activity", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].type", is("DEBIT")));
    }

    @Test
    void testGetTransactionById_returnsCorrectTransaction() throws Exception {
        Transaction tx = transactionRepository.findAll().get(0);

        mockMvc.perform(get("/accounts/{accountId}/transactions/{transactionId}", accountId, tx.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(tx.getId().intValue())))
                .andExpect(jsonPath("$.amount", is(tx.getAmount())))
                .andExpect(jsonPath("$.type", is(tx.getType().name())))
                .andExpect(jsonPath("$.detail", is(tx.getDetail())));
    }
}
