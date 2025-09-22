package dgdiego_digital_money.user_service.controller;

import dgdiego_digital_money.user_service.entity.dto.RegistrationRequestDTO;
import dgdiego_digital_money.user_service.entity.dto.RegistrationResponseDTO;
import dgdiego_digital_money.user_service.service.implementation.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
@Tag(name = "Usuarios", description = "Operaciones relacionadas con administración de usuarios")
public class UserController {
    @Autowired
    private UserService userService;

    /*@GetMapping(path = "/list")
    public ResponseEntity<?> login() {
        return ResponseEntity.ok(userService.listAll());
    }*/

    @PostMapping(path = "/register")
    @Operation(summary = "Register", description = "Registrarse en la aplicación")
    public ResponseEntity<RegistrationResponseDTO> register(@Valid @RequestBody RegistrationRequestDTO registrationRequestDTO) {
        //try{
            return  ResponseEntity.ok(userService.register(registrationRequestDTO));
        /*} catch (BadRequestException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (Exception ex){
            log.error("Registro incorrecto para el usuario: {}", registrationRequestDTO.getName() + " "+registrationRequestDTO.getLastname());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }*/
    }

    @PostMapping(path = "/login-lookup")
    @Hidden
    public ResponseEntity<?> loginLookup(@RequestBody String email) {
        return  ResponseEntity.ok(userService.mapToResponseDto(userService.findByEmail(email)));
    }

    @PostMapping(path = "/logout")
    @Operation(summary = "Logout", description = "Desloguearse de la aplicación")
    @Parameter(
            name = "Authorization",
            in = ParameterIn.HEADER,
            required = true,
            description = "JWT Bearer token",
            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
    )
    public ResponseEntity<?> logout() {
        userService.logout();
        return  ResponseEntity.ok().build();
    }

}
