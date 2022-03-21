package com.example.conciflex.model.classes;

public class Query {
    private int id;
    private String field;
    private String query;

    public Query(int id, String field, String query) {
        this.id = id;
        this.field = field;
        this.query = query;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
