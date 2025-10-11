package dgdiego_digital_money.account_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dgdiego_digital_money.account_service.entity.dto.AccountRequestInitDTO;
import dgdiego_digital_money.account_service.entity.dto.AccountResponseDTO;
import dgdiego_digital_money.account_service.service.implementation.AccountService;
import dgdiego_digital_money.account_service.service.implementation.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAccount_ShouldReturnAccountId() throws Exception {
        // Arrange
        AccountRequestInitDTO request = new AccountRequestInitDTO();
        request.setUserId(1L);
        request.setAlias("mi.alias");
        request.setCvu("1234567890123456789012");

        Long accountId = 10L;
        when(accountService.create(any(AccountRequestInitDTO.class))).thenReturn(accountId);

        // Act & Assert
        mockMvc.perform(post("/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(accountId.toString()));
    }


    @Test
    void getBalance_ShouldReturnAccountDetails() throws Exception {
        // Arrange
        Long accountId = 10L;
        AccountResponseDTO response = new AccountResponseDTO();
        response.setId(accountId);
        response.setBalance(5000.0);
        response.setAlias("mi.alias");
        response.setCvu("1234567890123456789012");

        when(accountService.mapToResponseDto(accountService.findById(accountId))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/accounts/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId))
                .andExpect(jsonPath("$.balance").value(response.getBalance()))
                .andExpect(jsonPath("$.alias").value(response.getAlias()))
                .andExpect(jsonPath("$.cvu").value(response.getCvu()));
    }

}

