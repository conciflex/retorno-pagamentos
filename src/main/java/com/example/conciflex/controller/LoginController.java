package com.example.conciflex.controller;

import com.example.conciflex.MainApplication;
import com.example.conciflex.model.classes.Client;
import com.example.conciflex.model.classes.IPConnection;
import com.example.conciflex.model.jdbc.JDBCClientDAO;
import com.example.conciflex.model.jdbc.JDBCConfigurationDAO;
import com.example.conciflex.model.jdbc.JDBCIPConnectionDAO;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LoginController {
    @FXML
    public ComboBox<Client> cbClients;

    @FXML
    public PasswordField pfPassword;

    @FXML
    public Parent mainWindow;

    public void initialize() {
        try {
            cbClients.setItems(JDBCClientDAO.getInstance().list());
        } catch (Exception e) {
            e.printStackTrace();
        }

        InetAddress inetAddress = null;

        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (inetAddress != null) {
            IPConnection ipConnection = null;

            System.out.println("IP: " + inetAddress.getHostAddress());

            try {
                ipConnection = JDBCIPConnectionDAO.getInstance().search(inetAddress.getHostAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(ipConnection.getIdClient() != 0) {
                Client client = null;

                try {
                    client = JDBCClientDAO.getInstance().search(ipConnection.getIdClient());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Cliente: " + client.getName());

                redirect(client.getId());
            }
        }
    }

    @FXML
    public void login() {
        InetAddress inetAddress = null;
        Client client = cbClients.getSelectionModel().getSelectedItem();
        String password = pfPassword.getText();

        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (inetAddress != null && client != null && password.equals("cflex1234")) {
            IPConnection ipConnection = new IPConnection();

            ipConnection.setIp(inetAddress.getHostAddress());
            ipConnection.setIdClient(client.getId());

            try {
                JDBCIPConnectionDAO.getInstance().create(ipConnection);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Cliente: " + client.getName());

            redirect(client.getId());
        }
    }

    public void redirect(int idClient) {
        JDBCConfigurationDAO.getInstance().setIdFixedClient(idClient);
        switchWindow("view/main.fxml");
    }

    public void switchWindow(String address) {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApplication.class.getResource(address));

            try {
                Parent layoutWindow = loader.load();

                FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), layoutWindow);
                fadeTransition.setFromValue(0.5);
                fadeTransition.setToValue(1.0);
                fadeTransition.play();

                Stage stage = (Stage) mainWindow.getScene().getWindow();
                stage.setScene(new Scene(layoutWindow, 800, 450));
                stage.setResizable(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
