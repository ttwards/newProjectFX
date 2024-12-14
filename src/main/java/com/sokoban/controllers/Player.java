package com.sokoban.controllers;
import javafx.scene.image.Image;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.InputStream;

public class Player extends DynamicShape {
    public Player(double x, double y, Level level, Pane container) {
		super(x, y, "/images/down.png");

		this.upImage = "/images/up.png";
		this.downImage = "/images/down.png";
		this.leftImage = "/images/left.png";
		this.rightImage = "/images/right.png";

        this.level = level;
        this.container = container;
    }
}