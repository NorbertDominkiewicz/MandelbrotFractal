module com.ndominkiewicz.mandelbrot {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;

    opens com.ndominkiewicz.mandelbrot to javafx.fxml;
    exports com.ndominkiewicz.mandelbrot;
    opens com.ndominkiewicz.mandelbrot.controller to javafx.fxml;
    exports com.ndominkiewicz.mandelbrot.controller;
}