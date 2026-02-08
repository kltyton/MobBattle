package com.kltyton.mob_battle.effect.harmful;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;

public class HeartEaterStatusEffect extends StatusEffect {
    public HeartEaterStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0x4A0404);
        this.addAttributeModifier(EntityAttributes.MAX_HEALTH,
                Identifier.of(Mob_battle.MOD_ID, "effect.heart_eater"),
                -2.0,
                EntityAttributeModifier.Operation.ADD_VALUE);
    }
}
