package com.jmc.vaultbank.Controllers.Admin;

import com.jmc.vaultbank.Models.Client;
import com.jmc.vaultbank.Models.Model;
import com.jmc.vaultbank.Views.ClientCellFactory;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientsController implements Initializable {
    public ListView<Client> clients_listView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initClientsList();
        clients_listView.setItems(Model.getInstance().getClients());
        clients_listView.setCellFactory(e -> new ClientCellFactory());
    }
    private void initClientsList() {
        if (Model.getInstance().getClients().isEmpty()) {
            Model.getInstance().setClient();
        }
    }
}
