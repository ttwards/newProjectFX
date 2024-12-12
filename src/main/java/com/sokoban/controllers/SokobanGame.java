package com.sokoban.controllers;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SokobanGame extends Application {


    private Player player;
    private Box box;
    private Target target;

    private Level level;
    private Pane gamePane;

    private long startTime = 0;


    private Label lblTimer = new Label("00:00");
    private AnimationTimer timer;
    private static final Font BIGGER_FONT = new Font("Arial", 30);
    private boolean hasStartedMoving = false; // 标记玩家是否已开始移动
    private int currentLevelIndex = 0; // 跟踪当前关卡索引
    private Label lblStepCount = new Label("Steps: 0"); // 步数标签
    public int stepCount = 0; // 记录步数



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

        // 添加“重新开始”按钮
        Button restartButton = new Button("Restart");
        restartButton.setOnAction(event -> restartLevel());
        buttonContainer.getChildren().add(restartButton); // 将重启按钮添加到按钮容器

        // 将按钮容器添加到顶部
        root.setTop(buttonContainer);

        // 初始化关卡管理器
        level = new Level(gamePane);

        // 添加计时标签到界面底部
        VBox bottomContainer = new VBox(10);
        bottomContainer.setPadding(new Insets(10));
        lblTimer.setFont(BIGGER_FONT);
        lblStepCount.setFont(BIGGER_FONT);
        bottomContainer.getChildren().addAll(lblTimer, lblStepCount); // 添加步数标签到界面底部
        root.setBottom(bottomContainer);

        // 默认加载第一关
        loadLevel(0);

        scene.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            if (!hasStartedMoving) { // 如果玩家还没有开始移动，则启动计时器
                resetTimer();
                hasStartedMoving = true;
            }
            switch (keyCode) {
                case W:
                    // 尝试将箱子向上移动，如果成功则移动玩家
                    if (level.moveBox(level.getPlayerX(), level.getPlayerY(), 0, -1)) {
                        level.getPlayer().moveUp();
                        incrementStepCount();
                    }
                    break;
                case S:
                    // 保持原有的向下移动逻辑
                    if (level.moveBox(level.getPlayerX(), level.getPlayerY(), 0, 1)) {
                        level.getPlayer().moveDown();
                        incrementStepCount();

                    }
                    break;
                case A:
                    // 尝试将箱子向左移动，如果成功则移动玩家
                    if (level.moveBox(level.getPlayerX(), level.getPlayerY(), -1, 0)) {
                        level.getPlayer().moveLeft();
                    	incrementStepCount();
                    }
                    break;
                case D:
                    // 尝试将箱子向右移动，如果成功则移动玩家
                    if (level.moveBox(level.getPlayerX(), level.getPlayerY(), 1, 0)) {
                        level.getPlayer().moveRight();
                        incrementStepCount();
                    }
                    break;
                default:
                    break;
            }
            if(level.gameEnd()) {
                System.out.println("Level ended");
                stopTimer();
                hasStartedMoving = false; // 防止计时器再次启动直到玩家再次开始移动
            }
        });

        primaryStage.setTitle("Sokoban Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    public static void main(String[] args) {
        // 完全禁用硬件加速
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.vsync", "false");
        launch(args);
    }

    private void loadLevel(int levelIndex) {
        this.currentLevelIndex = levelIndex; // 更新当前关卡索引
        level.loadLevel(levelIndex);
        hasStartedMoving = false; // 当加载新关卡时重置移动状态
        stopTimer(); // 确保停止任何正在运行的计时器
        resetStepCount();
    }

    private void resetTimer() {
        startTime = System.currentTimeMillis();
        lblTimer.setText("00:00"); // 重置时间标签
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!hasStartedMoving|| level.gameEnd()) return; // 如果玩家还没开始移动，则不更新时间
                long elapsedTime = System.currentTimeMillis() - startTime;
                updateLabel(elapsedTime);
            }
        };
        timer.start();
    }

    private void restartLevel() {
        stopTimer(); // 确保停止任何正在运行的计时器
        int currentLevelIndex = getCurrentLevelIndex(); // 获取当前关卡索引
        loadLevel(currentLevelIndex); // 重新加载当前关卡
        resetTimer(); // 重置并启动计时器
        hasStartedMoving = false; // 重置移动状态
        resetStepCount();
    }

    private int getCurrentLevelIndex() {
        return currentLevelIndex; // 返回当前关卡索引
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    private void updateLabel(long elapsedTime) {
        int totalseconds = (int) (elapsedTime / 1000);
        int seconds = totalseconds % 60;
        int minutes = totalseconds / 60;

        String timeString = String.format("%02d:%02d", minutes, seconds);
        Platform.runLater(() -> lblTimer.setText(timeString)); // 确保在JavaFX应用程序线程上更新UI
    }

    public void incrementStepCount() {

        Platform.runLater(() -> lblStepCount.setText("Steps: " + level.stepnum)); // 确保在JavaFX应用程序线程上更新UI
    }

    private void resetStepCount() {
        level.stepnum = 0;
        Platform.runLater(() -> lblStepCount.setText("Steps: " + level.stepnum)); // 确保在JavaFX应用程序线程上更新UI
    }
}