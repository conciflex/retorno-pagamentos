package com.example.conciflex.model.jdbc;

import com.example.conciflex.model.ConnectionFactory;
import com.example.conciflex.model.classes.Client;
import com.example.conciflex.model.classes.Payment;
import com.example.conciflex.model.dao.PaymentDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

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

        String cfam_operacao = resultSet.getString("cfam_operacao");
        Date cfam_datavenda = resultSet.getDate("cfam_datavenda");
        String cfam_unid_codigo = resultSet.getString("cfam_unid_codigo");
        int cfam_pger_conta = resultSet.getInt("cfam_pger_conta");
        int cfam_lpgt_codigo = resultSet.getInt("cfam_lpgt_codigo");
        Date cfam_datapgto = resultSet.getDate("cfam_datapgto");
        double cfam_valorbruto = resultSet.getDouble("cfam_valorbruto");
        double cfam_valorliquido = resultSet.getDouble("cfam_valorliquido");
        int cfam_parcela = resultSet.getInt("cfam_parcela");
        String cfam_nsu = resultSet.getString("cfam_nsu");
        int cfam_adqu_codigo = resultSet.getInt("cfam_adqu_codigo");
        int cfam_band_codigo = resultSet.getInt("cfam_band_codigo");
        String cfam_band_descricao = resultSet.getString("cfam_band_descricao");
        int cfam_banc_numero = resultSet.getInt("cfam_banc_numero");
        int cfam_agen_numero = resultSet.getInt("cfam_agen_numero");
        String cfam_ccor_numero = resultSet.getString("cfam_ccor_numero");
        String cfam_antecipado = resultSet.getString("cfam_antecipado");
        String cfam_registrooperacao = resultSet.getString("cfam_registrooperacao");
        int cfam_lanc_codigo = resultSet.getInt("cfam_lanc_codigo");
        String cfam_lanc_descricao = resultSet.getString("cfam_lanc_descricao");
        String cfam_ajus_codigo = resultSet.getString("cfam_ajus_codigo");
        String cfam_ajus_motivo = resultSet.getString("cfam_ajus_motivo");
        String cfam_estabelecimento_conciflex = resultSet.getString("cfam_estabelecimento_conciflex");
        double cfam_perctaxaantecipacao = resultSet.getDouble("cfam_perctaxaantecipacao");
        double cfam_valortaxaantecipacao = resultSet.getDouble("cfam_valortaxaantecipacao");
        String cfam_statusconc = resultSet.getString("cfam_statusconc");
        String cfam_statusprocessamento = resultSet.getString("cfam_statusprocessamento");
        Date cfam_dataprocessamento = resultSet.getDate("cfam_dataprocessamento");
        String cfam_horaprocessamento = resultSet.getString("cfam_horaprocessamento");
        String cfam_transacaobaixa = resultSet.getString("cfam_transacaobaixa");
        int cfam_idconciflex = resultSet.getInt("cfam_idconciflex");
        Date cfam_dataincconciflex = resultSet.getDate("cfam_dataincconciflex");
        Time cfam_horaincconciflex = resultSet.getTime("cfam_horaincconciflex");
        float cfam_taxaadm = resultSet.getFloat("cfam_taxaadm");
        String cfam_versao = resultSet.getString("cfam_versao");

        payment.setCfam_operacao(cfam_operacao);
        payment.setCfam_datavenda(cfam_datavenda);
        payment.setCfam_unid_codigo(cfam_unid_codigo);
        payment.setCfam_pger_conta(cfam_pger_conta);
        payment.setCfam_lpgt_codigo(cfam_lpgt_codigo);
        payment.setCfam_datapgto(cfam_datapgto);
        payment.setCfam_valorbruto(cfam_valorbruto);
        payment.setCfam_valorliquido(cfam_valorliquido);
        payment.setCfam_parcela(cfam_parcela);
        payment.setCfam_nsu(cfam_nsu);
        payment.setCfam_adqu_codigo(cfam_adqu_codigo);
        payment.setCfam_band_codigo(cfam_band_codigo);
        payment.setCfam_band_descricao(cfam_band_descricao);
        payment.setCfam_banc_numero(cfam_banc_numero);
        payment.setCfam_agen_numero(cfam_agen_numero);
        payment.setCfam_ccor_numero(cfam_ccor_numero);
        payment.setCfam_antecipado(cfam_antecipado);
        payment.setCfam_registrooperacao(cfam_registrooperacao);
        payment.setCfam_lanc_codigo(cfam_lanc_codigo);
        payment.setCfam_lanc_descricao(cfam_lanc_descricao);
        payment.setCfam_ajus_codigo(cfam_ajus_codigo);
        payment.setCfam_ajus_motivo(cfam_ajus_motivo);
        payment.setCfam_estabelecimento_conciflex(cfam_estabelecimento_conciflex);
        payment.setCfam_perctaxaantecipacao(cfam_perctaxaantecipacao);
        payment.setCfam_valortaxaantecipacao(cfam_valortaxaantecipacao);
        payment.setCfam_statusconc(cfam_statusconc);
        payment.setCfam_statusprocessamento(cfam_statusprocessamento);
        payment.setCfam_dataprocessamento(cfam_dataprocessamento);
        payment.setCfam_horaprocessamento(cfam_horaprocessamento);
        payment.setCfam_transacaobaixa(cfam_transacaobaixa);
        payment.setCfam_idconciflex(cfam_idconciflex);
        payment.setCfam_dataincconciflex(cfam_dataincconciflex);
        payment.setCfam_horaincconciflex(cfam_horaincconciflex);
        payment.setCfam_taxaadm(cfam_taxaadm);
        payment.setCfam_versao(cfam_versao);

        return payment;
    }

    @Override
    public ObservableList<Payment> list(Client client, Date startDate, Date endDate) throws Exception {
        paymentObservableList.clear();

        Connection connection = ConnectionFactory.getConnectionConciflex();

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

        sql += "CURDATE() as cfam_dataincconciflex, ";

        sql += "CURTIME() as cfam_horaincconciflex, ";

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

    @Override
    public void create(Payment payment) throws Exception {
        Connection connection = ConnectionFactory.getConnectionRPInfo();

        String sql = "insert into cflexarquivomovimento(" +
                "cfam_operacao, cfam_datavenda, cfam_unid_codigo, cfam_pger_conta, cfam_lpgt_codigo, " +
                "cfam_datapgto, cfam_valorbruto, cfam_valorliquido, cfam_parcela, cfam_nsu, " +
                "cfam_adqu_codigo, cfam_band_codigo, cfam_band_descricao, cfam_banc_numero, cfam_agen_numero, " +
                "cfam_ccor_numero, cfam_antecipado, cfam_registrooperacao, cfam_lanc_codigo, cfam_lanc_descricao, " +
                "cfam_ajus_codigo, cfam_ajus_motivo, cfam_estabelecimento_conciflex, cfam_perctaxaantecipacao, cfam_valortaxaantecipacao, " +
                "cfam_statusconc, cfam_statusprocessamento, cfam_dataprocessamento, cfam_horaprocessamento, cfam_transacaobaixa, " +
                "cfam_idconciflex, cfam_dataincconciflex, cfam_horaincconciflex, cfam_taxaadm, cfam_versao" +
                ") values(" +
                "?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?" +
                ")";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, payment.getCfam_operacao());
        preparedStatement.setDate(2, payment.getCfam_datavenda());
        preparedStatement.setString(3, payment.getCfam_unid_codigo());
        preparedStatement.setInt(4, payment.getCfam_pger_conta());
        preparedStatement.setInt(5, payment.getCfam_lpgt_codigo());
        preparedStatement.setDate(6, payment.getCfam_datapgto());
        preparedStatement.setDouble(7, payment.getCfam_valorbruto());
        preparedStatement.setDouble(8, payment.getCfam_valorliquido());
        preparedStatement.setInt(9, payment.getCfam_parcela());
        preparedStatement.setString(10, payment.getCfam_nsu());
        preparedStatement.setInt(11, payment.getCfam_adqu_codigo());
        preparedStatement.setInt(12, payment.getCfam_band_codigo());
        preparedStatement.setString(13, payment.getCfam_band_descricao());
        preparedStatement.setInt(14, payment.getCfam_banc_numero());
        preparedStatement.setInt(15, payment.getCfam_agen_numero());
        preparedStatement.setString(16, payment.getCfam_ccor_numero());
        preparedStatement.setString(17, payment.getCfam_antecipado());
        preparedStatement.setString(18, payment.getCfam_registrooperacao());
        preparedStatement.setInt(19, payment.getCfam_lanc_codigo());
        preparedStatement.setString(20, payment.getCfam_lanc_descricao());
        preparedStatement.setString(21, payment.getCfam_ajus_codigo());
        preparedStatement.setString(22, payment.getCfam_ajus_motivo());
        preparedStatement.setString(23, payment.getCfam_estabelecimento_conciflex());
        preparedStatement.setDouble(24, payment.getCfam_perctaxaantecipacao());
        preparedStatement.setDouble(25, payment.getCfam_valortaxaantecipacao());
        preparedStatement.setString(26, payment.getCfam_statusconc());
        preparedStatement.setString(27, payment.getCfam_statusprocessamento());
        preparedStatement.setDate(28, payment.getCfam_dataprocessamento());
        preparedStatement.setString(29, payment.getCfam_horaprocessamento());
        preparedStatement.setString(30, payment.getCfam_transacaobaixa());
        preparedStatement.setInt(31, payment.getCfam_idconciflex());
        preparedStatement.setDate(32, payment.getCfam_dataincconciflex());
        preparedStatement.setTime(33, payment.getCfam_horaincconciflex());
        preparedStatement.setFloat(34, payment.getCfam_taxaadm());
        preparedStatement.setString(35, payment.getCfam_versao());

        preparedStatement.execute();

        preparedStatement.close();
        connection.close();
    }

    @Override
    public void clearTable() throws Exception {
        String sql = "delete from cflexarquivomovimento";

        Connection connection = ConnectionFactory.getConnectionRPInfo();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.execute();
        preparedStatement.close();
        connection.close();
    }
}
