package dgdiego_digital_money.auth_service.security;

import dgdiego_digital_money.auth_service.entity.domian.ExpiredToken;
import dgdiego_digital_money.auth_service.entity.dto.Role;
import dgdiego_digital_money.auth_service.entity.dto.User;
import dgdiego_digital_money.auth_service.repository.IExpiredTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Value("${token.duration.hours}")
    private Integer DURATION_HOURS;

    @Autowired
    private IExpiredTokenRepository expiredTokenRepository;

    public String getToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        claims.put("roles", roleNames);
        claims.put("id", user.getId());

        if (user.getName() != null) {
            claims.put("name", user.getName() +" "+user.getLastname());
        }

        return getToken(claims, user);
    }


    private String getToken(Map<String, Object> extraClaims, User user) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * DURATION_HOURS))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !isTokenRevoked(token));
    }

    private Claims getAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    private boolean isTokenRevoked(String token) {
        Optional<ExpiredToken> expiredToken = expiredTokenRepository.findByToken(token);
        return expiredToken.isPresent();
    }
}
