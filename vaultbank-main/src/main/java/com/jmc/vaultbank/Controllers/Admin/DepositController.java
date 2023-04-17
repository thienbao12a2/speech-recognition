package com.jmc.vaultbank.Controllers.Admin;

import com.jmc.vaultbank.Models.Client;
import com.jmc.vaultbank.Models.Model;
import com.jmc.vaultbank.Views.ClientCellFactory;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DepositController implements Initializable {
    public TextField address_field;
    public Button search_btn;
    public ListView<Client> result_listview;
    public TextField amount_field;
    public Button deposit_btn;
    private Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        search_btn.setOnAction(event -> onSearchClient());
        deposit_btn.setOnAction(event -> onDeposit());
    }
    private void onSearchClient() {
        ObservableList<Client> searchResults = Model.getInstance().searchClient(address_field.getText());
        result_listview.setItems(searchResults);
        result_listview.setCellFactory(e -> new ClientCellFactory());
        client = searchResults.get(0);
    }
    private void onDeposit() {
        double amount = Double.parseDouble(amount_field.getText());
        double newBalance = amount + client.savingAccountProperty().get().balanceProperty().get();
        if (amount_field.getText() != null) {
            Model.getInstance().getDatabaseDriver().depositSavingAccount(client.payeeAddressProperty().get(), newBalance);
        }
        emptyFields();
    }
    private void emptyFields() {
        address_field.setText("");
        amount_field.setText("");
    }
}
