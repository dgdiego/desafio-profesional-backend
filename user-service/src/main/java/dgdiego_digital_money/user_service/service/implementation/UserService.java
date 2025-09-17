package dgdiego_digital_money.user_service.service.implementation;

import com.netflix.discovery.converters.Auto;
import dgdiego_digital_money.user_service.entity.domian.Rol;
import dgdiego_digital_money.user_service.entity.domian.User;
import dgdiego_digital_money.user_service.entity.dto.RegistrationRequestDTO;
import dgdiego_digital_money.user_service.entity.dto.RegistrationResponseDTO;
import dgdiego_digital_money.user_service.entity.dto.UserDto;
import dgdiego_digital_money.user_service.exceptions.ResourceNotFoundException;
import dgdiego_digital_money.user_service.repository.IRolRepository;
import dgdiego_digital_money.user_service.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
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
    PasswordEncoder passwordEncoder;


    public List<User> listAll() {
        log.info("Listando los usuarios");
        return userRepository.findAll();
    }

    public RegistrationResponseDTO register (RegistrationRequestDTO registrationDto) throws BadRequestException {
        //busco si ya existe un usuario por email y dni
        if(userRepository.findByEmailAndDni(registrationDto.getEmail(), registrationDto.getDni()).isPresent()) {
            throw new BadRequestException("Usuario duplicado. No es posible registrar");
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
                .cvu(generateCvu())
                .alias(generateAlias())
                .roles(Set.of(userRol))
                .build();

        userRepository.save(user);

        return mapToRegistrationResponseDto(user);

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

    private String generateCvu(){
        SecureRandom random = new SecureRandom();
        return String.valueOf(
                (long) (Math.pow(10, 21) + random.nextDouble() * (Math.pow(10, 22) - Math.pow(10, 21) - 1))
        );
    }

    private String generateAlias(){
        String filePath = "/list-alias.txt";
        String alias = "";
        try {

            InputStream inputStream = getClass().getResourceAsStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            List<String> aliases = reader.lines().collect(Collectors.toList());



            if (aliases.size() < 3) {
                String mensaje = "El archivo aliases no tiene suficientes palabras.";
                log.error(mensaje);
                throw new IOException(mensaje);
            }

            Random random = new Random();
            for (int i = 0; i < 3; i++) {
                int index = random.nextInt(aliases.size());
                alias+=aliases.get(index)+".";
            }
            alias = alias.substring(0,alias.length()-1);

        } catch (IOException e) {
            log.error(e.getMessage());
            alias = "perro-gato-liebre";
        }
        return alias;
    }


    public RegistrationResponseDTO mapToRegistrationResponseDto(User user){
        RegistrationResponseDTO response = null;
        if (user != null){
            response = new RegistrationResponseDTO();
            response.setId(user.getId());
            response.setName(user.getName());
            response.setLastname(user.getLastname());
            response.setDni(user.getDni());
            response.setEmail(user.getEmail());
            response.setAlias(user.getAlias());
            response.setPhone(user.getPhone());
            response.setCvu(user.getCvu());
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
            response.setAlias(user.getAlias());
            response.setPhone(user.getPhone());
            response.setCvu(user.getCvu());
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
}
