package com.example.conciflex.model.dao;

import com.example.conciflex.model.classes.Client;
import javafx.collections.ObservableList;
import java.sql.Date;

public interface BaixaDAO {
    ObservableList<String> listarBaixasAtualizar(Date startDate, Date endDate) throws Exception;
    void atualizarBaixa(String idERP, Client client) throws Exception;
}
