package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

public final class CombatEffectUtil {
    private CombatEffectUtil() {
    }

    public static void addStackingArmorPiercing(LivingEntity target, LivingEntity source) {
        int amplifier = 0;
        StatusEffectInstance existing = target.getStatusEffect(ModEffects.ARMOR_PIERCING_ENTRY);
        if (existing != null) {
            amplifier = Math.min(existing.getAmplifier() + 1, 4);
        }
        target.addStatusEffect(new StatusEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, 3 * 20, amplifier), source);
    }

    public static void addPigSpiritMark(LivingEntity target, LivingEntity source, int layers) {
        int duration = target instanceof PlayerEntity ? 20 * 20 : 3 * 20;
        addPigSpiritMark(target, source, layers, duration);
    }

    public static void addPigSpiritMark(LivingEntity target, LivingEntity source, int layers, int durationTicks) {
        if (layers <= 0) {
            return;
        }
        StatusEffectInstance current = target.getStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
        int currentAmplifier = current == null ? -1 : current.getAmplifier();
        int newAmplifier = Math.min(currentAmplifier + layers, 79);
        target.addStatusEffect(new StatusEffectInstance(ModEffects.PIG_SPIRIT_MARK_ENTRY, durationTicks, newAmplifier, false, false, true), source);
    }
}
