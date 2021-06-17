package com.demo.project.sbhatia.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Player {

    @JsonProperty("playerPoints")
    private long playerPoints;

    @JsonProperty("nickName")
    private String nickName;

    public Player(long playerPoints, String nickName) {
        this.playerPoints = playerPoints;
        this.nickName = nickName;
    }

    public Player() {
    }

    public long getPlayerPoints() {
        return playerPoints;
    }

    public void setPlayerPoints(long playerPoints) {
        this.playerPoints = playerPoints;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
