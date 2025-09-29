package dgdiego_digital_money.account_service.service.implementation;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.exceptions.ResourceNotFoundException;
import dgdiego_digital_money.account_service.repository.IAccountRepository;
import jakarta.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AccountService {

    @Autowired
    IAccountRepository accountRepository;

    @Autowired
    PermissionService permissionService;

    public Long create (Long userId){
        Account newAccount = null;
        try{
            newAccount = findByUserId(userId);
            throw new IllegalArgumentException("Ya existe una cuenta con el usuario "+userId);
        }catch (ResourceNotFoundException ex){}

        newAccount = Account.builder()
                .userId(userId)
                .balance(0.0)
                .build();

        accountRepository.save(newAccount);

        return newAccount.getId();
    }

    public Double getBalance(Long accountId){
        Account account = findById(accountId);
        permissionService.canAccess(account.getUserId());

        return account.getBalance();
    }

    public Account findById(Long id) {
        Optional<Account> accountSearched = accountRepository.findById(id);
        if (accountSearched.isPresent()) {
            return accountSearched.get();
        } else {
            String message = "No se encontro la cuenta con Id " + id;
            log.info(message);
            throw new ResourceNotFoundException(message);
        }
    }

    public Account findByUserId(Long userId) {
        Optional<Account> accountSearched = accountRepository.findByUserId(userId);
        if (accountSearched.isPresent()) {
            return accountSearched.get();
        } else {
            String message = "No se encontro la cuenta para el usuario " + userId;
            log.info(message);
            throw new ResourceNotFoundException(message);
        }
    }

}
