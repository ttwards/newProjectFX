package com.sokoban.controllers;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SokobanGame extends Application {


    private Player player;
    private Box box;
    private Target target;

    private Level level;
    private Pane gamePane;

    @Override
    public void start(Stage primaryStage) {
        // 创建主布局
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);

        // 创建游戏区域
        gamePane = new Pane();
        root.setCenter(gamePane);

        // 创建关卡选择按钮容器
        HBox buttonContainer = new HBox(10); // 水平间距为10
        buttonContainer.setPadding(new Insets(10));

        // 创建并配置五个按钮
        for (int i = 0; i < 5; i++) {
            final int levelIndex = i + 1; // 关卡索引从1开始
            Button button = new Button("Level " + levelIndex);
            button.setOnAction(event -> loadLevel(levelIndex - 1)); // 索引从0开始
            buttonContainer.getChildren().add(button);
        }

        // 将按钮容器添加到顶部
        root.setTop(buttonContainer);

        // 初始化关卡管理器
        level = new Level(gamePane);
        loadLevel(0); // 默认加载第一关

        scene.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            switch (keyCode) {
                case W:
                    level.getPlayer().moveUp();
                    if(!level.getBox(level.getPlayerX(), level.getPlayerY()).isEmpty){
                        level.getBox(level.getPlayerX(), level.getPlayerY()).moveUp();
                    }
                    break;
                case S:
                    level.getPlayer().moveDown();
                    if(!level.getBox(level.getPlayerX(), level.getPlayerY()).isEmpty) {
                        level.getBox(level.getPlayerX(), level.getPlayerY()).moveDown();
                    }
                    break;
                case A:
                    level.getPlayer().moveLeft();
                    if(!level.getBox(level.getPlayerX(), level.getPlayerY()).isEmpty) {
                        level.getBox(level.getPlayerX(), level.getPlayerY()).moveLeft();
                    }
                    break;
                case D:
                   level.getPlayer().moveRight();
                    if(!level.getBox(level.getPlayerX(), level.getPlayerY()).isEmpty) {
                        level.getBox(level.getPlayerX(), level.getPlayerY()).moveRight();
                    }
                    break;
                default:
                    break;
            }
        });

        primaryStage.setTitle("Sokoban Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadLevel(int levelIndex) {
        level.loadLevel(levelIndex);
    }



    public static void main(String[] args) {
        // 完全禁用硬件加速
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.vsync", "false");
        launch(args);
    }
}