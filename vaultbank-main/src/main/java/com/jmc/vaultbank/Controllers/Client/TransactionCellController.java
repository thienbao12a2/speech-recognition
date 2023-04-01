package com.jmc.vaultbank.Controllers.Client;

import com.jmc.vaultbank.Models.Model;
import com.jmc.vaultbank.Models.Transaction;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionCellController implements Initializable {
    public FontAwesomeIconView incoming_icon;
    public FontAwesomeIconView outgoing_icon;
    public Label transaction_date_label;
    public Label sender_label;
    public Label receiver_label;
    public Label amount_label;
    private final Transaction transaction;
    public TransactionCellController(Transaction transaction) {
        this.transaction = transaction;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sender_label.textProperty().bind(transaction.senderProperty());
        receiver_label.textProperty().bind(transaction.receiverProperty());
        amount_label.textProperty().bind(transaction.amountProperty().asString());
        transaction_date_label.textProperty().bind(transaction.dateProperty().asString());
        transactionIcon();
    }
    private void transactionIcon() {
        if (transaction.senderProperty().get().equals(Model.getInstance().getClient().payeeAddressProperty().get())) {
            incoming_icon.setFill(Color.rgb(240, 240, 240));
            outgoing_icon.setFill(Color.RED);
        } else {
            incoming_icon.setFill(Color.GREEN);
            outgoing_icon.setFill(Color.rgb(240, 240, 240));
        }
    }
}
