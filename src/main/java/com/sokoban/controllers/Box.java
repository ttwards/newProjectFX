package com.sokoban.controllers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class Box {
    private ImageView imageView;
    private double x, y;
    boolean isEmpty = false;

    public Box(double x, double y) {
        this.x = x;
        this.y = y;
        Image image = new Image(getClass().getResourceAsStream("/images/yellow.png"));
        this.imageView = new ImageView(image);
        this.imageView.setFitWidth(50);
        this.imageView.setFitHeight(50);
        this.imageView.relocate(x, y);
    }

    public Box(){isEmpty = true;};

    private boolean isEmpty() {return isEmpty;}

    private void setImage(String imagePath) {
        System.out.println("Loading image from path: " + imagePath);
        InputStream inputStream = getClass().getResourceAsStream(imagePath);
        if (inputStream == null) {
            throw new RuntimeException("Failed to find resource at path: " + imagePath);
        }
        Image image = new Image(inputStream);
        this.imageView = new ImageView(image);
    }

    public void relocate(double x, double y) {
        this.x = x;
        this.y = y;
        this.imageView.setLayoutX(x);
        this.imageView.setLayoutY(y);
    }
    public void moveUp() {
        relocate(x, y - 50);
    }

    public void moveDown() {
        relocate(x, y + 50);
    }

    public void moveLeft() {
        relocate(x - 50, y);
    }

    public void moveRight() {
        relocate(x + 50, y);
    }

    public void moveXY(int x, int y) {
        relocate(this.x + x * 50, this.y + y * 50);
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

    public void destroy() {
        this.imageView = null;
    }
}