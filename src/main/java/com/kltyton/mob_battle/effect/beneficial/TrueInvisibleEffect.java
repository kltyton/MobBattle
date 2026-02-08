package com.kltyton.mob_battle.effect.beneficial;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;

public class TrueInvisibleEffect extends StatusEffect {
    public TrueInvisibleEffect() {
        super(StatusEffectCategory.BENEFICIAL, 16185078);
        this.addAttributeModifier(
                EntityAttributes.WAYPOINT_TRANSMIT_RANGE,
                Identifier.ofVanilla("effect.waypoint_transmit_range_hide"),
                -1.0,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }
}
