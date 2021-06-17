package com.demo.project.sbhatia.service;

import com.demo.project.sbhatia.model.Player;

public interface StorageService {

    Player getOrCreateplayer(String nickName);

    boolean transferPoints(String fromplayerNickName, String toplayerNickName, Long points);

    boolean withdraw(String playerNickName, Long point);

    boolean add(String playerNickName, Long point);
}
