abstract class Employee implements Taxable {
    protected String name;
    protected int id;
    protected String department;

    public Employee(String name, int id, String department) {
        this.name = name;
        this.id = id;
        this.department = department;
    }

    public String displayInfo() {
        return """
            Employee
                Name       : %s
                ID         : %d
                Department : %s
            """.formatted(name, id, department);
    }
    abstract double calculateSalary();
}

class FullTimeEmployee extends Employee {
    double monthlySalary;

    FullTimeEmployee(String name, int id, String department, double monthlySalary) {
        super(name, id, department);
        this.monthlySalary = monthlySalary;
    }

    @Override
    public double calculateSalary(){
        return monthlySalary;
    }

    @Override
    public double calculateTax() {
        return calculateSalary() / 10;
    }
}


class PartTimeEmployee extends Employee {
    double hourlyRate;
    double hoursWorked;

    PartTimeEmployee(String name, int id, String department, double hourlyRate, double hoursWorked){
        super(name, id, department);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    @Override
    public double calculateSalary(){
        return hourlyRate * hoursWorked;
    }

    @Override
    public double calculateTax(){
        return  calculateSalary() / 5;
    }
}


class Contractor extends Employee {
    double dailyRate;
    double daysWorked;

    Contractor(String name, int id, String department, double dailyRate, double daysWorked){
        super(name, id, department);
        this.dailyRate = dailyRate;
        this.daysWorked = daysWorked;
    }

    @Override
    public  double calculateSalary(){
        return  dailyRate * daysWorked;
    }

    @Override
    public  double calculateTax(){
        return  calculateSalary() / 15;
    }
}