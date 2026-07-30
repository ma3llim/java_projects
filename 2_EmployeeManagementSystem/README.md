# Employee Management System

A Java-based Employee Management System that demonstrates core Object-Oriented Programming principles through a flexible payroll processing application.

## Overview

This project implements a simple yet extensible employee management system that handles different employee types with their own salary calculation rules. Built using Core Java, it showcases how OOP principles can be applied to create maintainable and scalable business applications.

## Features

- Manage Full-Time, Part-Time, and Contract employees
- Calculate salaries based on employee type
- Automatic tax calculation for each employee type
- Generate payroll reports using runtime polymorphism
- Easy to add new employee types without modifying existing code

## Architecture

Class Hierarchy:
- Employee (Abstract Class) implements Taxable (Interface)
  - FullTimeEmployee extends Employee
  - PartTimeEmployee extends Employee
  - Contractor extends Employee

Employee Types and Salary Calculation:
- Full-Time: Monthly Salary (Tax: 10%)
- Part-Time: Hourly Rate x Hours Worked (Tax: 20%)
- Contractor: Daily Rate x Days Worked (Tax: ~6.67%)

## Acknowledgments

This project was built as a practical application of Object-Oriented Programming concepts learned during Java development studies.