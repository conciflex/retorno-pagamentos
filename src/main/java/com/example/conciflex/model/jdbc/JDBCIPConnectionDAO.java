package com.example.conciflex.model.jdbc;

import com.example.conciflex.model.ConnectionFactory;
import com.example.conciflex.model.classes.DBConnection;
import com.example.conciflex.model.classes.IPConnection;
import com.example.conciflex.model.dao.IPConnectionDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JDBCIPConnectionDAO implements IPConnectionDAO {
    private static JDBCIPConnectionDAO instance;

    private JDBCIPConnectionDAO(){}

    public static JDBCIPConnectionDAO getInstance() {
        if(instance == null) {
            instance = new JDBCIPConnectionDAO();
        }
        return instance;
    }

    @Override
    public IPConnection search(String ip) throws Exception {
        Connection connection = ConnectionFactory.getConnectionConciflex();

        PreparedStatement preparedStatement;
        String sql = "select * from ip_connection_integrador where IP LIKE ?";

        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, ip);

        ResultSet resultSet = preparedStatement.executeQuery();

        IPConnection ipConnection = new IPConnection();

        if(resultSet.next()) {
            int idClient = resultSet.getInt("COD_CLIENTE");

            ipConnection.setIdClient(idClient);
            ipConnection.setIp(ip);
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return ipConnection;
    }

    @Override
    public void create(IPConnection ipConnection) throws Exception {
        Connection connection = ConnectionFactory.getConnectionConciflex();

        String sql = "insert into ip_connection_integrador(IP, COD_CLIENTE) values(?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, ipConnection.getIp());
        preparedStatement.setInt(2, ipConnection.getIdClient());

        preparedStatement.execute();

        preparedStatement.close();
        connection.close();
    }
}
