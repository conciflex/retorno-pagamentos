package com.example.conciflex.controller;

import com.example.conciflex.MainApplication;
import com.example.conciflex.model.classes.*;
import com.example.conciflex.model.jdbc.*;
import com.example.conciflex.util.TimeSpinner;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class MainController {
    @FXML
    public Parent mainWindow;

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
    public TableView tvRetornoPagamento;

    @FXML
    public TableView tvBaixaConfiguration;

    @FXML
    public TableColumn tcHour;

    @FXML
    public TableColumn tcRetorno;

    @FXML
    public TableColumn tcOption;

    @FXML
    public TableColumn tcBaixaHour;

    @FXML
    public TableColumn tcBaixaRetorno;

    @FXML
    public TableColumn tcBaixaOption;

    @FXML
    public Spinner spInicioDias;

    @FXML
    public ToggleGroup group;

    private ObservableList<Configuration> configurationRetornoPagamentosList = FXCollections.observableArrayList();
    private ObservableList<Configuration> configurationBaixaPagamentosList = FXCollections.observableArrayList();

    private ObservableList<java.sql.Date> returnDaysObservableList = FXCollections.observableArrayList();
    private ObservableList<java.sql.Date> returnDaysBaixaObservableList = FXCollections.observableArrayList();

    private static Client selectedClient;

    private static int paymentsReturnSize;

    private String successType = "Sucesso";
    private String errorType = "Erro";

    public void initialize() {
        Font.loadFont(MainApplication.class.getResource("fonts/IBMPlexSans-Regular.ttf").toExternalForm(), 10);
        Font.loadFont(MainApplication.class.getResource("fonts/IBMPlexSans-SemiBold.ttf").toExternalForm(), 10);

        mainWindow.getStylesheets().add(MainApplication.class.getResource("css/main.css").toExternalForm());

        try {
            selectedClient = JDBCClientDAO.getInstance().search(JDBCConfigurationDAO.getInstance().getIdFixedClient());
        } catch (Exception e) {
            writeMessageLog("#1 " + e, errorType);
        }

        lbMensagem.setVisible(false);

        spRetornarDias.getValueFactory().setValue(0);
        dpDataInicial.setValue(LocalDate.now());
        dpDataFinal.setValue(LocalDate.now());

        DBConnection dbConnection = new DBConnection();

        try {
            dbConnection = JDBCDBConnectionDAO.getInstance().search(selectedClient.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dbConnection.getIp() == null) {
            showMessage("Por favor, cadastre os parâmetros de conexão do cliente!");
        } else {
            this.loadRetornoPagamentoTable();
            this.loadBaixaTable();
            this.getPaymentReturnConfiguration();
            this.processData();
            this.searchProcess();
        }
    }

    @FXML
    public void configSave() {
        RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
        String toogleGroupValue = selectedRadioButton.getText();

        Time time = null;
        String timeString = String.valueOf(tfTime.getValue());
        int days = (int) spRetornarDias.getValue();
        int initDays = (int) spInicioDias.getValue();
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
            configuration.setInitDays(initDays);

            if(toogleGroupValue.equals("Retorno de Pagamento")) {
                configuration.setBaixa("N");
            } else {
                configuration.setBaixa("S");
            }

            try {
                JDBCConfigurationDAO.getInstance().insert(configuration);
                showMessage("Configuração adicionada!");
            } catch (Exception e) {
                writeMessageLog("#3 " + e, errorType);
            }
        }

        loadRetornoPagamentoTable();
        loadBaixaTable();
    }

    public Thread sendPaymentReturn(java.sql.Date startDate, java.sql.Date endDate) {
        Thread threadSendPaymentReturn = new Thread(() -> {
            String query = QueryController.getSearchQuery();

            String message_search = "Buscando os dados do cliente " + selectedClient.getName() + " do dia "+startDate+" ao dia "+endDate+"...";

            writeMessageLog(message_search, successType);

            Platform.runLater(() -> showMessage(message_search));

            ObservableList<Payment> paymentObservableList = FXCollections.observableArrayList();

            try {
                paymentObservableList = JDBCPaymentDAO.getInstance().list(selectedClient, startDate, endDate, query);
            } catch (Exception e) {
                writeMessageLog("#4 " + e, errorType);
            }

            writeMessageLog("Limpando a tabela cflexarquivomovimento", successType);

            Platform.runLater(() -> showMessage("Limpando a tabela cflexarquivomovimento"));

            try {
                JDBCPaymentDAO.getInstance().clearTable();
            } catch (Exception e) {
                writeMessageLog("#5 " + e, errorType);
            }

            String message_insert = "Inserindo os dados do cliente " + selectedClient.getName() + " do dia "+startDate+" ao dia "+endDate+"...";

            Platform.runLater(() -> showMessage(message_insert));

            writeMessageLog(message_insert, successType);

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

        return threadSendPaymentReturn;
    }

    public Thread sendBaixaReturn(java.sql.Date startDate, java.sql.Date endDate) {
        Thread threadSendBaixa = new Thread(() -> {
            ObservableList<String> idERPAtualizarList = FXCollections.observableArrayList();

            Platform.runLater(() -> {
                showMessage("Buscando dados para realizar baixa do dia "+startDate+" ao dia "+endDate+"...");
            });

            writeMessageLog("Buscando dados para realizar baixa do dia "+startDate+" ao dia "+endDate+"...", successType);

            try {
                idERPAtualizarList = JDBCBaixaDAO.getInstance().listarBaixasAtualizar(startDate, endDate);
            } catch (Exception e) {
                writeMessageLog("#7 " + e, errorType);
            }

            Platform.runLater(() -> {
                showMessage("Atualizando baixa dos pagamentos");
            });

            writeMessageLog("Atualizando baixa dos pagamentos", successType);

            for (String idERP:idERPAtualizarList) {
                try {
                    JDBCBaixaDAO.getInstance().atualizarBaixa(idERP, selectedClient);
                } catch (Exception e) {
                    writeMessageLog("#8 " + e, errorType);
                }
            }

            Platform.runLater(() -> {
                showMessage("Baixa realizada com sucesso!");
            });

            writeMessageLog("Baixa realizada com sucesso!", successType);
        });

        return threadSendBaixa;
    }

    @FXML
    public void send() {
        RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
        String toogleGroupValue = selectedRadioButton.getText();

        java.sql.Date startDate = java.sql.Date.valueOf(dpDataInicial.getValue());
        java.sql.Date endDate = java.sql.Date.valueOf(dpDataFinal.getValue());

        if(toogleGroupValue.equals("Retorno de Pagamento")) {
            Thread threadSendPaymentReturn = sendPaymentReturn(startDate, endDate);

            threadSendPaymentReturn.setDaemon(true);
            threadSendPaymentReturn.start();
        } else {
            Thread threadSendBaixa = sendBaixaReturn(startDate, endDate);

            threadSendBaixa.setDaemon(true);
            threadSendBaixa.start();
        }
    }

    public void searchProcess() {
        Thread threadsearchProcess = new Thread(() -> {
            while(true) {
                ObservableList<Job> jobObservableList = FXCollections.observableArrayList();

                try {
                    jobObservableList = JDBCJobDAO.getInstance().listarJobs(selectedClient);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(jobObservableList.size() > 0) {
                    for (Job job: jobObservableList) {
                        String mensagem = "";

                        switch (job.getTipoJob()) {
                            case 1 -> {
                                writeMessageLog("Processando retorno de pagamentos por envio pontual", successType);

                                Platform.runLater(() -> {
                                    showMessage("Processando retorno de pagamentos por envio pontual");
                                });

                                mensagem = "Retorno de pagamento processado!";
                                Thread threadSendPaymentReturn = sendPaymentReturn(job.getDataInicial(), job.getDataFinal());

                                threadSendPaymentReturn.setDaemon(true);
                                threadSendPaymentReturn.start();

                                try {
                                    threadSendPaymentReturn.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            case 2 -> {
                                writeMessageLog("Processando baixa de pagamentos por envio pontual", successType);

                                Platform.runLater(() -> {
                                    showMessage("Processando baixa de pagamentos por envio pontual");
                                });

                                mensagem = "Baixa processada!";

                                Thread threadSendBaixa = sendBaixaReturn(job.getDataInicial(), job.getDataFinal());

                                threadSendBaixa.setDaemon(true);
                                threadSendBaixa.start();

                                try {
                                    threadSendBaixa.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        Notificacao notificacao = new Notificacao();

                        java.sql.Date dataImportacao = new java.sql.Date(Calendar.getInstance().getTime().getTime());
                        Time horaImportacao = java.sql.Time.valueOf(new SimpleDateFormat("HH:mm:ss").format(new java.util.Date()));

                        notificacao.setIdCliente(selectedClient.getId());
                        notificacao.setStatus(1);
                        notificacao.setMensagem(mensagem);
                        notificacao.setLido(0);
                        notificacao.setData(dataImportacao);
                        notificacao.setHora(horaImportacao);
                        notificacao.setIdTipoNotificacao(9);
                        notificacao.setIdUsuario(job.getIdUsuario());
                        notificacao.setDataInicial(job.getDataInicial());
                        notificacao.setDataFinal(job.getDataFinal());

                        try {
                            JDBCNotificacaoDAO.getInstance().notificar(notificacao);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            JDBCJobDAO.getInstance().atualizarJob(job.getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    writeMessageLog("#13 " + e, errorType);
                }
            }
        });

        threadsearchProcess.setDaemon(true);
        threadsearchProcess.start();
    }

    public void generateQueryClient() {
        ObservableList<Client> clientObservableList = FXCollections.observableArrayList();

        try {
            clientObservableList = JDBCClientDAO.getInstance().listWithoutQuery();
        } catch (Exception e) {
            writeMessageLog("#9 " + e, errorType);
        }

        for (Client client: clientObservableList) {
            System.out.println("Criando query para cliente: " + client.getName());

            try {
                JDBCQueryDAO.getInstance().create(client);
            } catch (Exception e) {
                writeMessageLog("#10 " + e, errorType);
            }
        }
    }

    public void processData() {
        Thread threadSendPaymentReturn = new Thread(() -> {
            while(true) {
                this.getPaymentReturnConfiguration();

                for (Configuration configuration: configurationRetornoPagamentosList) {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    String now = formatter.format(new Date());
                    Date processDateTime = null;
                    Date nowDateTime = null;

                    try {
                        processDateTime = formatter.parse(configuration.getTime());
                        nowDateTime = formatter.parse(now);
                    } catch (ParseException e) {
                        writeMessageLog("#11 " + e, errorType);
                    }

                    paymentsReturnSize = 0;

                    if (processDateTime != null && processDateTime.equals(nowDateTime)) {
                        returnDaysObservableList.clear();

                        int initDays = configuration.getInitDays();

                        java.sql.Date todayDateSQL = java.sql.Date.valueOf(LocalDate.now().minusDays(initDays));

                        returnDaysObservableList.add(todayDateSQL);

                        if(configuration.getReturnDays() != 0) {
                            for (int i = 1; i <= configuration.getReturnDays(); i++) {
                                java.sql.Date date = java.sql.Date.valueOf(LocalDate.now().minusDays(initDays).minusDays(i));
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
                            writeMessageLog("#12 " + e, errorType);
                        }

                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            writeMessageLog("#13 " + e, errorType);
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
                                writeMessageLog("#14 " + e, errorType);
                            }

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                writeMessageLog("#15 " + e, errorType);
                            }

                            Platform.runLater(() -> {
                                showMessage("Inserindo os dados do cliente " + selectedClient.getName() + " do dia "+dayString+"...");
                            });

                            writeMessageLog("Inserindo os dados do cliente " + selectedClient.getName() + " do dia "+dayString+"...", successType);

                            for (Payment payment:paymentObservableList) {
                                try {
                                    JDBCPaymentDAO.getInstance().create(payment);
                                } catch (Exception e) {
                                    writeMessageLog("#16 " + e, errorType);
                                }
                            }

                            paymentsReturnSize += paymentObservableList.size();

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                writeMessageLog("#17 " + e, errorType);
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

        Thread threadSendPaymentBaixa = new Thread(() -> {
            while(true) {
                this.getPaymentBaixaConfiguration();

                for (Configuration configuration: configurationBaixaPagamentosList) {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    String now = formatter.format(new Date());
                    Date processDateTime = null;
                    Date nowDateTime = null;

                    try {
                        processDateTime = formatter.parse(configuration.getTime());
                        nowDateTime = formatter.parse(now);
                    } catch (ParseException e) {
                        writeMessageLog("#18 " + e, errorType);
                    }

                    if (processDateTime != null && processDateTime.equals(nowDateTime)) {
                        returnDaysBaixaObservableList.clear();

                        int initDays = configuration.getInitDays();

                        java.sql.Date todayDateSQL = java.sql.Date.valueOf(LocalDate.now().minusDays(initDays));

                        returnDaysBaixaObservableList.add(todayDateSQL);

                        if(configuration.getReturnDays() != 0) {
                            for (int i = 1; i <= configuration.getReturnDays(); i++) {
                                java.sql.Date date = java.sql.Date.valueOf(LocalDate.now().minusDays(initDays).minusDays(i));
                                returnDaysBaixaObservableList.add(date);
                            }
                        }

                        for (java.sql.Date returnDay: returnDaysBaixaObservableList) {
                            SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
                            String dayString = formatter2.format(returnDay);

                            ObservableList<String> idERPAtualizarList = FXCollections.observableArrayList();

                            Platform.runLater(() -> {
                                showMessage("Buscando dados para realizar baixa do dia "+dayString+"...");
                            });

                            writeMessageLog("Buscando dados para realizar baixa do dia "+dayString+"...", successType);

                            try {
                                idERPAtualizarList = JDBCBaixaDAO.getInstance().listarBaixasAtualizar(returnDay, returnDay);
                            } catch (Exception e) {
                                writeMessageLog("#7 " + e, errorType);
                            }

                            Platform.runLater(() -> {
                                showMessage("Atualizando baixa dos pagamentos do dia "+dayString+"");
                            });

                            writeMessageLog("Atualizando baixa dos pagamentos do dia "+dayString+"", successType);

                            for (String idERP:idERPAtualizarList) {
                                try {
                                    JDBCBaixaDAO.getInstance().atualizarBaixa(idERP, selectedClient);
                                } catch (Exception e) {
                                    writeMessageLog("#8 " + e, errorType);
                                }
                            }

                            Platform.runLater(() -> {
                                showMessage("Baixa do dia "+dayString+" realizada com sucesso!");
                            });

                            writeMessageLog("Baixa do dia "+dayString+" realizada com sucesso!", successType);
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

        threadSendPaymentBaixa.setDaemon(true);
        threadSendPaymentBaixa.start();
    }

    public void loadRetornoPagamentoTable() {
        ObservableList<Configuration> configRetornoPagamentoList = FXCollections.observableArrayList();

        tcHour.setCellValueFactory(new PropertyValueFactory<>("time"));
        tcRetorno.setCellValueFactory(new PropertyValueFactory<>("returnDays"));

        try {
            configRetornoPagamentoList = JDBCConfigurationDAO.getInstance().listRetornoPagamento();
        } catch (Exception e) {
            writeMessageLog("#19 " + e, errorType);
        }

        addButtonTableRetornoPagamento();

        tvRetornoPagamento.setItems(configRetornoPagamentoList);
    }

    public void loadBaixaTable() {
        ObservableList<Configuration> configBaixaList = FXCollections.observableArrayList();

        tcBaixaHour.setCellValueFactory(new PropertyValueFactory<>("time"));
        tcBaixaRetorno.setCellValueFactory(new PropertyValueFactory<>("returnDays"));

        try {
            configBaixaList = JDBCConfigurationDAO.getInstance().listBaixa();
        } catch (Exception e) {
            writeMessageLog("#20 " + e, errorType);
        }

        addButtonTableBaixa();

        tvBaixaConfiguration.setItems(configBaixaList);
    }

    public void addButtonTableRetornoPagamento() {
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

    public void addButtonTableBaixa() {
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

        tcBaixaOption.setCellFactory(cellFactory);
    }

    public void remove(Configuration configuration) {
        try {
            JDBCConfigurationDAO.getInstance().delete(configuration);
            showMessage("Configuração deletada!");
        } catch (Exception e) {
            writeMessageLog("#21 " + e, errorType);
        }

        loadRetornoPagamentoTable();
        loadBaixaTable();
    }

    public void getPaymentReturnConfiguration() {
        configurationRetornoPagamentosList.clear();

        try {
            configurationRetornoPagamentosList = JDBCConfigurationDAO.getInstance().listRetornoPagamento();
        } catch (Exception e) {
            writeMessageLog("#22 " + e, errorType);
        }
    }

    public void getPaymentBaixaConfiguration() {
        configurationBaixaPagamentosList.clear();

        try {
            configurationBaixaPagamentosList = JDBCConfigurationDAO.getInstance().listBaixa();
        } catch (Exception e) {
            writeMessageLog("#23 " + e, errorType);
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
            e.printStackTrace();
        }
    }
}