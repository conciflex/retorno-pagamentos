package com.example.conciflex.model.jdbc;

import com.example.conciflex.model.ConnectionFactory;
import com.example.conciflex.model.classes.Log;
import com.example.conciflex.model.dao.LogDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class JDBCLogDAO implements LogDAO {
    private static JDBCLogDAO instance;

    public static JDBCLogDAO getInstance() {
        if(instance == null) {
            instance = new JDBCLogDAO();
        }
        return instance;
    }

    @Override
    public void create(Log log) throws Exception {
        Connection connection = ConnectionFactory.getConnectionConciflex();

        String sql = "insert into integrador_log(MENSAGEM, COD_CLIENTE, DATA, HORA, TIPO) values(?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, log.getMessage());
        preparedStatement.setInt(2, log.getClientId());
        preparedStatement.setDate(3, log.getDate());
        preparedStatement.setTime(4, log.getTime());
        preparedStatement.setString(5, log.getType());

        preparedStatement.execute();

        preparedStatement.close();
        connection.close();
    }
}
