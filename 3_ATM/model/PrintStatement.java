package ATM.model;

interface Printable {
    void printStatement();
}

public class PrintStatement implements Printable {
    private String name;
    private String bankNumber;
    private String accountType;
    private double balance;

    public PrintStatement(String name, String bankNumber, String accountType, double balance){
        this.name = name;
        this.bankNumber = bankNumber;
        this.accountType = accountType;
        this.balance = balance;
    }

    @Override
    public void printStatement(){
        System.out.println("======================================");
        System.out.println("            ATM STATEMENT             ");
        System.out.println("======================================");
        System.out.printf("Name         : %s%n", name);
        System.out.printf("Bank Number  : %s%n", bankNumber);
        System.out.printf("Account Type : %s%n", accountType);
        System.out.printf("Balance      : %.2f%n", balance);
        System.out.println("======================================");
    }
}
