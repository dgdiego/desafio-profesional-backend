package dgdiego_digital_money.user_service.service;

import dgdiego_digital_money.user_service.entity.domian.Rol;
import dgdiego_digital_money.user_service.entity.domian.User;
import dgdiego_digital_money.user_service.entity.dto.AccountRequestInitDTO;
import dgdiego_digital_money.user_service.entity.dto.RegistrationRequestDTO;
import dgdiego_digital_money.user_service.entity.dto.RegistrationResponseDTO;
import dgdiego_digital_money.user_service.exceptions.ResourceNotFoundException;
import dgdiego_digital_money.user_service.repository.IUserRepository;
import dgdiego_digital_money.user_service.service.implementation.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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

    @Mock
    private AccountService accountService;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    @Spy
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
    void register_ShouldSaveNewUserAndCreateAccount_WhenNotDuplicate() {
        // Arrange
        RegistrationRequestDTO dto = new RegistrationRequestDTO();
        dto.setEmail("new@example.com");
        dto.setDni("12345678");
        dto.setPassword("password");
        dto.setName("Diego");
        dto.setLastname("Gimenez");
        dto.setPhone("099999999");

        // El repositorio no encuentra un usuario existente
        when(userRepository.findByEmailAndDni(dto.getEmail(), dto.getDni()))
                .thenReturn(Optional.empty());

        // Simulamos la codificación de la contraseña
        when(passwordEncoder.encode(dto.getPassword()))
                .thenReturn("encodedPassword");

        // Simulamos el rol USER
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setName("USER");
        when(rolService.findByName("USER")).thenReturn(rol);

        // Simulamos la creación del usuario (el save asigna ID)
        User savedUser = User.builder()
                .id(10L)
                .email(dto.getEmail())
                .password("encodedPassword")
                .dni(dto.getDni())
                .name(dto.getName())
                .lastname(dto.getLastname())
                .phone(dto.getPhone())
                .roles(Set.of(rol))
                .build();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(10L); // Simula que el repositorio le asigna ID al guardar
            return u;
        });

        // Simulamos generación de alias y CVU
        when(accountService.generateAlias()).thenReturn("alias123");
        when(accountService.generateCvu()).thenReturn("cvu123");

        // Como create devuelve un Long, mockeamos el retorno
        when(accountService.create(any(AccountRequestInitDTO.class))).thenReturn(1L);

        // Act
        RegistrationResponseDTO response = userService.register(dto);

        // Assert
        assertNotNull(response);
        assertEquals(dto.getEmail(), response.getEmail());
        assertEquals(dto.getDni(), response.getDni());
        assertEquals(dto.getName(), response.getName());
        assertEquals(dto.getLastname(), response.getLastname());

        // Verifica que se haya guardado el usuario
        verify(userRepository, times(1)).save(any(User.class));

        // Verifica que se haya buscado el rol USER
        verify(rolService, times(1)).findByName("USER");

        // Verifica que se haya llamado a accountService para generar alias y CVU
        verify(accountService, times(1)).generateAlias();
        verify(accountService, times(1)).generateCvu();

        // Verifica que se haya llamado a create con los datos correctos
        ArgumentCaptor<AccountRequestInitDTO> accountCaptor = ArgumentCaptor.forClass(AccountRequestInitDTO.class);
        verify(accountService, times(1)).create(accountCaptor.capture());
        AccountRequestInitDTO capturedAccount = accountCaptor.getValue();

        assertEquals(savedUser.getId(), capturedAccount.getUserId());
        assertEquals("alias123", capturedAccount.getAlias());
        assertEquals("cvu123", capturedAccount.getCvu());
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

    @Test
    void update_ShouldUpdateUserSuccessfully_WhenValidDataProvided() {
        User userRequest = new User();
        userRequest.setId(1L);
        userRequest.setEmail("new@example.com");
        userRequest.setPassword("password1");
        userRequest.setName("Diego");
        userRequest.setLastname("Gimenez");
        userRequest.setDni("12345678");
        userRequest.setPhone("099999999");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("encodedOldPassword");

        doNothing().when(permissionService).canAccess(1L);
        doReturn(existingUser).when(userService).findById(1L, true);

        // Simula que no existe otro usuario con ese email
        doThrow(new ResourceNotFoundException("No existe usuario con email"))
                .when(userService).findByEmail("new@example.com");

        when(passwordEncoder.encode("password1")).thenReturn("encodedPassword1");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.update(userRequest);

        assertNotNull(updatedUser);
        assertEquals("new@example.com", updatedUser.getEmail());
        assertEquals("encodedPassword1", updatedUser.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void update_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        User userRequest = new User();
        userRequest.setId(1L);
        userRequest.setEmail("existing@example.com");
        userRequest.setPassword("password1");
        userRequest.setName("Diego");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("encodedOldPassword");

        doNothing().when(permissionService).canAccess(1L);
        doReturn(existingUser).when(userService).findById(1L, true);

        // Simula que ya existe otro usuario con ese email
        doReturn(new User()).when(userService).findByEmail("existing@example.com");

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.update(userRequest)
        );

        assertEquals("No es posible asignar este email existing@example.com", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_ShouldThrowException_WhenPasswordFormatIsInvalid() {
        // Arrange
        User userRequest = new User();
        userRequest.setId(1L);
        userRequest.setEmail("new@example.com");
        userRequest.setPassword("short"); // inválida (no cumple regex)
        userRequest.setName("Diego");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("encodedOldPassword");

        doNothing().when(permissionService).canAccess(1L);
        doReturn(existingUser).when(userService).findById(1L, true);

        // Simula que no existe otro usuario con ese email
        doThrow(new ResourceNotFoundException("No existe usuario con email"))
                .when(userService).findByEmail("new@example.com");

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.update(userRequest)
        );

        assertEquals("El formato del password no es correcto", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }



}

