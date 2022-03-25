package com.example.conciflex.model;

import com.example.conciflex.model.classes.DBConnection;
import com.example.conciflex.model.jdbc.JDBCConfigurationDAO;
import com.example.conciflex.model.jdbc.JDBCDBConnectionDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static Connection[] pool;

    private static String CONNECTION_STR = "jdbc:mysql:"+
            "//129.159.54.96:3306/conciflex" +
            "?autoReconnect=true&useSSL=false" +
            "&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static String USERNAME = "admin";
    private static String PASSWORD = "Conc!flex5";

    private static int MAX_CONNECTIONS=15;

    static {
        pool = new Connection[MAX_CONNECTIONS];
    }

    public static Connection getConnectionConciflex() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for(int i=0;i<pool.length;i++){
            if((pool[i]==null) || (pool[i].isClosed())){
                pool[i] = DriverManager.getConnection(CONNECTION_STR, USERNAME, PASSWORD);
                return pool[i];
            }
        }
        throw new SQLException("Muitas conexões abertas!");
    }

    public static Connection getConnectionRPInfo() throws SQLException {

        DBConnection dbConnection = null;

        try {
            dbConnection = JDBCDBConnectionDAO.getInstance().search(JDBCConfigurationDAO.getInstance().getIdFixedClient());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dbConnection != null) {
            String CONNECTION_STR_POST =
                    "jdbc:postgresql://" +dbConnection.getIp()+":"+dbConnection.getPort() + "/"+dbConnection.getInstance();

            String USERNAME_POST = dbConnection.getUser();
            String PASSWORD_POST = dbConnection.getPassword();

            for(int i=0;i<pool.length;i++){
                if((pool[i]==null) || (pool[i].isClosed())){
                    pool[i] = DriverManager.getConnection(CONNECTION_STR_POST, USERNAME_POST, PASSWORD_POST);
                    return pool[i];
                }
            }
        }

        throw new SQLException("Muitas conexões abertas!");
    }
}
