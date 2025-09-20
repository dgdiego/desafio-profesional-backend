package dgdiego_digital_money.auth_service.service;



import dgdiego_digital_money.auth_service.entity.dto.LoginRequestDto;
import dgdiego_digital_money.auth_service.entity.dto.LoginResponseDto;
import dgdiego_digital_money.auth_service.entity.dto.User;
import dgdiego_digital_money.auth_service.repository.IExpiredTokenRepository;
import dgdiego_digital_money.auth_service.security.JwtService;
import dgdiego_digital_money.auth_service.service.implementation.AuthService;
import dgdiego_digital_money.auth_service.service.implementation.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private IExpiredTokenRepository expiredTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_WithCorrectCredentials_ReturnsToken() {
        // Arrange
        LoginRequestDto loginDto = new LoginRequestDto("test@example.com", "password123");

        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("encodedPass");

        when(userService.loginLookup("test@example.com")).thenReturn(mockUser);
        when(passwordEncoder.matches("password123", "encodedPass")).thenReturn(true);
        when(jwtService.getToken(mockUser)).thenReturn("jwt-token");

        // Act
        LoginResponseDto response = authService.login(loginDto);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void login_WithWrongPassword_ThrowsException() {
        // Arrange
        LoginRequestDto loginDto = new LoginRequestDto("test@example.com", "wrongPassword");

        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("encodedPass");

        when(userService.loginLookup("test@example.com")).thenReturn(mockUser);
        when(passwordEncoder.matches("wrongPassword", "encodedPass")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.login(loginDto));
    }

    @Test
    void logout_SavesExpiredToken() {
        // Arrange
        String token = "jwt-token";
        Date expirationDate = new Date();

        when(jwtService.getExpiration(token)).thenReturn(expirationDate);

        // Act
        authService.logout(token);

        // Assert
        verify(expiredTokenRepository, times(1)).save(argThat(expired ->
                expired.getToken().equals("jwt-token") &&
                        expired.getExpirationDate().equals(expirationDate)
        ));
    }

}
