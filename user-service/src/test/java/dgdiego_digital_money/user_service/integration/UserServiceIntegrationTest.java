package dgdiego_digital_money.user_service.integration;

import dgdiego_digital_money.user_service.entity.domian.Rol;
import dgdiego_digital_money.user_service.entity.domian.User;
import dgdiego_digital_money.user_service.entity.dto.AccountRequestInitDTO;
import dgdiego_digital_money.user_service.entity.dto.RegistrationRequestDTO;
import dgdiego_digital_money.user_service.entity.dto.RegistrationResponseDTO;
import dgdiego_digital_money.user_service.exceptions.ResourceNotFoundException;
import dgdiego_digital_money.user_service.repository.IFeingAccountRepository;
import dgdiego_digital_money.user_service.repository.IRolRepository;
import dgdiego_digital_money.user_service.repository.IUserRepository;
import dgdiego_digital_money.user_service.service.implementation.PermissionService;
import dgdiego_digital_money.user_service.service.implementation.RolService;
import dgdiego_digital_money.user_service.service.implementation.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
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

    @MockBean
    private IFeingAccountRepository feignAccountRepository;

    @MockBean
    private PermissionService permissionService;

    @BeforeEach
    void setup() {
        // Insertamos un rol en la BD porque el servicio lo necesita
        Rol rol = new Rol();
        rol.setName("USER");
        rolRepository.save(rol);

        doNothing().when(permissionService).canAccess(anyLong());
    }

    @Test
    void register_ShouldPersistUser() {

        Mockito.when(feignAccountRepository.create(Mockito.any(AccountRequestInitDTO.class)))
                .thenReturn(1L); // devuelve el ID de la cuenta creada

        // 📌 Datos de registro
        RegistrationRequestDTO dto = new RegistrationRequestDTO();
        dto.setEmail("integration@example.com");
        dto.setDni("12345678");
        dto.setPassword("password123");
        dto.setName("Diego");
        dto.setLastname("Gimenez");
        dto.setPhone("099999999");

        // 📌 Ejecuto el método
        RegistrationResponseDTO response = userService.register(dto);

        // 📌 Verificaciones
        assertNotNull(response);
        assertEquals("integration@example.com", response.getEmail());

        List<User> allUsers = userRepository.findAll();
        assertEquals(1, allUsers.size());

        User saved = allUsers.get(0);
        assertTrue(passwordEncoder.matches("password123", saved.getPassword()));

        // 📌 Verifico que el Feign client fue invocado una vez
        Mockito.verify(feignAccountRepository, times(1)).create(Mockito.any(AccountRequestInitDTO.class));
    }

    @Test
    void findByEmail_ShouldThrow_WhenNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> userService.findByEmail("noexist@example.com"));
    }

    @Test
    void update_ShouldPersistChanges() {
        // 📌 Creamos un usuario inicial
        User existingUser = new User();
        existingUser.setEmail("original@example.com");
        existingUser.setDni("12345678");
        existingUser.setPassword(passwordEncoder.encode("OldPassword1"));
        existingUser.setName("Diego");
        existingUser.setLastname("Gimenez");
        existingUser.setPhone("099999999");
        userRepository.save(existingUser);

        // 📌 Preparamos los cambios
        User updateRequest = new User();
        updateRequest.setId(existingUser.getId());
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPassword("Newpass1"); // cumple regex
        updateRequest.setName("Diego Updated");
        updateRequest.setLastname("Gimenez Updated");
        updateRequest.setDni("87654321");
        updateRequest.setPhone("098888888");

        // 📌 Llamamos al servicio
        User updatedUser = userService.update(updateRequest);

        // 📌 Verificamos la respuesta
        assertNotNull(updatedUser);
        assertEquals(existingUser.getId(), updatedUser.getId());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("Diego Updated", updatedUser.getName());
        assertEquals("Gimenez Updated", updatedUser.getLastname());
        assertEquals("87654321", updatedUser.getDni());
        assertEquals("098888888", updatedUser.getPhone());
        assertTrue(passwordEncoder.matches("Newpass1", updatedUser.getPassword()));

        // 📌 Verificamos la persistencia en la base de datos
        User persisted = userRepository.findById(existingUser.getId()).orElseThrow();
        assertEquals("updated@example.com", persisted.getEmail());
        assertTrue(passwordEncoder.matches("Newpass1", persisted.getPassword()));
    }

    @Test
    void update_ShouldThrow_WhenEmailAlreadyExists() {
        // 📌 Usuario existente
        User user1 = new User();
        user1.setEmail("existing@example.com");
        user1.setPassword(passwordEncoder.encode("Password1"));
        user1.setDni("12345678");
        user1.setName("Diego");
        user1.setLastname("Gimenez");
        user1.setPhone("099999999");
        userRepository.save(user1);

        // 📌 Usuario a actualizar
        User user2 = new User();
        user2.setEmail("original@example.com");
        user2.setPassword(passwordEncoder.encode("Password2"));
        user2.setDni("143347899");
        user2.setName("Diego");
        user2.setLastname("Gómez");
        user2.setPhone("098888888");
        userRepository.save(user2);

        // Intentamos actualizar user2 con email de user1
        User updateRequest = new User();
        updateRequest.setId(user2.getId());
        updateRequest.setEmail("existing@example.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.update(updateRequest);
        });

        assertEquals("No es posible asignar este email existing@example.com", exception.getMessage());
    }

    @Test
    void update_ShouldThrow_WhenPasswordInvalid() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("Valid123"));
        user.setDni("12345678");
        user.setName("Diego");
        user.setLastname("Gimenez");
        user.setPhone("099999999");
        userRepository.save(user);

        User updateRequest = new User();
        updateRequest.setId(user.getId());
        updateRequest.setPassword("short"); // inválido según regex

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.update(updateRequest);
        });

        assertEquals("El formato del password no es correcto", exception.getMessage());
    }

}
