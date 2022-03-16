module com.example.conciflex.retornopagamento {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.conciflex.retornopagamento to javafx.fxml;
    exports com.example.conciflex.retornopagamento;
}