package dgdiego_digital_money.auth_service.service;

import dgdiego_digital_money.auth_service.entity.dto.LoginRequestDto;
import dgdiego_digital_money.auth_service.entity.dto.LoginResponseDto;
import dgdiego_digital_money.auth_service.entity.dto.User;

import java.util.List;

public interface IUserService {
    User loginLookup(String email);
}
