package dgdiego_digital_money.user_service.service.implementation;

import dgdiego_digital_money.user_service.repository.IFeingAccountRepository;
import dgdiego_digital_money.user_service.repository.IFeingAuthRepository;
import dgdiego_digital_money.user_service.service.IAccountService;
import dgdiego_digital_money.user_service.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements IAccountService {


    @Autowired
    private IFeingAccountRepository feingAccountRepository;

    @Override
    public Long create(Long userId) {
        return feingAccountRepository.create(userId);
    }
}
