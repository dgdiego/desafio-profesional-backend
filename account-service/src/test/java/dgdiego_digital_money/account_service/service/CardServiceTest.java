package dgdiego_digital_money.account_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.entity.domian.Card;
import dgdiego_digital_money.account_service.entity.domian.CardType;
import dgdiego_digital_money.account_service.entity.dto.CardCreateDto;
import dgdiego_digital_money.account_service.entity.dto.CardDto;
import dgdiego_digital_money.account_service.exceptions.ResourceAlreadyExistsException;
import dgdiego_digital_money.account_service.exceptions.ResourceNotFoundException;
import dgdiego_digital_money.account_service.repository.ICardRepository;
import dgdiego_digital_money.account_service.service.implementation.AccountService;
import dgdiego_digital_money.account_service.service.implementation.CardService;
import dgdiego_digital_money.account_service.service.implementation.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private PermissionService permissionService;

    @Mock
    private ICardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    private Account account;
    private CardCreateDto cardCreateDto;
    private Card card;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .id(1L)
                .userId(10L)
                .balance(1000.0)
                .build();

        cardCreateDto = CardCreateDto.builder()
                .number("1234567890123456")
                .type(CardType.DEBIT)
                .expirationDate(LocalDate.of(2030, 12, 31))
                .build();

        card = Card.builder()
                .id(1L)
                .number(cardCreateDto.getNumber())
                .type(cardCreateDto.getType())
                .expirationDate(cardCreateDto.getExpirationDate())
                .account(account)
                .accountId(account.getId())
                .build();
    }

    @Test
    void create_ShouldReturnNewCard_WhenCardDoesNotExist() {
        when(accountService.findById(1L)).thenReturn(account);
        doNothing().when(permissionService).canAccess(account.getUserId());
        when(cardRepository.findByNumber(cardCreateDto.getNumber())).thenReturn(Optional.empty());
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        Card result = cardService.create(cardCreateDto, 1L);

        assertNotNull(result);
        assertEquals(cardCreateDto.getNumber(), result.getNumber());
        assertEquals(account, result.getAccount());

        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void create_ShouldThrowException_WhenCardAlreadyExists() {
        when(accountService.findById(1L)).thenReturn(account);
        doNothing().when(permissionService).canAccess(account.getUserId());
        when(cardRepository.findByNumber(cardCreateDto.getNumber())).thenReturn(Optional.of(card));

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> cardService.create(cardCreateDto, 1L));

        assertEquals("Ya existe una tarjeta con el número " + cardCreateDto.getNumber(), exception.getMessage());
    }

    @Test
    void getAllFromAccount_ShouldReturnCards() {
        when(accountService.findById(1L)).thenReturn(account);
        doNothing().when(permissionService).canAccess(account.getUserId());
        when(cardRepository.findByAccountId(1L)).thenReturn(Arrays.asList(card));

        List<Card> result = cardService.getAllFromAccount(1L);

        assertEquals(1, result.size());
        assertEquals(card.getNumber(), result.get(0).getNumber());
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(accountService.findById(account.getId())).thenReturn(account);
        doNothing().when(permissionService).canAccess(account.getUserId());

        cardService.delete(1L);

        verify(cardRepository).deleteById(1L);
    }

    @Test
    void getFromId_ShouldReturnCard() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(accountService.findById(account.getId())).thenReturn(account);
        doNothing().when(permissionService).canAccess(account.getUserId());

        Card result = cardService.getFromId(1L);

        assertNotNull(result);
        assertEquals(card.getNumber(), result.getNumber());
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> cardService.findById(1L));

        assertEquals("No se encontro la tarjeta con Id 1", exception.getMessage());
    }

    @Test
    void mapToResponseDto_ShouldMapCorrectly() {
        CardDto dto = cardService.mapToResponseDto(card);

        assertNotNull(dto);
        assertEquals(card.getId(), dto.getId());
        assertEquals(card.getNumber(), dto.getNumber());
        assertEquals(card.getType(), dto.getType());
        assertEquals(card.getAccount().getId(), dto.getAccountId());
        assertEquals(card.getExpirationDate(), dto.getExpirationDate());
    }

    @Test
    void mapToResponseDto_ShouldReturnNull_WhenCardIsNull() {
        CardDto dto = cardService.mapToResponseDto(null);
        assertNull(dto);
    }
}

