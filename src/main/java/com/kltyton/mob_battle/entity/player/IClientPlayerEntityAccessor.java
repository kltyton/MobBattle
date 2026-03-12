package com.kltyton.mob_battle.entity.player;

public interface IClientPlayerEntityAccessor {
    void clientSend(String message);
    void setPerson(int person);
}
