package com.sokoban.controllers;

import java.util.*;

public class SokobanSolver {
	// 定义方向: 上, 左, 下, 右
	private static final int[][] DIRECTIONS = {
			{ -1, 0 }, // W - 上
			{ 0, -1 }, // A - 左
			{ 1, 0 }, // S - 下
			{ 0, 1 } // D - 右
	};
	private static final char[] DIR_NAMES = { 'A', 'W', 'D', 'S' };

	// 状态类
	static class State {
		int playerX, playerY;
		Set<String> boxes;
		String path;

		State(int playerX, int playerY, Set<String> boxes, String path) {
			this.playerX = playerX;
			this.playerY = playerY;
			this.boxes = new HashSet<>(boxes);
			this.path = path;
		}

		@Override
		public int hashCode() {
			return Objects.hash(playerX, playerY, boxes.hashCode());
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof State))
				return false;
			State other = (State) obj;
			return this.playerX == other.playerX &&
					this.playerY == other.playerY &&
					this.boxes.equals(other.boxes);
		}

		// 创建状态的字符串表示，用于调试
		@Override
		public String toString() {
			return String.format("Player(%d,%d), Boxes: %s, Path: %s",
					playerX, playerY, boxes.toString(), path);
		}
	}

	public String solve(int[][] grid) {
		long startTime = System.currentTimeMillis();
		int numNodes = 0;

		int rows = grid.length;
		int cols = grid[0].length;

		// 解析初始状态
		Set<String> boxes = new HashSet<>();
		Set<String> goals = new HashSet<>();
		int playerX = -1, playerY = -1;

		// 输出地图
		System.out.println("地图:");
		for (int[] row : grid) {
			System.out.println(Arrays.toString(row));
		}

		// 解析地图
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				switch (grid[y][x]) {
					case 2: // 玩家
						playerX = y;
						playerY = x;
						break;
					case 3: // 箱子
						boxes.add(y + "," + x);
						break;
					case 4: // 目标
						goals.add(y + "," + x);
						break;
					case 5: // 玩家在目标点
						playerX = y;
						playerY = x;
						goals.add(y + "," + x);
						break;
					case 6: // 箱子在目标点
						boxes.add(y + "," + x);
						goals.add(y + "," + x);
						break;
				}
			}
		}

		// 验证初始状态
		if (playerX == -1 || playerY == -1 || boxes.isEmpty() || goals.isEmpty()) {
			System.out.println("无效的初始状态");
			return "";
		}

		// 使用A*算法
		PriorityQueue<State> open = new PriorityQueue<>((a, b) -> {
			int fa = a.path.length() + manhattanDistance(a.boxes, goals);
			int fb = b.path.length() + manhattanDistance(b.boxes, goals);
			return Integer.compare(fa, fb);
		});

		State initialState = new State(playerX, playerY, boxes, "");
		open.add(initialState);
		Set<State> closed = new HashSet<>();

		while (!open.isEmpty()) {
			State current = open.poll();
			numNodes++;

			if (isGoalState(current.boxes, goals)) {
				long endTime = System.currentTimeMillis();
				System.out.printf("找到解决方案!\n路径: %s\n步数: %d\n耗时: %d ms\n探索节点数: %d\n",
						current.path, current.path.length(), endTime - startTime, numNodes);
				return current.path;
			}

			if (closed.contains(current)) {
				continue;
			}
			closed.add(current);

			// 尝试四个方向
			for (int dir = 0; dir < 4; dir++) {
				int newPlayerX = current.playerX + DIRECTIONS[dir][0];
				int newPlayerY = current.playerY + DIRECTIONS[dir][1];

				// 检查移动是否有效
				if (!isValidPosition(newPlayerX, newPlayerY, grid)) {
					continue;
				}

				// 检查新位置
				String newPos = newPlayerX + "," + newPlayerY;
				Set<String> newBoxes = new HashSet<>(current.boxes);

				if (newBoxes.contains(newPos)) {
					// 尝试推箱子
					int newBoxX = newPlayerX + DIRECTIONS[dir][0];
					int newBoxY = newPlayerY + DIRECTIONS[dir][1];

					if (!isValidBoxMove(newBoxX, newBoxY, grid, newBoxes, goals)) {
						continue;
					}

					// 移动箱子
					newBoxes.remove(newPos);
					newBoxes.add(newBoxX + "," + newBoxY);

					// 检查死锁
					if (isDeadlock(newBoxX, newBoxY, grid, goals, newBoxes)) {
						continue;
					}
				}

				// 创建新状态
				State newState = new State(newPlayerX, newPlayerY, newBoxes,
						current.path + DIR_NAMES[dir]);

				if (!closed.contains(newState)) {
					open.add(newState);
				}
			}
		}

		long endTime = System.currentTimeMillis();
		System.out.printf("未找到解决方案\n耗时: %d ms\n探索节点数: %d\n",
				endTime - startTime, numNodes);
		return "";
	}

	private boolean isValidPosition(int x, int y, int[][] grid) {
		return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length && grid[x][y] != 1;
	}

	private boolean isValidBoxMove(int x, int y, int[][] grid, Set<String> boxes, Set<String> goals) {
		if (!isValidPosition(x, y, grid))
			return false;
		String pos = x + "," + y;
		return !boxes.contains(pos);
	}

	private boolean isGoalState(Set<String> boxes, Set<String> goals) {
		return boxes.equals(goals);
	}

	private int manhattanDistance(Set<String> boxes, Set<String> goals) {
		int totalDistance = 0;
		for (String box : boxes) {
			String[] boxCoord = box.split(",");
			int boxX = Integer.parseInt(boxCoord[0]);
			int boxY = Integer.parseInt(boxCoord[1]);

			int minDist = Integer.MAX_VALUE;
			for (String goal : goals) {
				String[] goalCoord = goal.split(",");
				int goalX = Integer.parseInt(goalCoord[0]);
				int goalY = Integer.parseInt(goalCoord[1]);

				int dist = Math.abs(boxX - goalX) + Math.abs(boxY - goalY);
				minDist = Math.min(minDist, dist);
			}
			totalDistance += minDist;
		}
		return totalDistance;
	}

	private boolean isDeadlock(int x, int y, int[][] grid, Set<String> goals, Set<String> boxes) {
		// 如果箱子在目标点上，不是死锁
		if (goals.contains(x + "," + y)) {
			return false;
		}

		// 检查角落死锁
		boolean isCornerDeadlock = checkCornerDeadlock(x, y, grid, boxes);
		if (isCornerDeadlock) {
			return true;
		}

		// 检查边缘死锁
		boolean isEdgeDeadlock = checkEdgeDeadlock(x, y, grid, boxes, goals);
		if (isEdgeDeadlock) {
			return true;
		}

		return false;
	}

	private boolean checkCornerDeadlock(int x, int y, int[][] grid, Set<String> boxes) {
		// 检查四个角落
		boolean[][] corners = {
				{ isBlocked(x - 1, y, grid, boxes), isBlocked(x, y - 1, grid, boxes) }, // 左上
				{ isBlocked(x - 1, y, grid, boxes), isBlocked(x, y + 1, grid, boxes) }, // 右上
				{ isBlocked(x + 1, y, grid, boxes), isBlocked(x, y - 1, grid, boxes) }, // 左下
				{ isBlocked(x + 1, y, grid, boxes), isBlocked(x, y + 1, grid, boxes) } // 右下
		};

		for (boolean[] corner : corners) {
			if (corner[0] && corner[1]) {
				return true;
			}
		}
		return false;
	}

	private boolean checkEdgeDeadlock(int x, int y, int[][] grid, Set<String> boxes, Set<String> goals) {
		// 检查箱子是否被卡在墙边
		boolean horizontalBlocked = isBlocked(x, y - 1, grid, boxes) &&
				isBlocked(x, y + 1, grid, boxes);
		boolean verticalBlocked = isBlocked(x - 1, y, grid, boxes) &&
				isBlocked(x + 1, y, grid, boxes);

		if (horizontalBlocked || verticalBlocked) {
			// 检查该位置是否有通向目标的路径
			for (String goal : goals) {
				String[] coords = goal.split(",");
				int goalX = Integer.parseInt(coords[0]);
				int goalY = Integer.parseInt(coords[1]);

				if (horizontalBlocked && x == goalX) {
					return false;
				}
				if (verticalBlocked && y == goalY) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private boolean isBlocked(int x, int y, int[][] grid, Set<String> boxes) {
		if (!isValidPosition(x, y, grid))
			return true;
		return boxes.contains(x + "," + y) || grid[x][y] == 1;
	}
}