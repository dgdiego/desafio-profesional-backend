package dgdiego_digital_money.account_service.controller;

import dgdiego_digital_money.account_service.entity.domian.Card;
import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.domian.TransactionType;
import dgdiego_digital_money.account_service.entity.dto.CardDepositDto;
import dgdiego_digital_money.account_service.repository.ICardRepository;
import dgdiego_digital_money.account_service.repository.ITransactionRepository;
import dgdiego_digital_money.account_service.service.implementation.AccountService;
import dgdiego_digital_money.account_service.service.implementation.PermissionService;
import dgdiego_digital_money.account_service.service.implementation.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
@AutoConfigureMockMvc(addFilters = false)
class TransferenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    private Long accountId;
    private CardDepositDto validDto;
    private CardDepositDto invalidDto;

    @BeforeEach
    void setUp() {
        accountId = 1L;

        validDto = new CardDepositDto();
        validDto.setCardId(100L);
        validDto.setAmount(100.0);
        validDto.setDetail("Depósito válido");

        invalidDto = new CardDepositDto();
        invalidDto.setCardId(100L);
        invalidDto.setAmount(0.0); // inválido
        invalidDto.setDetail("Depósito inválido");
    }

    @Test
    void testCreateDepositWithCard_returnsOk() throws Exception {
        doNothing().when(transactionService).createDepositWithCard(anyLong(), any(CardDepositDto.class));

        mockMvc.perform(post("/accounts/{accountId}/transferences", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateDepositWithCard_invalidAmount_returnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("No es posible realizar la operación"))
                .when(transactionService).createDepositWithCard(anyLong(), any(CardDepositDto.class));

        mockMvc.perform(post("/accounts/{accountId}/transferences", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No es posible realizar la operación"));
    }
}

