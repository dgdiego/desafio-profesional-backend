package dgdiego_digital_money.user_service.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GatewayAuthFilter extends OncePerRequestFilter {

    private static final String GATEWAY_HEADER = "X-Gateway-Auth";
    private static final String GATEWAY_SECRET = "requestFromGateway";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(GATEWAY_HEADER);

        if (header == null || !header.equals(GATEWAY_SECRET)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Requests deben pasar por el API Gateway");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

