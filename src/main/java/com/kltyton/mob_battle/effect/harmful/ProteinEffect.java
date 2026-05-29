package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ProteinEffect extends MobEffect {
    public ProteinEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF9999);
    }

    public boolean applyEffectTick(ServerLevel world, LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            int maxHeal = amplifier + 2;
            int healAmount = entity.getRandom().nextIntBetweenInclusive(1, maxHeal);

            entity.heal((float) healAmount);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
