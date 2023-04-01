package com.jmc.vaultbank.Controllers.Client;

import com.jmc.vaultbank.Models.Model;
import com.jmc.vaultbank.Models.Transaction;
import com.jmc.vaultbank.Views.TransactionCellFactory;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionsController implements Initializable {
    public ListView<Transaction> transactions_listview;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initAllTransactionList();
        transactions_listview.setItems(Model.getInstance().getAllTransaction());
        transactions_listview.setCellFactory(event -> new TransactionCellFactory());
    }
    private void initAllTransactionList() {
        if (Model.getInstance().getAllTransaction().isEmpty()) {
            Model.getInstance().setAllTransaction();
        }
    }
}
