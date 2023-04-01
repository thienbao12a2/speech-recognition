module com.jmc.vaultbank {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.sql;
    requires mysql.connector.j;
    requires java.desktop;
    requires org.json;
    requires okhttp3;
    requires jlayer;


    opens com.jmc.vaultbank to javafx.fxml;
    exports com.jmc.vaultbank;
    exports com.jmc.vaultbank.Controllers;
    exports com.jmc.vaultbank.Controllers.Admin;
    exports com.jmc.vaultbank.Controllers.Client;
    exports com.jmc.vaultbank.Models;
    exports com.jmc.vaultbank.Views;
}