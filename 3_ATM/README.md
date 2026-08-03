# ATM Management System

A simple Java-based ATM Management System developed using Object-Oriented Programming (OOP) principles. This project demonstrates inheritance, polymorphism, abstraction, interfaces, exception handling, and package organization.

## Features

- Create Savings and Current accounts
- Close existing accounts
- Deposit money
- Withdraw money
- Transfer funds between accounts
- Print account statement
- Balance validation
- Daily withdrawal limit
- Minimum balance validation
- Custom exception handling

## Technologies Used

- Java
- Object-Oriented Programming (OOP)
- Collections (ArrayList)
- Custom Exceptions

## Project Structure

```
ATM/
│
├── Main.java
│
├── model/
│   ├── Account.java
│   ├── SavingsAccount.java
│   ├── CurrentAccount.java
│   ├── PrintStatement.java
│   ├── Printable.java
│   └── Transactable.java
│
├── services/
│   └── ATMService.java
│
└── Exceptions/
    ├── InsufficientFundsException.java
    └── AccountNotFoundException.java
```

## OOP Concepts Used

- Encapsulation
- Inheritance
- Polymorphism
- Abstraction
- Interfaces
- Method Overriding
- Exception Handling
