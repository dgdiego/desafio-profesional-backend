package dgdiego_digital_money.user_service.repository;

import dgdiego_digital_money.user_service.entity.domian.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmailAndDni(String email, String dni);

    Optional<User> findByEmail(String email);
}
