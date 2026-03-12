package com.kltyton.mob_battle.effect.beneficial;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.RegenerationStatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

public class SuperRegenerationStatusEffect extends RegenerationStatusEffect {
    public SuperRegenerationStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 13458603);
    }
    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(100.0F);
        }

        return true;
    }
}
