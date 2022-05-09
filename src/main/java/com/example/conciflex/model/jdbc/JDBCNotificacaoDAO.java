package com.example.conciflex.model.jdbc;

import com.example.conciflex.model.ConnectionFactory;
import com.example.conciflex.model.classes.Notificacao;
import com.example.conciflex.model.dao.NotificacaoDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class JDBCNotificacaoDAO implements NotificacaoDAO {
    private static JDBCNotificacaoDAO instance;

    private JDBCNotificacaoDAO(){}

    public static JDBCNotificacaoDAO getInstance() {
        if(instance == null) {
            instance = new JDBCNotificacaoDAO();
        }
        return instance;
    }

    @Override
    public void notificar(Notificacao notificacao) throws Exception {
        Connection connection = ConnectionFactory.getConnectionConciflex();

        String sql = "insert into notificacoes(COD_CLIENTE, STATUS, MENSAGEM, LIDO, DATA, HORA, COD_TIPO_NOTIFICACAO, COD_USUARIO, DATA_INICIO, DATA_FINAL) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, notificacao.getIdCliente());
        preparedStatement.setInt(2, notificacao.getStatus());
        preparedStatement.setString(3, notificacao.getMensagem());
        preparedStatement.setInt(4, notificacao.getLido());
        preparedStatement.setDate(5, notificacao.getData());
        preparedStatement.setTime(6, notificacao.getHora());
        preparedStatement.setInt(7, notificacao.getIdTipoNotificacao());
        preparedStatement.setInt(8, notificacao.getIdUsuario());
        preparedStatement.setDate(9, notificacao.getDataInicial());
        preparedStatement.setDate(10, notificacao.getDataFinal());

        preparedStatement.execute();

        preparedStatement.close();
        connection.close();
    }
}
