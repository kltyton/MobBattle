package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class CombatEffectUtil {
    private CombatEffectUtil() {
    }

    public static void addStackingArmorPiercing(LivingEntity target, LivingEntity source) {
        int amplifier = 0;
        MobEffectInstance existing = target.getEffect(ModEffects.ARMOR_PIERCING_ENTRY);
        if (existing != null) {
            amplifier = Math.min(existing.getAmplifier() + 1, 4);
        }
        target.addEffect(new MobEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, 3 * 20, amplifier), source);
    }

    public static void addPigSpiritMark(LivingEntity target, LivingEntity source, int layers) {
        int duration = target instanceof Player ? 20 * 20 : 3 * 20;
        addPigSpiritMark(target, source, layers, duration);
    }

    public static void addPigSpiritMark(LivingEntity target, LivingEntity source, int layers, int durationTicks) {
        if (layers <= 0) {
            return;
        }
        MobEffectInstance current = target.getEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
        int currentAmplifier = current == null ? -1 : current.getAmplifier();
        int newAmplifier = Math.min(currentAmplifier + layers, 79);
        target.addEffect(new MobEffectInstance(ModEffects.PIG_SPIRIT_MARK_ENTRY, durationTicks, newAmplifier, false, false, true), source);
    }
}
