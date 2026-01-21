package com.kltyton.mob_battle.components;

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
}
