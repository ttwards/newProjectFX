package com.sokoban.controllers;

import java.util.*;

import javafx.scene.layout.Pane;

public class Backend {
	public static final int NULL = -1;
	public static final int EMPTY = 0;
	public static final int WALL = 1;
	public static final int PLAYER = 2;
	public static final int BOX = 3;
	public static final int TARGET = 4;
	public static final int PLAYER_ON_TARGET = 5;
	public static final int BOX_ON_TARGET = 6;

	private static int mapX, mapY;

	private static int currentLevelIndex = 0; // 当前关卡索引

	private static StaticShape[][] levelShape = new StaticShape[12][12];

	private static DynamicShape[][] levelDynamicShapes = new DynamicShape[12][12];

	private static int[][] levelArray = new int[12][12];

	private static int[][] levelArrayDynamic = new int[12][12];

	private static ArrayList<DynamicShape> dynamicShapes = new ArrayList<>();

	private Stack<double[]> moveHistory = new Stack<>();

	private Stack<Integer> moveSteps = new Stack<>();

	private static Player player;

	public Backend(int x, int y, Level level, Pane container) {
		mapX = x;
		mapY = y;
		generateMap(level, container);
	}

	public static int[][] getLevelArray() {
		return levelArray;
	}

	public static int[][] getLevelArrayDynamic() {
		int[][] returnArray = new int[mapX][mapY];
		for (int i = 0; i < mapX; i++) {
			for (int j = 0; j < mapY; j++) {
				if (levelArray[i][j] == TARGET) {
					if(levelArrayDynamic[i][j] == PLAYER) {
						returnArray[i][j] = PLAYER_ON_TARGET;
					} else if(levelArrayDynamic[i][j] == BOX) {
						returnArray[i][j] = BOX_ON_TARGET;
					} else {
						returnArray[i][j] = TARGET;
					}
				} else if(levelArrayDynamic[i][j] == PLAYER || levelArrayDynamic[i][j] == BOX) {
					returnArray[i][j] = levelArrayDynamic[i][j];
				} else if(levelArray[i][j] == PLAYER || levelArray[i][j] == BOX) {
					returnArray[i][j] = levelArrayDynamic[i][j];
				} else {
					returnArray[i][j] = levelArray[i][j];
				}
			}
		}
		return returnArray;
	}

	public static StaticShape[][] getLevel() {
		return levelShape;
	}

	public static StaticShape getAt(double x, double y) {
		return levelShape[(int) x][(int) y];
	}

	public void generateMap(Level level, Pane container) {
		MapGenerator mapGenerator = new MapGenerator();
		mapGenerator.mapMake();
		levelArray = mapGenerator.getMap();
		levelArrayDynamic = mapGenerator.getMap();
		cleanShape();
		loadLevel(level, container);
	}

	public void reloadLevel(Level level, Pane container) {
		cleanShape();
		loadLevel(level, container);
	}

	public void loadLevel(Level level, Pane container) {
		for (int y = 0; y < mapY; y++) {
			for (int x = 0; x < mapX; x++) {
				int n = levelArray[x][y];
				// 检查是否是直接继承StaticShape
				switch (n) {
					case EMPTY:
						levelShape[x][y] = new Empty(x, y);
						break;
					case WALL:
						levelShape[x][y] = new Wall(x, y);
						break;
					case PLAYER:
						player = new Player(x, y, level, container);
						dynamicShapes.add(player);
						levelShape[x][y] = new Empty(x, y);
						levelDynamicShapes[x][y] = player;
						break;
					case BOX:
						levelDynamicShapes[x][y] = new Box(x, y, level, container);
						dynamicShapes.add(levelDynamicShapes[x][y]);
						levelShape[x][y] = new Empty(x, y);
						break;
					case TARGET:
						levelShape[x][y] = new Target(x, y);
						break;
					default:
						break;
				}
			}
		}
	}

	// 记录移动
	public void recordMove(double originX, double originY, double deltaX, double deltaY) {
		double[] move = new double[4];
		move[0] = originX;
		move[1] = originY;
		move[2] = deltaX;
		move[3] = deltaY;
		moveHistory.push(move);
		System.out.println("Record move: " + originX + ", " + originY + ", " + deltaX + ", " + deltaY);
	}

