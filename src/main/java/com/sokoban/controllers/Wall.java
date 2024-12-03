package com.sokoban.controllers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class Wall {
    private ImageView imageView;
    private double x, y;

    public Wall(double x, double y) {
        this.x = x;
        this.y = y;
        Image image = new Image(getClass().getResourceAsStream("/images/white.png"));
        this.imageView = new ImageView(image);
        this.imageView.setFitWidth(50);
        this.imageView.setFitHeight(50);
        this.imageView.relocate(x, y);
    }

    private void setImage(String imagePath) {
        System.out.println("Loading image from path: " + imagePath);
        InputStream inputStream = getClass().getResourceAsStream(imagePath);
        if (inputStream == null) {
            throw new RuntimeException("Failed to find resource at path: " + imagePath);
        }
        Image image = new Image(inputStream);
        this.imageView = new ImageView(image);
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

