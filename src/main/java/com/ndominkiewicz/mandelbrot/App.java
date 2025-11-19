package com.ndominkiewicz.mandelbrot;

import com.ndominkiewicz.mandelbrot.utils.CLI;
import com.ndominkiewicz.mandelbrot.utils.Cords;
import com.ndominkiewicz.mandelbrot.utils.Sizer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class App extends Application {
    public      static      App     instance;
    public      static      Stage   stage;
    public      static      Scene   scene;
    final       Cords   position = new Cords(0,0);
    protected   final       double  APP_WIDTH = Sizer.getFourFifthOfScreenWidth();
    protected   final       double  APP_HEIGHT = Sizer.getThreeQuartersOfScreenHeight();

    @Override
    public void start(Stage mainStage) throws IOException {
        instance = this;
        stage = mainStage;

        scene = new Scene(loadFXML(), APP_WIDTH, APP_HEIGHT);
        scene.setFill(Color.TRANSPARENT);

        stage.setTitle("PRiR - MonteCarlo");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

        loadMovement();
    }

    private Parent loadFXML() {
        String fileName = "app.fxml";
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fileName));
            return fxmlLoader.load();
        } catch (IOException e) {
            CLI.error("Could not load the " + fileName + "file");
            throw new RuntimeException(e);
        }
    }

    private void loadMovement() {
        scene.setOnMousePressed((MouseEvent event) -> {
            position.setX((int) (event.getScreenX() - stage.getX()));
            position.setY((int) (event.getScreenY() - stage.getY()));
        });

        scene.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - position.getX());
            stage.setY(event.getScreenY() - position.getY());
        });
    }

    public static void close() {
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(300),
                new KeyValue(stage.getScene().getRoot().opacityProperty(), 0)
        );
        timeline.getKeyFrames().add(keyFrame);
        timeline.setOnFinished(actionEvent -> System.exit(0));
        timeline.play();
    }
}
