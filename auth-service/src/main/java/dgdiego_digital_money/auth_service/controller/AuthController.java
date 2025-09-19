package dgdiego_digital_money.auth_service.controller;

import dgdiego_digital_money.auth_service.entity.dto.LoginRequestDto;
import dgdiego_digital_money.auth_service.entity.dto.LoginResponseDto;
import dgdiego_digital_money.auth_service.service.implementation.AuthService;
import dgdiego_digital_money.auth_service.service.implementation.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Autorización", description = "Operaciones relacionadas con autenticación de usuarios")
public class AuthController {

    @Autowired
    private AuthService authService;
    @PostMapping(path = "/login")
    @Operation(summary = "Login", description = "Atenticarse en la aplicación")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginDTO) {
        log.info("Intento de logueo para el usuario: {}", loginDTO.getEmail());
        return ResponseEntity.ok(authService.login(loginDTO));
    }

    @PostMapping(path = "/logout")
    @Hidden
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token.substring(7));
        return ResponseEntity.ok().build();
    }
}
