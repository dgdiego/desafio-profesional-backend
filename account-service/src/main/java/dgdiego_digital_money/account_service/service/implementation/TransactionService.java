package dgdiego_digital_money.account_service.service.implementation;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.entity.domian.Transaction;
import dgdiego_digital_money.account_service.entity.dto.TransactionDto;
import dgdiego_digital_money.account_service.repository.ITransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;


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
    ITransactionRepository transactionRepository;

    public List<Transaction> transactionsDashboard(Long accountId){
        Account account = accountService.findById(accountId);
        permissionService.canAccess(account.getUserId());

        Pageable limit = PageRequest.of(0, 5);
        return transactionRepository.findByAccountIdOrderByDateTimeDesc(accountId, limit);
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
        }
        return  response;
    }

}
