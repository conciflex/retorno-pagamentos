package com.example.conciflex.model;

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

    private static String CONNECTION_STR_POST = "jdbc:postgresql:"+
            "//192.168.1.200:5432/erp";
    private static String USERNAME_POST = "postgres";
    private static String PASSWORD_POST = "rp@1064";

    /*private static String CONNECTION_STR_POST = "jdbc:postgresql:"+
            "//10.0.0.200:5432/erp";
    private static String USERNAME_POST = "postgres";
    private static String PASSWORD_POST = "z@nde001";*/

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
        for(int i=0;i<pool.length;i++){
            if((pool[i]==null) || (pool[i].isClosed())){
                pool[i] = DriverManager.getConnection(CONNECTION_STR_POST, USERNAME_POST, PASSWORD_POST);
                return pool[i];
            }
        }
        throw new SQLException("Muitas conexões abertas!");
    }
}
