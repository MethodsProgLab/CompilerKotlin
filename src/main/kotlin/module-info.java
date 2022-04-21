module ru.sacmi5.compiler {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires tornadofx;

    opens ru.sacmi5.compiler to javafx.fxml;
    exports ru.sacmi5.compiler;
}