package com.jmc.vaultbank.Models;

import com.jmc.vaultbank.Views.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private final DatabaseDriver databaseDriver;

    private final Client client;
    private boolean clientSuccessLogin;
    private final ObservableList<Transaction> latestTransactions;
    private final ObservableList<Transaction> allTransaction;
    private boolean adminSuccessLogin;
    private final ObservableList<Client> clients;

    private Model() {
        this.viewFactory = new ViewFactory();
        this.databaseDriver = new DatabaseDriver();
        this.clientSuccessLogin = false;
        this.adminSuccessLogin = false;
        this.client = new Client("", "", "", null, null, null);
        this.latestTransactions = FXCollections.observableArrayList();
        this.allTransaction = FXCollections.observableArrayList();
        this.clients = FXCollections.observableArrayList();
    }

    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }
    public ViewFactory getViewFactory() {
        return viewFactory;
    }
    public DatabaseDriver getDatabaseDriver() {
        return databaseDriver;
    }

    public boolean getClientSuccessLogin() {
        return this.clientSuccessLogin;
    }
    public void setclientSuccessLogin(boolean state) {
        this.clientSuccessLogin = state;
    }
    public Client getClient() {
        return client;
    }
    public void evaluateClientCred(String username, String password) {
        CheckingAccount checkingAccount;
        SavingAccount savingAccount;
        ResultSet resultSet = databaseDriver.getClientData(username, password);
        try {
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    this.client.firstNameProperty().set(resultSet.getString("FirstName"));
                    this.client.lastNameProperty().set(resultSet.getString("LastName"));
                    this.client.payeeAddressProperty().set(resultSet.getString("Username"));
                    String[] dateArray = resultSet.getString("Date").split("-");
                    LocalDate date = LocalDate.of(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]));
                    this.client.dateCreatedProperty().set(date);
                }
                checkingAccount = getCheckingAccount(username);
                savingAccount = getSavingAccount(username);
                this.client.checkingAccountProperty().set(checkingAccount);
                this.client.savingAccountProperty().set(savingAccount);
                this.clientSuccessLogin = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void prepareTransactions(ObservableList<Transaction> transactions, int limit) {
        ResultSet resultSet = databaseDriver.getTransactions(this.client.payeeAddressProperty().get(), limit);
        try {
            while (resultSet.next()) {
                String sender = resultSet.getString("Sender");
                String receiver = resultSet.getString("Receiver");
                double amount = resultSet.getDouble("Amount");
                String[] dateArray = resultSet.getString("Date").split("-");
                LocalDate date = LocalDate.of(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]));
                String message = resultSet.getString("Message");
                transactions.add(new Transaction(sender, receiver, amount, date, message));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void prepareAllTransactions(ObservableList<Transaction> transactions) {
        ResultSet resultSet = databaseDriver.getAllTransactions(this.client.payeeAddressProperty().get());
        try {
            while (resultSet.next()) {
                String sender = resultSet.getString("Sender");
                String receiver = resultSet.getString("Receiver");
                double amount = resultSet.getDouble("Amount");
                String[] dateArray = resultSet.getString("Date").split("-");
                LocalDate date = LocalDate.of(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]));
                String message = resultSet.getString("Message");
                transactions.add(new Transaction(sender, receiver, amount, date, message));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setLatestTransactions() {
        prepareTransactions(latestTransactions, 4);
    }
    public ObservableList<Transaction> getLatestTransactions() {
        return this.latestTransactions;
    }
    public void setAllTransaction() {
        prepareAllTransactions(allTransaction);
    }
    public ObservableList<Transaction> getAllTransaction() {
        return allTransaction;
    }
    public boolean getAdminSuccessLogin() {
        return adminSuccessLogin;
    }
    public void setAdminSuccessLogin(boolean adminSuccessLogin) {
        this.adminSuccessLogin = adminSuccessLogin;
    }
    public void evaluateAdminCred(String username, String password) {
        ResultSet resultSet = databaseDriver.getAdminData(username, password);
        try {
            if (resultSet.isBeforeFirst()) {
                this.adminSuccessLogin = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ObservableList<Client> getClients() {
        return clients;
    }
    public void setClient() {
        CheckingAccount checkingAccount;
        SavingAccount savingAccount;
        ResultSet resultSet = databaseDriver.getAllClientData();
        try {
            while (resultSet.next()) {
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                String username = resultSet.getString("Username");
                String[] datePart = resultSet.getString("Date").split("-");
                LocalDate date = LocalDate.of(Integer.parseInt(datePart[0]), Integer.parseInt(datePart[1]), Integer.parseInt(datePart[2]));
                checkingAccount = getCheckingAccount(username);
                savingAccount = getSavingAccount(username);
                clients.add(new Client(firstName, lastName, username, checkingAccount, savingAccount, date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ObservableList<Client> searchClient(String username) {
        ObservableList<Client> searchResults = FXCollections.observableArrayList();
        ResultSet resultSet = databaseDriver.searchClient(username);
        CheckingAccount checkingAccount;
        SavingAccount savingAccount;
        try {
            while (resultSet.next()) {
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                String[] datePart = resultSet.getString("Date").split("-");
                LocalDate date = LocalDate.of(Integer.parseInt(datePart[0]), Integer.parseInt(datePart[1]), Integer.parseInt(datePart[2]));
                checkingAccount = getCheckingAccount(username);
                savingAccount = getSavingAccount(username);
                searchResults.add(new Client(firstName, lastName, username, checkingAccount, savingAccount, date));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchResults;
    }
    public CheckingAccount getCheckingAccount(String username) {
        CheckingAccount account = null;
        ResultSet resultSet = databaseDriver.getCheckingAccountData(username);
        try {
            while (resultSet.next()) {
                String number = resultSet.getString("AccountNumber");
                int transactionLimit = (int) resultSet.getDouble("TransactionLimit");
                double balance = resultSet.getDouble("Balance");
                account = new CheckingAccount(username, number, balance, transactionLimit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }
    public SavingAccount getSavingAccount(String username) {
        SavingAccount account = null;
        ResultSet resultSet = databaseDriver.getSavingAccountData(username);
        try {
            while (resultSet.next()) {
                String number = resultSet.getString("AccountNumber");
                double withdrawalLimist = resultSet.getDouble("WithdrawalLimit");
                double balance = resultSet.getDouble("Balance");
                account = new SavingAccount(username, number, balance, withdrawalLimist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }
}
