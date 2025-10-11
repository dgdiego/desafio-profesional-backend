package dgdiego_digital_money.user_service.service.implementation;

import dgdiego_digital_money.user_service.entity.dto.AccountRequestInitDTO;
import dgdiego_digital_money.user_service.repository.IFeingAccountRepository;
import dgdiego_digital_money.user_service.repository.IFeingAuthRepository;
import dgdiego_digital_money.user_service.service.IAccountService;
import dgdiego_digital_money.user_service.service.IAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountService implements IAccountService {


    @Autowired
    private IFeingAccountRepository feingAccountRepository;

    @Override
    public Long create(AccountRequestInitDTO data) {
        return feingAccountRepository.create(data);
    }

    public String generateCvu(){
        SecureRandom random = new SecureRandom();
        return String.valueOf(
                (long) (Math.pow(10, 21) + random.nextDouble() * (Math.pow(10, 22) - Math.pow(10, 21) - 1))
        );
    }

    public String generateAlias(){
        String filePath = "/list-alias.txt";
        String alias = "";
        try {

            InputStream inputStream = getClass().getResourceAsStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            List<String> aliases = reader.lines().collect(Collectors.toList());



            if (aliases.size() < 3) {
                String mensaje = "El archivo aliases no tiene suficientes palabras.";
                log.error(mensaje);
                throw new IOException(mensaje);
            }

            Random random = new Random();
            for (int i = 0; i < 3; i++) {
                int index = random.nextInt(aliases.size());
                alias+=aliases.get(index)+".";
            }
            alias = alias.substring(0,alias.length()-1);

        } catch (IOException e) {
            log.error(e.getMessage());
            alias = "perro-gato-liebre";
        }
        return alias;
    }
}
