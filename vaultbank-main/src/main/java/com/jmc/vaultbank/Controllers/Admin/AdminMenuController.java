package com.jmc.vaultbank.Controllers.Admin;

import com.jmc.vaultbank.Controllers.Client.DashboardController;
import com.jmc.vaultbank.Models.Model;
import com.jmc.vaultbank.Views.AdminMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable {
    public Button create_client_btn;
    public Button my_clients_btn;
    public Button deposit_btn;
    public Button sign_out_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListener();
    }
    public void addListener() {
        create_client_btn.setOnAction(event -> onCreateClient());
        my_clients_btn.setOnAction(event -> onClients());
        deposit_btn.setOnAction(event -> onDeposit());
        sign_out_btn.setOnAction(event -> onLogout());
    }
    private void onCreateClient() {
        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().set(AdminMenuOptions.CREATE_CLIENT);
    }
    private void onClients() {
        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().set(AdminMenuOptions.CLIENTS);
    }
    private void onDeposit() {
        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().set(AdminMenuOptions.DEPOSIT);
    }
    private void onLogout() {
        Stage stage = (Stage) my_clients_btn.getScene().getWindow();
        Model.getInstance().getViewFactory().closeState(stage);
        Model.getInstance().getViewFactory().showLoginWindow();
        Model.getInstance().setAdminSuccessLogin(false);
    }
}
