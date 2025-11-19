package com.ndominkiewicz.mandelbrot.model;

import com.ndominkiewicz.mandelbrot.controller.MainController;
import javafx.fxml.Initializable;
import javafx.scene.Node;

public abstract class ViewController implements Initializable {
    protected MainController mainController;
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    public Node getView() {
        return null;
    }
    public ViewController getController() {
        return this;
    }
    public MainController getMainController() {
        return mainController;
    }
}