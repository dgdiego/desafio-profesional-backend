package dgdiego_digital_money.auth_service.repository;

import dgdiego_digital_money.auth_service.entity.dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name ="users-service")
public interface IFeingUserRepository {
    @RequestMapping(method= RequestMethod.GET,value ="/users/login-lookup")
    User loginLookup(String email);
}
