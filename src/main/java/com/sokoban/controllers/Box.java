package com.sokoban.controllers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class Box extends DynamicShape {
    public Box(double x, double y) {
        super((int)(50 * x), (int)(50 * y), "/images/yellow.png");
        this.upImage = "/images/yellow.png";
		this.downImage = "/images/yellow.png";
		this.leftImage = "/images/yellow.png";
		this.rightImage = "/images/yellow.png";
    }

    public void destroy() {
        this.imageView = null;
    }
}