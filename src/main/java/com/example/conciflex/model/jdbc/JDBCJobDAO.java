package com.example.conciflex.model.jdbc;

import com.example.conciflex.model.ConnectionFactory;
import com.example.conciflex.model.classes.Client;
import com.example.conciflex.model.classes.Job;
import com.example.conciflex.model.dao.JobDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JDBCJobDAO implements JobDAO {
    private static JDBCJobDAO instance;
    private ObservableList<Job> jobObservableList;

    private JDBCJobDAO(){
        jobObservableList = FXCollections.observableArrayList();
    }

    public static JDBCJobDAO getInstance() {
        if(instance == null) {
            instance = new JDBCJobDAO();
        }
        return instance;
    }

    @Override
    public ObservableList<Job> listarJobs(Client client) throws Exception {
        jobObservableList.clear();

        Connection connection = ConnectionFactory.getConnectionConciflex();
        PreparedStatement preparedStatement;

        String sql = "select * from rp_info_job where COD_CLIENTE = ? and STATUS LIKE ?";

        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, client.getId());
        preparedStatement.setString(2, "N");

        ResultSet resultSet = preparedStatement.executeQuery();

        while(resultSet.next()) {
            Job job = new Job();

            job.setId(resultSet.getInt("CODIGO"));
            job.setIdCliente(resultSet.getInt("COD_CLIENTE"));
            job.setDataInicial(resultSet.getDate("DATA_INICIAL"));
            job.setDataFinal(resultSet.getDate("DATA_FINAL"));
            job.setTipoJob(resultSet.getInt("TIPO_ENVIO"));
            job.setStatusJob(resultSet.getString("STATUS"));
            job.setIdUsuario(resultSet.getInt("COD_USUARIO"));

            jobObservableList.add(job);
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return jobObservableList;
    }

    @Override
    public void atualizarJob(int id) throws Exception {
        String sql = "update rp_info_job set STATUS = ? where CODIGO = ?";

        Connection connection = ConnectionFactory.getConnectionConciflex();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, "S");
        preparedStatement.setInt(2, id);

        preparedStatement.execute();
        preparedStatement.close();
        connection.close();
    }
}
