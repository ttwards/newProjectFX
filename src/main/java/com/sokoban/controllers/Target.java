package com.sokoban.controllers;

import javafx.scene.image.ImageView;

public class Target extends StaticShape{
    public Target(double x, double y) {
        super((int) x * 50, (int) y * 50, "/images/black.png");
    }
}
