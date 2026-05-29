package com.kltyton.mob_battle.effect.beneficial;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.RegenerationMobEffect;
import net.minecraft.world.entity.LivingEntity;

public class SuperRegenerationStatusEffect extends RegenerationMobEffect {
    public SuperRegenerationStatusEffect() {
        super(MobEffectCategory.BENEFICIAL, 13458603);
    }
    @Override
    public boolean applyEffectTick(ServerLevel world, LivingEntity entity, int amplifier) {
        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(100.0F);
        }

        return true;
    }
}
