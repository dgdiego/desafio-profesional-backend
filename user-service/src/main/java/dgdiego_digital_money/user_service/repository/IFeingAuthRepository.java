package dgdiego_digital_money.user_service.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name ="auth-service")
public interface IFeingAuthRepository {
    @RequestMapping(method= RequestMethod.POST,value ="/auth/logout")
    void logout();
}
