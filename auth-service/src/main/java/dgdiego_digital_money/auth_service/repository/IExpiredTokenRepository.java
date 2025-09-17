package dgdiego_digital_money.auth_service.repository;

import dgdiego_digital_money.auth_service.entity.domian.ExpiredToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface IExpiredTokenRepository extends JpaRepository<ExpiredToken,Long> {
    Optional<ExpiredToken> findByToken(String token);

    /**
     * Devuelve un long con la cantidad de filas afectadas
     */
    Integer deleteByExpirationDateLessThanEqual(Date date);
}
