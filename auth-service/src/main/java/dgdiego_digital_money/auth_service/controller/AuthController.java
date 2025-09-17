package dgdiego_digital_money.auth_service.controller;

import dgdiego_digital_money.auth_service.entity.dto.LoginRequestDto;
import dgdiego_digital_money.auth_service.service.implementation.AuthService;
import dgdiego_digital_money.auth_service.service.implementation.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginDTO) {
        log.info("Intento de logueo para el usuario: {}", loginDTO.getEmail());
        return ResponseEntity.ok(authService.login(loginDTO));
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token.substring(7));
        return ResponseEntity.ok().build();
    }
}
