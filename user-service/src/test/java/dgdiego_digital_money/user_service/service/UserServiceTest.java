package dgdiego_digital_money.user_service.service;

import dgdiego_digital_money.user_service.entity.domian.Rol;
import dgdiego_digital_money.user_service.entity.domian.User;
import dgdiego_digital_money.user_service.entity.dto.RegistrationRequestDTO;
import dgdiego_digital_money.user_service.entity.dto.RegistrationResponseDTO;
import dgdiego_digital_money.user_service.exceptions.ResourceNotFoundException;
import dgdiego_digital_money.user_service.repository.IUserRepository;
import dgdiego_digital_money.user_service.service.implementation.AuthService;
import dgdiego_digital_money.user_service.service.implementation.RolService;
import dgdiego_digital_money.user_service.service.implementation.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private RolService rolService;

    @Mock
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void listAll_ShouldReturnUsers() {
        User user = User.builder().email("test@example.com").build();
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.listAll();

        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
    }

    @Test
    void register_ShouldSaveNewUser_WhenNotDuplicate() {
        RegistrationRequestDTO dto = new RegistrationRequestDTO();
        dto.setEmail("new@example.com");
        dto.setDni("12345678");
        dto.setPassword("password");
        dto.setName("Diego");
        dto.setLastname("Gimenez");
        dto.setPhone("099999999");

        when(userRepository.findByEmailAndDni(dto.getEmail(), dto.getDni())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        Rol rol = new Rol();
        rol.setName("USER");
        when(rolService.findByName("USER")).thenReturn(rol);

        RegistrationResponseDTO response = userService.register(dto);

        assertEquals("new@example.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenUserExists() {
        RegistrationRequestDTO dto = new RegistrationRequestDTO();
        dto.setEmail("dup@example.com");
        dto.setDni("87654321");

        when(userRepository.findByEmailAndDni(dto.getEmail(), dto.getDni()))
                .thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> userService.register(dto));
    }

    @Test
    void logout_ShouldCallAuthServiceLogout() {
        userService.logout();
        verify(authService, times(1)).logout();
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        User user = User.builder().email("test@example.com").build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("test@example.com");

        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void findByEmail_ShouldThrowException_WhenNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.findByEmail("notfound@example.com"));
    }
}

