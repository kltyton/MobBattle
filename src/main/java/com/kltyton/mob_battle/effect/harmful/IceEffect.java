package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class IceEffect extends MobEffect {
    public static final int MAX_LEVEL = 5;
    public static final int FULL_FREEZE_TICKS = 140;
    public static final int STEP_FREEZE_TICKS = 28;
    public static final int DEFAULT_DURATION = 100; // 5秒

    public IceEffect() {
        super(MobEffectCategory.HARMFUL, 0xBFE9FF);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(ServerLevel world, LivingEntity entity, int amplifier) {
        int level = Math.min(amplifier + 1, MAX_LEVEL);
        int targetFrozenTicks = Math.min(level * STEP_FREEZE_TICKS, FULL_FREEZE_TICKS);
        // 模拟“进入细雪”的积累过程
        if (entity.getTicksFrozen() < targetFrozenTicks) {
            entity.setTicksFrozen(targetFrozenTicks);
        }
        // 让实体持续处于“在细雪中”的判定效果
        entity.setIsInPowderSnow(true);
        entity.hurtServer(world, entity.damageSources().freeze(), 3.0F);

        return true;
    }
}
