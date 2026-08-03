package ATM.model;

import ATM.Exceptions.InsufficientFundsException;

public class Account implements Transactable {
    private String bankNumber;
    private String name;
    private String contactNumber;
    private String address;
    private double balance;

    public Account(String bankNumber, String name, String contactNumber, String address, double balance) {
        this.bankNumber = bankNumber;
        this.name = name;
        this.contactNumber = contactNumber;
        this.address = address;
        this.balance = balance;
    }


    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public void deposit(double amount) {
        if(amount < 0){
            try {
                throw new InsufficientFundsException("Amount must be positive");
            } catch (InsufficientFundsException e) {
                throw new RuntimeException(e);
            }
        }
        this.balance += amount;
    }

    @Override
    public void withdraw(double amount) {
        if(amount < 0){
            try {
                throw new InsufficientFundsException("Amount must be positive");
            } catch (InsufficientFundsException e) {
                throw new RuntimeException(e);
            }
        }
        if(amount > balance){
            try {
                throw new InsufficientFundsException("Insufficient balance");
            } catch (InsufficientFundsException e) {
                throw new RuntimeException(e);
            }
        }
        this.balance -= amount;
    }

    @Override
    public void transfer(Account recipient, double amount) {
        if(amount < 0){
            try {
                throw new InsufficientFundsException("Amount must be positive");
            } catch (InsufficientFundsException e) {
                throw new RuntimeException(e);
            }
        }
        if(amount > balance){
            try {
                throw new InsufficientFundsException("Insufficient balance");
            } catch (InsufficientFundsException e) {
                throw new RuntimeException(e);
            }
        }
        withdraw(amount);
        recipient.deposit(amount);
    }
}

