package com.example.conciflex.model.jdbc;

import com.example.conciflex.model.ConnectionFactory;
import com.example.conciflex.model.classes.Client;
import com.example.conciflex.model.dao.BaixaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

    public class JDBCBaixaDAO implements BaixaDAO {
    private static JDBCBaixaDAO instance;

    private JDBCBaixaDAO(){}

    public static JDBCBaixaDAO getInstance() {
        if(instance == null) {
            instance = new JDBCBaixaDAO();
        }
        return instance;
    }

    @Override
    public ObservableList<String> listarBaixasAtualizar(Date startDate, Date endDate) throws Exception {
        ObservableList<String> idERPAtualizarList = FXCollections.observableArrayList();

        Connection connection = ConnectionFactory.getConnectionRPInfo();
        PreparedStatement preparedStatement;

        String sql = "select a.pfin_operacao " +
                "from pendfin a, cflexinterbandeira b " +
                "where cast(a.pfin_pger_conta as varchar) = b.iban_codigo_conta " +
                "and a.pfin_databaixa BETWEEN ? AND ? " +
                "and a.pfin_status = ? ";

        preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setDate(1, startDate);
        preparedStatement.setDate(2, endDate);
        preparedStatement.setString(3, "B");

        ResultSet resultSet = preparedStatement.executeQuery();

        while(resultSet.next()) {
            String idERP = resultSet.getString(1);
            idERPAtualizarList.add(idERP);
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return idERPAtualizarList;
    }

        @Override
        public void atualizarBaixa(String idERP, Client client) throws Exception {
            String sql = "update pagamentos_operadoras set retorno_baixa = ? where cod_cliente = ? and id_venda_erp LIKE ?";

            Connection connection = ConnectionFactory.getConnectionConciflex();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, "S");
            preparedStatement.setInt(2, client.getId());
            preparedStatement.setString(3, idERP);

            preparedStatement.execute();
            preparedStatement.close();
            connection.close();
        }
    }
