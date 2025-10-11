package dgdiego_digital_money.account_service.entity.domian;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
    public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    private LocalDateTime dateTime;
    private String detail;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    // **************   ACCOUNT   ********************
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;

    // **************   CARD   ********************
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    @JsonIgnore
    private Card card;

    // **************   ACCOUNT FROM   ********************
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_from_id")
    @JsonIgnore
    private Account relatedAccount;

}
