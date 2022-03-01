module ru.sacmi5.compiler {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens ru.sacmi5.compiler to javafx.fxml;
    exports ru.sacmi5.compiler;
}