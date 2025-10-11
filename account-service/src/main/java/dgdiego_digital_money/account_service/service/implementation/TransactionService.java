package dgdiego_digital_money.account_service.service.implementation;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.entity.domian.Card;
import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.domian.TransactionType;
import dgdiego_digital_money.account_service.entity.dto.CardDepositDto;
import dgdiego_digital_money.account_service.entity.dto.TransactionDto;
import dgdiego_digital_money.account_service.entity.dto.TransferenceCreateDto;
import dgdiego_digital_money.account_service.exceptions.InsufficientFundsException;
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

    public List<Account> getLastRecipientsByAccount(Long accountId) {
        Account account = accountService.findById(accountId);
        permissionService.canAccess(account.getUserId());
        Pageable topFive = PageRequest.of(0, 5);
        return transactionRepository.findLastRecipientsByAccountId(accountId, topFive);
    }

    @Transactional
    public void createDepositWithCard(Long accountId, CardDepositDto cardDepositDto){
        Account account = accountService.findById(accountId);
        permissionService.canAccess(account.getUserId());

        if(cardDepositDto.getAmount() <= 0){
            throw new IllegalArgumentException("El monto de la operación no puede ser menor o igual a cero");
        }

        Card card = cardService.findById(cardDepositDto.getCardId());

        // si la tarjeta no corresponde a la cuenta
        if(card.getAccountId() != accountId){
            throw new IllegalArgumentException("No es posible realizar la operación");
        }

        account.setBalance(account.getBalance() + cardDepositDto.getAmount());
        accountService.updateBalance(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setCard(card);
        transaction.setDetail(cardDepositDto.getDetail());
        transaction.setType(TransactionType.INCOME);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setAmount(cardDepositDto.getAmount());

        transactionRepository.save(transaction);
    }

    @Transactional
    public void createTransference(Long accountId, TransferenceCreateDto requestDto){
        Account account = accountService.findById(accountId);
        permissionService.canAccess(account.getUserId());

        if(requestDto.getAmount() <= 0){
            throw new IllegalArgumentException("El monto de la operación no puede ser menor o igual a cero");
        }

        //busco la cuenta de origen
        Account originAccount = null;
        try{
            originAccount = accountService.findByParam(requestDto.getOrigin());
        }catch (ResourceNotFoundException ex){
            throw new ResourceNotFoundException("ORIGEN: "+ex.getMessage());
        }

        if(!originAccount.getId().equals(account.getId())){
            throw new IllegalArgumentException("La cuenta de ORIGEN no se corresponde con accountId "+accountId);
        }

        //chequeo que tenga saldo
        if(originAccount.getBalance() < requestDto.getAmount() ){
            throw new InsufficientFundsException("Fondos insuficientes");
        }

        //busco la cuenta de destino
        Account destinationAccount = null;
        try{
            destinationAccount = accountService.findByParam(requestDto.getDestination());
        }catch (ResourceNotFoundException ex){
            throw new ResourceNotFoundException("DESTINO: "+ex.getMessage());
        }

        originAccount.setBalance(originAccount.getBalance() - requestDto.getAmount());
        accountService.updateBalance(originAccount);

        destinationAccount.setBalance(destinationAccount.getBalance() + requestDto.getAmount());
        accountService.updateBalance(destinationAccount);

        //Creo las transacciones para ambas cuentas
        LocalDateTime transactionDateTime = LocalDateTime.now();

        Transaction transactionDebit = new Transaction();
        transactionDebit.setAccount(originAccount);
        transactionDebit.setRelatedAccount(destinationAccount);
        transactionDebit.setDetail(requestDto.getDetail());
        transactionDebit.setType(TransactionType.DEBIT);
        transactionDebit.setDateTime(transactionDateTime);
        transactionDebit.setAmount(requestDto.getAmount());

        Transaction transactionCredit = new Transaction();
        transactionCredit.setAccount(destinationAccount);
        transactionCredit.setRelatedAccount(originAccount);
        transactionCredit.setDetail(requestDto.getDetail());
        transactionCredit.setType(TransactionType.INCOME);
        transactionCredit.setDateTime(transactionDateTime);
        transactionCredit.setAmount(requestDto.getAmount());


        transactionRepository.save(transactionDebit);
        transactionRepository.save(transactionCredit);
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
            else if(transaction.getRelatedAccount() != null){
                if(transaction.getType() == TransactionType.INCOME){
                    response.setOrigin("Transferencia desde cuenta " +transaction.getRelatedAccount().getId());
                    response.setDestination("Transferencia a mi cuenta "+transaction.getAccount().getId());
                }else{
                    response.setOrigin("Transferencia desde mi cuenta "+transaction.getAccount().getId());
                    response.setDestination("Transferencia a cuenta " +transaction.getRelatedAccount().getId());

                }
            }
        }
        return  response;
    }

}
