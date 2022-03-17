package com.example.conciflex.model.jdbc;

import com.example.conciflex.model.ConnectionFactory;
import com.example.conciflex.model.classes.Client;
import com.example.conciflex.model.classes.Payment;
import com.example.conciflex.model.dao.PaymentDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JDBCPaymentDAO implements PaymentDAO {
    private static JDBCPaymentDAO instance;
    private ObservableList<Payment> paymentObservableList;

    private JDBCPaymentDAO(){
        paymentObservableList = FXCollections.observableArrayList();
    }

    public static JDBCPaymentDAO getInstance() {
        if(instance == null) {
            instance = new JDBCPaymentDAO();
        }
        return instance;
    }

    public Payment loadPayment(ResultSet resultSet) throws Exception {
        Payment payment = new Payment();

        return payment;
    }

    @Override
    public ObservableList<Payment> list(Client client, Date startDate, Date endDate) throws Exception {
        paymentObservableList.clear();

        Connection connection = ConnectionFactory.getConnection();

        String sql = "SELECT ";

        sql += "CASE WHEN (campo_adicional1 is null or campo_adicional1 = '') " +
                "THEN NULL " +
                "ELSE pagamentos_operadoras.id_venda_erp " +
                "END AS cfam_operacao, ";

        sql += "pagamentos_operadoras.data_venda AS cfam_datavenda, ";

        sql += "CASE WHEN pagamentos_operadoras.cod_tipo_lancamento IN (2,3,4,5) " +
                "THEN grupos_clientes.id_erp_ext " +
                "WHEN pagamentos_operadoras.id_venda_erp IS NULL " +
                "THEN grupos_clientes.id_erp_ext " +
                "ELSE substr(pagamentos_operadoras.campo_adicional1,-3,3) " +
                "END AS cfam_unid_codigo, ";

        sql += "CASE WHEN (pagamentos_operadoras.divergencia like '%Forma%' " +
                "OR pagamentos_operadoras.divergencia like '%Bandeira%') " +
                "THEN NULL " +
                "WHEN (pagamentos_operadoras.campo_adicional1 = '' " +
                "OR pagamentos_operadoras.campo_adicional1 IS NULL AND id_venda_erp IS NOT NULL) " +
                "THEN NULL " +
                "ELSE substr(pagamentos_operadoras.campo_adicional1,1,6) " +
                "END AS cfam_pger_conta, ";

        sql += "0 AS cfam_lpgt_codigo, ";

        sql += "pagamentos_operadoras.data_pagamento AS cfam_datapgto, ";

        sql += "pagamentos_operadoras.valor_bruto AS cfam_valorbruto, ";

        sql += "pagamentos_operadoras.valor_liquido AS cfam_valorliquido, ";

        sql += "pagamentos_operadoras.parcela AS cfam_parcela, ";

        sql += "pagamentos_operadoras.nsu AS cfam_nsu, ";

        sql += "pagamentos_operadoras.cod_adquirente AS cfam_adqu_codigo, ";

        sql += "CASE WHEN (pagamentos_operadoras.cod_forma_pagamento = 17 " +
                "OR pagamentos_operadoras.cod_forma_pagamento = 1) " +
                "THEN CONCAT(pagamentos_operadoras.cod_bandeira, '00') " +
                "WHEN pagamentos_operadoras.valor_bruto < 0 AND pagamentos_operadoras.cod_bandeira='160' " +
                "AND grupos_clientes.id_erp_ext='004' THEN '16000' " +
                "WHEN pagamentos_operadoras.cod_tipo_lancamento IN (2,3) " +
                "THEN CONCAT(pagamentos_operadoras.cod_bandeira, '0') " +
                "WHEN pagamentos_operadoras.cod_tipo_lancamento IN (4,5,6,7) " +
                "THEN pagamentos_operadoras.cod_bandeira " +
                "ELSE " +
                "CONCAT(pagamentos_operadoras.cod_bandeira, cod_forma_pagamento) " +
                "END AS cfam_band_codigo, ";

        sql += "bandeira.bandeira as cfam_band_descricao, ";

        sql += "pagamentos_operadoras.cod_banco AS cfam_banc_numero, ";

        sql += "pagamentos_operadoras.agencia AS cfam_agen_numero, ";

        sql += "pagamentos_operadoras.conta AS cfam_ccor_numero, ";

        sql += "CASE WHEN pagamentos_operadoras.cod_tipo_pagamento = 1 " +
                "THEN 'N' ELSE 'S' " +
                "END AS cfam_antecipado, ";

        sql += "pagamentos_operadoras.id_venda_erp AS cfam_registrooperacao, " +
                "CASE WHEN pagamentos_operadoras.cod_tipo_lancamento = 6 " +
                "THEN 1 " +
                "ELSE pagamentos_operadoras.cod_tipo_lancamento " +
                "END as cfam_lanc_codigo, ";

        sql += "CASE " +
                "WHEN pagamentos_operadoras.cod_tipo_lancamento = 1 THEN 'Pagamento' " +
                "WHEN pagamentos_operadoras.cod_tipo_lancamento = 2 THEN 'Ajuste a Debito' " +
                "WHEN pagamentos_operadoras.cod_tipo_lancamento = 3 THEN 'Ajuste a Credito' " +
                "WHEN pagamentos_operadoras.cod_tipo_lancamento = 4 THEN 'Cancelamento' " +
                "WHEN pagamentos_operadoras.cod_tipo_lancamento = 5 THEN 'Chargeback' " +
                "WHEN pagamentos_operadoras.cod_tipo_lancamento = 6 THEN 'Gravame' " +
                "WHEN pagamentos_operadoras.cod_tipo_lancamento = 7 THEN 'Cessao' " +
                "ELSE" +
                "'Nao identificado' " +
                "END as cfam_lanc_descricao, ";

        sql += "controle_ajustes.codigo_operadora AS cfam_ajus_codigo, ";

        sql += "controle_ajustes.descricao_operadora AS cfam_ajus_motivo, ";

        sql += "pagamentos_operadoras.id_loja as cfam_estabelecimento_conciflex, ";

        sql += "pagamentos_operadoras.taxa_antecipacao as cfam_perctaxaantecipacao, ";

        sql += "pagamentos_operadoras.valor_taxa_antecipacao as cfam_valortaxaantecipacao, ";

        sql += "null as cfam_statusconc, ";

        sql += "null as cfam_statusprocessamento, ";

        sql += "null as cfam_dataprocessamento, ";

        sql += "null as cfam_horaprocessamento, ";

        sql += "null as cfam_transacaobaixa, ";

        sql += "pagamentos_operadoras.codigo as cfam_idconciflex, ";

        sql += "null as cfam_dataincconciflex, ";

        sql += "null as cfam_horaincconciflex, ";

        sql += "pagamentos_operadoras.taxa_percentual as cfam_taxaadm, ";

        sql += "null as cfam_versao ";

        sql += "FROM pagamentos_operadoras " +
                "LEFT JOIN bandeira ON pagamentos_operadoras.cod_bandeira = bandeira.codigo " +
                "LEFT JOIN grupos_clientes ON pagamentos_operadoras.cod_grupo_cliente = grupos_clientes.codigo " +
                "LEFT JOIN controle_ajustes ON pagamentos_operadoras.cod_ajuste = controle_ajustes.codigo " +
                "WHERE pagamentos_operadoras.cod_cliente = ? " +
                "AND pagamentos_operadoras.data_pagamento BETWEEN ? AND ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, client.getId());
        preparedStatement.setDate(2, startDate);
        preparedStatement.setDate(3, endDate);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()){
            Payment payment = loadPayment(resultSet);
            paymentObservableList.add(payment);
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return paymentObservableList;
    }
}
