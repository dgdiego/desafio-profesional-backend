package dgdiego_digital_money.auth_service.entity.domian;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "expiredTokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpiredToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Date expirationDate;
}
