module com.example.conciflex {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires mysql.connector.java;
    requires org.postgresql.jdbc;

    exports com.example.conciflex to javafx.graphics;
    exports com.example.conciflex.controller;
    exports com.example.conciflex.util;
    exports com.example.conciflex.model.classes;

    opens com.example.conciflex.controller to javafx.fxml;
    opens com.example.conciflex to javafx.fxml;
    opens com.example.conciflex.util to javafx.fxml;
    opens com.example.conciflex.model.classes to javafx.fxml;
}