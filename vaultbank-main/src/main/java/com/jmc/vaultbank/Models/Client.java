package com.jmc.vaultbank.Models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Client {
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty payeeAddress;
    private final ObjectProperty<Account> checkingAccount;
    private final ObjectProperty<Account> savingAccount;
    private final ObjectProperty<LocalDate> dateCreated;

    public Client(String firstName, String lastName, String payeeAddress, Account checkingAccount, Account savingAccount, LocalDate date) {
        this.firstName = new SimpleStringProperty(this, "FirstName", firstName);
        this.lastName = new SimpleStringProperty(this, "LastName", lastName);
        this.payeeAddress = new SimpleStringProperty(this, "Payee Address", payeeAddress);
        this.checkingAccount = new SimpleObjectProperty<>(this, "Checking Account", checkingAccount);
        this.savingAccount = new SimpleObjectProperty<>(this, "Saving Account", savingAccount);
        this.dateCreated = new SimpleObjectProperty<>(this, "Date Created", date);
    }
    public StringProperty firstNameProperty() {
        return this.firstName;
    }
    public StringProperty lastNameProperty() {
        return this.lastName;
    }
    public StringProperty payeeAddressProperty() {
        return this.payeeAddress;
    }
    public ObjectProperty<Account> checkingAccountProperty() {
        return this.checkingAccount;
    }
    public ObjectProperty<Account> savingAccountProperty() {
        return this.savingAccount;
    }
    public ObjectProperty<LocalDate> dateCreatedProperty() {
        return this.dateCreated;
    }

}
