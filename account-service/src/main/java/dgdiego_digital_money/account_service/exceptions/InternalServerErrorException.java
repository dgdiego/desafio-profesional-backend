package dgdiego_digital_money.account_service.exceptions;

public class InternalServerErrorException extends RuntimeException{
    public  InternalServerErrorException(String message){
        super(message);
    }
}
