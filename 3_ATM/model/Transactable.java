package ATM.model;

public interface Transactable {
    public void deposit(double amount);
    public void withdraw(double amount) throws IllegalArgumentException;
    public void transfer(Account recipient, double amount);
}
