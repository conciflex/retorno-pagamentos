package com.example.conciflex.controller;

import com.example.conciflex.model.classes.Query;
import com.example.conciflex.model.jdbc.JDBCConfigurationDAO;
import com.example.conciflex.model.jdbc.JDBCQueryDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class QueryController {
    public static String getSearchQuery() {
        int clientId = JDBCConfigurationDAO.getInstance().getIdFixedClient();

        ObservableList<Query> queryObservableList = FXCollections.observableArrayList();

        try {
            queryObservableList = JDBCQueryDAO.getInstance().listQueries(clientId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String querySQL = "SELECT ";

        int i = 0;

        for (Query query: queryObservableList) {
            i++;

            if(!query.getQuery().contains("FROM pagamentos_operadoras")) {
                querySQL += query.getQuery();

                if(i < queryObservableList.size() - 1) {
                    querySQL += ", ";
                }
            }

        }

        for (Query query: queryObservableList) {
            if(query.getQuery().contains("FROM pagamentos_operadoras")) {
                querySQL += " " + query.getQuery();
            }
        }

        return querySQL;
    }
}