	public boolean undoMove(int stepCount) {
		if (moveSteps.isEmpty()) {
			return false;
		}
		for (int i = 0; i < stepCount; i++) {
			int steps = moveSteps.pop();
			for (int j = 0; j < steps; j++) {
				double[] move = moveHistory.pop();
				double originX = move[0];
				double originY = move[1];
				double deltaX = move[2];
				double deltaY = move[3];
				moveShape(false, originX + deltaX, originY + deltaY, -deltaX, -deltaY);
			}
		}
		return true;
	}

	public boolean isMoveValid(double x, double y, double deltaX, double deltaY) {
		double newX = x + deltaX;
		double newY = y + deltaY;

		// 检查新位置是否在关卡范围内
		if (newX < 0 || x >= mapX || newY < 0 || newY >= mapY) {
			System.out.printf("Out of bounds: %d, %d\n", (int) x, (int) y);
			return false; // 超出范围
		}

		if (levelShape[(int) newX][(int) newY].getClass() == Wall.class) {
			System.out.println("Met Wall at" + (int) newX + ", " + (int) newY + ", move failed");
			return false;
		}

		if (levelDynamicShapes[(int) newX][(int) newY] != null) {
			if (levelDynamicShapes[(int) newX][(int) newY].getClass() == Box.class) {
				System.out.printf("Met Box, move failed");
				return false;
			}
		}

		return true;
	}

	private static void cleanShape() {
		for (DynamicShape shape : dynamicShapes) {
			if (shape.getClass() == Box.class) {
				Box box = (Box) shape;
				box.destroy();
			}
		}
		dynamicShapes.clear();
		for (DynamicShape[] row : levelDynamicShapes) {
			Arrays.fill(row, null);
		}
	}

	public boolean playerMove(double deltaX, double deltaY) {
		int playerX = (int) player.getX();
		int playerY = (int) player.getY();
		int boxX = (int) (playerX + deltaX);
		int boxY = (int) (playerY + deltaY);
		if (boxX > mapX || boxY > mapY) {
			System.out.println("Invalid box position: " + boxX + ", " + boxY);
			return false;
		}
		if (levelDynamicShapes[boxX][boxY] == null) {
			System.out.println("No box at: " + boxX + ", " + boxY);
			if (!isMoveValid(playerX, playerY, deltaX, deltaY)) {
				return false;
			}
			moveShape(true, playerX, playerY, (int) deltaX, (int) deltaY);
			moveSteps.push(1);
			return true;
		}
		System.out.println("Moving box at: " + boxX + ", " + boxY + " by: " + deltaX + ", " + deltaY);
		if (!isMoveValid(boxX, boxY, deltaX, deltaY)) {
			return false;
		}
		moveShape(true, boxX, boxY, (int) deltaX, (int) deltaY);
		moveShape(true, playerX, playerY, (int) deltaX, (int) deltaY);
		moveSteps.push(2);

		System.out.println("Box moved successfully");
		return true;
	}

	public void moveShape(boolean record, double originX, double originY, double deltaX, double deltaY) {
		double newX = originX + deltaX;
		double newY = originY + deltaY;
		levelArrayMove((int) originX, (int) originY, (int) deltaX, (int) deltaY);
		if (levelDynamicShapes[(int) originX][(int) originY] != null) {
			DynamicShape shape = levelDynamicShapes[(int) originX][(int) originY];
			shape.moveXY(deltaX, deltaY);
			System.out.println("Moving shape at: " + originX + ", " + originY + " by: " + deltaX + ", " + deltaY);
			levelDynamicShapes[(int) newX][(int) newY] = levelDynamicShapes[(int) originX][(int) originY];
			levelDynamicShapes[(int) originX][(int) originY] = null;
			if (record) {
				recordMove(originX, originY, deltaX, deltaY);
			}
		}
	}

	public DynamicShape[] getDynamicShapes() {
		return dynamicShapes.toArray(new DynamicShape[0]);
	}

	private void levelArrayMove(int x, int y, int deltaX, int deltaY) {
		int newX = x + deltaX;
		int newY = y + deltaY;
		levelArrayDynamic[newX][newY] = levelArrayDynamic[x][y];
		levelArrayDynamic[x][y] = EMPTY;
	}
}
