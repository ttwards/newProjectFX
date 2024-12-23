package com.sokoban.controllers;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
import java.io.IOException;

import com.sokoban.ui.LevelSelector;
import com.sokoban.ui.SaveController;
import com.sokoban.ui.User;

public class SokobanGame extends Application {

	private User user;

	private Level level;
	private Pane gamePane;

	private long startTime = 0;

	private Label lblTimer = new Label("00:00");
	private Label lblLevel = new Label("");
	private AnimationTimer timer;
	private static final Font BIGGER_FONT = new Font("Consolas", 30);
	private boolean hasStartedMoving = false; // 标记玩家是否已开始移动
	private int currentLevelIndex = 0; // 跟踪当前关卡索引
	private Label lblStepCount = new Label("Steps: 0"); // 步数标签
	public int stepCount = 0; // 记录步数

	private boolean congratulated = false;

	private Thread aiThread;

	private volatile boolean stopAI = false;

	private Backend map;

	private boolean mapSet = false;

	HBox buttonContainer;
	Button solveButton;

	private int afterVictory = 0;

	private boolean reload = false;

	private String name;

	@Override
	public void start(Stage primaryStage) {
		// 创建主布局
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 800, 700);

		// 创建游戏区域
		gamePane = new Pane();
		root.setCenter(gamePane);

		// 创建关卡选择按钮容器
		buttonContainer = new HBox(10); // 水平间距为10
		buttonContainer.setPadding(new Insets(10));

		if (user.getLevel() < 1) {
			user.updateLevel(1);
		}

		if (level != null) {
			System.out.println("Clearing graph");
			level.clearGraph();
		}

		// 添加选择关卡按钮
		Button levelButton = new Button("Level");
		levelButton.setOnAction(event -> {
			levelSelect(levelButton);
		});
		buttonContainer.getChildren().add(levelButton); // 将AI求解按钮添加到按钮容器
		if (user.getLevel() < 2)
			levelButton.setDisable(true);

		// 添加无限模式按钮
		Button infiButton = new Button("Infinity");
		infiButton.setOnAction(event -> {
			level.loadLevel();
			map = level.getMap();
			restartLevel();
		});
		buttonContainer.getChildren().add(infiButton); // 将AI求解按钮添加到按钮容器
		if (user.getLevel() < 5)
			infiButton.setDisable(true);

		// 功能按钮放右边
		VBox functionContainer = new VBox(20);
		functionContainer.setPadding(new Insets(10));
		functionContainer.setAlignment(Pos.TOP_CENTER); // 设置VBox内容顶部居中
		// 添加“重新开始”按钮
		HBox restartContainer = new HBox();
		restartContainer.setAlignment(Pos.CENTER);
		Button restartButton = new Button("Restart");
		restartButton.setPrefSize(190, 30);
		restartButton.setOnAction(event -> {
			map.undoMove(999);
			if(!mapSet) resetTimer();
			if(mapSet) setTimer(map.getDuration());
			hasStartedMoving = false;
			resetStepCount();
		});

		restartContainer.getChildren().add(restartButton);
		functionContainer.getChildren().add(restartContainer); // 将重启按钮添加到按钮容器

		// 添加“AI求解”按钮
		solveButton = new Button("AI Solve");
		solveButton.setOnAction(event -> aiSolve(primaryStage));
		buttonContainer.getChildren().add(solveButton); // 将AI求解按钮添加到按钮容器
		if (user.getLevel() < 5)
			solveButton.setDisable(true);

		// 添加重做按钮
		Button redoButton = new Button("Undo");
		redoButton.setOnAction(event -> {
			if (map.undoMove(1)) {
				incrementStepCount();
			}
		});
		buttonContainer.getChildren().add(redoButton);

		// 添加保存按钮
		Button saveButton = new Button("Save");
		saveButton.setOnAction(event -> {
			saveButton(saveButton);
		});
		buttonContainer.getChildren().add(saveButton);
		if (user.getUsername().equals("visitor")) {
			saveButton.setDisable(true);
		}

		// 创建带有图标的按钮
		Button upButton = createImageButton("/arrows/arrowUp.png", "");
		Button downButton = createImageButton("/arrows/arrowDown.png", "");
		Button leftButton = createImageButton("/arrows/arrowLeft.png", "");
		Button rightButton = createImageButton("/arrows/arrowRight.png", "");

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

