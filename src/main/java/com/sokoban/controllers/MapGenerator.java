package com.sokoban.controllers;

import java.util.*;

public class MapGenerator {
	private static int H = 10;
	private static int L = 10;

	private static final int EMPTY = 0;
	private static final int WALL = 1;
	private static final int PLAYER = 2;
	private static final int BOX = 3;
	private static final int TARGET = 4;
	private static final int PLAYER_ON_TARGET = 5;
	private static final int BOX_ON_TARGET = 6;

	private int[][] map = new int[H][L];
	private Random rand = new Random();

	// 检查位置是否是死角
	private boolean isCorner(int x, int y) {
		int wallCount = 0;
		if (x > 0 && map[x - 1][y] == WALL)
			wallCount++;
		if (x < H - 1 && map[x + 1][y] == WALL)
			wallCount++;
		if (y > 0 && map[x][y - 1] == WALL)
			wallCount++;
		if (y < L - 1 && map[x][y + 1] == WALL)
			wallCount++;
		return wallCount >= 1;
	}

	// 使用BFS检查两点间是否有路径
	private boolean hasPath(int startX, int startY, int endX, int endY) {
		if (startX == endX && startY == endY)
			return true;

		boolean[][] visited = new boolean[H][L];
		Queue<int[]> queue = new LinkedList<>();
		queue.offer(new int[] { startX, startY });
		visited[startX][startY] = true;

		int[][] dirs = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

		while (!queue.isEmpty()) {
			int[] curr = queue.poll();

			for (int[] dir : dirs) {
				int newX = curr[0] + dir[0];
				int newY = curr[1] + dir[1];

				if (newX >= 0 && newX < H && newY >= 0 && newY < L
						&& !visited[newX][newY] && map[newX][newY] != WALL) {
					if (newX == endX && newY == endY)
						return true;
					queue.offer(new int[] { newX, newY });
					visited[newX][newY] = true;
				}
			}
		}
		return false;
	}

	// 获取空位置列表
	private List<int[]> getEmptySpaces() {
		List<int[]> spaces = new ArrayList<>();
		for (int i = 1; i < H - 1; i++) {
			for (int j = 1; j < L - 1; j++) {
				if (map[i][j] == EMPTY && !isCorner(i, j)) {
					spaces.add(new int[] { i, j });
				}
			}
		}
		return spaces;
	}

	private boolean isValidMove(boolean visited[][], )

	// 验证箱子位置的合法性
	private boolean isValidBoxPosition(int x, int y) {
		// 检查是否在死角
		if (isCorner(x, y))
			return false;

		// 确保箱子周围至少有两个方向可以推动
		int movableDirections = 0;
		if (map[x - 1][y] != WALL && map[x + 1][y] != WALL)
			movableDirections++;
		if (map[x][y - 1] != WALL && map[x][y + 1] != WALL)
			movableDirections++;

		return movableDirections > 0;
	}

	public void mapMake() {
		long startTime = System.currentTimeMillis();
		System.out.println("Generating map...");

		// 1. 初始化边界
		for (int i = 0; i < H; i++) {
			for (int j = 0; j < L; j++) {
				if (i == 0 || i == H - 1 || j == 0 || j == L - 1) {
					map[i][j] = WALL;
				} else {
					map[i][j] = EMPTY;
				}
			}
		}

		// 2. 随机生成内部墙
		for (int i = 2; i < H - 2; i++) {
			for (int j = 2; j < L - 2; j++) {
				if (rand.nextDouble() < 0.2) { // 20%概率生成墙
					map[i][j] = WALL;
				}
			}
		}

		// 3. 放置玩家
		int playerX, playerY;
		do {
			playerX = rand.nextInt(H / 2) + H / 4;
			playerY = rand.nextInt(L / 2) + L / 4;
		} while (map[playerX][playerY] == WALL);
		map[playerX][playerY] = PLAYER;

		// 4. 放置箱子和目标
		int boxCount = 3;
		List<int[]> emptySpaces = getEmptySpaces();
		List<int[]> boxes = new ArrayList<>();
		List<int[]> targets = new ArrayList<>();

		// 放置箱子
		for (int i = 0; i < boxCount; i++) {
			if (emptySpaces.isEmpty()) {
				System.out.println("Not enough space for boxes!");
				return;
			}

			boolean placed = false;
			while (!placed && !emptySpaces.isEmpty()) {
				int index = rand.nextInt(emptySpaces.size());
				int[] pos = emptySpaces.get(index);

				if (isValidBoxPosition(pos[0], pos[1])) {
					map[pos[0]][pos[1]] = BOX;
					boxes.add(pos);
					emptySpaces.remove(index);
					placed = true;
				} else {
					emptySpaces.remove(index);
				}
			}

			if (!placed) {
				System.out.println("Failed to place all boxes!");
				return;
			}
		}

		// 放置目标点
		emptySpaces = getEmptySpaces();
		for (int i = 0; i < boxCount; i++) {
			if (emptySpaces.isEmpty()) {
				System.out.println("Not enough space for targets!");
				return;
			}

			boolean placed = false;
			while (!placed && !emptySpaces.isEmpty()) {
				int index = rand.nextInt(emptySpaces.size());
				int[] pos = emptySpaces.get(index);

				// 确保目标点和箱子之间有可行路径
				boolean hasValidPath = false;
				for (int[] box : boxes) {
					if (hasPath(box[0], box[1], pos[0], pos[1])) {
						hasValidPath = true;
						break;
					}
				}

				if (hasValidPath) {
					map[pos[0]][pos[1]] = TARGET;
					targets.add(pos);
					emptySpaces.remove(index);
					placed = true;
				} else {
					emptySpaces.remove(index);
				}
			}

			if (!placed) {
				System.out.println("Failed to place all targets!");
				return;
			}
		}

		// 验证地图是否可解
		boolean isValid = validateMap(playerX, playerY, boxes, targets);
		if (!isValid) {
			System.out.println("Generated map is not solvable!");
			return;
		}

		System.out.println("Map generated successfully!");
		long endTime = System.currentTimeMillis();
		System.out.println("Generation time: " + (endTime - startTime) + "ms");
	}

	// 验证地图是否可解
	private boolean validateMap(int playerX, int playerY, List<int[]> boxes, List<int[]> targets) {
		// 检查玩家是否能到达所有箱子
		for (int[] box : boxes) {
			if (!hasPath(playerX, playerY, box[0], box[1])) {
				return false;
			}
		}

		// 检查每个箱子是否能到达至少一个目标点
		for (int[] box : boxes) {
			boolean canReachTarget = false;
			for (int[] target : targets) {
				if (hasPath(box[0], box[1], target[0], target[1])) {
					canReachTarget = true;
					break;
				}
			}
			if (!canReachTarget)
				return false;
		}

		return true;
	}

	// 获取生成的地图
	public int[][] getMap() {
		return map;
	}

	// 打印地图（用于调试）
	public void printMap() {
		for (int i = 0; i < H; i++) {
			for (int j = 0; j < L; j++) {
				System.out.print(map[i][j] + " ");
			}
			System.out.println();
		}
	}
}