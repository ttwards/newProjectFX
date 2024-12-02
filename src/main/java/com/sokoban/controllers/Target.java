package com.sokoban.controllers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Target {
    private ImageView imageView;
    private double x, y;

    public Target(double x, double y) {
        this.x = x;
        this.y = y;
        Image image = new Image(getClass().getResourceAsStream("/images/black.png"));
        this.imageView = new ImageView(image);
        this.imageView.setFitWidth(50);
        this.imageView.setFitHeight(50);
        this.imageView.relocate(x, y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void relocate(double x, double y) {
        this.x = x;
        this.y = y;
        this.imageView.relocate(x, y);
    }
    public ImageView getImageView() {
        return imageView;
    }
}
