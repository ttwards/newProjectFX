package com.sokoban.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
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

	private int[][] levelArray = new int[12][12];

	private int[][] levelArrayDynamic = new int[12][12];

	private static ArrayList<DynamicShape> dynamicShapes = new ArrayList<>();

	private Stack<double[]> moveHistory = new Stack<>();

	private Stack<Integer> moveSteps = new Stack<>();

	private static Player player;

	private Level level;

	private Pane container;

	private static final String SAVE_DIR = System.getProperty("user.home") + File.separator + ".sokoban"
			+ File.separator + "maps";

	public Backend(int x, int y, Level level, Pane container) {
		mapX = x;
		mapY = y;
		this.level = level;
		this.container = container;
		generateMap();
	}

	public Backend(String mapName) {
		loadMap(mapName);
	}

	public Backend(int[][] levelArray) {
		mapX = levelArray.length;
		mapY = levelArray[0].length;
		proceedSaveData(levelArray, levelArray);
	}

	public int[][] getLevelArray() {
		return levelArray;
	}

	public int[][] getLevelArrayDynamic() {
		int[][] returnArray = new int[mapX][mapY];
		for (int i = 0; i < mapX; i++) {
			for (int j = 0; j < mapY; j++) {
				if (levelArray[i][j] == TARGET) {
					if (levelArrayDynamic[i][j] == PLAYER) {
						returnArray[i][j] = PLAYER_ON_TARGET;
					} else if (levelArrayDynamic[i][j] == BOX) {
						returnArray[i][j] = BOX_ON_TARGET;
					} else {
						returnArray[i][j] = TARGET;
					}
				} else if (levelArrayDynamic[i][j] == PLAYER || levelArrayDynamic[i][j] == BOX) {
					returnArray[i][j] = levelArrayDynamic[i][j];
				} else if (levelArray[i][j] == PLAYER || levelArray[i][j] == BOX) {
					returnArray[i][j] = levelArrayDynamic[i][j];
				} else {
					returnArray[i][j] = levelArray[i][j];
				}
			}
		}
		return returnArray;
	}

	// 保存地图到本地
	public boolean saveMap(String mapName) {
		try {
			// 确保保存目录存在
			Files.createDirectories(Paths.get(SAVE_DIR));

			// 1. 创建可序列化的数据结构
			int[][] staticMapCopy = new int[mapX][mapY];
			int[][] dynamicMapCopy = new int[mapX][mapY];

			// 2. 进行深拷贝并验证数据
			for (int i = 0; i < mapX; i++) {
				for (int j = 0; j < mapY; j++) {
					staticMapCopy[i][j] = levelArray[i][j];
					dynamicMapCopy[i][j] = levelArrayDynamic[i][j];

					// 验证是否有数据
					if (staticMapCopy[i][j] != 0 || dynamicMapCopy[i][j] != 0) {
						System.out.println("Found non-zero data at [" + i + "][" + j + "]");
					}
				}
			}

			// 3. 创建新的HashMap并存入深拷贝的数据
			Map<String, Serializable> mapData = new HashMap<>();
			mapData.put("staticMap", staticMapCopy);
			mapData.put("dynamicMap", dynamicMapCopy);
			mapData.put("mapX", Integer.valueOf(mapX));
			mapData.put("mapY", Integer.valueOf(mapY));
			mapData.put("moveHistory", moveHistory.toArray(new double[moveHistory.size()][]));
			mapData.put("moveSteps", moveSteps.toArray(new Integer[moveSteps.size()]));

			validateMapData(mapData);

			// 保存到文件
			String filePath = SAVE_DIR + File.separator + mapName + "\\|" + new Date().getTime() + ".map";
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
				oos.writeObject(mapData);
				oos.flush();
			}
			System.out.println("地图已保存: " + filePath);
			return true;
		} catch (IOException e) {
			System.err.println("保存地图失败: " + e.getMessage());
			return false;
		}
	}

	private void validateMapData(Map<String, Serializable> mapData) {
		int[][] staticMap = (int[][]) mapData.get("staticMap");
		int[][] dynamicMap = (int[][]) mapData.get("dynamicMap");

		System.out.println("Validating map data before save:");
		System.out.println("Static map:");
		printArray(staticMap);
		System.out.println("Dynamic map:");
		printArray(dynamicMap);
	}

	private void printArray(int[][] array) {
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				System.out.print(array[i][j] + " ");
			}
			System.out.println();
		}
	}

	// 列出所有已保存的地图
	public static List<String> listSavedMaps() {
		List<String> mapNames = new ArrayList<>();
		try {
			Files.createDirectories(Paths.get(SAVE_DIR));
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(SAVE_DIR), "*.map")) {
				for (Path path : stream) {
					String fileName = path.getFileName().toString();
					mapNames.add(fileName.substring(0, fileName.length() - 4)); // 移除.map后缀
				}
			}
		} catch (IOException e) {
			System.err.println("读取保存的地图列表失败: " + e.getMessage());
		}
		return mapNames;
	}

	// 从本地加载地图
	@SuppressWarnings("unchecked")
	public boolean loadMap(String mapName) {
		String filePath = SAVE_DIR + File.separator + mapName + ".map";
		System.out.println("Loading map: " + filePath);
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
			Map<String, Object> mapData = (Map<String, Object>) ois.readObject();

			// 恢复地图状态
			int[][] dynamicMap = (int[][]) mapData.get("dynamicMap");
			int[][] staticMap = (int[][]) mapData.get("staticMap");

			// Restore move history
			double[][] moveHistoryArray = (double[][]) mapData.get("moveHistory");
			Integer[] moveStepsArray = (Integer[]) mapData.get("moveSteps");

			moveHistory.clear();
			moveSteps.clear();

			for (double[] move : moveHistoryArray) {
				moveHistory.push(move);
			}
			for (Integer step : moveStepsArray) {
				moveSteps.push(step);
			}

			mapX = (int) mapData.get("mapX");
			mapY = (int) mapData.get("mapY");

			proceedSaveData(dynamicMap, staticMap);

			System.out.println("Loading static map:");
			printArray(staticMap);

			System.out.println("Loading dynamic map:");
			printArray(dynamicMap);

			System.out.println("Map size: " + mapX + "x" + mapY);
			// 打印读取到的地图
			int[][] combinedMap = getLevelArrayDynamic();
			for (int y = 0; y < mapY; y++) {
				for (int x = 0; x < mapX; x++) {
					System.out.print(combinedMap[x][y] + " ");
				}
				System.out.println();
			}

			System.out.println("地图已加载: " + mapName);
			return true;
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("加载地图失败: " + e.getMessage());
			return false;
		}
	}

	public void restoreMap() {
		proceedSaveData(levelArray, levelArray);
		cleanShape();
		loadLevel();
	}

	// 检查地图是否存在
	public boolean mapExists(String mapName) {
		return Files.exists(Paths.get(SAVE_DIR, mapName + ".map"));
	}

	public void reMakeImage(Level level, Pane container) {
		this.level = level;
		this.container = container;
		loadLevel();
	}

	public StaticShape[][] getLevel() {
		return levelShape;
	}

	public StaticShape getAt(double x, double y) {
		return levelShape[(int) x][(int) y];
	}

	private void proceedSaveData(int[][] dynamicMap, int[][] staticMap) {
		for (int y = 0; y < mapY; y++) {
			for (int x = 0; x < mapX; x++) {
				if (dynamicMap[x][y] >= 5) {
					this.levelArrayDynamic[x][y] = dynamicMap[x][y] - 3;
				} else {
					levelArrayDynamic[x][y] = dynamicMap[x][y];
				}
				levelArray[x][y] = staticMap[x][y];
			}
		}
	}

	public void generateMap() {
		MapGenerator mapGenerator = new MapGenerator();
		mapGenerator.mapMake();
		// 深拷贝数组
		int[][] originalMap = mapGenerator.getMap();
		levelArray = new int[mapX][mapY];
		levelArrayDynamic = new int[mapX][mapY];

		// 分别复制数据
		for (int i = 0; i < mapX; i++) {
			levelArray[i] = Arrays.copyOf(originalMap[i], mapY);
			levelArrayDynamic[i] = Arrays.copyOf(originalMap[i], mapY);
		}
		cleanShape();
		loadLevel();
	}

	public void loadLevel() {
		for (int y = 0; y < mapY; y++) {
			for (int x = 0; x < mapX; x++) {
				int m = levelArrayDynamic[x][y];
				switch (m) {
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
					case PLAYER_ON_TARGET:
						player = new Player(x, y, level, container);
						dynamicShapes.add(player);
						levelShape[x][y] = new Target(x, y);
						levelDynamicShapes[x][y] = player;
						break;
					case BOX_ON_TARGET:
						levelDynamicShapes[x][y] = new Box(x, y, level, container);
						dynamicShapes.add(levelDynamicShapes[x][y]);
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

	public void cleanShape() {
		for (DynamicShape shape : dynamicShapes) {
			if (shape.getClass() == Box.class) {
				Box box = (Box) shape;
				box.destroy();
			} else {
				Player player = (Player) shape;
				player.destroy();
			}
		}
		dynamicShapes.clear();
		for (DynamicShape[] row : levelDynamicShapes) {
			Arrays.fill(row, null);
		}
		for(StaticShape[] row : levelShape) {
			Arrays.fill(row, null);
		}
		player = null;
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
			if(record)	{shape.moveXY(deltaX, deltaY);}
			else 		{shape.directMove(deltaX, deltaY);}
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
