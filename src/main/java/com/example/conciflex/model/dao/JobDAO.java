package com.example.conciflex.model.dao;

import com.example.conciflex.model.classes.Client;
import com.example.conciflex.model.classes.Job;
import javafx.collections.ObservableList;

public interface JobDAO {
    ObservableList<Job> listarJobs(Client client) throws Exception;
    void atualizarJob(int id) throws Exception;
}
