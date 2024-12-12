package com.sokoban.controllers;

import java.io.InputStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class StaticShape {
	protected int x, y;
	protected ImageView imageView;
	public StaticShape(double x, double y, String imagePath) {
        this.x = (int) (x * 50);
        this.y = (int) (y * 50);
		Image image = setImage(imagePath);
        this.imageView = new ImageView(image);
        this.imageView.setFitWidth(50);
        this.imageView.setFitHeight(50);
        this.imageView.relocate(this.x, this.y);

		System.out.println("StaticShape created at: (" + x + ", " + y + ")");
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
        return x / 50;
    }

    public double getY() {
        return y / 50;
    }

	public ImageView getImageView() {
        return imageView;
    }
}
