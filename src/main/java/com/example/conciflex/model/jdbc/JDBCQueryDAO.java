package com.example.conciflex.model.jdbc;

import com.example.conciflex.model.ConnectionFactory;
import com.example.conciflex.model.classes.Client;
import com.example.conciflex.model.classes.Query;
import com.example.conciflex.model.dao.QueryDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JDBCQueryDAO implements QueryDAO {
    private static JDBCQueryDAO instance;
    private ObservableList<Query> queryObservableList;

    private JDBCQueryDAO(){
        queryObservableList = FXCollections.observableArrayList();
    }

    public static JDBCQueryDAO getInstance() {
        if(instance == null) {
            instance = new JDBCQueryDAO();
        }
        return instance;
    }

    @Override
    public ObservableList<Query> listQueries(int clienteId) throws Exception {
        queryObservableList.clear();

        Connection connection = ConnectionFactory.getConnectionConciflex();
        PreparedStatement preparedStatement;

        String sql = "select * from query_rp_info where COD_CLIENTE = ?";

        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, clienteId);

        ResultSet resultSet = preparedStatement.executeQuery();

        while(resultSet.next()) {
            int id = resultSet.getInt("CODIGO");
            String field = resultSet.getString("CAMPO");
            String queryString = resultSet.getString("QUERY");

            Query query = new Query(id, field, queryString);
            queryObservableList.add(query);
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return queryObservableList;
    }

    @Override
    public void create(Client client) throws Exception {
        Connection connection = ConnectionFactory.getConnectionConciflex();

        String sql = "INSERT INTO query_rp_info(CAMPO, QUERY, COD_CLIENTE) SELECT CAMPO, QUERY, "+client.getId()+" AS COD_CLIENTE FROM query_rp_info WHERE COD_CLIENTE = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, client.getId());

        preparedStatement.execute();

        preparedStatement.close();
        connection.close();
    }
}
