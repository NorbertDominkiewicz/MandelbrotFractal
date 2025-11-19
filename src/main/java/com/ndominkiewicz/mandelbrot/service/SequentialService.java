package com.ndominkiewicz.mandelbrot.service;

import com.ndominkiewicz.mandelbrot.model.MandelbrotResult;
import com.ndominkiewicz.mandelbrot.utils.Complex;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.Random;

public class SequentialService {
    protected int width;
    protected int height;
    protected MandelbrotResult result;
    private final double X_MIN = -2.0;
    private final double X_MAX = 1.0;
    private final double Y_MIN = -1.0;
    private final double Y_MAX = 1.0;
    private final int MAX_ITERATIONS = 1000;

    private final LongProperty elapsedTime = new SimpleLongProperty(0);
    private final BooleanProperty isRunning = new SimpleBooleanProperty(false);
    private final ListProperty<IntegerProperty[]> matrix = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<int[][]> colorMatrix = new SimpleObjectProperty<>(); // DODANE

    public SequentialService() {}

    private void fillMatrixWithZeros() {
        for (int row = 0; row < height; row ++) {
            IntegerProperty[] matrixRow = new IntegerProperty[width];
            for (int column = 0; column < width; column++) {
                matrixRow[column] = new SimpleIntegerProperty(0);
            }
            matrix.add(matrixRow);
        }
    }

    public void run(int width, int height) {
        if (width <= 0 || height <= 0)
            throw new RuntimeException("Resolution cannot be 0 or lower");

        this.width = width;
        this.height = height;

        matrix.clear();
        fillMatrixWithZeros();

        colorMatrix.set(new int[height][width]);

        if (isRunning.get()) return;
        isRunning.set(true);

        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            int[][] localColorMatrix = new int[height][width];

            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {

                    double cx = X_MIN + (X_MAX - X_MIN) * column / (width - 1);
                    double cy = Y_MIN + (Y_MAX - Y_MIN) * row / (height - 1);
                    Complex c = new Complex(cx, cy);

                    int iterations = mandelbrot(c);
                    localColorMatrix[row][column] = iterations;

                    int r = row, col = column;
                    Platform.runLater(() ->
                            matrix.get(r)[col].set(iterations)
                    );
                }

                if (row % 10 == 0) {
                    final int[][] matrixCopy = copyMatrix(localColorMatrix);
                    Platform.runLater(() -> {
                        colorMatrix.set(matrixCopy);
                    });
                }
            }

            Platform.runLater(() -> {
                colorMatrix.set(localColorMatrix);
            });

            isRunning.set(false);
            long finishTime = System.currentTimeMillis() - startTime;
            result = new MandelbrotResult(finishTime);
            Platform.runLater(() -> elapsedTime.set(finishTime));
        }).start();
    }

    private int[][] copyMatrix(int[][] source) {
        int[][] copy = new int[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();
        }
        return copy;
    }

    private int mandelbrot(Complex c) {
        Complex z = new Complex(0, 0);

        for (int n = 0; n < MAX_ITERATIONS; n++) {
            if (z.modulus() > 2.0) {
                return n;
            }
            z = z.square().add(c);
        }
        return MAX_ITERATIONS;
    }

    public int getMAX_ITERATIONS() {
        return MAX_ITERATIONS;
    }

    public MandelbrotResult getResult() {
        return result;
    }

    public ListProperty<IntegerProperty[]> getMatrix() {
        return matrix;
    }

    public ObjectProperty<int[][]> colorMatrixProperty() {
        return colorMatrix;
    }

    public BooleanProperty getIsRunning() {
        return isRunning;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}