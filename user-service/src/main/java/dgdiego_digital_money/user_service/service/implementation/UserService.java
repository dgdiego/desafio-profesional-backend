package dgdiego_digital_money.user_service.service.implementation;

import dgdiego_digital_money.user_service.entity.domian.Rol;
import dgdiego_digital_money.user_service.entity.domian.User;
import dgdiego_digital_money.user_service.entity.dto.*;
import dgdiego_digital_money.user_service.exceptions.ResourceNotFoundException;
import dgdiego_digital_money.user_service.repository.IUserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private RolService rolService;

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    PasswordEncoder passwordEncoder;


    public List<User> listAll() {
        log.info("Listando los usuarios");
        return userRepository.findAll();
    }

    @Transactional
    public RegistrationResponseDTO register (RegistrationRequestDTO registrationDto) {
        //busco si ya existe un usuario por email y dni
        if(userRepository.findByEmailAndDni(registrationDto.getEmail(), registrationDto.getDni()).isPresent()) {
            throw new IllegalArgumentException("Usuario duplicado. No es posible registrar");
        }

        //TODO validaciones

        Rol userRol = rolService.findByName("USER");

        User user = User.builder()
                .email(registrationDto.getEmail())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .dni(registrationDto.getDni())
                .name(registrationDto.getName())
                .lastname(registrationDto.getLastname())
                .phone(registrationDto.getPhone())
                .roles(Set.of(userRol))
                .build();

        userRepository.save(user);

        AccountRequestInitDTO initAccount = AccountRequestInitDTO.builder()
                .userId(user.getId())
                .alias(accountService.generateAlias())
                .cvu(accountService.generateCvu())
                .build();

        accountService.create(initAccount);

        return mapToRegistrationResponseDto(user, initAccount);

    }

    @Transactional
    public User update(User userRequest){
        permissionService.canAccess(userRequest.getId());

        User userToUpdate = findById(userRequest.getId(),true);

        //si actualiza el email chequeo que no exista otro usuario con ese
        if(!userToUpdate.getEmail().equals(userRequest.getEmail())){
            try{
                User existedUser = findByEmail(userRequest.getEmail());
                throw new IllegalArgumentException("No es posible asignar este email "+ userRequest.getEmail());
            }catch (ResourceNotFoundException ex){
                userToUpdate.setEmail(userRequest.getEmail());
            }
        }
        // si actualiza el password
        if(userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()){
            String regex = "^(?=.*[a-z])(?=.*?[0-9]).{8,}$";
            Pattern pattern = Pattern.compile(regex);
            if(!pattern.matcher(userRequest.getPassword()).matches()){
                throw new IllegalArgumentException("El formato del password no es correcto");
            }
            userToUpdate.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        userToUpdate.setName(userRequest.getName());
        userToUpdate.setLastname(userRequest.getLastname());
        userToUpdate.setDni(userRequest.getDni());
        userToUpdate.setPhone(userRequest.getPhone());

        userRepository.save(userToUpdate);

        return userToUpdate;
    }


    public void logout(){
        authService.logout();
    }

    public User findByEmail(String email){
        Optional<User> userSearched = userRepository.findByEmail(email);
        if (userSearched.isPresent()) {
            return userSearched.get();
        } else {
            String message = "No se encontro el usuario con email " + email;
            log.info(message);
            throw new ResourceNotFoundException(message);
        }

    }

    public User findById(Long id, Boolean withPassword){
        permissionService.canAccess(id);
        Optional<User> userSearched = userRepository.findById(id);
        if (userSearched.isPresent()) {
            User userResponse = userSearched.get();
            if(!withPassword){
                userResponse.setPassword(null);
            }
            return userResponse;
        } else {
            String message = "No se encontro el usuario con ID " + id;
            log.info(message);
            throw new ResourceNotFoundException(message);
        }

    }




    public RegistrationResponseDTO mapToRegistrationResponseDto(User user, AccountRequestInitDTO initAccount){
        RegistrationResponseDTO response = null;
        if (user != null){
            response = new RegistrationResponseDTO();
            response.setId(user.getId());
            response.setName(user.getName());
            response.setLastname(user.getLastname());
            response.setDni(user.getDni());
            response.setEmail(user.getEmail());
            response.setAlias(initAccount.getAlias());
            response.setPhone(user.getPhone());
            response.setCvu(initAccount.getCvu());
        }
        return response;
    }

    public UserDto mapToResponseDto(User user){
        UserDto response = null;
        if (user != null){
            response = new UserDto();
            response.setId(user.getId());
            response.setName(user.getName());
            response.setLastname(user.getLastname());
            response.setDni(user.getDni());
            response.setEmail(user.getEmail());
            response.setPhone(user.getPhone());
            response.setPassword(user.getPassword());

            if(!user.getRoles().isEmpty()){
                response.setRoles(user.getRoles()
                        .stream()
                        .map(role -> rolService.mapToResponseDto(role))
                        .collect(Collectors.toList())
                );
            }
        }
        return response;
    }

    public User mapToEntity(UserRequestDTO requestDTO){
        User user = null;
        if(requestDTO != null){
            user = new User();
            user.setId(requestDTO.getId());
            user.setName(requestDTO.getName());
            user.setLastname(requestDTO.getLastname());
            user.setDni(requestDTO.getDni());
            user.setEmail(requestDTO.getEmail());
            user.setPassword(requestDTO.getPassword());
            user.setPhone(requestDTO.getPhone());

        }
        return user;
    }
}
