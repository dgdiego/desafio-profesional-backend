package dgdiego_digital_money.account_service.repository;

import dgdiego_digital_money.account_service.entity.domian.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAccountRepository extends JpaRepository<Account,Long> {
    Optional<Account> findByUserId(Long userId);

    Optional<Account> findByAlias(String alias);

    Optional<Account> findByCvu(String cvu);

}
