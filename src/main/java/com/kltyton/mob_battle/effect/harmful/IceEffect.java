package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

public class IceEffect extends StatusEffect {
    public static final int MAX_LEVEL = 5;
    public static final int FULL_FREEZE_TICKS = 140;
    public static final int STEP_FREEZE_TICKS = 28;
    public static final int DEFAULT_DURATION = 100; // 5秒

    public IceEffect() {
        super(StatusEffectCategory.HARMFUL, 0xBFE9FF);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        int level = Math.min(amplifier + 1, MAX_LEVEL);
        int targetFrozenTicks = Math.min(level * STEP_FREEZE_TICKS, FULL_FREEZE_TICKS);
        // 模拟“进入细雪”的积累过程
        if (entity.getFrozenTicks() < targetFrozenTicks) {
            entity.setFrozenTicks(targetFrozenTicks);
        }
        // 让实体持续处于“在细雪中”的判定效果
        entity.setInPowderSnow(true);

        return true;
    }
}
