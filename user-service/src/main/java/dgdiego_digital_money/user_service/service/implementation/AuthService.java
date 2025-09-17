package dgdiego_digital_money.user_service.service.implementation;

import dgdiego_digital_money.user_service.repository.IFeingAuthRepository;
import dgdiego_digital_money.user_service.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {


    @Autowired
    private IFeingAuthRepository feingAuthRepository;
    @Override
    public void logout() {
        feingAuthRepository.logout();
    }
}
