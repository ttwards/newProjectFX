package com.sokoban.controllers;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SokobanGame extends Application {

    private Pane gamePane;
    private Player player;
    private Box box;
    private Target target;

    @Override
    public void start(Stage primaryStage) {
        // 初始化游戏界面
        gamePane = new Pane();
        gamePane.setPrefSize(400, 400);
        gamePane.setFocusTraversable(true);// 设置根节点可聚焦

        // 创建玩家、箱子和目标
        player = new Player(100, 100);
        box = new Box(200, 200);
        target = new Target(300, 300);

        // 添加到游戏界面
        gamePane.getChildren().addAll(player.getImageView(), box.getImageView(), target.getImageView());

        // 创建重置按钮
        Button resetButton = new Button("Reset Level");
        resetButton.setOnAction(event -> resetLevel());

        // 创建场景
        Scene scene = new Scene(new Pane(gamePane, resetButton), 400, 450);
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        gamePane.requestFocus();

        primaryStage.setTitle("Sokoban Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleKeyPress(KeyCode keyCode) {
        switch (keyCode) {
            case UP:
                movePlayer(0, -50, "up");
                break;
            case DOWN:
                movePlayer(0, 50, "down");
                break;
            case LEFT:
                movePlayer(-50, 0, "left");
                break;
            case RIGHT:
                movePlayer(50, 0, "right");
                break;
        }
    }

    private void movePlayer(int dx, int dy, String direction) {
        double newX = player.getX() + dx;
        double newY = player.getY() + dy;

        // 检查边界
        if (newX < 0 || newY < 0 || newX >= 350 || newY >= 350) {
            return; // 超出边界，不允许移动
        }

        // 检查是否碰到箱子
        if (newX == box.getX() && newY == box.getY()) {
            double boxNewX = box.getX() + dx;
            double boxNewY = box.getY() + dy;

            // 检查箱子移动后是否会超出边界
            if (boxNewX < 0 || boxNewY < 0 || boxNewX >= 350 || boxNewY >= 350) {
                return; // 箱子超出边界，不允许移动
            }

            // 移动箱子
            animateMove(box, boxNewX, boxNewY, 200);
            box.relocate(boxNewX, boxNewY);
        }

        // 根据方向切换图片并移动玩家
        switch (direction) {
            case "up":
                player.moveUp();
                break;
            case "down":
                player.moveDown();
                break;
            case "left":
                player.moveLeft();
                break;
            case "right":
                player.moveRight();
                break;
        }
    }

    private void animateMove(Object obj, double targetX, double targetY, int duration) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(duration),
                        event -> {
                            if (obj instanceof Player) {
                                ((Player) obj).relocate(targetX, targetY);
                            } else if (obj instanceof Box) {
                                ((Box) obj).relocate(targetX, targetY);
                            }
                        },
                        new KeyValue(((ImageView) ((obj instanceof Player) ? ((Player) obj).getImageView() : ((Box) obj).getImageView())).layoutXProperty(), targetX, Interpolator.LINEAR),
                        new KeyValue(((ImageView) ((obj instanceof Player) ? ((Player) obj).getImageView() : ((Box) obj).getImageView())).layoutYProperty(), targetY, Interpolator.LINEAR)
                )
        );
        timeline.play();
    }

    private void resetLevel() {
        player.relocate(100, 100);
        box.relocate(200, 200);
        target.relocate(300, 300);
    }

    public static void main(String[] args) {
        launch(args);
    }
}