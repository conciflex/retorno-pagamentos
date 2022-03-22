package com.example.conciflex.model.classes;

import java.sql.Date;
import java.sql.Time;

public class Log {
    private int clientId;
    private String message;
    private Date date;
    private Time time;
    private String type;

    public Log(int clientId, String message, Date date, Time time, String type) {
        this.clientId = clientId;
        this.message = message;
        this.date = date;
        this.time = time;
        this.type = type;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
