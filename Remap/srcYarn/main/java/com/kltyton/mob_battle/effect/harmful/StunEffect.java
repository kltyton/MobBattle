package com.kltyton.mob_battle.effect.harmful;

import com.kltyton.mob_battle.accessor.IStunAiState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class StunEffect extends StatusEffect {
    private static final ThreadLocal<Boolean> STUN_AI_CHANGE = ThreadLocal.withInitial(() -> false);

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
        if (entity instanceof MobEntity mob && mob instanceof IStunAiState stunAiState) {
            if (!stunAiState.mobBattle$hasStunAiState()) {
                stunAiState.mobBattle$beginStunAiState(mob.isAiDisabled());
            }
            setAiDisabledFromStun(mob, true);
        }
        return true;
    }

    public static boolean isStunAiChange() {
        return STUN_AI_CHANGE.get();
    }

    public static void restoreAiState(LivingEntity entity) {
        if (entity instanceof MobEntity mob && mob instanceof IStunAiState stunAiState && stunAiState.mobBattle$hasStunAiState()) {
            boolean shouldStayDisabled = stunAiState.mobBattle$getOriginalAiDisabled()
                    || stunAiState.mobBattle$hasExternalAiDisable();
            stunAiState.mobBattle$clearStunAiState();
            setAiDisabledFromStun(mob, shouldStayDisabled);
        }
    }

    private static void setAiDisabledFromStun(MobEntity mob, boolean aiDisabled) {
        boolean previous = STUN_AI_CHANGE.get();
        STUN_AI_CHANGE.set(true);
        try {
            mob.setAiDisabled(aiDisabled);
        } finally {
            STUN_AI_CHANGE.set(previous);
        }
    }
}
