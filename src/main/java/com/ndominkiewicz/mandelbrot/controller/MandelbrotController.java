package com.ndominkiewicz.mandelbrot.controller;

import com.ndominkiewicz.mandelbrot.App;
import com.ndominkiewicz.mandelbrot.model.Algorithm;
import com.ndominkiewicz.mandelbrot.model.MandelbrotResult;
import com.ndominkiewicz.mandelbrot.model.ViewController;
import com.ndominkiewicz.mandelbrot.service.SequentialService;
import com.ndominkiewicz.mandelbrot.utils.CLI;
import com.ndominkiewicz.mandelbrot.utils.Point;
import com.ndominkiewicz.mandelbrot.utils.Sizer;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MandelbrotController extends ViewController {
    private final int CANVAS_WIDTH = 400;
    private final int CANVAS_HEIGHT = 400;
    protected GraphicsContext graphicsContext;
    protected Canvas canvas;
    protected AnimationTimer animationTimer;
    private Algorithm currentAlgorithm;
    private MainController mainController;
    private final SequentialService sequentialService = new SequentialService();
    private final List<Point> currentPoints = new ArrayList<>();
    @FXML private GridPane rootPane;
    @FXML private FlowPane entryPane;
    @FXML private BorderPane canvasPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadEntryComponent();
        startAnimationLoop();
        //loadDataBinding();
        loadCanvas();
    }

    private void startAnimationLoop() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderCanvas();
            }
        };
        animationTimer.start();
    }

    private void loadDataBinding() {
        sequentialService.getMatrix().addListener((observableValue, oldData, newData) -> {
            if (newData != null && currentAlgorithm.equals(Algorithm.SEQUENTIAL)) {
                synchronized (currentPoints) {
                    currentPoints.clear();

                }
            }
        });

        sequentialService.getIsRunning().addListener(((observableValue, oldData, newData) -> {
            if (newData.equals(false) && currentAlgorithm.equals(Algorithm.SEQUENTIAL)) {
                Platform.runLater(() -> {
                    canvasPane.setBottom(createResult(sequentialService.getResult()));
                });
            }
        }));
    }

    private void loadCanvas() {
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();
        canvasPane.setCenter(canvas);
    }

    private void renderCanvas() {
        synchronized (currentPoints) {
            if (!currentPoints.isEmpty()) {
                for (Point point : currentPoints) {
                    double x = (point.getX() + 1) * (CANVAS_WIDTH / 2.0);
                    double y = (point.getY() + 1) * (CANVAS_HEIGHT / 2.0);

                    boolean inCircle = (point.getX() * point.getX() + point.getY() * point.getY()) <= 1;
                    graphicsContext.setFill(inCircle ? Color.YELLOW : Color.BLACK);
                    graphicsContext.fillOval(x - 1.5, y - 1.5, 3, 3);
                }
            }
        }
    }

    public void run(int width, int height, Algorithm algorithm) {
        this.currentAlgorithm = algorithm;
        graphicsContext.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        drawTemplate();
        switch (currentAlgorithm) {
            case SEQUENTIAL -> {
                sequentialService.run(width, height);
                canvasPane.setBottom(createResult(sequentialService.getResult()));
            }
            case PARALLEL -> {}
        }
    }

    private void drawTemplate() {
        graphicsContext.setStroke(Color.ROSYBROWN);
        graphicsContext.setLineWidth(2);
        graphicsContext.strokeRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        graphicsContext.strokeOval(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    private void loadEntryComponent() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/ndominkiewicz/mandelbrot/components/entry.fxml"));
            Node node = loader.load();
            EntryController entryController = loader.getController();
            entryController.setMandelbrotController(this);
            entryPane.getChildren().add(entryController.getView());
        } catch (IOException e) {
            CLI.error("Could not load the entry.fxml file");
            throw new RuntimeException(e);
        }
    }

    private Node createResult(MandelbrotResult result) {
        HBox resultBox = new HBox();
        resultBox.getStyleClass().add("result-container");

        Label labelTimeInSeconds = new Label("Time in seconds:: ");
        Label timeInSecondsLabel = new Label(String.valueOf(result.elapsedTime() / 1000.0));
        Label labelTimeInMillis = new Label("Time in millis: ");
        Label timeInMillisLabel = new Label(result.elapsedTime()+ " ms");

        labelTimeInMillis.getStyleClass().add("result-label");
        labelTimeInSeconds.getStyleClass().add("result-label");
        timeInMillisLabel.getStyleClass().add("result-value");
        timeInSecondsLabel.getStyleClass().add("result-value");

        resultBox.getChildren().addAll(
                new HBox(labelTimeInSeconds, timeInSecondsLabel),
                new HBox(labelTimeInMillis, timeInMillisLabel)
        );
        return resultBox;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public Node getView() {
        return rootPane;
    }

    public int getCANVAS_HEIGHT() {
        return CANVAS_HEIGHT;
    }

    public int getCANVAS_WIDTH() {
        return CANVAS_WIDTH;
    }
}
