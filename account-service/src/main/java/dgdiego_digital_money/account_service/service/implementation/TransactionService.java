package dgdiego_digital_money.account_service.service.implementation;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.entity.domian.Card;
import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.domian.TransactionType;
import dgdiego_digital_money.account_service.entity.dto.CardDepositDto;
import dgdiego_digital_money.account_service.entity.dto.TransactionDto;
import dgdiego_digital_money.account_service.exceptions.ResourceNotFoundException;
import dgdiego_digital_money.account_service.repository.ITransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TransactionService {

    @Autowired
    AccountService accountService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    CardService cardService;

    @Autowired
    ITransactionRepository transactionRepository;

    @Transactional
    public List<Transaction> transactionsDashboard(Long accountId){
        Account account = accountService.findById(accountId);
        permissionService.canAccess(account.getUserId());

        Pageable limit = PageRequest.of(0, 5);
        return transactionRepository.findByAccountIdOrderByDateTimeDesc(accountId, limit);
    }

    @Transactional
    public Transaction getById(Long accountId, Long transactionId){
        Account account = accountService.findById(accountId);
        permissionService.canAccess(account.getUserId());

        Transaction transaction = findById(transactionId);
        if (transaction.getAccount().getId() != accountId){
            throw new IllegalArgumentException("La transacción no pertenece a la cuenta "+accountId);
        }
        return  transaction;
    }

    public Transaction findById(Long id) {
        Optional<Transaction> transacionSearched = transactionRepository.findById(id);
        if (transacionSearched.isPresent()) {
            return transacionSearched.get();
        } else {
            String message = "No se encontro la transacción con Id " + id;
            log.info(message);
            throw new ResourceNotFoundException(message);
        }
    }

    @Transactional
    public List<Transaction> listAllByAccount(Long accountId){
        Account account = accountService.findById(accountId);
        permissionService.canAccess(account.getUserId());

        return transactionRepository.findByAccountIdOrderByDateTimeDesc(accountId);
    }

    @Transactional
    public void createDepositWithCard(Long accountId, CardDepositDto cardDepositDto){
        Account account = accountService.findById(accountId);
        permissionService.canAccess(account.getUserId());

        Card card = cardService.findById(cardDepositDto.getCardId());

        // si la tarjeta no corresponde a la cuenta
        if(card.getAccountId() != accountId){
            throw new IllegalArgumentException("No es posible realizar la operación");
        }

        if(cardDepositDto.getAmount() <= 0){
            throw new IllegalArgumentException("El monto de la operación no puede ser menor o igual a cero");
        }

        account.setBalance(account.getBalance() + cardDepositDto.getAmount());
        accountService.update(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setCard(card);
        transaction.setDetail(cardDepositDto.getDetail());
        transaction.setType(TransactionType.INCOME);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setAmount(cardDepositDto.getAmount());

        transactionRepository.save(transaction);
    }

    public TransactionDto mapToResponseDto(Transaction transaction){
        TransactionDto response = null;
        if(transaction != null){
            response = new TransactionDto();
            response.setId(transaction.getId());
            response.setType(transaction.getType());
            response.setAmount(transaction.getAmount());
            response.setDateTime(transaction.getDateTime());
            response.setAccountId(transaction.getAccount().getId());
            response.setDetail(transaction.getDetail());

            if(transaction.getCard() != null){
                if(transaction.getType() == TransactionType.INCOME){
                    response.setOrigin("Transferencia desde tarjeta " +transaction.getCard().getNumber());
                    response.setDestination("Acreditación en mi cuenta " +transaction.getAccount().getId());

                }
            }
            else if(transaction.getAccountFrom() != null){
                if(transaction.getType() == TransactionType.INCOME){
                    response.setOrigin("Transferencia desde cuenta " +transaction.getAccountFrom().getId());
                    response.setDestination("Transferencia a mi cuenta "+transaction.getAccount().getId());
                }else{
                    response.setOrigin("Transferencia desde mi cuenta "+transaction.getAccount().getId());
                    response.setDestination("Transferencia a cuenta " +transaction.getAccountFrom().getId());

                }
            }
        }
        return  response;
    }

}
