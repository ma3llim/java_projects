package ATM.model;

import ATM.Exceptions.InsufficientFundsException;

public class CurrentAccount extends Account implements Transactable {
    private double interestRate;
    private double minimumBalance;
    private double withdrawalLimit;

    public CurrentAccount(String bankNumber, String name, String contactNumber, String address, double balance){
        super(bankNumber, name, contactNumber, address, balance);
        interestRate = 8.0;
        withdrawalLimit = 30000;
        minimumBalance = 5000;
    }

    public double calculateInterest(){
        return getBalance() + interestRate / 100;
    }

    @Override
    public void deposit(double amount) {
        super.deposit(amount);
    }

    @Override
    public void withdraw(double amount) {
        if(amount > withdrawalLimit){
            try {
                throw new InsufficientFundsException("Exceeds daily withdrawal limit");
            } catch (InsufficientFundsException e) {
                throw new RuntimeException(e);
            }
        }
        if(this.getBalance() - amount < minimumBalance){
            try {
                throw new InsufficientFundsException("Exceeds minimum balance limit");
            } catch (InsufficientFundsException e) {
                throw new RuntimeException(e);
            }
        }
        super.withdraw(amount);
    }

    @Override
    public void transfer(Account recipient, double amount) {
        if(amount > withdrawalLimit){
            try {
                throw new InsufficientFundsException("Exceeds daily withdrawal limit");
            } catch (InsufficientFundsException e) {
                throw new RuntimeException(e);
            }
        }
        if(this.getBalance() - amount < minimumBalance){
            try {
                throw new InsufficientFundsException("Exceeds minimum balance limit");
            } catch (InsufficientFundsException e) {
                throw new RuntimeException(e);
            }
        }
        super.transfer(recipient, amount);
    }
}
