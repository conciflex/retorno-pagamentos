package com.example.conciflex.model.jdbc;

import com.example.conciflex.model.classes.Log;
import com.example.conciflex.model.dao.LogDAO;

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

    }
}
