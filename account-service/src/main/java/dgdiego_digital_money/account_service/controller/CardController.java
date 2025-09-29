package dgdiego_digital_money.account_service.controller;

import dgdiego_digital_money.account_service.entity.domian.Card;
import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.dto.CardCreateDto;
import dgdiego_digital_money.account_service.entity.dto.CardDto;
import dgdiego_digital_money.account_service.entity.dto.TransactionDto;
import dgdiego_digital_money.account_service.service.implementation.CardService;
import dgdiego_digital_money.account_service.service.implementation.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/accounts/{accountId}/cards")
@Tag(name = "Tarjetas", description = "Operaciones relacionadas con administración de tarjetas")
public class CardController {

    @Autowired
    private CardService cardService;

    @PostMapping
    public ResponseEntity<CardDto> create(@RequestBody CardCreateDto cardData, @PathVariable Long accountId) {
        return ResponseEntity.ok(cardService.mapToResponseDto(cardService.create(cardData, accountId)));
    }

    @GetMapping
    public ResponseEntity<List<CardDto>> getAllFromAccount(@PathVariable Long accountId) {
        List<Card> list = cardService.getAllFromAccount(accountId);
        List<CardDto> listDto = new ArrayList<>();

        for(Card card : list){
            listDto.add(cardService.mapToResponseDto(card));
        }

        return ResponseEntity.ok(listDto);
    }

    @GetMapping(path = "/{cardId}")
    public ResponseEntity<CardDto> getFromId(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.mapToResponseDto(cardService.getFromId(cardId)));
    }

    @DeleteMapping(path = "/{cardId}")
    public ResponseEntity<?> delete(@PathVariable Long cardId) {
        cardService.delete(cardId);
        return ResponseEntity.ok().build();
    }
}
