package ATM;

import ATM.model.Account;
import ATM.model.CurrentAccount;
import ATM.model.PrintStatement;
import ATM.model.SavingsAccount;
import ATM.services.ATMService;

public class Main {
    public static void main(String[] args){
        ATMService service = new ATMService();

        Account account1 = new SavingsAccount("ACC1001", "John Doe", "9000000001", "Hyderabad", 6000.0);
        Account account2 = new SavingsAccount("ACC1002", "Jane Smith", "9000000002", "Hyderabad", 6000.0);
        Account account3 = new CurrentAccount("ACC1003", "Alex Johnson", "9000000003", "Hyderabad", 6000.0);
        Account account4 = new CurrentAccount("ACC1004", "Emily Davis", "9000000004", "Hyderabad", 6000.0);
        
        // Saving accounts ATM
        service.createdAccount(account1);
        service.createdAccount(account2);
        service.createdAccount(account3);
        service.createdAccount(account4);

        account1.deposit(900);
        account1.transfer(account2, 100);

        // Closed the ATM
        service.closedAccount(account4);
    }
}
