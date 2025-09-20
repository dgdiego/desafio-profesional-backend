package dgdiego_digital_money.user_service.integration;

import dgdiego_digital_money.user_service.entity.domian.Rol;
import dgdiego_digital_money.user_service.entity.domian.User;
import dgdiego_digital_money.user_service.entity.dto.RegistrationRequestDTO;
import dgdiego_digital_money.user_service.entity.dto.RegistrationResponseDTO;
import dgdiego_digital_money.user_service.exceptions.ResourceNotFoundException;
import dgdiego_digital_money.user_service.repository.IRolRepository;
import dgdiego_digital_money.user_service.repository.IUserRepository;
import dgdiego_digital_money.user_service.service.implementation.RolService;
import dgdiego_digital_money.user_service.service.implementation.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRolRepository rolRepository;

    @Autowired
    private RolService rolService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        // Insertamos un rol en la BD porque el servicio lo necesita
        Rol rol = new Rol();
        rol.setName("USER");
        rolRepository.save(rol);
    }

    @Test
    void register_ShouldPersistUser() {
        RegistrationRequestDTO dto = new RegistrationRequestDTO();
        dto.setEmail("integration@example.com");
        dto.setDni("12345678");
        dto.setPassword("password123");
        dto.setName("Diego");
        dto.setLastname("Gimenez");
        dto.setPhone("099999999");

        RegistrationResponseDTO response = userService.register(dto);

        assertNotNull(response);
        assertEquals("integration@example.com", response.getEmail());

        List<User> allUsers = userRepository.findAll();
        assertEquals(1, allUsers.size());

        User saved = allUsers.get(0);
        assertTrue(passwordEncoder.matches("password123", saved.getPassword()));
    }

    @Test
    void findByEmail_ShouldThrow_WhenNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> userService.findByEmail("noexist@example.com"));
    }
}
