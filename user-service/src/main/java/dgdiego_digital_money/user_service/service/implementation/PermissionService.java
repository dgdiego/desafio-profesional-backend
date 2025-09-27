package dgdiego_digital_money.user_service.service.implementation;

import dgdiego_digital_money.user_service.config.security.JwtService;
import dgdiego_digital_money.user_service.exceptions.ForbiddenAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PermissionService {

    @Autowired
    JwtService jwtService;

    public void canAccess(Long userId){
        Long currentUser = getUserId();
        List<String> roles = getRoles();
        if (roles.stream().anyMatch("USER"::equals) && currentUser != userId){
            throw new ForbiddenAccessException("¡ACCESO DENEGADO!");
        }
    }

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return  jwtService.getIdUsuarioFromToken(authentication.getCredentials().toString());
    }

    public List<String> getRoles(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return Collections.emptyList();
        }

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

}
