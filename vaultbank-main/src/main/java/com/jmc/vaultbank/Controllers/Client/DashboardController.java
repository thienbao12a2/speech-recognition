package com.jmc.vaultbank.Controllers.Client;

import com.jmc.vaultbank.Models.MicrophoneStreamRecognizer;
import com.jmc.vaultbank.Models.Model;
import com.jmc.vaultbank.Models.Transaction;
import com.jmc.vaultbank.Models.Trying_Different_Languages;
import com.jmc.vaultbank.Views.TransactionCellFactory;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public static Thread speechThread;
    public static boolean useVoice = true;
    public static boolean welcome = true;
    public static boolean mute = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindData();
        initLatestTransaction();
        transaction_listview.setItems(Model.getInstance().getLatestTransactions());
        transaction_listview.setCellFactory(event -> new TransactionCellFactory());
        transfer_money_btn.setOnAction(event -> onSendMoney());
        accountSummary();
        //Advanced function
        if (useVoice) voiceCommand();
    }
    private void bindData() {
        user_name.textProperty().bind(Bindings.concat("Hi , ").concat(Model.getInstance().getClient().firstNameProperty()));
        login_date.setText("Today, " + LocalDate.now());
        checking_balance.textProperty().bind(Bindings.concat("$").concat(Model.getInstance().getClient().checkingAccountProperty().get().balanceProperty().asString()));
        checking_number.textProperty().bind(Model.getInstance().getClient().checkingAccountProperty().get().accountNumberProperty());
        saving_balance.textProperty().bind(Bindings.concat("$").concat(Model.getInstance().getClient().savingAccountProperty().get().balanceProperty().asString()));
        saving_number.textProperty().bind(Model.getInstance().getClient().savingAccountProperty().get().accountNumberProperty());
    }
    public void voiceCommand() {
        speechThread = new Thread(() -> {
            Trying_Different_Languages I = new Trying_Different_Languages();
            String firstName = Model.getInstance().getClient().firstNameProperty().get();
            String checkingNumber = Model.getInstance().getClient().checkingAccountProperty().get().accountNumberProperty().get();
            double savingBalance = Model.getInstance().getClient().savingAccountProperty().get().balanceProperty().get();
            double checkingBalance = Model.getInstance().getClient().checkingAccountProperty().get().balanceProperty().get();
            String savingNumber = Model.getInstance().getClient().savingAccountProperty().get().accountNumberProperty().get();
            MicrophoneStreamRecognizer we = new MicrophoneStreamRecognizer();
            String customerSay;
            do {
                if (welcome) {
                    I.speak("Hello! Welcome to Vault Bank. Do you wanna use voice command?");
                    try {
                        customerSay = (String) we.recordAndTranscribe(5);
                    } catch (LineUnavailableException | IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (customerSay.contains("yes")) {
                        useVoice = true;
                        I.speak("Voice command is activated. Hello, " + firstName);
                    } else if (customerSay.contains("no")) {
                        useVoice = false;
                        I.speak("Voice command deactivated");
                    }
                    welcome = false;
                }
                while (useVoice && mute) {
                    try {
                        customerSay = (String) we.recordAndTranscribe(5);
                    } catch (LineUnavailableException | IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (customerSay.contains("I need assistance")) mute = false;
                }
                while (useVoice && !mute) {
                    I.speak("What would you like to do? You can say something like: I want to check my balance, or give me the last 8 digits of my account number, or I want to send money.");
                    try {
                        customerSay = (String) we.recordAndTranscribe(5);
                        if (customerSay.contains("balance")) {
                            I.speak("You want to check balance, is that correct?");
                            customerSay = (String) we.recordAndTranscribe(5);
                            if (customerSay.contains("yes")) {
                                DecimalFormat df = new DecimalFormat("#.00");
                                String formattedCheckingBalance = df.format(checkingBalance);
                                String[] checkingBalanceParts = formattedCheckingBalance.split("\\.");
                                String checkingDollars = checkingBalanceParts[0];
                                String checkingCents = checkingBalanceParts[1];
                                String formattedSavingBalance = df.format(savingBalance);
                                String[] savingBalanceParts = formattedSavingBalance.split("\\.");
                                String savingDollars = savingBalanceParts[0];
                                String savingCents = savingBalanceParts[1];
                                I.speak("Your checking account balance is " + (Integer.parseInt(checkingDollars) == 0 ? "zero" : checkingDollars) + " dollars and " + (Integer.parseInt(checkingCents) == 0 ? "zero" : checkingCents) + " cents.");
                                I.speak("Your saving account balance is " + (Integer.parseInt(savingDollars) == 0 ? "zero" : savingDollars) + " dollars and " + (Integer.parseInt(savingCents) == 0 ? "zero" : savingCents) + " cents.");
                            }
                        } else if (customerSay.contains("account number")) {
                            I.speak("You want to get your account number, is that correct?");
                            customerSay = (String) we.recordAndTranscribe(5);
                            if (customerSay.contains("yes")) {
                                I.speak("Last 8 digits of your checking account number is " + checkingNumber + ".");
                                I.speak("Last 8 digits of your saving account number is " + savingNumber + ".");
                            }
                        } else if (customerSay.contains("send money")) {
                            I.speak("You want to send money, is that correct?");
                            customerSay = (String) we.recordAndTranscribe(5);
                            while (customerSay.contains("yes")) {
                                String customerSpell= spellMode();
                                I.speak("So, the receiver is " + customerSpell + ", is that correct?");
                                customerSay = (String) we.recordAndTranscribe(5);
                                if (customerSay.contains("yes")) {
                                    customerSpell = customerSpell.replaceAll("\\s", "");
                                    payee_field.setText("@" + customerSpell);
                                    boolean amountConfirm = false;
                                    while (!amountConfirm){
                                        I.speak("How much do you want send to the receiver?");
                                        customerSpell = spellMode();
                                        customerSpell = customerSpell.toLowerCase();
                                        I.speak("Your total amount is " + customerSpell + " .Is that correct?");
                                        customerSay = (String) we.recordAndTranscribe(5);
                                        if (customerSay.contains("yes")){
                                            amount_field.setText(customerSpell);
                                            amountConfirm = true;
                                        } else {
                                            I.speak("Let's try again!");
                                        }
                                    }
                                    I.speak("What is your message to the receiver? If you don't say anything, this field is going to be blank");
                                    customerSay = (String) we.recordAndTranscribe(15);
                                    message_field.setText(customerSay);
                                    Platform.runLater(() -> onSendMoney());
                                    customerSay="Done";
                                } else {
                                    I.speak("Sorry for my mistake. Let's start over");
                                    customerSay="yes";
                                    I.speak("You want to send money, is that correct?");
                                }
                            }
                        } else if (customerSay.contains("mute")) {
                            I.speak("You want to mute voice command, is that correct?");
                            customerSay = (String) we.recordAndTranscribe(2);
                            if (customerSay.contains("yes")) {
                                I.speak("Sure, I will be here when you need me. Just say, I need assistance, when you need help.");
                                mute = true;
                            }
                        } else if (customerSay.contains("deactivate")) {
                            I.speak("You want to deactivate voice command, is that correct?");
                            customerSay = (String) we.recordAndTranscribe(2);
                            if (customerSay.contains("yes")) {
                                I.speak("Voice command deactivated, good bye" + firstName);
                                useVoice = false;
                                break;
                            }
                        } else {
                            I.speak("I am sorry, but I couldn't hear you clearly.");
                        }
                    } catch (LineUnavailableException | InterruptedException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } while (useVoice);
        });
        speechThread.setDaemon(true);
        speechThread.start();
    }

    public void initLatestTransaction() {
        if (Model.getInstance().getLatestTransactions().isEmpty()) {
            Model.getInstance().setLatestTransactions();
        }
    }

    public String spellMode() {
        Trying_Different_Languages I = new Trying_Different_Languages();
        MicrophoneStreamRecognizer we = new MicrophoneStreamRecognizer();
        String customerSay;
        List<String> spelledLetters = new ArrayList<>();
        Map<String, String> phoneticAlphabet = Map.ofEntries(
                Map.entry("ALPHA", "A"),
                Map.entry("BRAVO", "B"),
                Map.entry("CHARLIE", "C"),
                Map.entry("DELTA", "D"),
                Map.entry("ECHO", "E"),
                Map.entry("FOXTROT", "F"),
                Map.entry("GOLF", "G"),
                Map.entry("HOTEL", "H"),
                Map.entry("INDIA", "I"),
                Map.entry("JULIETT", "J"),
                Map.entry("KILO", "K"),
                Map.entry("LIMA", "L"),
                Map.entry("MIKE", "M"),
                Map.entry("NOVEMBER", "N"),
                Map.entry("OSCAR", "O"),
                Map.entry("PAPA", "P"),
                Map.entry("QUEBEC", "Q"),
                Map.entry("ROMEO", "R"),
                Map.entry("SIERRA", "S"),
                Map.entry("TANGO", "T"),
                Map.entry("UNIFORM", "U"),
                Map.entry("VICTOR", "V"),
                Map.entry("WHISKEY", "W"),
                Map.entry("XRAY", "X"),
                Map.entry("YANKEE", "Y"),
                Map.entry("ZULU", "Z"),
                Map.entry("1", "1"),
                Map.entry("2", "2"),
                Map.entry("3", "3"),
                Map.entry("4", "4"),
                Map.entry("5", "5"),
                Map.entry("6", "6"),
                Map.entry("7", "7"),
                Map.entry("8", "8"),
                Map.entry("9", "9"),
                Map.entry("0", "0"),
                Map.entry("done", "done")
        );

        I.speak("Please spell the text or number using the NATO phonetic alphabet, and say 'done' when you're finished. Please say 1 word or 1 number at a time only.");

        while (true) {
            try {
                customerSay = (String) we.recordAndTranscribe(5);
                if (customerSay.equalsIgnoreCase("done")) {
                    break;
                } else {
                    String letter = phoneticAlphabet.get(customerSay.toUpperCase());
                    if (letter != null) {
                        spelledLetters.add(letter.toLowerCase());
                        I.speak("You said '" + letter + "'. Next letter please!");
                    } else {
                        System.out.println(customerSay);
                        I.speak("I didn't recognize that. Please try again.");
                    }
                }
            } catch (LineUnavailableException | InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        StringBuilder spelledText = new StringBuilder();
        for (String letter : spelledLetters) {
            spelledText.append(letter);
        }
        return spelledText.toString();
    }

    private void onSendMoney() {
        String receiver = payee_field.getText();
        double amount = Double.parseDouble((amount_field.getText()));
        String message = message_field.getText();
        String sender = Model.getInstance().getClient().payeeAddressProperty().get();
        ResultSet resultSet = Model.getInstance().getDatabaseDriver().searchClient(receiver);
        boolean receiverNotFound = true;
        try {
            if (resultSet.next()) {
                // Receiver was found, so set the flag to false
                receiverNotFound = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (receiverNotFound) {
            return; // Do nothing if the receiver is not found
        }
        Model.getInstance().getDatabaseDriver().updateBalance(receiver, amount, "ADD");
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
    public static void resetVoice() {
        welcome = true;
        useVoice = true;
    }
}