package com.jmc.vaultbank.Controllers;

import com.jmc.vaultbank.Models.Model;
import com.jmc.vaultbank.Views.AccountType;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public ChoiceBox<AccountType> acc_selector;
    public Label username_label;
    public TextField username_field;
    public Label password_label;
    public TextField password_field;
    public Button signin_btn;
    public Label error_label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        acc_selector.setItems(FXCollections.observableArrayList(AccountType.CLIENT, AccountType.ADMIN));
        acc_selector.setValue(Model.getInstance().getViewFactory().getLoginAccountType());
        acc_selector.valueProperty().addListener(observable -> setAccount_selector());
        signin_btn.setOnAction(event -> onLogin());
    }
    private void onLogin() {
        Stage stage = (Stage) error_label.getScene().getWindow();
        if (Model.getInstance().getViewFactory().getLoginAccountType() == AccountType.CLIENT) {
            Model.getInstance().evaluateClientCred(username_field.getText(), password_field.getText());
            if (Model.getInstance().getClientSuccessLogin()) {
                Model.getInstance().getViewFactory().showClientWindow();
                Model.getInstance().getViewFactory().closeState(stage);
            } else {
                error_label.setText("Incorrect Username or Password!");
            }
        } else {
            Model.getInstance().evaluateAdminCred(username_field.getText(), password_field.getText());
            if (Model.getInstance().getAdminSuccessLogin()) {
                Model.getInstance().getViewFactory().showAdminWindow();
                Model.getInstance().getViewFactory().closeState(stage);
            } else {
                error_label.setText("Incorrect Username or Password!");
            }
        }
    }
    private void setAccount_selector() {
        Model.getInstance().getViewFactory().setLoginAccountType(acc_selector.getValue());
        if (acc_selector.getValue() == AccountType.ADMIN) {
            username_label.setText("Admin Username");
        } else {
            username_label.setText("Username");
        }
    }



}
