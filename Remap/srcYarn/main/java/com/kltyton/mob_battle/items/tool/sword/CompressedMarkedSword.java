package com.kltyton.mob_battle.items.tool.sword;

import com.kltyton.mob_battle.items.tool.BaseSword;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

public class CompressedMarkedSword extends BaseSword {
    private final RegistryEntry<StatusEffect> markEffect;

    public CompressedMarkedSword(Settings settings, RegistryEntry<StatusEffect> markEffect) {
        super(settings);
        this.markEffect = markEffect;
    }

    @Override
    public void addStatusEffect(LivingEntity target, LivingEntity attacker) {
        target.addStatusEffect(new StatusEffectInstance(this.markEffect, 7 * 20, 0, false, true, true), attacker);
    }
}
