package com.kltyton.mob_battle.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class StunEffect extends StatusEffect {
    public MobEntity target;
    public StunEffect() {
        super(StatusEffectCategory.HARMFUL, 0xAAAAFF); // 眩晕颜色，可自定义
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // 每 tick 都应用一次
        return true;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        entity.setVelocity(Vec3d.ZERO);
        entity.velocityModified = true;
        if (entity instanceof MobEntity mob) {
            target = mob;
            mob.setAiDisabled(true);
        }
        return true;
    }

    public void onRemoved(AttributeContainer attributeContainer) {
        super.onRemoved(attributeContainer);
        if (target != null && target.isAiDisabled()) {
            target.setAiDisabled(false);
        }
    }
}
