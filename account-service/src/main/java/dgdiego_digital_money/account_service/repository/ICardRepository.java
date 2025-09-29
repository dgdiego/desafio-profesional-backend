package dgdiego_digital_money.account_service.repository;

import dgdiego_digital_money.account_service.entity.domian.Card;
import dgdiego_digital_money.account_service.entity.domian.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICardRepository extends JpaRepository<Card,Long> {
    Optional<Card> findByNumber(String number);

    List<Card> findByAccountId(Long accountId);
}
