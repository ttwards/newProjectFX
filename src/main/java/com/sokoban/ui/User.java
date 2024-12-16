package com.sokoban.ui;

import java.io.Serializable;

public class User implements Serializable{
    private String username;
    private String password;
    private int score;
    private String nickname;
    private int level;
	private static final long serialVersionUID = 1L;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.score = 0;
        this.nickname = username;
        this.level = 1;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
}