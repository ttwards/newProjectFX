package com.sokoban.controllers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public enum GamePiece {
    WALL("wall.png"),
    FLOOR("floor.png"),
    BOX("box.png"),
    TARGET("target.png"),
    PLAYER("player.png"),
    BOX_ON_TARGET("box_on_target.png"),  // 箱子在目标点上
    PLAYER_ON_TARGET("player_on_target.png"); // 玩家在目标点上

    private Image image;
    private static final int CELL_SIZE = 40; // 每个格子的大小

    GamePiece(String imageName) {
        // 从resources目录加载图片
        this.image = new Image(getClass().getResourceAsStream("/images/" + imageName));
    }

    public ImageView getImageView() {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(CELL_SIZE);
        imageView.setFitHeight(CELL_SIZE);
        return imageView;
    }
}