package dgdiego_digital_money.account_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dgdiego_digital_money.account_service.entity.dto.AccountRequestInitDTO;
import dgdiego_digital_money.account_service.entity.dto.CardCreateDto;
import dgdiego_digital_money.account_service.entity.dto.CardDto;
import dgdiego_digital_money.account_service.entity.domian.CardType;
import dgdiego_digital_money.account_service.repository.IAccountRepository;
import dgdiego_digital_money.account_service.service.implementation.AccountService;
import dgdiego_digital_money.account_service.service.implementation.CardService;
import dgdiego_digital_money.account_service.service.implementation.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
        }
)
@AutoConfigureMockMvc(addFilters = false)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CardService cardService;

    @Autowired
    private IAccountRepository accountRepository;

    @MockBean
    private PermissionService permissionService;

    private Long accountId;

    @BeforeEach
    void setup() {
        accountRepository.deleteAll();

        // Crear una cuenta de prueba
        AccountRequestInitDTO request = new AccountRequestInitDTO();
        request.setUserId(200L);
        request.setAlias("test.alias");
        request.setCvu("1234567890123456789012");

        accountId = accountService.create(request);
    }

    @Test
    void createAndGetCard_ShouldWorkCorrectly() throws Exception {

        //  Crear DTO de tarjeta
        CardCreateDto cardDto = CardCreateDto.builder()
                .number("1234-4446-7891-4444")
                .type(CardType.CREDIT)
                .expirationDate(LocalDate.now().plusYears(4))
                .build();

        //  POST /accounts/{accountId}/cards
        MvcResult postResult = mockMvc.perform(post("/accounts/{accountId}/cards", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andExpect(status().isCreated())
                .andReturn();

        CardDto createdCard = objectMapper.readValue(postResult.getResponse().getContentAsString(), CardDto.class);

        // Setear accountId
        createdCard.setAccountId(accountId);

        //  GET /accounts/{accountId}/cards/{cardId}
        mockMvc.perform(get("/accounts/{accountId}/cards/{cardId}", accountId, createdCard.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdCard.getId()))
                .andExpect(jsonPath("$.number").value("1234-4446-7891-4444"))
                .andExpect(jsonPath("$.type").value("CREDIT"))
                .andExpect(jsonPath("$.accountId").value(accountId));

        //  GET /accounts/{accountId}/cards
        mockMvc.perform(get("/accounts/{accountId}/cards", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(createdCard.getId()));

        //  DELETE /accounts/{accountId}/cards/{cardId}
        mockMvc.perform(delete("/accounts/{accountId}/cards/{cardId}", accountId, createdCard.getId()))
                .andExpect(status().isOk());

        // Verificar que la tarjeta ya no existe
        mockMvc.perform(get("/accounts/{accountId}/cards/{cardId}", accountId, createdCard.getId()))
                .andExpect(status().isNotFound());
    }
}
    

