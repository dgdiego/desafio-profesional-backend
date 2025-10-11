package dgdiego_digital_money.account_service.service.implementation;

import dgdiego_digital_money.account_service.entity.domian.Account;
import dgdiego_digital_money.account_service.entity.dto.AccountRequestDTO;
import dgdiego_digital_money.account_service.entity.dto.AccountRequestInitDTO;
import dgdiego_digital_money.account_service.entity.dto.AccountResponseDTO;
import dgdiego_digital_money.account_service.exceptions.ResourceNotFoundException;
import dgdiego_digital_money.account_service.repository.IAccountRepository;
import jakarta.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.Optional;

@Service
@Slf4j
public class AccountService {

    @Autowired
    IAccountRepository accountRepository;

    @Autowired
    PermissionService permissionService;

    public Long create (AccountRequestInitDTO data){
        Account newAccount = null;
        try{
            newAccount = findByUserId(data.getUserId());
            throw new IllegalArgumentException("Ya existe una cuenta con el usuario "+data.getUserId());
        }catch (ResourceNotFoundException ex){}

        newAccount = Account.builder()
                .userId(data.getUserId())
                .cvu(data.getCvu())
                .alias(data.getAlias())
                .balance(0.0)
                .build();

        accountRepository.save(newAccount);

        return newAccount.getId();
    }

    public Account update (Account accountRequest){
        Account accountToUpdate = findById(accountRequest.getId());
        permissionService.canAccess(accountToUpdate.getUserId());

        String[] parts =  accountRequest.getAlias().split("\\.");
        if (parts.length!=3 || !accountRequest.getAlias().matches("^[^.]+\\.[^.]+\\.[^.]+$")){
            throw new IllegalArgumentException("El formato del alias debe ser palabra.palabra.palabra");
        }

        if(!accountToUpdate.getAlias().equals(accountRequest.getAlias())){
            try{
                Account existedAccount = findByAlias(accountRequest.getAlias());
                throw new IllegalArgumentException("No es posible asignar el alias "+accountRequest.getAlias());
            }catch (ResourceNotFoundException ex){
                accountToUpdate.setAlias(accountRequest.getAlias());
            }
        }

        return accountRepository.save(accountToUpdate);

    }

    public Account findByAlias(String alias){
        Optional<Account> accountSearched = accountRepository.findByAlias(alias);
        if (accountSearched.isPresent()) {
            return accountSearched.get();
        } else {
            String message = "No se encontro la cuenta con alias " + alias;
            log.info(message);
            throw new ResourceNotFoundException(message);
        }
    }

    public Account findByCvu(String cvu){
        Optional<Account> accountSearched = accountRepository.findByCvu(cvu);
        if (accountSearched.isPresent()) {
            return accountSearched.get();
        } else {
            String message = "No se encontro la cuenta con Cvu " + cvu;
            log.info(message);
            throw new ResourceNotFoundException(message);
        }
    }

    public Account updateBalance (Account accountToUpdate){
        Account account = findById(accountToUpdate.getId());
        return accountRepository.save(accountToUpdate);
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

    public Account findByParam(String param){
        return accountRepository.findByAlias(param)
                .or(() -> accountRepository.findByCvu(param))
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la cuenta" + param));
    }

    public AccountResponseDTO mapToResponseDto(Account account){
        AccountResponseDTO response = null;
        if(account != null){
            response = new AccountResponseDTO();
            response.setId(account.getId());
            response.setUserId(account.getUserId());
            response.setCvu(account.getCvu());
            response.setAlias(account.getAlias());
            response.setBalance(account.getBalance());
        }
        return response;
    }

    public Account mapToEntity(AccountRequestDTO request){
        Account response = null;
        if(request != null){
            response = new Account();
            response.setId(request.getId());
            response.setAlias(request.getAlias());
        }
        return response;
    }

}
