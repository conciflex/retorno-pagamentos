package com.example.conciflex.model.dao;

import com.example.conciflex.model.classes.IPConnection;

public interface IPConnectionDAO {
    IPConnection search(String ip) throws Exception;
    void create(IPConnection ipConnection) throws Exception;
}
