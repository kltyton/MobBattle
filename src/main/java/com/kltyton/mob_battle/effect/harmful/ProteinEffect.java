package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

public class ProteinEffect extends StatusEffect {
    public ProteinEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xFF9999);
    }

    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient) {
            int maxHeal = amplifier + 2;
            int healAmount = entity.getRandom().nextBetween(1, maxHeal);

            entity.heal((float) healAmount);
        }
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
