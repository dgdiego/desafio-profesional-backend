package dgdiego_digital_money.user_service.controller;

import dgdiego_digital_money.user_service.entity.dto.RegistrationRequestDTO;
import dgdiego_digital_money.user_service.service.implementation.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    /*@GetMapping(path = "/list")
    public ResponseEntity<?> login() {
        return ResponseEntity.ok(userService.listAll());
    }*/

    @PostMapping(path = "/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequestDTO registrationRequestDTO) {
        try{
            return  ResponseEntity.ok(userService.register(registrationRequestDTO));
        } catch (BadRequestException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (Exception ex){
            log.error("Registro incorrecto para el usuario: {}", registrationRequestDTO.getName() + " "+registrationRequestDTO.getLastname());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping(path = "/login-lookup")
    public ResponseEntity<?> loginLookup(@RequestBody String email) {
        return  ResponseEntity.ok(userService.mapToResponseDto(userService.findByEmail(email)));
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<?> logout(@RequestHeader() Map<String,String> headers) {
        log.info("entró al logout del userservice");
        userService.logout();
        return  ResponseEntity.ok().build();
    }

}
