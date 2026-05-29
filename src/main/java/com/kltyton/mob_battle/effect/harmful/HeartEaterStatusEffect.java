package com.kltyton.mob_battle.effect.harmful;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class HeartEaterStatusEffect extends MobEffect {
    public HeartEaterStatusEffect() {
        super(MobEffectCategory.HARMFUL, 0x4A0404);
        this.addAttributeModifier(Attributes.MAX_HEALTH,
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "effect.heart_eater"),
                -2.0,
                AttributeModifier.Operation.ADD_VALUE);
    }
}
