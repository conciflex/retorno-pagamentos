package com.example.conciflex.model.classes;

public class Configuration {
    public int id;
    public String time;
    public int clientId;
    public String clientName;
    public int returnDays;
    public int initDays;
    public String baixa;

    public String getBaixa() {
        return baixa;
    }

    public void setBaixa(String baixa) {
        this.baixa = baixa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getReturnDays() {
        return returnDays;
    }

    public void setReturnDays(int returnDays) {
        this.returnDays = returnDays;
    }

    public int getInitDays() {
        return initDays;
    }

    public void setInitDays(int initDays) {
        this.initDays = initDays;
    }
}
