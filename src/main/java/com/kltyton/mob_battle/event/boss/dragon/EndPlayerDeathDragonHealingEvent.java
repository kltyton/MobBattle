package com.kltyton.mob_battle.event.boss.dragon;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/**
 * 末地玩家死亡时，为同一末地世界中的存活末影龙恢复 2000 点生命值。
 */
public final class EndPlayerDeathDragonHealingEvent {
    private static final float HEAL_AMOUNT = 2000.0F;

    private EndPlayerDeathDragonHealingEvent() {
    }

    public static void init() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!(entity instanceof ServerPlayer player)
                    || !(player.level() instanceof ServerLevel level)
                    || level.dimension() != Level.END) {
                return;
            }
            level.getDragons().stream()
                    .filter(dragon -> dragon.isAlive() && !dragon.isRemoved())
                    .forEach(dragon -> dragon.heal(HEAL_AMOUNT));
        });
    }
}
