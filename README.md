# Bank Management System

## Overview
Bank Management System is a Java-based application developed using Swing for the GUI and MySQL for database management. This system allows an admin to manage bank accounts, perform transactions such as deposits and withdrawals, view account details, and delete accounts.

## Features
- Create a new bank account
- Deposit money into an account
- Withdraw money from an account
- View account details
- Delete an account

## Technologies Used
- **Programming Language**: Java
- **GUI Framework**: Swing
- **Database**: MySQL
- **JDBC**: Java Database Connectivity for MySQL connection

## Installation and Setup
### Prerequisites
- Install Java Development Kit (JDK)
- Install MySQL Server
- Install an IDE like IntelliJ IDEA, Eclipse, or NetBeans

### Database Setup
1. Start MySQL and create a new database:
   ```sql
   CREATE DATABASE bank;
   ```
2. Switch to the newly created database:
   ```sql
   USE bank;
   ```
3. Create the `users` table:
   ```sql
   CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
    );
   ```
4. Add user in `users` table:
   ```sql
   INSERT INTO users (username, password) VALUES ('admin', 'admin123');
   ```
5. Create the `accounts` table:
   ```sql
   CREATE TABLE accounts (
    account_number INT PRIMARY KEY AUTO_INCREMENT,
    account_holder_name VARCHAR(100) NOT NULL,
    aadhar BIGINT UNIQUE NOT NULL,
    phone BIGINT UNIQUE NOT NULL,
    age INT NOT NULL
    );
   ```

### Running the Application
1. Clone this repository:
   ```sh
   git clone https://github.com/TheCoderSaurabh/Banking-Management-System.git
   ```
2. Open the project in your preferred Java IDE.
3. Update the database connection details in `BankSystem.java`:
   ```java
   conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_management", "your_username", "your_password");
   ```
4. Compile and run the `BankSystem.java` file.

<!-- ## Usage
- Enter account details and click **Create Account** to register a new bank account.
- Enter account number and deposit amount, then click **Deposit** to add funds.
- Enter account number and withdrawal amount, then click **Withdraw** to remove funds.
- Enter an account number and click **View Details** to check account balance and details.
- Enter an account number and click **Delete Account** to remove an account permanently. -->

## Contact
For any inquiries or issues, feel free to reach out via email or GitHub Issues.