package com.example.conciflex.model.dao;

import com.example.conciflex.model.classes.Client;
import javafx.collections.ObservableList;

public interface ClientDAO {
    ObservableList<Client> list() throws Exception;
    Client search(int id) throws Exception;
}
