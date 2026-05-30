package com.kltyton.mob_battle.components;

import com.kltyton.mob_battle.effect.ModEffects;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;


public class ModConsumableComponents {
    public static final Consumable COOKED_HIGHBIRD_EGG = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(MobEffects.INSTANT_HEALTH, 20, 0),
                                    new MobEffectInstance(MobEffects.RESISTANCE, 200, 4)
                            )
                    )
            )
            .build();
    public static final Consumable LOBSTER = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(ModEffects.PROTEIN_ENTRY, 100, 2)
                            )
                    )
            )
            .build();
    public static final Consumable MAGMA_LOBSTER = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(ModEffects.PROTEIN_ENTRY, 20 * 3, 4),
                                    new MobEffectInstance(MobEffects.ABSORPTION, 10 * 20, 4),
                                    new MobEffectInstance(MobEffects.RESISTANCE, 10 * 20, 0)
                            )
                    )
            )
            .build();

    public static final Consumable BURST_OBSIDIAN_LOBSTER = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3 * 60 * 20, 0),
                                    new MobEffectInstance(MobEffects.ABSORPTION, 8 * 20, 9),
                                    new MobEffectInstance(MobEffects.RESISTANCE, 8 * 20, 0),
                                    new MobEffectInstance(MobEffects.REGENERATION, 8 * 20, 1),
                                    new MobEffectInstance(ModEffects.PROTEIN_ENTRY, 400, 2)
                            )
                    )
            )
            .build();
    public static final Consumable BEER = Consumables.defaultDrink()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(ModEffects.EXCITEMENT_ENTRY, 10 * 20, 0)
                            )
                    )
            )
            .build();
}
