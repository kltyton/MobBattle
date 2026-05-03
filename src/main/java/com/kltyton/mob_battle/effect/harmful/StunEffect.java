package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.WeakHashMap;

public class StunEffect extends StatusEffect {
    private static final Map<MobEntity, Boolean> ORIGINAL_AI_DISABLED = new WeakHashMap<>();

    public StunEffect() {
        super(StatusEffectCategory.HARMFUL, 0xAAAAFF);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        entity.setVelocity(Vec3d.ZERO);
        entity.velocityModified = true;
        if (entity instanceof MobEntity mob) {
            ORIGINAL_AI_DISABLED.putIfAbsent(mob, mob.isAiDisabled());
            mob.setAiDisabled(true);
        }
        return true;
    }

    public static void restoreAiState(LivingEntity entity) {
        if (entity instanceof MobEntity mob) {
            Boolean originalAiDisabled = ORIGINAL_AI_DISABLED.remove(mob);
            if (originalAiDisabled != null) {
                mob.setAiDisabled(originalAiDisabled);
            }
        }
    }
}
