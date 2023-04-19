package com.jmc.vaultbank.Controllers.Admin;

import com.jmc.vaultbank.Models.Client;
import com.jmc.vaultbank.Models.DatabaseDriver;
import com.jmc.vaultbank.Views.ClientCellFactory;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientCellController implements Initializable {
    public Label first_name_label;
    public Label last_name_label;
    public Label userID_label;
    public Label checking_acc_label;
    public Label saving_account_label;
    public Label date_label;
    public Button delete_btn;
    private final Client client;
    private final DatabaseDriver databaseDriver;
    private final ClientsController clientsController;

    public ClientCellController(Client client) {
        this.client = client;
        this.databaseDriver = new DatabaseDriver();
        this.clientsController = new ClientsController();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        first_name_label.textProperty().bind(client.firstNameProperty());
        last_name_label.textProperty().bind(client.lastNameProperty());
        userID_label.textProperty().bind(client.payeeAddressProperty());
        checking_acc_label.textProperty().bind(client.checkingAccountProperty().asString());
        saving_account_label.textProperty().bind(client.savingAccountProperty().asString());
        date_label.textProperty().bind(client.dateCreatedProperty().asString());
        delete_btn.setOnAction(event -> databaseDriver.deleteClient(userID_label.getText()));
    }
}
