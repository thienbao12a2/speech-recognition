package com.jmc.vaultbank.Models;

import javax.sql.StatementEvent;
import java.sql.*;
import java.time.LocalDate;

public class DatabaseDriver {
    private Connection connection;

    public DatabaseDriver() {
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Open a connection
            String url = "jdbc:mysql://localhost:3306/vaultbank";
            String user = "root";
//            String password = "Seattle2020#";
            String password = "Seattle2020#";
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to Vault Bank Database");
            // Do something with the connection
        } catch (SQLException e) {
            System.out.println("Failed to connect to Vault Bank Database");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC driver not found");
            e.printStackTrace();
        }
    }
    //DELETE CLIENTS
    public void deleteClient(String username) {
        Statement statement;
        try {
            statement = this.connection.createStatement();
            statement.executeUpdate("DELETE FROM clients WHERE Username='" + username + "';");
            statement.executeUpdate("DELETE FROM checkingaccounts WHERE Username='" + username + "';");
            statement.executeUpdate("DELETE FROM savingaccounts WHERE Username='" + username + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getClientData(String username, String password) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM clients WHERE Username='"+username+"' AND Password='"+password+"';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    public ResultSet getTransactions(String username, int limit) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM transactions WHERE Sender='"+username+"' OR Receiver='"+username+"' ORDER BY ID DESC LIMIT 10;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    public ResultSet getAllTransactions(String username) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM transactions WHERE Sender='"+username+"' OR Receiver='"+username+"';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    public double getSavingAccountBalance(String username) {
        Statement statement;
        ResultSet resultSet;
        double balance = 0;
        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM SavingsAccounts WHERE Owner='"+username+"';");
            while (resultSet.next()) {
                balance = resultSet.getDouble("Balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }
    //UPDATE
public void updateBalance(String username, double amount, String operation) {
    Statement statement;
    ResultSet resultSet;
    try {
        statement = this.connection.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM SavingsAccounts WHERE Owner='"+username+"';");
        resultSet.next();
        double newBalance;
        if (operation.equals("ADD")){
            newBalance = resultSet.getDouble("Balance") + amount;
            statement.executeUpdate("UPDATE SavingsAccounts SET Balance="+newBalance+" WHERE Owner='"+username+"';");
        } else {
            if (resultSet.getDouble("Balance") >= amount) {
                newBalance = resultSet.getDouble("Balance") - amount;
                statement.executeUpdate("UPDATE SavingsAccounts SET Balance="+newBalance+" WHERE Owner='"+username+"';");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    public void newTransaction(String sender, String receiver, double amount, String message) {
        Statement statement;
        try {
            statement = this.connection.createStatement();
            LocalDate date = LocalDate.now();
            statement.executeUpdate("INSERT INTO transactions(Sender, Receiver, Amount, Date, Message) VALUES ('"+sender+"', '"+receiver+"', "+amount+", '"+date+"', '"+message+"');");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getAdminData(String username, String password) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM admins WHERE Username='"+username+"' AND Password='"+password+"';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    //JOIN
    public ResultSet checkGoldMember(String username) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery("SELECT c.Owner, c.Balance + s.Balance AS TotalBalance" +
                    "FROM checkingaccounts c" +
                    "INNER JOIN savingsaccounts s" +
                    "ON c.Owner = s.Owner" +
                    "WHERE c.Owner = '"+username+"' AND (c.Balance + s.Balance) > 50000;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    // ADD
    public void createClient(String firstName, String lastName, String username, String password, LocalDate date) {
        Statement statement;
        try {
            statement = this.connection.createStatement();
            statement.executeUpdate("INSERT INTO clients(FirstName, LastName, Username, Password, Date)" +
                    " VALUES ('"+firstName+"', '"+lastName+"', '"+username+"', '"+password+"', '"+date+"');");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // ADD
    public void createCheckingAccount(String owner, String number, double transactionLimit, double balance) {
        Statement statement;
        try {
            statement = this.connection.createStatement();
            statement.executeUpdate("INSERT INTO checkingaccounts (Owner, AccountNumber, TransactionLimit, Balance)" +
                    " VALUES ('"+owner+"', '"+number+"', '"+transactionLimit+"', '"+balance+"');");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //ADD
    public void createSavingAccount(String owner, String number, double withdrawalLimit, double balance) {
        Statement statement;
        try {
            statement = this.connection.createStatement();
            statement.executeUpdate("INSERT INTO savingsaccounts (Owner, AccountNumber, WithdrawalLimit, Balance)" +
                    " VALUES ('"+owner+"', '"+number+"', '"+withdrawalLimit+"', '"+balance+"');");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ResultSet getAllClientData() {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM Clients");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    //SEARCHING
    public ResultSet searchClient(String username) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM clients WHERE Username='"+username+"';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    //UPDATE
    public void depositSavingAccount(String username, double amount) {
        Statement statement;
        try {
            statement = this.connection.createStatement();
            statement.executeUpdate("UPDATE savingsaccounts SET Balance="+amount+" WHERE Owner='"+username+"';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getLastClientsId() {
        Statement statement;
        ResultSet resultSet;
        int id = 0;
        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM table_sequences WHERE table_name='Clients';");
            if (resultSet.next()) {
                id = resultSet.getInt("seq");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
    public ResultSet getCheckingAccountData(String username) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM checkingaccounts WHERE Owner='"+username+"';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    public ResultSet getSavingAccountData(String username) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM savingsaccounts WHERE Owner='"+username+"';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    public void dummy() {
        checkGoldMember("baoton");
    }
}
