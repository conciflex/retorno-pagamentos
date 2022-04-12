package com.example.conciflex.model.jdbc;

import com.example.conciflex.model.ConnectionFactory;
import com.example.conciflex.model.classes.Client;
import com.example.conciflex.model.dao.ClientDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCClientDAO implements ClientDAO {
    private static JDBCClientDAO instance;
    private ObservableList<Client> clientObservableList;

    private JDBCClientDAO(){
        clientObservableList = FXCollections.observableArrayList();
    }

    public static JDBCClientDAO getInstance() {
        if(instance == null) {
            instance = new JDBCClientDAO();
        }
        return instance;
    }

    private Client loadClient(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("CODIGO");
        String name = resultSet.getString("NOME");

        Client client = new Client(id, name);

        return client;
    }

    @Override
    public ObservableList<Client> list() throws Exception {
        clientObservableList.clear();

        Connection connection = ConnectionFactory.getConnectionConciflex();

        PreparedStatement preparedStatement;
        String sql = "select CODIGO, NOME from clientes WHERE COD_ERP IN (5, 6, 7) order by NOME";
        preparedStatement = connection.prepareStatement(sql);

        ResultSet resultSet = preparedStatement.executeQuery();

        while(resultSet.next()) {
            clientObservableList.add(loadClient(resultSet));
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return clientObservableList;
    }

    @Override
    public Client search(int id) throws Exception {
        Connection connection = ConnectionFactory.getConnectionConciflex();

        PreparedStatement preparedStatement;
        String sql = "select CODIGO, NOME from clientes where CODIGO = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        Client client = null;

        if(resultSet.next()) {
            client = loadClient(resultSet);
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return client;
    }

    @Override
    public ObservableList<Client> listWithoutQuery() throws Exception {
        clientObservableList.clear();

        Connection connection = ConnectionFactory.getConnectionConciflex();

        PreparedStatement preparedStatement;
        String sql = "SELECT * FROM clientes WHERE clientes.COD_ERP IN (5, 6, 7) AND CODIGO NOT IN (692, 668, 696)";
        preparedStatement = connection.prepareStatement(sql);

        ResultSet resultSet = preparedStatement.executeQuery();

        while(resultSet.next()) {
            clientObservableList.add(loadClient(resultSet));
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return clientObservableList;
    }
}
