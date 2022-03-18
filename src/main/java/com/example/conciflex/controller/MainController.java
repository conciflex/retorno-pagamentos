package com.example.conciflex.controller;

import com.example.conciflex.model.classes.Client;
import com.example.conciflex.model.classes.Configuration;
import com.example.conciflex.model.classes.Payment;
import com.example.conciflex.model.jdbc.JDBCClientDAO;
import com.example.conciflex.model.jdbc.JDBCConfigurationDAO;
import com.example.conciflex.model.jdbc.JDBCPaymentDAO;
import com.example.conciflex.util.TimeSpinner;
import javafx.application.Platform;
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
    public DatePicker dpDataInicial;

    @FXML
    public DatePicker dpDataFinal;

    @FXML
    public TableView tvConfiguration;

    @FXML
    public TableColumn tcHour;

    @FXML
    public TableColumn tcRetorno;

    @FXML
    public TableColumn tcOption;

    private ObservableList<Client> clientObservableList = FXCollections.observableArrayList();
    private ObservableList<Configuration> configurationObservableList = FXCollections.observableArrayList();
    private ObservableList<String> processTimesObservableList = FXCollections.observableArrayList();
    private ObservableList<java.sql.Date> returnDaysObservableList = FXCollections.observableArrayList();
    private static Client selectedClient;
    private static int paymentsReturnSize;

    public void initialize() {
        try {
            selectedClient = JDBCClientDAO.getInstance().search(JDBCConfigurationDAO.getInstance().getIdFixedClient());
        } catch (Exception e) {
            e.printStackTrace();
        }

        lbMensagem.setVisible(false);
        spRetornarDias.getValueFactory().setValue(1);

        dpDataInicial.setValue(LocalDate.now());
        dpDataFinal.setValue(LocalDate.now());

        loadConfig();

        this.getConfiguration();

        processData();
    }

    @FXML
    public void configSave() {
        Time time = null;
        String timeString = String.valueOf(tfTime.getValue());
        int days = (int) spRetornarDias.getValue();
        boolean verificar = true;

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

        if(verificar) {
            Configuration configuration = new Configuration();

            configuration.setTime(time.toString());
            configuration.setClientId(JDBCConfigurationDAO.getInstance().getIdFixedClient());
            configuration.setReturnDays(days);

            try {
                JDBCConfigurationDAO.getInstance().insert(configuration);
                mostrarMensagem("Configuração adicionada!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        loadConfig();
    }

    @FXML
    public void sendPaymentReturn() {
        Thread threadSendPaymentReturn = new Thread(() -> {
            boolean verify = true;

            if(verify) {
                java.sql.Date startDate = java.sql.Date.valueOf(dpDataInicial.getValue());
                java.sql.Date endDate = java.sql.Date.valueOf(dpDataFinal.getValue());

                Platform.runLater(() -> {
                    mostrarMensagem("Buscando os dados do cliente " + selectedClient.getName() + "...");
                });

                ObservableList<Payment> paymentObservableList = FXCollections.observableArrayList();

                try {
                    paymentObservableList = JDBCPaymentDAO.getInstance().list(selectedClient, startDate, endDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> {
                    mostrarMensagem("Limpando a tabela cflexarquivomovimento");
                });

                try {
                    JDBCPaymentDAO.getInstance().clearTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> {
                    mostrarMensagem("Inserindo os dados do cliente " + selectedClient.getName() + "...");
                });

                for (Payment payment:paymentObservableList) {
                    try {
                        JDBCPaymentDAO.getInstance().create(payment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                paymentsReturnSize = paymentObservableList.size();

                Platform.runLater(() -> {
                    mostrarMensagem("Processamento concluído! Quantidade inserida: " + paymentsReturnSize);
                });
            }
        });

        threadSendPaymentReturn.setDaemon(true);
        threadSendPaymentReturn.start();
    }

    public void processData() {
        Thread threadSendPaymentReturn = new Thread(() -> {
            while(true) {
                this.getConfiguration();

                for (String processTime: processTimesObservableList) {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    String now = formatter.format(new Date());
                    Date processDateTime = null;
                    Date nowDateTime = null;

                    try {
                        processDateTime = formatter.parse(processTime);
                        nowDateTime = formatter.parse(now);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (processDateTime != null && processDateTime.equals(nowDateTime)) {
                        Platform.runLater(() -> {
                            mostrarMensagem("Limpando a tabela cflexarquivomovimento");
                        });

                        try {
                            JDBCPaymentDAO.getInstance().clearTable();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                        for (java.sql.Date returnDay: returnDaysObservableList) {
                            SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
                            String dayString = formatter2.format(returnDay);

                            Platform.runLater(() -> {
                                mostrarMensagem("Buscando os dados do cliente " + selectedClient.getName() + " do dia "+dayString+"...");
                            });

                            ObservableList<Payment> paymentObservableList = FXCollections.observableArrayList();

                            try {
                                paymentObservableList = JDBCPaymentDAO.getInstance().list(selectedClient, returnDay, returnDay);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }

                            Platform.runLater(() -> {
                                mostrarMensagem("Inserindo os dados do cliente " + selectedClient.getName() + " do dia "+dayString+"...");
                            });

                            for (Payment payment:paymentObservableList) {
                                try {
                                    JDBCPaymentDAO.getInstance().create(payment);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            paymentsReturnSize = paymentObservableList.size();

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }

                            Platform.runLater(() -> {
                                mostrarMensagem("Processamento concluído! Quantidade inserida: " + paymentsReturnSize);
                            });
                        }
                    }
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        threadSendPaymentReturn.setDaemon(true);
        threadSendPaymentReturn.start();
    }

    public void loadConfig() {
        configurationObservableList.clear();

        tcHour.setCellValueFactory(new PropertyValueFactory<>("time"));
        /*tcCliente.setCellValueFactory(new PropertyValueFactory<>("clientName"));*/
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

    public void getConfiguration() {
        returnDaysObservableList.clear();
        processTimesObservableList.clear();

        int returnDaysCount = 0;

        try {
            returnDaysCount = JDBCConfigurationDAO.getInstance().searchReturnDays();
        } catch (Exception e) {
            e.printStackTrace();
        }

        java.sql.Date todayDateSQL = java.sql.Date.valueOf(LocalDate.now());
        returnDaysObservableList.add(todayDateSQL);

        if(returnDaysCount != 0) {
            for (int i = 1; i <= returnDaysCount; i++) {
                java.sql.Date date = java.sql.Date.valueOf(LocalDate.now().minusDays(i));
                returnDaysObservableList.add(date);
            }
        }

        try {
            processTimesObservableList = JDBCConfigurationDAO.getInstance().listProcessingTimes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void listClients() {
        clientObservableList.clear();

        clientObservableList.add(new Client(0, "Selecione um cliente"));

        try {
            clientObservableList.addAll(JDBCClientDAO.getInstance().list());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cbCliente.setItems(clientObservableList);
        cbCliente.getSelectionModel().select(0);
    }*/

    public void mostrarMensagem(String mensagem) {
        lbMensagem.setVisible(true);
        lbMensagem.setText(mensagem);
    }
}