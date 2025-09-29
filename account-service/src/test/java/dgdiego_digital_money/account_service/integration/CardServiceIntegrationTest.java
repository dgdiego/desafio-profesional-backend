package dgdiego_digital_money.account_service.integration;


import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.entity.domian.Card;
import dgdiego_digital_money.account_service.entity.domian.CardType;
import dgdiego_digital_money.account_service.entity.dto.CardCreateDto;
import dgdiego_digital_money.account_service.repository.ICardRepository;
import dgdiego_digital_money.account_service.service.implementation.AccountService;
import dgdiego_digital_money.account_service.service.implementation.CardService;
import dgdiego_digital_money.account_service.service.implementation.PermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CardServiceIntegrationTest {

    @Autowired
    private CardService cardService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ICardRepository cardRepository;

    @MockBean
    private PermissionService permissionService; // 🔹 mock en test de integración

    @Test
    void delete_ShouldRemoveCard() {

        Authentication auth = new UsernamePasswordAuthenticationToken("200", "password");
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 1. Crear una cuenta real
        Long userId = 99L;
        Long accountId = accountService.create(userId);
        Account account = accountService.findById(accountId);

        // 2. Crear y guardar una tarjeta asociada
        Card card = Card.builder()
                .number("1234567890123456")
                .type(CardType.DEBIT)
                .expirationDate(LocalDate.now().plusYears(2))
                .account(account)
                .accountId(accountId) // 👈 importante: usar accountId
                .build();

        card = cardRepository.save(card);

        // Verificar que existe en la BD
        assertThat(cardRepository.findById(card.getId())).isPresent();

        // 3. Ejecutar el delete del servicio
        cardService.delete(card.getId());

        // 4. Verificar que fue eliminada
        assertThat(cardRepository.findById(card.getId())).isNotPresent();
    }

    @Test
    void getFromId_ShouldReturnCard() {
        // 1. Crear una cuenta real
        Long userId = 100L;
        Long accountId = accountService.create(userId);

        Account account = accountService.findById(accountId);

        // 2. Crear y guardar una tarjeta asociada
        Card card = Card.builder()
                .number("6543210987654321")
                .type(CardType.CREDIT)
                .expirationDate(LocalDate.now().plusYears(3))
                .account(account)
                .accountId(accountId)
                .build();

        card = cardRepository.save(card);

        // 3. Ejecutar el método del servicio
        Card foundCard = cardService.getFromId(card.getId());

        // 4. Verificar que los datos coinciden
        assertThat(foundCard).isNotNull();
        assertThat(foundCard.getId()).isEqualTo(card.getId());
        assertThat(foundCard.getNumber()).isEqualTo("6543210987654321");
        assertThat(foundCard.getType()).isEqualTo(CardType.CREDIT);
        assertThat(foundCard.getAccountId()).isEqualTo(accountId);
    }

    @Test
    void createAndGetFromId_ShouldWorkCorrectly() {
        // 1. Crear una cuenta real
        Long userId = 200L;
        Long accountId = accountService.create(userId);

        // 2. Crear DTO de tarjeta
        CardCreateDto cardDto = CardCreateDto.builder()
                .number("1111222233334444")
                .type(CardType.CREDIT)
                .expirationDate(LocalDate.now().plusYears(4))
                .build();

        // 3. Usar el servicio para crear la tarjeta
        Card createdCard = cardService.create(cardDto, accountId);
        createdCard.setAccountId(accountId);

        // 4. Recuperar la tarjeta con el servicio
        Card foundCard = cardService.getFromId(createdCard.getId());

        // 5. Verificar datos
        assertThat(foundCard).isNotNull();
        assertThat(foundCard.getId()).isEqualTo(createdCard.getId());
        assertThat(foundCard.getNumber()).isEqualTo("1111222233334444");
        assertThat(foundCard.getType()).isEqualTo(CardType.CREDIT);
        assertThat(foundCard.getAccountId()).isEqualTo(accountId);
    }
}


