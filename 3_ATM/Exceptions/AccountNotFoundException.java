package ATM.Exceptions;

public class AccountNotFoundException extends Exception {
    AccountNotFoundException(String message){
        super(message);
    }
}