		upButton.setOnMouseClicked(event -> handleKeyPress('W', primaryStage));
		downButton.setOnMouseClicked(event -> handleKeyPress('S', primaryStage));
		leftButton.setOnMouseClicked(event -> handleKeyPress('A', primaryStage));
		rightButton.setOnMouseClicked(event -> handleKeyPress('D', primaryStage));

		// 添加方向按钮事件监听器（保持原有的键盘事件监听器）
		scene.setOnKeyPressed(event -> {
			KeyCode keyCode = event.getCode();
			if (!hasStartedMoving) { // 如果玩家开始移动，则启动计时器
				if (!mapSet)
					resetTimer();
				if (mapSet)
					setTimer(map.getDuration());
				hasStartedMoving = true;
			}
			handleKeyPress(keyCode.toString().charAt(0), primaryStage);
			if (level.gameEnd()) {
				System.out.println("Level ended");
				stopTimer();
				hasStartedMoving = false; // 防止计时器再次启动直到玩家再次开始移动
			}
		});

		// 将方向按钮容器添加到功能按钮容器中
		functionContainer.getChildren().add(directionButtonsContainer);

		// 将功能按钮容器添加到界面右侧
		root.setRight(functionContainer);

		buttonContainer.getChildren().add(restartButton); // 将重启按钮添加到按钮容器

		// 将按钮容器添加到顶部
		root.setTop(buttonContainer);

		// 初始化关卡管理器
		level = new Level(gamePane);

		level.setGame(this);

		// 添加计时标签到界面底部
		lblLevel.setText("Level " + (currentLevelIndex));
		VBox bottomContainer = new VBox(10);
		bottomContainer.setPadding(new Insets(10));
		lblTimer.setFont(BIGGER_FONT);
		lblLevel.setFont(BIGGER_FONT);
		lblStepCount.setFont(BIGGER_FONT);
		bottomContainer.getChildren().addAll(lblLevel, lblTimer, lblStepCount); // 添加步数标签到界面底部
		root.setBottom(bottomContainer);

		if (!mapSet) {
			if (user.getLevel() < 1) {
				user.updateLevel(1);
			} else if (user.getLevel() > 5) {
				user.updateLevel(5);
			}
			loadLevel(currentLevelIndex);
		} else {
			loadLevel(map);
		}

		scene.setOnKeyPressed(event -> {
			KeyCode keyCode = event.getCode();
			if (!hasStartedMoving) { // 如果玩家开始移动，则启动计时器
				if (!mapSet)
					resetTimer();
				if (mapSet)
					setTimer(map.getDuration());
				hasStartedMoving = true;
			}
			handleKeyPress(keyCode.toString().charAt(0), primaryStage);
		});

		if (reload) {
			restartLevel();
		}

		primaryStage.setTitle("Sokoban Game");
		primaryStage.setScene(scene);
		primaryStage.show();

