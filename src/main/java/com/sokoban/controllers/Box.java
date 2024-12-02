package com.sokoban.controllers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Box {
    private ImageView imageView;
    private double x, y;

    public Box(double x, double y) {
        this.x = x;
        this.y = y;
        Image image = new Image(getClass().getResourceAsStream("/images/yellow.png"));
        this.imageView = new ImageView(image);
        this.imageView.setFitWidth(50);
        this.imageView.setFitHeight(50);
        this.imageView.relocate(x, y);
    }

    public void relocate(double x, double y) {
        this.x = x;
        this.y = y;
        this.imageView.setLayoutX(x);
        this.imageView.setLayoutY(y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public ImageView getImageView() {
        return imageView;
    }
}