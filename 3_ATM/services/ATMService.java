package ATM.services;

import ATM.model.Account;

import java.util.ArrayList;
import java.util.List;

public class ATMService {
    private List<Account> accounts = new ArrayList<>();

    public void createdAccount(Account account){
        accounts.add(account);
        System.out.println("Account Created Successfully " + account.getName());
    }

    public void closedAccount(Account account) {
        accounts.remove(account);
        System.out.println("Account Closed Successfully " + account.getName());
    }
}
