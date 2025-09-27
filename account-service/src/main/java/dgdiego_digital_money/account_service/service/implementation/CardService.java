package dgdiego_digital_money.account_service.service.implementation;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.entity.domian.Card;
import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.dto.CardDto;
import dgdiego_digital_money.account_service.entity.dto.TransactionDto;
import dgdiego_digital_money.account_service.exceptions.ResourceNotFoundException;
import dgdiego_digital_money.account_service.repository.IAccountRepository;
import dgdiego_digital_money.account_service.repository.ICardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CardService {

    @Autowired
    AccountService accountService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    ICardRepository cardRepository;

    public Card create (CardDto cardDto){
        Optional<Card> existCard = cardRepository.findByNumber(cardDto.getNumber());
        if(existCard.isPresent()) {
            throw new IllegalArgumentException("Ya existe una tarjeta con el número "+cardDto.getNumber());
        }

        Account account = accountService.findById(cardDto.getAccountId());

        Card newCard = Card.builder()
                .number(cardDto.getNumber())
                .type(cardDto.getType())
                .expirationDate(cardDto.getExpirationDate())
                .account(account)
                .build();

        cardRepository.save(newCard);

        return newCard;
    }

    public Card findById(Long id) {
        Optional<Card> cardSearched = cardRepository.findById(id);
        if (cardSearched.isPresent()) {
            return cardSearched.get();
        } else {
            String message = "No se encontro la tarjeta con Id " + id;
            log.info(message);
            throw new ResourceNotFoundException(message);
        }
    }

    public CardDto mapToResponseDto(Card card){
        CardDto response = null;
        if(card != null){
            response = new CardDto();
            response.setId(card.getId());
            response.setType(card.getType());
            response.setAccountId(card.getAccount().getId());
            response.setNumber(card.getNumber());
            response.setExpirationDate(card.getExpirationDate());
        }
        return  response;
    }


}
