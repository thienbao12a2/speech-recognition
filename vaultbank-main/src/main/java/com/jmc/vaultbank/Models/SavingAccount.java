package com.jmc.vaultbank.Models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class SavingAccount extends Account {
    private final DoubleProperty withdrawalLimit;

    public SavingAccount(String owner, String accountNumber, double balance, double withdrawalLimit) {
        super(owner, accountNumber, balance);
        this.withdrawalLimit = new SimpleDoubleProperty(this, "Withdrawal Limit", withdrawalLimit);
    }
    public DoubleProperty withdrawalLimitProperty() {
        return this.withdrawalLimit;
    }
    @Override
    public String toString() {
        return accountNumberProperty().get();
    }
}
