package dgdiego_digital_money.auth_service.service.implementation;

import dgdiego_digital_money.auth_service.entity.dto.User;
import dgdiego_digital_money.auth_service.repository.IFeingUserRepository;
import dgdiego_digital_money.auth_service.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    private IFeingUserRepository feingUserRepository;
    @Override
    public User loginLookup(String email) {
        return feingUserRepository.loginLookup(email);
    }
}
