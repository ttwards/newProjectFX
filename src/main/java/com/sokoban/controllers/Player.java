package com.sokoban.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.image.Image;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.InputStream;

public class Player {
    private ImageView imageView;
    private double x, y;
    private Level level;
    private Pane container;

    public Player(double x, double y, Level level, Pane container) {
        this.x = x;
        this.y = y;
        this.level = level;
        this.container = container;

        // 首先创建 ImageView
        this.imageView = new ImageView();
        this.imageView.setFitWidth(50);
        this.imageView.setFitHeight(50);

        // 然后设置初始图像
        setImage("/images/down.png");

        // 设置初始位置
        this.imageView.relocate(x, y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void moveUp() {
        setImage("/images/up.png");
        relocate(x, y - 50);
    }

    public void moveDown() {
        setImage("/images/down.png");
        relocate(x, y + 50);
    }

    public void moveLeft() {
        setImage("/images/left.png");
        relocate(x - 50, y);
    }

    public void moveRight() {
        setImage("/images/right.png");
        relocate(x + 50, y);
    }

    public void setImage(String imagePath) {
        System.out.println("Loading image from path: " + imagePath);
        InputStream inputStream = getClass().getResourceAsStream(imagePath);
        if (inputStream == null) {
            throw new RuntimeException("Failed to find resource at path: " + imagePath);
        }
        this.imageView.setImage(new Image(inputStream));
    }

    private void relocate(double newX, double newY) {
        if (level.isMoveValid(newX, newY)) {
            this.x = newX;
            this.y = newY;

            // 创建动画
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0.2), // 缩短动画时间使移动更流畅
                            new KeyValue(imageView.layoutXProperty(), newX),
                            new KeyValue(imageView.layoutYProperty(), newY)
                    )
            );
            timeline.play();

        } else {
            System.out.println("Invalid move: (" + newX + ", " + newY + ")");
        }
    }

    public ImageView getImageView() {
        return imageView;
    }

    // 其他方法保持不变
}