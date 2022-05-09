package com.example.conciflex.model.jdbc;

import com.example.conciflex.model.ConnectionFactory;
import com.example.conciflex.model.classes.Configuration;
import com.example.conciflex.model.dao.ConfigurationDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLOutput;

public class JDBCConfigurationDAO implements ConfigurationDAO {
    private static JDBCConfigurationDAO instance;
    private ObservableList<Configuration> configuracaoObservableList;
    private int idFixedClient = 0;

    private JDBCConfigurationDAO(){
        configuracaoObservableList = FXCollections.observableArrayList();
    }

    public static JDBCConfigurationDAO getInstance() {
        if(instance == null) {
            instance = new JDBCConfigurationDAO();
        }
        return instance;
    }

    public Configuration loadConfiguration(ResultSet resultSet) throws Exception {
        Configuration configuration = new Configuration();

        int id = resultSet.getInt("CODIGO");
        String time = resultSet.getString("HORARIO");
        int clientId = resultSet.getInt("COD_CLIENTE");
        int returnDays = resultSet.getInt("RETORNO_DIAS");
        String name = resultSet.getString("NOME");
        int initDays = resultSet.getInt("DIAS_INICIO");
        String baixa = resultSet.getString("BAIXA");

        configuration.setId(id);
        configuration.setTime(time);
        configuration.setClientId(clientId);
        configuration.setReturnDays(returnDays);
        configuration.setClientName(name);
        configuration.setInitDays(initDays);
        configuration.setBaixa(baixa);

        return configuration;
    }

    @Override
    public void insert(Configuration configuration) throws Exception {
        Connection connection = ConnectionFactory.getConnectionConciflex();

        String sql = "insert into retorno_pagamento_rp_info(HORARIO, COD_CLIENTE, RETORNO_DIAS, DIAS_INICIO, BAIXA) " +
                "values(?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, configuration.getTime());
        preparedStatement.setInt(2, configuration.getClientId());
        preparedStatement.setInt(3, configuration.getReturnDays());
        preparedStatement.setInt(4, configuration.getInitDays());
        preparedStatement.setString(5, configuration.getBaixa());
        preparedStatement.execute();

        preparedStatement.close();
        connection.close();
    }

    @Override
    public ObservableList<Configuration> listRetornoPagamento() throws Exception {
        ObservableList<Configuration> configurationObservableList = FXCollections.observableArrayList();

        Connection connection = ConnectionFactory.getConnectionConciflex();

        String sql = "select retorno_pagamento_rp_info.CODIGO, " +
                "retorno_pagamento_rp_info.HORARIO, " +
                "retorno_pagamento_rp_info.COD_CLIENTE, " +
                "retorno_pagamento_rp_info.RETORNO_DIAS, " +
                "retorno_pagamento_rp_info.DIAS_INICIO, " +
                "clientes.NOME, " +
                "retorno_pagamento_rp_info.BAIXA " +
                "from retorno_pagamento_rp_info " +
                "LEFT JOIN clientes ON clientes.CODIGO = retorno_pagamento_rp_info.COD_CLIENTE " +
                "WHERE retorno_pagamento_rp_info.COD_CLIENTE = ? " +
                "AND BAIXA LIKE ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, JDBCConfigurationDAO.getInstance().idFixedClient);
        preparedStatement.setString(2, "N");
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()){
            Configuration configuration = loadConfiguration(resultSet);
            configurationObservableList.add(configuration);
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return configurationObservableList;
    }

    @Override
    public ObservableList<Configuration> listBaixa() throws Exception {
        ObservableList<Configuration> configurationObservableList = FXCollections.observableArrayList();

        Connection connection = ConnectionFactory.getConnectionConciflex();

        String sql = "select retorno_pagamento_rp_info.CODIGO, " +
                "retorno_pagamento_rp_info.HORARIO, " +
                "retorno_pagamento_rp_info.COD_CLIENTE, " +
                "retorno_pagamento_rp_info.RETORNO_DIAS, " +
                "retorno_pagamento_rp_info.DIAS_INICIO, " +
                "clientes.NOME, " +
                "retorno_pagamento_rp_info.BAIXA " +
                "from retorno_pagamento_rp_info " +
                "LEFT JOIN clientes ON clientes.CODIGO = retorno_pagamento_rp_info.COD_CLIENTE " +
                "WHERE retorno_pagamento_rp_info.COD_CLIENTE = ? " +
                "AND BAIXA LIKE ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, JDBCConfigurationDAO.getInstance().idFixedClient);
        preparedStatement.setString(2, "S");
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()){
            Configuration configuration = loadConfiguration(resultSet);
            configurationObservableList.add(configuration);
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return configurationObservableList;
    }

    @Override
    public void delete(Configuration configuration) throws Exception {
        String sql = "delete from retorno_pagamento_rp_info where CODIGO = ?";

        Connection connection = ConnectionFactory.getConnectionConciflex();
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, configuration.getId());

        statement.execute();
        statement.close();
        connection.close();
    }

    @Override
    public int searchReturnDays() throws Exception {
        Connection connection = ConnectionFactory.getConnectionConciflex();

        PreparedStatement preparedStatement;
        String sql = "SELECT RETORNO_DIAS FROM retorno_pagamento_rp_info WHERE COD_CLIENTE = ?";
        preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, idFixedClient);

        ResultSet resultSet = preparedStatement.executeQuery();

        int returnDaysCount = 0;

        if(resultSet.next()) {
            returnDaysCount = resultSet.getInt("RETORNO_DIAS");
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return returnDaysCount;
    }

    @Override
    public int searchInitDays() throws Exception {
        Connection connection = ConnectionFactory.getConnectionConciflex();

        PreparedStatement preparedStatement;
        String sql = "SELECT DIAS_INICIO FROM retorno_pagamento_rp_info WHERE COD_CLIENTE = ?";
        preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, idFixedClient);

        ResultSet resultSet = preparedStatement.executeQuery();

        int initDays = 0;

        if(resultSet.next()) {
            initDays = resultSet.getInt("DIAS_INICIO");
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return initDays;
    }

    @Override
    public ObservableList<String> listProcessingTimes() throws Exception {
        ObservableList<String> processingTimesObservableList = FXCollections.observableArrayList();

        Connection connection = ConnectionFactory.getConnectionConciflex();
        PreparedStatement preparedStatement;

        String sql = "select HORARIO from retorno_pagamento_rp_info where COD_CLIENTE = ?";

        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, idFixedClient);

        ResultSet resultSet = preparedStatement.executeQuery();

        while(resultSet.next()) {
            processingTimesObservableList.add(resultSet.getString("HORARIO"));
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return processingTimesObservableList;
    }

    public int getIdFixedClient() {
        return idFixedClient;
    }

    public void setIdFixedClient(int idFixedClient) {
        this.idFixedClient = idFixedClient;
    }
}
