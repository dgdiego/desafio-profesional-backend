package dgdiego_digital_money.auth_service.service.implementation;

import com.netflix.discovery.converters.Auto;
import dgdiego_digital_money.auth_service.entity.domian.ExpiredToken;
import dgdiego_digital_money.auth_service.entity.dto.LoginRequestDto;
import dgdiego_digital_money.auth_service.entity.dto.LoginResponseDto;
import dgdiego_digital_money.auth_service.entity.dto.User;
import dgdiego_digital_money.auth_service.repository.IExpiredTokenRepository;
import dgdiego_digital_money.auth_service.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private IExpiredTokenRepository expiredTokenRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto loginDto){
        User user = userService.loginLookup(loginDto.getEmail());

        if (!passwordEncoder.matches(loginDto.getPassword(),user.getPassword())){
            throw new IllegalArgumentException("Credenciales incorrectas");
        }

        LoginResponseDto response = LoginResponseDto.builder()
                .token(jwtService.getToken(user))
                .build();

        return response;
    }

    public void logout(String token){
        Date expirationDate = jwtService.getExpiration(token);

        ExpiredToken expiredToken = ExpiredToken.builder()
                .token(token)
                .expirationDate(expirationDate)
                .build();

        expiredTokenRepository.save(expiredToken);
    }

}
