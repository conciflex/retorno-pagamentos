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

    private static int MAX_CONNECTIONS=15;

    static {
        pool = new Connection[MAX_CONNECTIONS];
    }

    public static Connection getConnection() throws SQLException {
        for(int i=0;i<pool.length;i++){
            if((pool[i]==null) || (pool[i].isClosed())){
                pool[i] = DriverManager.getConnection(CONNECTION_STR,
                        USERNAME,
                        PASSWORD
                );
                return pool[i];
            }
        }
        throw new SQLException("Muitas conexões abertas!");
    }
}
