module com.example.conciflex {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    exports com.example.conciflex;
    exports com.example.conciflex.controller;
    exports com.example.conciflex.util;
    exports com.example.conciflex.model.classes;

    opens com.example.conciflex.controller to javafx.fxml;
    opens com.example.conciflex to javafx.fxml;
    opens com.example.conciflex.util to javafx.fxml;
    opens com.example.conciflex.model.classes to javafx.fxml;
}