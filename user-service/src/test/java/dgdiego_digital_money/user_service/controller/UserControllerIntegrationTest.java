package dgdiego_digital_money.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dgdiego_digital_money.user_service.entity.domian.User;
import dgdiego_digital_money.user_service.entity.dto.RegistrationRequestDTO;
import dgdiego_digital_money.user_service.entity.dto.RegistrationResponseDTO;
import dgdiego_digital_money.user_service.entity.dto.UserDto;
import dgdiego_digital_money.user_service.service.implementation.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_ShouldReturnRegistrationResponse() throws Exception {
        // Arrange
        RegistrationRequestDTO requestDTO = new RegistrationRequestDTO();
        requestDTO.setEmail("test@example.com");
        requestDTO.setPassword("password123");
        requestDTO.setDni("12345678");
        requestDTO.setName("Diego");
        requestDTO.setLastname("Gimenez");
        requestDTO.setPhone("099123456");

        RegistrationResponseDTO responseDTO = new RegistrationResponseDTO();
        responseDTO.setEmail("test@example.com");
        responseDTO.setName("Diego");
        responseDTO.setLastname("Gimenez");

        Mockito.when(userService.register(any(RegistrationRequestDTO.class)))
                .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Diego"))
                .andExpect(jsonPath("$.lastname").value("Gimenez"));
    }

    @Test
    void loginLookup_ShouldReturnUserDto() throws Exception {
        User mockUser = User.builder()
                .id(1L)
                .name("Diego")
                .lastname("Gimenez")
                .email("test@example.com")
                .dni("1234")
                .phone("123456")
                .alias("perro-gato-liebre")
                .cvu("9999999")
                .build();

        UserDto mockDto = new UserDto(
                mockUser.getId(),
                mockUser.getName(),
                mockUser.getLastname(),
                mockUser.getEmail(),
                mockUser.getDni(),
                mockUser.getPhone(),
                mockUser.getAlias(),
                mockUser.getCvu(),
                "",
                null
        );

        Mockito.when(userService.findByEmail(anyString())).thenReturn(mockUser);
        Mockito.when(userService.mapToResponseDto(any(User.class))).thenReturn(mockDto);

        mockMvc.perform(post("/users/login-lookup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"test@example.com\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void logout_ShouldReturnOk() throws Exception {
        doNothing().when(userService).logout();
        String token = "Bearer mocked-jwt-token";
        mockMvc.perform(post("/users/logout"))
                .andExpect(status().isOk());
    }
}
