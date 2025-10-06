package dgdiego_digital_money.auth_service.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Autowired
    private HttpServletRequest request;

    @Override
    public void apply(RequestTemplate template) {

        /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getCredentials() instanceof String jwt) {
            template.header("Authorization", "Bearer " + jwt);
        }*/

        String headerGatewayName = "X-Gateway-Auth";
        String headerGatewayValue = request.getHeader(headerGatewayName);

        if (headerGatewayValue != null) {
            template.header(headerGatewayName, headerGatewayValue);
        }
    }
}
