module com.example.conciflex {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    exports com.example.conciflex;
    exports com.example.conciflex.controller;
    exports com.example.conciflex.util;

    opens com.example.conciflex.controller to javafx.fxml;
    opens com.example.conciflex to javafx.fxml;
    opens com.example.conciflex.util to javafx.fxml;
}