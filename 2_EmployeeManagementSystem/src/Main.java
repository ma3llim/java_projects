public class Main {
    public static void main(String[] args){
        Employee[] employees = {
                new FullTimeEmployee("Sameer", 1, "Front End Developer", 35000),
                new PartTimeEmployee("Rahul", 2, "Support", 500, 40.5),
                new Contractor("Aman", 103, "Security", 3000, 20),
        };

        for (Employee employee : employees){
            System.out.println("-----------------------------------------------------------");
            System.out.print(employee.displayInfo());
            System.out.println("Salary: " + employee.calculateSalary());
            System.out.println("Tax: " + employee.calculateTax());
        }
    }
}
