package ATM.model;

import ATM.Exceptions.InsufficientFundsException;

public class SavingsAccount extends Account implements Transactable {
    private double interestRate;
    private double withdrawalLimit;
    private double minimumBalance;

    public SavingsAccount(String bankNumber, String name, String contactNumber, String address, double balance){
        super(bankNumber, name, contactNumber, address, balance);
        interestRate = 4.0;
        withdrawalLimit = 30000;
        minimumBalance = 1000;
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
