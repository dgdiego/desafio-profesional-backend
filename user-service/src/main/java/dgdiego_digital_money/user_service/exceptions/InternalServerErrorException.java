package dgdiego_digital_money.user_service.exceptions;

public class InternalServerErrorException extends RuntimeException{
    public  InternalServerErrorException(String message){
        super(message);
    }
}
