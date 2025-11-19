package com.ndominkiewicz.mandelbrot.model;

public enum View {
    HOME("home"),
    DASHBOARD("dashboard"),
    MANDELBROT("mandelbrot"),
    USER("user");
    private final String value;
    View(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
