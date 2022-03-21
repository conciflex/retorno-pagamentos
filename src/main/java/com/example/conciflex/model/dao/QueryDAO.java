package com.example.conciflex.model.dao;

import com.example.conciflex.model.classes.Query;
import javafx.collections.ObservableList;

public interface QueryDAO {
    ObservableList<Query> listQueries(int clienteId) throws Exception;
}
