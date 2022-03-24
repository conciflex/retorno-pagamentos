package com.example.conciflex.model.jdbc;

import com.example.conciflex.model.ConnectionFactory;
import com.example.conciflex.model.classes.DBConnection;
import com.example.conciflex.model.dao.DBConnectionDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JDBCDBConnectionDAO implements DBConnectionDAO {
    private static JDBCDBConnectionDAO instance;

    private JDBCDBConnectionDAO(){}

    public static JDBCDBConnectionDAO getInstance() {
        if(instance == null) {
            instance = new JDBCDBConnectionDAO();
        }
        return instance;
    }

    @Override
    public DBConnection search(int id) throws Exception {
        Connection connection = ConnectionFactory.getConnectionConciflex();

        PreparedStatement preparedStatement;
        String sql = "select * from conexao_integrador where COD_CLIENTE = ?";

        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        DBConnection dbConnection = null;

        if(resultSet.next()) {
            String ip = resultSet.getString("IP");
            String port = resultSet.getString("PORTA");
            String instance = resultSet.getString("INSTANCIA");
            String base = resultSet.getString("BANCO");
            String user = resultSet.getString("USUARIO");
            String password = resultSet.getString("SENHA");

            dbConnection.setIp(ip);
            dbConnection.setPort(port);
            dbConnection.setInstance(instance);
            dbConnection.setBase(base);
            dbConnection.setUser(user);
            dbConnection.setPassword(password);
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return dbConnection;
    }
}
