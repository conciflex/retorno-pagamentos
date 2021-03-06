package com.example.conciflex.model.dao;

import com.example.conciflex.model.classes.Client;
import com.example.conciflex.model.classes.Payment;
import javafx.collections.ObservableList;
import java.sql.Date;

public interface PaymentDAO {
    ObservableList<Payment> list(Client client, Date startDate, Date endDate, String querySQL) throws Exception;
    void create(Payment payment) throws Exception;
    void clearTable() throws Exception;
}
