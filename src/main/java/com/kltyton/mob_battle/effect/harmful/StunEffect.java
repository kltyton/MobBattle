package com.kltyton.mob_battle.effect.harmful;

import com.kltyton.mob_battle.accessor.IStunAiState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class StunEffect extends MobEffect {
    private static final ThreadLocal<Boolean> STUN_AI_CHANGE = ThreadLocal.withInitial(() -> false);

    public StunEffect() {
        super(MobEffectCategory.HARMFUL, 0xAAAAFF);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(ServerLevel world, LivingEntity entity, int amplifier) {
        entity.setDeltaMovement(Vec3.ZERO);
        entity.hurtMarked = true;
        if (entity instanceof Mob mob && mob instanceof IStunAiState stunAiState) {
            if (!stunAiState.mobBattle$hasStunAiState()) {
                stunAiState.mobBattle$beginStunAiState(mob.isNoAi());
            }
            setAiDisabledFromStun(mob, true);
        }
        return true;
    }

    public static boolean isStunAiChange() {
        return STUN_AI_CHANGE.get();
    }

    public static void restoreAiState(LivingEntity entity) {
        if (entity instanceof Mob mob && mob instanceof IStunAiState stunAiState && stunAiState.mobBattle$hasStunAiState()) {
            boolean shouldStayDisabled = stunAiState.mobBattle$getOriginalAiDisabled()
                    || stunAiState.mobBattle$hasExternalAiDisable();
            stunAiState.mobBattle$clearStunAiState();
            setAiDisabledFromStun(mob, shouldStayDisabled);
        }
    }

    private static void setAiDisabledFromStun(Mob mob, boolean aiDisabled) {
        boolean previous = STUN_AI_CHANGE.get();
        STUN_AI_CHANGE.set(true);
        try {
            mob.setNoAi(aiDisabled);
        } finally {
            STUN_AI_CHANGE.set(previous);
        }
    }
}
