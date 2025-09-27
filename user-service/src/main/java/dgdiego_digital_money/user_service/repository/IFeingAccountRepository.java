package dgdiego_digital_money.user_service.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name ="accounts-service")
public interface IFeingAccountRepository {
    @RequestMapping(method= RequestMethod.POST,value ="/accounts/create")
    Long create(Long userId);
}
