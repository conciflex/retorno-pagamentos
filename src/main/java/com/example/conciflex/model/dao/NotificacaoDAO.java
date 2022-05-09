package com.example.conciflex.model.dao;

import com.example.conciflex.model.classes.Notificacao;

public interface NotificacaoDAO {
    void notificar(Notificacao notificacao) throws Exception;
}
