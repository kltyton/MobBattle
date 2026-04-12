package com.kltyton.mob_battle.components;

import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;

import java.util.List;


public class ModConsumableComponents {
    public static final ConsumableComponent COOKED_HIGHBIRD_EGG = ConsumableComponents.food()
            .consumeEffect(
                    new ApplyEffectsConsumeEffect(
                            List.of(
                                    new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 20, 0),
                                    new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 4)
                            )
                    )
            )
            .build();
    public static final ConsumableComponent LOBSTER = ConsumableComponents.food()
            .consumeEffect(
                    new ApplyEffectsConsumeEffect(
                            List.of(
                                    new StatusEffectInstance(ModEffects.PROTEIN_ENTRY, 100, 2)
                            )
                    )
            )
            .build();
    public static final ConsumableComponent MAGMA_LOBSTER = ConsumableComponents.food()
            .consumeEffect(
                    new ApplyEffectsConsumeEffect(
                            List.of(
                                    new StatusEffectInstance(ModEffects.PROTEIN_ENTRY, 20 * 3, 4),
                                    new StatusEffectInstance(StatusEffects.ABSORPTION, 10 * 20, 4),
                                    new StatusEffectInstance(StatusEffects.RESISTANCE, 10 * 20, 0)
                            )
                    )
            )
            .build();

    public static final ConsumableComponent BURST_OBSIDIAN_LOBSTER = ConsumableComponents.food()
            .consumeEffect(
                    new ApplyEffectsConsumeEffect(
                            List.of(
                                    new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 3 * 60 * 20, 0),
                                    new StatusEffectInstance(StatusEffects.ABSORPTION, 8 * 20, 9),
                                    new StatusEffectInstance(StatusEffects.RESISTANCE, 8 * 20, 0),
                                    new StatusEffectInstance(StatusEffects.REGENERATION, 8 * 20, 1),
                                    new StatusEffectInstance(ModEffects.PROTEIN_ENTRY, 400, 2)
                            )
                    )
            )
            .build();
}
