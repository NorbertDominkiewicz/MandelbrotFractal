package com.ndominkiewicz.mandelbrot.controller;

import com.ndominkiewicz.mandelbrot.model.Algorithm;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import java.net.URL;
import java.util.ResourceBundle;

public class EntryController implements Initializable {
    MandelbrotController mandelbrotController;
    Algorithm algorithm;
    @FXML private TextField pointsField;
    @FXML private ComboBox<Algorithm> algorithmsBox;
    @FXML private Button runBtn;
    @FXML private GridPane rootPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCombo();
        runBtn.setOnAction(actionEvent -> {
            switch (algorithm) {
                case SEQUENTIAL -> mandelbrotController.run(mandelbrotController.getCANVAS_WIDTH(), mandelbrotController.getCANVAS_HEIGHT(), algorithm);
                case PARALLEL -> {}
            }
        });
    }

    public void setMandelbrotController(MandelbrotController mandelbrotController) {
        this.mandelbrotController = mandelbrotController;
    }

    public Node getView() {
        return rootPane;
    }

    private void loadCombo() {
        algorithmsBox.getItems().setAll(Algorithm.values());
        algorithmsBox.setPromptText("Choose an option:");
        algorithmsBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            algorithm = newVal;
        });
    }
}
