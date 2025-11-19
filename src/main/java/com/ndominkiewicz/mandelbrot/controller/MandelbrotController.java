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
    private final int CANVAS_WIDTH = 500;
    private final int CANVAS_HEIGHT = 500;
    protected GraphicsContext graphicsContext;
    protected Canvas canvas;
    protected AnimationTimer animationTimer;
    private Algorithm currentAlgorithm;
    private MainController mainController;
    private final SequentialService sequentialService = new SequentialService();
    private int[][] currentColorMatrix;

    @FXML private GridPane rootPane;
    @FXML private FlowPane entryPane;
    @FXML private BorderPane canvasPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadEntryComponent();
        startAnimationLoop();
        loadDataBinding();
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
        sequentialService.colorMatrixProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && currentAlgorithm == Algorithm.SEQUENTIAL) {
                synchronized (this) {
                    currentColorMatrix = newValue;
                }
            }
        });

        sequentialService.getIsRunning().addListener(((observableValue, oldData, newData) -> {
            if (!newData && currentAlgorithm == Algorithm.SEQUENTIAL) {
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
        drawTemplate();
    }

    private void renderCanvas() {
        synchronized (this) {
            if (currentColorMatrix != null) {
                drawMandelbrot();
            }
        }
    }

    private void drawMandelbrot() {
        int width = sequentialService.getWidth();
        int height = sequentialService.getHeight();

        if (width <= 0 || height <= 0) return;

        double pixelWidth = (double) CANVAS_WIDTH / width;
        double pixelHeight = (double) CANVAS_HEIGHT / height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int iterations = currentColorMatrix[y][x];
                Color color = getColorForIterations(iterations, sequentialService.getMAX_ITERATIONS());

                graphicsContext.setFill(color);
                graphicsContext.fillRect(x * pixelWidth, y * pixelHeight, pixelWidth, pixelHeight);
            }
        }
    }

    private Color getColorForIterations(int iterations, int maxIterations) {
        if (iterations == maxIterations) {
            return Color.BLACK;
        }

        double ratio = (double) iterations / maxIterations;
        return Color.hsb(240 * ratio, 0.8, 1.0);

        // Schemat 2: Tęcza
        // return Color.hsb(360.0 * iterations / maxIterations, 0.8, 1.0);

        // Schemat 3: Ciepłe kolory
        // return Color.hsb(30 + 300.0 * iterations / maxIterations, 0.8, 1.0);
    }

    public void run(int width, int height, Algorithm algorithm) {
        this.currentAlgorithm = algorithm;
        graphicsContext.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        switch (currentAlgorithm) {
            case SEQUENTIAL -> {
                sequentialService.run(width, height);
            }
            case PARALLEL -> {

            }
        }
    }

    private void drawTemplate() {
        graphicsContext.setStroke(Color.LIGHTGRAY);
        graphicsContext.setLineWidth(1);
        graphicsContext.strokeRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        graphicsContext.setStroke(Color.rgb(200, 200, 200, 0.3));
        for (int i = 1; i < 4; i++) {
            double x = i * CANVAS_WIDTH / 4.0;
            double y = i * CANVAS_HEIGHT / 4.0;
            graphicsContext.strokeLine(x, 0, x, CANVAS_HEIGHT);
            graphicsContext.strokeLine(0, y, CANVAS_WIDTH, y);
        }
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
        HBox resultBox = new HBox(20);
        resultBox.getStyleClass().add("result-container");

        Label labelTimeInSeconds = new Label("Time in seconds: ");
        Label timeInSecondsLabel = new Label(String.format("%.3f s", result.elapsedTime() / 1000.0));
        Label labelTimeInMillis = new Label("Time in millis: ");
        Label timeInMillisLabel = new Label(result.elapsedTime() + " ms");
        Label labelResolution = new Label("Resolution: ");
        Label resolutionLabel = new Label(sequentialService.getWidth() + "x" + sequentialService.getHeight());

        labelTimeInMillis.getStyleClass().add("result-label");
        labelTimeInSeconds.getStyleClass().add("result-label");
        labelResolution.getStyleClass().add("result-label");
        timeInMillisLabel.getStyleClass().add("result-value");
        timeInSecondsLabel.getStyleClass().add("result-value");
        resolutionLabel.getStyleClass().add("result-value");

        resultBox.getChildren().addAll(
                new HBox(10, labelTimeInSeconds, timeInSecondsLabel),
                new HBox(10, labelTimeInMillis, timeInMillisLabel),
                new HBox(10, labelResolution, resolutionLabel)
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