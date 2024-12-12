package com.sokoban.controllers;

import java.io.InputStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class StaticShape {
	protected int x, y;
	protected ImageView imageView;
	public StaticShape(int x, int y, String imagePath) {
        this.x = x;
        this.y = y;
		Image image = setImage(imagePath);
        this.imageView = new ImageView(image);
        this.imageView.setFitWidth(50);
        this.imageView.setFitHeight(50);
        this.imageView.relocate(x, y);
	}

	private Image setImage(String imagePath) {
        System.out.println("Loading image from path: " + imagePath);
        InputStream inputStream = getClass().getResourceAsStream(imagePath);
        if (inputStream == null) {
            throw new RuntimeException("Failed to find resource at path: " + imagePath);
        }
        Image image = new Image(inputStream);
		return image;
	}

	public double getX() {
        return x / 50.0d;
    }

    public double getY() {
        return y / 50.0d;
    }

	public ImageView getImageView() {
        return imageView;
    }
}
