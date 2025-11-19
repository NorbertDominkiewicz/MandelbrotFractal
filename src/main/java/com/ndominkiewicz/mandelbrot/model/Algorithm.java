package com.ndominkiewicz.mandelbrot.model;

public enum Algorithm {
    SEQUENTIAL("sequential"),
    PARALLEL("parallel");
    private final String value;
    Algorithm(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
