package com.sokoban.controllers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.InputStream;

public class Box extends DynamicShape {
    public Box(double x, double y, Level level, Pane container) {
        super(x, y, "/images/yellow.png");

        this.upImage = "/images/yellow.png";
		this.downImage = "/images/yellow.png";
		this.leftImage = "/images/yellow.png";
		this.rightImage = "/images/yellow.png";

		this.level = level;
        this.container = container;
    }

    public void destroy() {
        this.imageView = null;
    }
}