		if (mapSet) {
			stepCount = map.getStep();
		}

		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
			if (afterVictory == 1) {
				System.out.println("Restarting level");
				map.undoMove(999);
				if(!mapSet) resetTimer();
				if(mapSet) setTimer(map.getDuration());
				hasStartedMoving = false;
				resetStepCount();
				afterVictory = 0;
			} else if (afterVictory == 2) {
				currentLevelIndex++;
				loadLevel(currentLevelIndex + 1);
				afterVictory = 0;
				restartLevel();
			} else if (afterVictory == 3) {
				level.loadLevel();
				map = level.getMap();
				restartLevel();
				afterVictory = 0;
			}
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}

	public void setLevel(int level) {
		this.currentLevelIndex = level;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static void main(String[] args) {
		launch(args);
	}

	private Button createImageButton(String imagePath, String text) {
		Image image = new Image(getClass().getResourceAsStream(imagePath));
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(32); // 设置图像宽度
		imageView.setFitHeight(32); // 设置图像高度
		Button button = new Button(text, imageView);
		return button;
	}

	private void loadLevel(int levelIndex) {
		stopAI();
		this.currentLevelIndex = levelIndex; // 更新当前关卡索引

		level.clearGraph();

		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		level.loadLevel(levelIndex);

		map = level.getMap();
		hasStartedMoving = false; // 当加载新关卡时重置移动状态
		stopTimer(); // 确保停止任何正在运行的计时器
		resetStepCount();
		congratulated = false;
	}

	private void loadLevel(Backend map) {
		stopAI();
		hasStartedMoving = false; // 当加载新关卡时重置移动状态
		this.map = map;
		level.loadLevel(map);
		stopTimer(); // 确保停止任何正在运行的计时器
		resetStepCount();
	}

	private void resetTimer() {
		startTime = System.currentTimeMillis();
		lblTimer.setText("00:00"); // 重置时间标签
		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (!hasStartedMoving || level.gameEnd())
					return; // 如果玩家还没开始移动，则不更新时间
				long elapsedTime = System.currentTimeMillis() - startTime;
				updateLabel(elapsedTime);
			}
		};
		timer.start();
	}

	private void setTimer(int duration) {
		startTime = System.currentTimeMillis() - duration;
		lblTimer.setText("00:00"); // 重置时间标签
		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (!hasStartedMoving || level.gameEnd())
					return; // 如果玩家还没开始移动，则不更新时间
				long elapsedTime = System.currentTimeMillis() - startTime;
				updateLabel(elapsedTime);
			}
		};
		timer.start();
	}

	public int getDuration() {
		return (int) (System.currentTimeMillis() - startTime);
	}

	private void restartLevel() {
		stopTimer(); // 确保停止任何正在运行的计时器
		hasStartedMoving = false; // 当加载新关卡时重置移动状态
		level.restartLevel();
		resetTimer(); // 重置并启动计时器
		hasStartedMoving = false; // 重置移动状态
		resetStepCount();
		congratulated = false;
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
		if (mapSet) {
			level.stepnum = map.getStep();
		}
		Platform.runLater(() -> lblStepCount.setText("Steps: " + level.stepnum)); // 确保在JavaFX应用程序线程上更新UI
	}

	private void saveButton(Button button) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SaveView.fxml"));

			Parent root = loader.load();

			SaveController controller = loader.getController();
			controller.setMap(map);
			controller.setName(name);

			Stage stage = new Stage();
			stage.setTitle("保存游戏");
			stage.initModality(Modality.APPLICATION_MODAL); // 设置为模态窗口
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error loading FXML: " + e.getMessage());
		}
	}

	private void levelSelect(Button button) {
		try {
			// 保存当前窗口引用
			Stage currentStage = (Stage) button.getScene().getWindow();

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LevelSelectView.fxml"));
			Parent root = loader.load();

			LevelSelector controller = loader.getController();
			controller.setUser(user);
			controller.setReload(true);
			controller.setStage(currentStage);

			Stage stage = new Stage();
			stage.setTitle("选择关卡");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(new Scene(root));

			// 显示新窗口后关闭当前窗口
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error loading FXML: " + e.getMessage());
		}
	}

	private void showVictoryDialog(Stage ownerstage) {
		if (!Platform.isFxApplicationThread() || congratulated) {
			congratulated = true;
			return;
		}

		if (user.getLevel() < 5) {
			user.updateLevel(user.getLevel() + 1);
		}

		for (int i = 0; i < user.getLevel(); i++) {
			buttonContainer.getChildren().get(i).setDisable(false);
		}

		if (user.getLevel() >= 5)
			solveButton.setDisable(false);

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
		restartButton.setPrefSize(100, 30);
		restartButton.setOnAction(event -> {
			dialogStage.setUserData(1);
			dialogStage.close(); // 关闭对话框
		});

		// 创建“下一关”按钮（如果还有下一关）
		Button nextLevelButton = new Button("Next Level");
		nextLevelButton.setPrefSize(80, 30);
		if (currentLevelIndex < 4) { // 假设有5个关卡
			nextLevelButton.setOnAction(event -> {
				dialogStage.setUserData(2);
				dialogStage.close(); // 关闭对话框
			});
		} else {
			nextLevelButton.setOnAction(event -> {
				dialogStage.setUserData(3);
				dialogStage.close(); // 关闭对话框
			});
		}

		// 将组件添加到布局容器中
		dialogVbox.getChildren().addAll(victoryLabel, restartButton, nextLevelButton);

		// 创建Scene并设置给对话框Stage
		Scene dialogScene = new Scene(dialogVbox, 300, 200);
		dialogStage.setScene(dialogScene);

		// 显示对话框
		dialogStage.showAndWait();

		afterVictory = (int) dialogStage.getUserData();
	}

	private void showFailedDialog(Stage ownerstage) {
		if (user.getLevel() >= 5)
			solveButton.setDisable(false);

		Stage dialogStage = new Stage();
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(ownerstage);

		VBox dialogVbox = new VBox(10);
		dialogVbox.setPadding(new Insets(10));
		dialogVbox.setAlignment(Pos.CENTER);

		Label victoryLabel = new Label("Failed!");
		victoryLabel.setFont(BIGGER_FONT);

		Button restartButton = new Button("Restart Level");
		restartButton.setPrefSize(100, 30);
		restartButton.setOnAction(event -> {
			dialogStage.setUserData(1);
			dialogStage.close();
		});

		// 添加组件到VBox
		dialogVbox.getChildren().addAll(victoryLabel, restartButton);

		Scene dialogScene = new Scene(dialogVbox, 300, 150);
		dialogStage.setScene(dialogScene);
		dialogStage.showAndWait();

		afterVictory = (int) dialogStage.getUserData();
	}

	private void aiSolve(Stage primaryStage) {
		stopTimer();
		stopAI();
		stopAI = false;
		aiThread = new Thread(() -> {
			try {
				while (!stopAI) {
					// 检查中断状态
					if (Thread.interrupted()) {
						throw new InterruptedException();
					}
					hasStartedMoving = true;
					System.out.println("Solving level " + (currentLevelIndex + 1));
					SokobanSolver solver = new SokobanSolver();
					String solution = solver.solve(map.getLevelArrayDynamic());
					System.out.println("Solution: " + solution);
					resetStepCount();
					for (char c : solution.toCharArray()) {
						if (Thread.interrupted()) {
							throw new InterruptedException();
						}
						if (stopAI) {
							break;
						}
						handleKeyPress(c, primaryStage);
						try {
							Thread.sleep(200); // 等待200毫秒以便观察动画
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					stopAI = true;
					if (stopAI) {
						break;
					}
				}
			} catch (InterruptedException e) {
				// 处理中断
				System.out.println("AI solving interrupted");
			} finally {
				// 清理资源
				stopAI = true;
			}
		});
		aiThread.start();
	}

	// 停止AI
	private void stopAI() {
		if (aiThread != null && aiThread.isAlive()) {
			stopAI = true; // 设置停止标志
			aiThread.interrupt(); // 发送中断信号
			try {
				aiThread.join(1000); // 等待线程结束,最多1秒
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (aiThread.isAlive()) {
				System.out.println("AI thread still running");
			}
		}
	}

	private void handleKeyPress(char keyCode, Stage primaryStage) {
		switch (keyCode) {
			case 'W':
				// 尝试将箱子向上移动，如果成功则移动玩家
				if (map.playerMove(0, -1)) {
					incrementStepCount();
				}
				break;
			case 'S':
				// 尝试将箱子向下移动，如果成功则移动玩家
				if (map.playerMove(0, 1)) {
					incrementStepCount();
				}
				break;
			case 'A':
				// 尝试将箱子向左移动，如果成功则移动玩家
				if (map.playerMove(-1, 0)) {
					incrementStepCount();
				}
				break;
			case 'D':
				// 尝试将箱子向右移动，如果成功则移动玩家
				if (map.playerMove(1, 0)) {
					incrementStepCount();
				}
				break;
			default:
				break;
		}

		if (level.gameEnd() && !congratulated) {
			System.out.println("Level ended");
			stopTimer();
			hasStartedMoving = false; // 防止计时器再次启动直到玩家再次开始移动
			showVictoryDialog(primaryStage);
		}

		if (map.isDead()) {
			System.out.println("Level ended");
			stopTimer();
			hasStartedMoving = false; // 防止计时器再次启动直到玩家再次开始移动
			showFailedDialog(primaryStage);
		}
	}

	public void setMap(Backend map) {
		this.map = map;
		this.mapSet = true;
	}

	public void setReload(boolean reload) {
		this.reload = reload;
	}

	public void setName(String name) {
		this.name = name;
	}
}
