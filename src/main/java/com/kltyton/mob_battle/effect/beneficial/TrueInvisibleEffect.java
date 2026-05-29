package com.kltyton.mob_battle.effect.beneficial;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class TrueInvisibleEffect extends MobEffect {
    public TrueInvisibleEffect() {
        super(MobEffectCategory.BENEFICIAL, 16185078);
        this.addAttributeModifier(
                Attributes.WAYPOINT_TRANSMIT_RANGE,
                ResourceLocation.withDefaultNamespace("effect.waypoint_transmit_range_hide"),
                -1.0,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }
}
