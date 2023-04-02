package com.jmc.vaultbank.Controllers.Client;

import com.jmc.vaultbank.Models.Model;
import com.jmc.vaultbank.Models.Transaction;
import com.jmc.vaultbank.Models.Trying_Different_Languages;
import com.jmc.vaultbank.Views.TransactionCellFactory;
import javafx.beans.binding.Bindings;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    public Text user_name;
    public Label login_date;
    public Label checking_balance;
    public Label checking_number;
    public Label saving_balance;
    public Label saving_number;
    public Label deposit_amount;
    public Label expense_amount;
    public ListView<Transaction> transaction_listview;
    public TextField payee_field;
    public TextField amount_field;
    public TextArea message_field;
    public Button transfer_money_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindData();
        initLatestTransaction();
        transaction_listview.setItems(Model.getInstance().getLatestTransactions());
        transaction_listview.setCellFactory(event -> new TransactionCellFactory());
        transfer_money_btn.setOnAction(event -> onSendMoney());
        accountSummary();
        Trying_Different_Languages I = new Trying_Different_Languages();
        I.speak("Hello, welcome to vault bank");
    }
    private void bindData() {
        user_name.textProperty().bind(Bindings.concat("Hi , ").concat(Model.getInstance().getClient().firstNameProperty()));
        login_date.setText("Today, " + LocalDate.now());
        checking_balance.textProperty().bind(Bindings.concat("$").concat(Model.getInstance().getClient().checkingAccountProperty().get().balanceProperty().asString()));
        checking_number.textProperty().bind(Model.getInstance().getClient().checkingAccountProperty().get().accountNumberProperty());
        saving_balance.textProperty().bind(Bindings.concat("$").concat(Model.getInstance().getClient().savingAccountProperty().get().balanceProperty().asString()));
        saving_number.textProperty().bind(Model.getInstance().getClient().savingAccountProperty().get().accountNumberProperty());
    }
    public void initLatestTransaction() {
        if (Model.getInstance().getLatestTransactions().isEmpty()) {
            Model.getInstance().setLatestTransactions();
        }
    }
    private void onSendMoney() {
        String receiver = payee_field.getText();
        double amount = Double.parseDouble((amount_field.getText()));
        String message = message_field.getText();
        String sender = Model.getInstance().getClient().payeeAddressProperty().get();
        ResultSet resultSet = Model. getInstance().getDatabaseDriver().searchClient(receiver);
        try {
            if (resultSet.isBeforeFirst()) {
                Model.getInstance().getDatabaseDriver().updateBalance(receiver, amount, "ADD");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Model.getInstance().getDatabaseDriver().updateBalance(sender, amount, "SUB");
        Model.getInstance().getClient().savingAccountProperty().get().setBalance(Model.getInstance().getDatabaseDriver().getSavingAccountBalance(sender));
        Model.getInstance().getDatabaseDriver().newTransaction(sender, receiver, amount, message);
        amount_field.setText("");
        payee_field.setText("");
        message_field.setText("");
    }
    private void accountSummary() {
        double income = 0;
        double expenses = 0;
        if (Model.getInstance().getAllTransaction().isEmpty()){
            Model.getInstance().setAllTransaction();
        }
        for (Transaction transaction : Model.getInstance().getAllTransaction()) {
            if (transaction.senderProperty().get().equals(Model.getInstance().getClient().payeeAddressProperty().get())){
                expenses = expenses + transaction.amountProperty().get();
            } else {
                income = income + transaction.amountProperty().get();
            }
        }
        deposit_amount.setText("+ $" + income);
        expense_amount.setText("- $" + expenses);
    }
}
