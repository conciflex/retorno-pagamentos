package com.example.conciflex.controller;

import com.example.conciflex.model.classes.Client;
import com.example.conciflex.model.classes.Configuration;
import com.example.conciflex.model.classes.Payment;
import com.example.conciflex.model.jdbc.JDBCClientDAO;
import com.example.conciflex.model.jdbc.JDBCConfigurationDAO;
import com.example.conciflex.model.jdbc.JDBCPaymentDAO;
import com.example.conciflex.util.TimeSpinner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class MainController {
    @FXML
    public Label lbMensagem;

    @FXML
    public Spinner spRetornarDias;

    @FXML
    public TimeSpinner tfTime;

    @FXML
    public ComboBox<Client> cbCliente;

    @FXML
    public DatePicker dpDataInicial;

    @FXML
    public DatePicker dpDataFinal;

    @FXML
    public TableView tvConfiguration;

    @FXML
    public TableColumn tcHour;

    @FXML
    public TableColumn tcCliente;

    @FXML
    public TableColumn tcRetorno;

    @FXML
    public TableColumn tcOption;

    private ObservableList<Client> clientObservableList = FXCollections.observableArrayList();
    private ObservableList<Configuration> configurationObservableList = FXCollections.observableArrayList();

    public void initialize() {
        lbMensagem.setVisible(false);
        spRetornarDias.getValueFactory().setValue(1);

        dpDataInicial.setValue(LocalDate.now());
        dpDataFinal.setValue(LocalDate.now());

        listClients();
        loadConfig();

        tvConfiguration.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    public void configSave() {
        Time time = null;
        String timeString = String.valueOf(tfTime.getValue());
        int days = (int) spRetornarDias.getValue();
        boolean verificar = true;
        Client client = cbCliente.getSelectionModel().getSelectedItem();

        if(timeString == null) {
            mostrarMensagem("Por favor selecione um horário de envio!");
        } else {
            Date timeDate = null;

            try {
                timeDate = new SimpleDateFormat("HH:mm").parse(timeString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (timeDate != null) {
                time = new Time(timeDate.getTime());
            }
        }

        if(dpDataInicial.getValue() != null){
            java.sql.Date dataInicial = java.sql.Date.valueOf(dpDataInicial.getValue());
        }

        if(dpDataFinal.getValue() != null){
            java.sql.Date dataFinal = java.sql.Date.valueOf(dpDataFinal.getValue());
        }

        if(time == null) {
            verificar = false;
            mostrarMensagem("Por favor, selecione um horário...");
        }

        if(client.getId() == 0) {
            verificar = false;
            mostrarMensagem("Por favor, selecione um cliente...");
        }

        if(verificar) {
            Configuration configuration = new Configuration();

            configuration.setTime(time.toString());
            configuration.setClientId(client.getId());
            configuration.setReturnDays(days);

            try {
                JDBCConfigurationDAO.getInstance().insert(configuration);
                mostrarMensagem("Configuração adicionada!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void sendPaymentReturn() {
        Client client = cbCliente.getSelectionModel().getSelectedItem();
        java.sql.Date startDate = java.sql.Date.valueOf(dpDataInicial.getValue());
        java.sql.Date endDate = java.sql.Date.valueOf(dpDataFinal.getValue());

        mostrarMensagem("Buscando os dados do cliente " + client.getName() + "...");

        ObservableList<Payment> paymentObservableList = FXCollections.observableArrayList();

        try {
            paymentObservableList = JDBCPaymentDAO.getInstance().list(client, startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadConfig() {
        configurationObservableList.clear();

        tcHour.setCellValueFactory(new PropertyValueFactory<>("time"));
        tcCliente.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        tcRetorno.setCellValueFactory(new PropertyValueFactory<>("returnDays"));


        try {
            configurationObservableList.addAll(JDBCConfigurationDAO.getInstance().list());
        } catch (Exception e) {
            e.printStackTrace();
        }

        addButtonTableConfiguration();

        tvConfiguration.setItems(configurationObservableList);
    }

    public void addButtonTableConfiguration() {
        Callback<TableColumn<Configuration, Void>, TableCell<Configuration, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Configuration, Void> call(final TableColumn<Configuration, Void> param) {
                final TableCell<Configuration, Void> cell = new TableCell<>() {
                    private Button btn = new Button("Remover");
                    private final HBox pane = new HBox(btn);

                    {
                        pane.alignmentProperty().set(Pos.CENTER);
                        pane.spacingProperty().setValue(5);
                        btn.setStyle("-fx-background-color: transparent");

                        btn.setOnAction((ActionEvent event) -> {
                            Configuration configuration = getTableView().getItems().get(getIndex());
                            if(configuration != null) {
                                remove(configuration);
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };

                return cell;
            }
        };

        tcOption.setCellFactory(cellFactory);
    }

    public void remove(Configuration configuration) {
        try {
            JDBCConfigurationDAO.getInstance().delete(configuration);
            mostrarMensagem("Configuração deletada!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadConfig();
    }

    public void listClients() {
        clientObservableList.clear();

        clientObservableList.add(new Client(0, "Selecione um cliente"));

        try {
            clientObservableList.addAll(JDBCClientDAO.getInstance().list());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cbCliente.setItems(clientObservableList);
        cbCliente.getSelectionModel().select(0);
    }

    public void mostrarMensagem(String mensagem) {
        lbMensagem.setVisible(true);
        lbMensagem.setText(mensagem);
    }
}