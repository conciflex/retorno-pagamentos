package com.example.conciflex.model.dao;

import com.example.conciflex.model.classes.DBConnection;

public interface DBConnectionDAO {
    DBConnection search(int id) throws Exception;
}
