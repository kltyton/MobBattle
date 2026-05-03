package com.kltyton.mob_battle.entity.player;

import net.minecraft.entity.Entity;

public interface IGeoEntityAnimationTickInvoker<T extends Entity> {
    void mobBattle$tickGeckoAnimations(T entity, float partialTick);
}
