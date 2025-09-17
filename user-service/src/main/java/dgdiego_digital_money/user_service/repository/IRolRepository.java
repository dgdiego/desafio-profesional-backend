package dgdiego_digital_money.user_service.repository;

import dgdiego_digital_money.user_service.entity.domian.Rol;
import dgdiego_digital_money.user_service.entity.domian.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRolRepository extends JpaRepository<Rol,Long> {

    Optional<Rol> findByName(String name);
}
