package dgdiego_digital_money.user_service.service;

import dgdiego_digital_money.user_service.entity.dto.AccountRequestInitDTO;

public interface IAccountService {
    Long create(AccountRequestInitDTO data);
}
