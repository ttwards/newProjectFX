package com.sokoban.controllers;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
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

        //功能按钮放右边
        VBox functionContainer=new VBox(20);
        functionContainer.setPadding(new Insets(10));
        functionContainer.setAlignment(Pos.TOP_CENTER); // 设置VBox内容顶部居中
        // 添加“重新开始”按钮
        HBox restartContainer = new HBox();
        restartContainer.setAlignment(Pos.CENTER);
        Button restartButton = new Button("Restart");
        restartButton.setPrefSize(190, 30);
        restartButton.setOnAction(event -> restartLevel());

        restartContainer.getChildren().add(restartButton);
        functionContainer.getChildren().add(restartContainer); // 将重启按钮添加到按钮容器

        // 创建带有图标的按钮
        Button upButton = createImageButton("/arrows/arrowUp.png", "↑");
        Button downButton = createImageButton("/arrows/arrowDown.png", "↓");
        Button leftButton = createImageButton("/arrows/arrowLeft.png", "←");
        Button rightButton = createImageButton("/arrows/arrowRight.png", "→");

			// 添加“AI求解”按钮
			Button solveButton = new Button("AI Solve");
			solveButton.setOnAction(event -> aiSolve());
			buttonContainer.getChildren().add(solveButton); // 将AI求解按钮添加到按钮容器

			// 添加重做按钮
			Button redoButton = new Button("Redo");
			redoButton.setOnAction(event -> {
				if (map.undoMove(1)) {
					incrementStepCount();
				}
			});
			buttonContainer.getChildren().add(redoButton);


        // 设置方向按钮大小
        upButton.setPrefSize(50, 50);
        downButton.setPrefSize(50, 50);
        leftButton.setPrefSize(50, 50);
        rightButton.setPrefSize(50, 50);

        // 使用 GridPane 分布按钮
        GridPane directionButtonsContainer = new GridPane();
        directionButtonsContainer.setAlignment(Pos.CENTER);
        directionButtonsContainer.setHgap(10); // 水平间距
        directionButtonsContainer.setVgap(10); // 垂直间距

        // 将方向按钮添加到 GridPane 中，并设置其位置
        directionButtonsContainer.add(upButton, 1, 0);
        directionButtonsContainer.add(downButton, 1, 2);
        directionButtonsContainer.add(leftButton, 0, 1);
        directionButtonsContainer.add(rightButton, 2, 1);



        upButton.setOnMouseClicked(event -> handleDirectionInput(KeyCode.UP,primaryStage));
        downButton.setOnMouseClicked(event -> handleDirectionInput(KeyCode.DOWN,primaryStage));
        leftButton.setOnMouseClicked(event -> handleDirectionInput(KeyCode.LEFT,primaryStage));
        rightButton.setOnMouseClicked(event -> handleDirectionInput(KeyCode.RIGHT,primaryStage));

        // 添加方向按钮事件监听器（保持原有的键盘事件监听器）

        



        // 将方向按钮容器添加到功能按钮容器中
        functionContainer.getChildren().add(directionButtonsContainer);

        // 将功能按钮容器添加到界面右侧
        root.setRight(functionContainer);


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
				handleKeyPress(keyCode.toString().charAt(0));
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
        launch(args);
    }

    private Button createImageButton(String imagePath, String text) {
        Image image = new Image(getClass().getResourceAsStream( imagePath));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(32); // 设置图像宽度
        imageView.setFitHeight(32); // 设置图像高度
        Button button = new Button(text, imageView);
        return button;
    }


    private void loadLevel(int levelIndex) {
			this.currentLevelIndex = levelIndex; // 更新当前关卡索引
			map = level.createLevel(levelIndex);
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

    private void showVictoryDialog(Stage ownerstage) {
        // 创建新的Stage作为对话框
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ownerstage); // 设置父窗口

        // 创建布局容器
        VBox dialogVbox = new VBox(10);
        dialogVbox.setPadding(new Insets(10));
        dialogVbox.setAlignment(Pos.CENTER);

        // 添加胜利信息
        Label victoryLabel = new Label("Congratulations!");
        victoryLabel.setFont(BIGGER_FONT);

        // 创建“重新开始”按钮
        Button restartButton = new Button("Restart Level");
        restartButton.setPrefSize(100,30);
        restartButton.setOnAction(event -> {
            restartLevel();
            dialogStage.close(); // 关闭对话框
        });

        // 创建“下一关”按钮（如果还有下一关）
        Button nextLevelButton = new Button("Next Level");
        nextLevelButton.setPrefSize(80,30);
        if (currentLevelIndex < 4) { // 假设有5个关卡
            nextLevelButton.setOnAction(event -> {
                loadLevel(currentLevelIndex + 1);
                dialogStage.close(); // 关闭对话框
            });
        } else {
            nextLevelButton.setDisable(true); // 如果没有下一关，则禁用按钮
        }

        // 将组件添加到布局容器中
        dialogVbox.getChildren().addAll(victoryLabel, restartButton, nextLevelButton);

        // 创建Scene并设置给对话框Stage
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialogStage.setScene(dialogScene);

        // 显示对话框
        dialogStage.showAndWait();
    }

}
