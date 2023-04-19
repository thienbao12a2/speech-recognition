package com.jmc.vaultbank.Controllers.Admin;

import com.jmc.vaultbank.Models.Model;
import com.jmc.vaultbank.Views.AccountType;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Random;
import java.util.ResourceBundle;

public class CreateClientController implements Initializable {
    public TextField fname_field;
    public TextField lname_field;
    public TextField password_field;
    public CheckBox address_box;
    public Label address_label;
    public CheckBox add_checking_acc_box;
    public TextField checking_balance_field;
    public CheckBox add_saving_acc_box;
    public TextField saving_balance_field;
    public Button create_client_btn;
    public Label error_label;
    private String username;
    private boolean createCheckingAccount = false;
    private boolean createSavingAccount = false;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        create_client_btn.setOnAction(event -> createClient());
        address_box.selectedProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal) {
                username = createUserName();
                onCreateUserName();
            }
        });
        add_checking_acc_box.selectedProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal) {
                createCheckingAccount = true;
            }
        });
        add_saving_acc_box.selectedProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal) {
                createSavingAccount = true;
            }
        });
    }
    private void createClient() {
        if (createCheckingAccount) {
            createAccount("Checking");
        }
        if (createSavingAccount) {
            createAccount("Saving");
        }
        String firstName = fname_field.getText();
        String lastName = lname_field.getText();
        String password = password_field.getText();
        Model.getInstance().getDatabaseDriver().createClient(firstName, lastName, username, password, LocalDate.now());
        error_label.setStyle("-fx-text-fill: blue; -fx-font-size: 1.3em; -fx-font-weight: bold");
        error_label.setText("Client Created Successfully");
        emptyField();
    }
    public void createAccount(String accountType) {
        String firstSection = "3201";
        String lastSection = Integer.toString((new Random()).nextInt(9999) + 1000);
        String accountNumber = firstSection + " " + lastSection;
        if (accountType.equals("Checking")) {
            double balance = Double.parseDouble(checking_balance_field.getText());
            Model.getInstance().getDatabaseDriver().createCheckingAccount(username, accountNumber, 10, balance);
        } else {
            double balance = Double.parseDouble(saving_balance_field.getText());
            Model.getInstance().getDatabaseDriver().createSavingAccount(username, accountNumber, 3000, balance);
        }
    }
    private void onCreateUserName() {
        if (fname_field.getText() != null && lname_field.getText() != null) {
            address_label.setText(username);
        }
    }
    private String createUserName() {
        int id = Model.getInstance().getDatabaseDriver().getLastClientsId() + 1;
        char firstChar = Character.toLowerCase(fname_field.getText().charAt(0));
        return "@" + firstChar + lname_field.getText().toLowerCase() + id;
    }
    private void emptyField() {
        fname_field.setText("");
        lname_field.setText("");
        password_field.setText("");
        address_box.setSelected(false);
        address_label.setText("");
        add_checking_acc_box.setSelected(false);
        checking_balance_field.setText("");
        add_saving_acc_box.setSelected(false);
        saving_balance_field.setText("");
    }


}
