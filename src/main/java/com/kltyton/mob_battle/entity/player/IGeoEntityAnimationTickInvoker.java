package com.kltyton.mob_battle.entity.player;

import net.minecraft.world.entity.Entity;

public interface IGeoEntityAnimationTickInvoker<T extends Entity> {
    void mobBattle$tickGeckoAnimations(T entity, float partialTick);
}
