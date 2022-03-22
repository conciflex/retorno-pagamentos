package com.example.conciflex.controller;

import com.example.conciflex.model.classes.Client;
import com.example.conciflex.model.classes.Configuration;
import com.example.conciflex.model.classes.Log;
import com.example.conciflex.model.classes.Payment;
import com.example.conciflex.model.jdbc.JDBCClientDAO;
import com.example.conciflex.model.jdbc.JDBCConfigurationDAO;
import com.example.conciflex.model.jdbc.JDBCLogDAO;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
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

    private ObservableList<Configuration> configurationObservableList = FXCollections.observableArrayList();
    private ObservableList<java.sql.Date> returnDaysObservableList = FXCollections.observableArrayList();
    private static Client selectedClient;
    private static int paymentsReturnSize;
    private String successType = "Sucesso";
    private String errorType = "Erro";

    public void initialize() {
        try {
            selectedClient = JDBCClientDAO.getInstance().search(JDBCConfigurationDAO.getInstance().getIdFixedClient());
        } catch (Exception e) {
            writeMessageLog("#1 " + e, errorType);
        }

        lbMensagem.setVisible(false);
        spRetornarDias.getValueFactory().setValue(1);

        dpDataInicial.setValue(LocalDate.now());
        dpDataFinal.setValue(LocalDate.now());

        this.loadConfig();
        this.getConfiguration();
        this.processData();
    }

    @FXML
    public void configSave() {
        Time time = null;
        String timeString = String.valueOf(tfTime.getValue());
        int days = (int) spRetornarDias.getValue();
        boolean verificar = true;

        if(timeString == null) {
            showMessage("Por favor selecione um horário de envio!");
        } else {
            Date timeDate = null;

            try {
                timeDate = new SimpleDateFormat("HH:mm").parse(timeString);
            } catch (ParseException e) {
                writeMessageLog("#2 " + e, errorType);
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
            showMessage("Por favor, selecione um horário...");
        }

        if(verificar) {
            Configuration configuration = new Configuration();

            configuration.setTime(time.toString());
            configuration.setClientId(JDBCConfigurationDAO.getInstance().getIdFixedClient());
            configuration.setReturnDays(days);

            try {
                JDBCConfigurationDAO.getInstance().insert(configuration);
                showMessage("Configuração adicionada!");
            } catch (Exception e) {
                writeMessageLog("#3 " + e, errorType);
            }
        }

        loadConfig();
    }

    @FXML
    public void sendPaymentReturn() {
        Thread threadSendPaymentReturn = new Thread(() -> {
            java.sql.Date startDate = java.sql.Date.valueOf(dpDataInicial.getValue());
            java.sql.Date endDate = java.sql.Date.valueOf(dpDataFinal.getValue());

            String query = QueryController.getSearchQuery();

            writeMessageLog("Buscando os dados do cliente " + selectedClient.getName() + "...", successType);

            Platform.runLater(() -> {
                showMessage("Buscando os dados do cliente " + selectedClient.getName() + "...");
            });

            ObservableList<Payment> paymentObservableList = FXCollections.observableArrayList();

            try {
                paymentObservableList = JDBCPaymentDAO.getInstance().list(selectedClient, startDate, endDate, query);
            } catch (Exception e) {
                writeMessageLog("#4 " + e, errorType);
            }

            writeMessageLog("Limpando a tabela cflexarquivomovimento", successType);

            Platform.runLater(() -> {
                showMessage("Limpando a tabela cflexarquivomovimento");
            });

            try {
                JDBCPaymentDAO.getInstance().clearTable();
            } catch (Exception e) {
                writeMessageLog("#5 " + e, errorType);
            }

            writeMessageLog("Inserindo os dados do cliente " + selectedClient.getName() + "...", successType);

            Platform.runLater(() -> {
                showMessage("Inserindo os dados do cliente " + selectedClient.getName() + "...");
            });

            for (Payment payment:paymentObservableList) {
                try {
                    JDBCPaymentDAO.getInstance().create(payment);
                } catch (Exception e) {
                    writeMessageLog("#6 " + e, errorType);
                }
            }

            paymentsReturnSize = paymentObservableList.size();

            writeMessageLog("Processamento concluído! Quantidade inserida: " + paymentsReturnSize, successType);

            Platform.runLater(() -> {
                showMessage("Processamento concluído! Quantidade inserida: " + paymentsReturnSize);
            });
        });

        threadSendPaymentReturn.setDaemon(true);
        threadSendPaymentReturn.start();
    }

    public void processData() {
        Thread threadSendPaymentReturn = new Thread(() -> {
            while(true) {
                this.getConfiguration();

                for (Configuration configuration: configurationObservableList) {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    String now = formatter.format(new Date());
                    Date processDateTime = null;
                    Date nowDateTime = null;

                    try {
                        processDateTime = formatter.parse(configuration.getTime());
                        nowDateTime = formatter.parse(now);
                    } catch (ParseException e) {
                        writeMessageLog("#7 " + e, errorType);
                    }

                    paymentsReturnSize = 0;

                    if (processDateTime != null && processDateTime.equals(nowDateTime)) {
                        returnDaysObservableList.clear();

                        java.sql.Date todayDateSQL = java.sql.Date.valueOf(LocalDate.now());
                        returnDaysObservableList.add(todayDateSQL);

                        if(configuration.getReturnDays() != 0) {
                            for (int i = 1; i <= configuration.getReturnDays(); i++) {
                                java.sql.Date date = java.sql.Date.valueOf(LocalDate.now().minusDays(i));
                                returnDaysObservableList.add(date);
                            }
                        }

                        writeMessageLog("Limpando a tabela cflexarquivomovimento", successType);

                        Platform.runLater(() -> {
                            showMessage("Limpando a tabela cflexarquivomovimento");
                        });

                        try {
                            JDBCPaymentDAO.getInstance().clearTable();
                        } catch (Exception e) {
                            writeMessageLog("#8 " + e, errorType);
                        }

                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            writeMessageLog("#9 " + e, errorType);
                        }

                        for (java.sql.Date returnDay: returnDaysObservableList) {
                            SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
                            String dayString = formatter2.format(returnDay);

                            Platform.runLater(() -> {
                                showMessage("Buscando os dados do cliente " + selectedClient.getName() + " do dia "+dayString+"...");
                            });

                            writeMessageLog("Buscando os dados do cliente " + selectedClient.getName() + " do dia "+dayString+"...", successType);

                            ObservableList<Payment> paymentObservableList = FXCollections.observableArrayList();

                            String query = QueryController.getSearchQuery();

                            try {
                                paymentObservableList = JDBCPaymentDAO.getInstance().list(selectedClient, returnDay, returnDay, query);
                            } catch (Exception e) {
                                writeMessageLog("#10 " + e, errorType);
                            }

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                writeMessageLog("#11 " + e, errorType);
                            }

                            Platform.runLater(() -> {
                                showMessage("Inserindo os dados do cliente " + selectedClient.getName() + " do dia "+dayString+"...");
                            });

                            writeMessageLog("Inserindo os dados do cliente " + selectedClient.getName() + " do dia "+dayString+"...", successType);

                            for (Payment payment:paymentObservableList) {
                                try {
                                    JDBCPaymentDAO.getInstance().create(payment);
                                } catch (Exception e) {
                                    writeMessageLog("#12 " + e, errorType);
                                }
                            }

                            paymentsReturnSize += paymentObservableList.size();

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                writeMessageLog("#13 " + e, errorType);
                            }

                            Platform.runLater(() -> {
                                showMessage("Processamento concluído! Quantidade inserida: " + paymentsReturnSize);
                            });

                            writeMessageLog("Processamento concluído! Quantidade inserida: " + paymentsReturnSize, successType);
                        }
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        threadSendPaymentReturn.setDaemon(true);
        threadSendPaymentReturn.start();
    }

    public void loadConfig() {
        ObservableList<Configuration> observableList = FXCollections.observableArrayList();

        tcHour.setCellValueFactory(new PropertyValueFactory<>("time"));
        tcRetorno.setCellValueFactory(new PropertyValueFactory<>("returnDays"));


        try {
            observableList.addAll(JDBCConfigurationDAO.getInstance().list());
        } catch (Exception e) {
            writeMessageLog("#14 " + e, errorType);
        }

        addButtonTableConfiguration();

        tvConfiguration.setItems(observableList);
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
            showMessage("Configuração deletada!");
        } catch (Exception e) {
            writeMessageLog("#15 " + e, errorType);
        }

        loadConfig();
    }

    public void getConfiguration() {
        configurationObservableList.clear();

        try {
            configurationObservableList.addAll(JDBCConfigurationDAO.getInstance().list());
        } catch (Exception e) {
            writeMessageLog("#16 " + e, errorType);
        }
    }

    public void showMessage(String mensagem) {
        lbMensagem.setVisible(true);
        lbMensagem.setText(mensagem);
    }

    public void writeMessageLog(String message, String type) {
        java.sql.Date nowDateSQL = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        Time nowTimeSQL = Time.valueOf(dtf.format(now));

        Log log = new Log(selectedClient.getId(), message, nowDateSQL, nowTimeSQL, type);

        try {
            JDBCLogDAO.getInstance().create(log);
        } catch (Exception e) {
            writeMessageLog("#17 " + e, errorType);
        }
    }
}