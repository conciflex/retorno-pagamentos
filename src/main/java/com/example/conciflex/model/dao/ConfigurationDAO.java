package com.example.conciflex.model.dao;

import com.example.conciflex.model.classes.Configuration;
import javafx.collections.ObservableList;

public interface ConfigurationDAO {
    void insert(Configuration configuration) throws Exception;
    ObservableList<Configuration> list() throws Exception;
    void delete(Configuration configuration) throws Exception;
}
