package com.sokoban.controllers;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import com.sokoban.controllers.*;

//-1表示空,0表示空地，1表示墙，2表示玩家，3表示箱子，4表示目标
//每个关卡都设置成8*8
public class Level {
	public int stepnum = 0;// 步数
	private int currentLevelIndex = 0; // 当前关卡索引
	private Pane root; // 游戏场景根节点

	private Backend map;

	public Level(Pane root) {
		this.root = root;
	}

	public void restartLevel() {
		clearGraph();

		map.restoreMap();

		StaticShape[][] staticShapes = map.getLevel();
		for (int y = 0; y < staticShapes.length; y++) {
			for (int x = 0; x < staticShapes[y].length; x++) {
				StaticShape shape = staticShapes[x][y];
				if (shape != null) {
					root.getChildren().add(shape.getImageView());
					System.out.println(
							"Picture at x: " + shape.getX() + ", y: " + shape.getY() + " path: " + shape.imagePath);
				}
			}
		}

		// 保证DynamicShape在静态对象之上
		DynamicShape dynamicShapes[] = map.getDynamicShapes();
		for (DynamicShape shape : dynamicShapes) {
			root.getChildren().add(shape.getImageView());
			System.out.println("Adding dynamic shape at " + shape.getX() + ", " + shape.getY());
		}
	}

	public void loadLevel(Backend map) {
		// 清空当前场景
		clearGraph();

		map.reMakeImage(this, root);

		this.map = map;

		System.out.println("Loading level " + (currentLevelIndex));

		StaticShape[][] staticShapes = map.getLevel();
		for (int y = 0; y < staticShapes.length; y++) {
			for (int x = 0; x < staticShapes[y].length; x++) {
				StaticShape shape = staticShapes[x][y];
				if (shape != null) {
					root.getChildren().add(shape.getImageView());
					System.out.println(
							"Picture at x: " + shape.getX() + ", y: " + shape.getY() + " path: " + shape.imagePath);
				}
			}
		}

		// 保证DynamicShape在静态对象之上
		DynamicShape dynamicShapes[] = map.getDynamicShapes();
		for (DynamicShape shape : dynamicShapes) {
			root.getChildren().add(shape.getImageView());
			System.out.println("Adding dynamic shape at " + shape.getX() + ", " + shape.getY());
		}
	}

	public void loadLevel(int levelIndex) {
		// 清空当前场景
		clearGraph();

		map = new Backend(LEVELS[levelIndex - 1]);

		map.reMakeImage(this, root);

		System.out.println("Loading level " + (currentLevelIndex));

		StaticShape[][] staticShapes = map.getLevel();
		for (int y = 0; y < staticShapes.length; y++) {
			for (int x = 0; x < staticShapes[y].length; x++) {
				StaticShape shape = staticShapes[x][y];
				if (shape != null) {
					root.getChildren().add(shape.getImageView());
					System.out.println(
							"Picture at x: " + shape.getX() + ", y: " + shape.getY() + " path: " + shape.imagePath);
				}
			}
		}

		// 保证DynamicShape在静态对象之上
		DynamicShape dynamicShapes[] = map.getDynamicShapes();
		for (DynamicShape shape : dynamicShapes) {
			root.getChildren().add(shape.getImageView());
			System.out.println("Adding dynamic shape at " + shape.getX() + ", " + shape.getY());
		}
	}

	public boolean gameEnd() {
		DynamicShape dynamicShapes[] = map.getDynamicShapes();
		for (DynamicShape shape : dynamicShapes) {
			if (shape.getClass() == Box.class &&
					map.getAt(shape.getX(), shape.getY()).getClass() != Target.class) {
				return false;
			}
		}
		return true;
	}

	public void clearGraph() {
		if (map != null) {
			if (root != null) {
				
				root.getChildren().clear();
			}
			stepnum = 0;
			currentLevelIndex = 0;
			root.setPickOnBounds(false);
		}
	}

	public Backend getMap() {
		return map;
	}

	// 定义五个关卡

	private static final int[][][] LEVELS = {
			{ // 关卡 1
					{ -1, -1, -1, -1, -1, -1, -1, -1 },
					{ -1, 1, 1, 1, 1, 1, 1, -1 },
					{ -1, 1, 2, 0, 0, 0, 1, -1 },
					{ -1, 1, 0, 0, 3, 4, 1, -1 },
					{ -1, 1, 0, 4, 3, 0, 1, -1 },
					{ -1, 1, 1, 1, 1, 1, 1, -1 },
					{ -1, -1, -1, -1, -1, -1, -1, -1 },
					{ -1, -1, -1, -1, -1, -1, -1, -1 }
			},
			{ // 关卡 2
					{ -1, -1, -1, -1, -1, -1, -1, -1 },
					{ -1, 1, 1, 1, 1, 1, 1, 0 },
					{ -1, 1, 2, 0, 0, 0, 1, 1 },
					{ -1, 1, 0, 3, 3, 0, 0, 1 },
					{ -1, 1, 0, 1, 4, 0, 4, 1 },
					{ -1, 1, 0, 0, 0, 0, 0, 1 },
					{ -1, 1, 1, 1, 1, 1, 1, 1 },
					{ -1, -1, -1, -1, -1, -1, -1, -1 }
			},
			{ // 关卡 3
					{ -1, -1, -1, -1, -1, -1, -1, -1 },
					{ -1, -1, -1, 1, 1, 1, 1, -1 },
					{ -1, 1, 1, 1, 0, 0, 1, -1 },
					{ -1, 1, 2, 0, 4, 3, 1, 1 },
					{ -1, 1, 0, 0, 0, 3, 0, 1 },
					{ -1, 1, 0, 1, 4, 0, 0, 1 },
					{ -1, 1, 0, 0, 0, 0, 0, 1 },
					{ -1, 1, 1, 1, 1, 1, 1, 1 }
			},
			{ // 关卡 4
					{ -1, -1, -1, -1, -1, -1, -1, -1 },
					{ -1, -1, 1, 1, 1, 1, 1, -1 },
					{ -1, 1, 1, 2, 0, 0, 1, 1 },
					{ -1, 1, 0, 0, 1, 0, 0, 1 },
					{ -1, 1, 0, 3, 6, 3, 0, 1 },
					{ -1, 1, 0, 0, 4, 0, 0, 1 },
					{ -1, 1, 1, 0, 4, 0, 1, 1 },
					{ -1, -1, 1, 1, 1, 1, 1, -1 }
			},
			{ // 关卡 5
					{ -1, -1, -1, -1, -1, -1, -1, -1 },
					{ 1, 1, 1, 1, 1, 1, -1, -1 },
					{ 1, 0, 0, 0, 0, 1, 1, 1 },
					{ 1, 0, 0, 0, 4, 4, 0, 1 },
					{ 1, 0, 3, 3, 3, 2, 0, 1 },
					{ 1, 0, 0, 1, 0, 4, 0, 1 },
					{ 1, 1, 1, 1, 1, 1, 1, 1 },
					{ -1, -1, -1, -1, -1, -1, -1, -1 }
			}
	};
}