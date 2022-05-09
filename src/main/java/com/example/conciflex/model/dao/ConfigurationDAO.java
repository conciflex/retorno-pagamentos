package com.example.conciflex.model.dao;

import com.example.conciflex.model.classes.Configuration;
import javafx.collections.ObservableList;

public interface ConfigurationDAO {
    void insert(Configuration configuration) throws Exception;
    ObservableList<Configuration> listRetornoPagamento() throws Exception;
    void delete(Configuration configuration) throws Exception;
    int searchReturnDays() throws Exception;
    int searchInitDays() throws Exception;
    ObservableList<String> listProcessingTimes() throws Exception;

    ObservableList<Configuration> listBaixa() throws Exception;
}
