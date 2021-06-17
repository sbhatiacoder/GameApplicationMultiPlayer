package com.demo.project.sbhatia.service.impl;

import com.demo.project.sbhatia.model.Player;
import com.demo.project.sbhatia.service.StorageService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

public class StorageServiceImpl implements StorageService {
    private final Map<String, Long> playerNickNameAndPoint = new ConcurrentHashMap<>();


    @Override
    public Player getOrCreateplayer(String nickName) {
        playerNickNameAndPoint.compute(nickName, (nick, point) -> point == null ? 0L : point);
        Long usePoint = playerNickNameAndPoint.get(nickName);
        return new Player(usePoint, nickName);
    }

    @Override
    public boolean transferPoints(String fromplayerNickName, String toplayerNickName, Long point) {
        getOrCreateplayer(fromplayerNickName);
        getOrCreateplayer(toplayerNickName);
        boolean withdraw = withdraw(fromplayerNickName, point);
        if (withdraw) {
            return add(toplayerNickName, point);
        } else {
            return false;
        }
    }

    @Override
    public boolean withdraw(String fromplayerNickName, Long point) {
        return processTransaction(fromplayerNickName, point, (transactionSuccess, curPoint) -> {
            long afterTransactionPoint = curPoint - point;
            if (afterTransactionPoint < 0) {
                transactionSuccess.set(false);
                return curPoint;
            } else {
                transactionSuccess.set(true);
                return afterTransactionPoint;
            }
        });
    }

    @Override
    public boolean add(String fromplayerNickName, Long point) {
        return processTransaction(fromplayerNickName, point, (transactionSuccess, curPoint) -> {
            transactionSuccess.set(true);
            return curPoint + point;
        });
    }

    private boolean processTransaction(String fromplayerNickName, Long point, BiFunction<AtomicBoolean, Long, Long> transactionFunction) {
        if (point < 0) {
            return false;
        }
        getOrCreateplayer(fromplayerNickName);
        AtomicBoolean res = new AtomicBoolean(false);
        playerNickNameAndPoint.computeIfPresent(fromplayerNickName, (nick, curPointpoint) -> transactionFunction.apply(res, curPointpoint));
        return res.get();
    }
}
