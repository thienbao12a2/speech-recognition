package com.jmc.vaultbank.Controllers.Client;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountsController implements Initializable {
    public Label checking_acc_number;
    public Label transactions_limit;
    public Label checking_account_date;
    public Label checking_account_balance;
    public Label saving_acc_number;
    public Label withdraw_limit;
    public Label saving_account_date;
    public Label saving_account_balance;
    public TextField amount_to_saving;
    public Button transfer_to_saving_btn;
    public TextField amount_to_checking;
    public Button transfer_to_checking_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
