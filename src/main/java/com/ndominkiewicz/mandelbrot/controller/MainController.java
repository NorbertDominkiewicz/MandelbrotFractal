package com.ndominkiewicz.mandelbrot.controller;

import com.ndominkiewicz.mandelbrot.App;
import com.ndominkiewicz.mandelbrot.model.View;
import com.ndominkiewicz.mandelbrot.model.ViewController;
import com.ndominkiewicz.mandelbrot.utils.CLI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author ndominkiewicz
 * @Github <a href="https://github.com/NorbertDominkiewicz">Direct Link</a>
 */

public class MainController implements Initializable {
    private final Map<View, ViewController> views = new LinkedHashMap<>();
    private final List<Button> viewsButtons = new ArrayList<>();
    private View currentView;
    @FXML private Button closeBtn;
    @FXML private Button dashboardBtn;
    @FXML private Button mandelbrotBtn;
    @FXML private Button homeBtn;
    @FXML private Label viewLabel;
    @FXML private StackPane contentPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadProps();
        loadActions();
        loadViews();
        changeView(View.MANDELBROT);
    }

    private void loadProps() {
        viewsButtons.add(dashboardBtn);
        viewsButtons.add(mandelbrotBtn);
        viewsButtons.add(homeBtn);
    }

    private void loadActions() {
        closeBtn.setOnAction(actionEvent -> App.close());
        mandelbrotBtn.setOnAction(actionEvent -> changeView(View.MANDELBROT));
    }

    private void loadViews() {
        loadView(View.MANDELBROT);
    }

    private void loadView(View view) {
        ViewController controller = getController(view);
        switch (view) {
            case HOME -> {}
            case MANDELBROT -> {
                MandelbrotController mandelbrotController = (MandelbrotController) controller;
                views.put(view, mandelbrotController);
            }
        }
    }

    private void changeView(View view) {
        if (!view.equals(currentView)) {
            contentPane.getChildren().clear();
            for (Map.Entry<View, ViewController> element : views.entrySet()) {
                if (element.getKey().equals(view)) {
                    Node node = element.getValue().getView();
                    contentPane.getChildren().add(node);
                    contentPane.setAlignment(Pos.CENTER);
                    currentView = view;
                    changeTitle(currentView);
                    changeActiveStyle(currentView);
                }
            }
        }
    }

    private ViewController getController(View view) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/ndominkiewicz/mandelbrot/view/" + view.getValue() + ".fxml"));
            Node node = loader.load();
            return loader.getController();
        } catch (IOException e) {
            CLI.error("Could not load the " + view.getValue() + ".fxml file");
            throw new RuntimeException(e);
        }
    }

    private void changeTitle(View view) {
        switch (view) {
            case HOME -> {}
            case MANDELBROT -> Platform.runLater(() -> viewLabel.setText("Metoda"));
        }
    }

    private void changeActiveStyle(View view) {
        for (Button button : viewsButtons) {
            button.getStyleClass().remove("active");
        }
        switch (view) {
            case HOME -> {}
            case MANDELBROT -> mandelbrotBtn.getStyleClass().add("active");
        }
    }
}
