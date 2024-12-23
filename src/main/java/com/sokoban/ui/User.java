package com.sokoban.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

public class User implements Serializable {
	private String username;
	private String password;
	private int score;
	private String nickname;
	private int level;
	private static final long serialVersionUID = 1L;

	private static final String SERVER_IP = "localhost";
	private static final int SERVER_PORT = 8888;

	public User(String username, String password) {
		this.username = username;
		this.password = password;
		this.score = 0;
		this.nickname = username;
		this.level = 0;
	}

	public void updateLevel(int level) {
		try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
			this.level = level;

			out.println("UPDATE|" + username + "|level|" + level);
			String updateResponse = in.readLine();
			System.out.println(updateResponse.equals("UPDATE_SUCCESS") ? "更新成功！" : "更新失败！");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void syncData() {
		try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

			out.println("LOGIN|" + username + "|" + password);
			String response = in.readLine();
			String[] parts = response.split("\\|");

			if ("SUCCESS".equals(parts[0])) {
				System.out.println("登录成功！");
				System.out.println("得分：" + parts[1]);
				System.out.println("昵称：" + parts[2]);
				System.out.println("等级：" + parts[3]);
				this.setNickname(parts[2]);
				this.setLevel(Integer.parseInt(parts[3]));
			} else {
				System.out.println("登录失败：" + parts[1]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}