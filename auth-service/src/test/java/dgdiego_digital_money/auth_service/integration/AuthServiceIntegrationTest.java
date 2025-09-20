package dgdiego_digital_money.auth_service.integration;

import dgdiego_digital_money.auth_service.entity.dto.LoginRequestDto;
import dgdiego_digital_money.auth_service.entity.dto.LoginResponseDto;
import dgdiego_digital_money.auth_service.entity.dto.User;
import dgdiego_digital_money.auth_service.repository.IExpiredTokenRepository;
import dgdiego_digital_money.auth_service.security.JwtService;
import dgdiego_digital_money.auth_service.service.implementation.AuthService;
import dgdiego_digital_money.auth_service.service.implementation.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class AuthServiceIntegrationTest {
    @Autowired
    private AuthService authService;

    @MockBean
    private UserService userService; // simulamos usuario existente

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IExpiredTokenRepository expiredTokenRepository;

    @MockBean
    private JwtService jwtService; // para no generar JWT real

    @Test
    void login_WithCorrectCredentials_ReturnsToken() {
        // Arrange
        User mockUser = new User();
        mockUser.setEmail("integration@test.com");
        mockUser.setPassword(passwordEncoder.encode("password123"));

        when(userService.loginLookup("integration@test.com")).thenReturn(mockUser);
        when(jwtService.getToken(mockUser)).thenReturn("fake-jwt-token");

        LoginRequestDto dto = new LoginRequestDto("integration@test.com", "password123");

        // Act
        LoginResponseDto response = authService.login(dto);

        // Assert
        assertEquals("fake-jwt-token", response.getToken());
    }

    @Test
    void logout_SavesExpiredToken() {
        // Arrange
        String token = "fake-jwt-token";
        Date expirationDate = new Date(System.currentTimeMillis() + 60000);

        when(jwtService.getExpiration(token)).thenReturn(expirationDate);

        // Act
        authService.logout(token);

        // Assert
        boolean exists = expiredTokenRepository.findAll().stream()
                .anyMatch(exp -> exp.getToken().equals(token));
        assertTrue(exists);
    }
}
