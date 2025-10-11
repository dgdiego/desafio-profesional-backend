package dgdiego_digital_money.account_service.exceptions;

public class InsufficientFundsException extends RuntimeException{

    public InsufficientFundsException(String message){
        super(message);
    }
}
