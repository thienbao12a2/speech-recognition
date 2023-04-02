package com.jmc.vaultbank.Controllers.Client;

import com.jmc.vaultbank.Models.MicrophoneStreamRecognizer;
import com.jmc.vaultbank.Models.Model;
import com.jmc.vaultbank.Models.Transaction;
import com.jmc.vaultbank.Models.Trying_Different_Languages;
import com.jmc.vaultbank.Views.TransactionCellFactory;
import javafx.beans.binding.Bindings;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
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

        //advanced function
        Trying_Different_Languages I = new Trying_Different_Languages();
        I.speak("Hello, welcome to vault bank");
        String firstName = Model.getInstance().getClient().firstNameProperty().get();
        String checkingNumber = Model.getInstance().getClient().checkingAccountProperty().get().accountNumberProperty().get();
        double savingBalance = Model.getInstance().getClient().savingAccountProperty().get().balanceProperty().get();
        double checkingBalance = Model.getInstance().getClient().checkingAccountProperty().get().balanceProperty().get();
        String savingNumber = Model.getInstance().getClient().savingAccountProperty().get().accountNumberProperty().get();
        MicrophoneStreamRecognizer we = new MicrophoneStreamRecognizer();
        I.speak("Do you wanna use voice command?");
        String customerSay;
        try {
            customerSay = (String) we.recordAndTranscribe(5);
        } catch (LineUnavailableException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (customerSay.contains("yes")) {
            boolean end = false;
            while (!end) {
                I.speak("How may I help you today, " + firstName + " .You can say something like: I want to check my balance, or give me the last 8 digits of my account number, or I want to send money");

                try {
                    customerSay = (String) we.recordAndTranscribe(10);
                    if (customerSay.contains("balance")) {
                        I.speak("You want to check balance, is that correct?");
                        customerSay = (String) we.recordAndTranscribe(5);
                        if (customerSay.contains("yes")) {
                            I.speak("Your check account balance is " + checkingBalance + ".");
                            I.speak("Your saving account balance is " + savingBalance + ".");
                        }
                    } else if (customerSay.contains("account number")) {
                        I.speak("You want to check account number, is that correct?");
                        customerSay = (String) we.recordAndTranscribe(5);
                        if (customerSay.contains("yes")) {
                            I.speak("Last 8 digits of your checking account number is " + checkingNumber + ".");
                            I.speak("Last 8 digits of your saving account number is " + savingNumber + ".");
                        }
                    } else if (customerSay.contains("send money")) {
                        I.speak("You want to send money, is that correct?");
                        customerSay = (String) we.recordAndTranscribe(5);
                        if (customerSay.contains("yes")) {
                            I.speak("Who would you like to transfer money for? Please spell their name address.");
                            customerSay = (String) we.recordAndTranscribe(10);
                            I.speak("You say " + customerSay + ", is that correct?");
                            customerSay = (String) we.recordAndTranscribe(5);
                            if (customerSay.contains("yes")) {
                                customerSay = customerSay.replaceAll("\\s", "");
                            }
                        }
                    } else {
                        end = true;
                    }
                    I.speak("Would you like to check something else?");
                    customerSay = (String) we.recordAndTranscribe(5);
                    if (customerSay.contains("no")) {
                        I.speak("Thank you for using vault bank voice command");
                        end = true;
                    }
                } catch (LineUnavailableException | InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
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
