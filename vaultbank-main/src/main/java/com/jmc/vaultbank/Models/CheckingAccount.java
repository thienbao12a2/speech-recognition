package com.jmc.vaultbank.Models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class CheckingAccount extends Account {
    private final IntegerProperty transactionLimit;

    public CheckingAccount(String owner, String accountNUmber, double balance, int limit) {
        super(owner, accountNUmber, balance);
        this.transactionLimit = new SimpleIntegerProperty(this, "Transaction Limit", limit);
    }
    public IntegerProperty transactionLimitProperty() {
        return this.transactionLimit;
    }

    @Override
    public String toString() {
        return accountNumberProperty().get();
    }
}
