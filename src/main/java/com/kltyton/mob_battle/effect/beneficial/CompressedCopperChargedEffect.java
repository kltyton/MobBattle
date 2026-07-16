package com.kltyton.mob_battle.effect.beneficial;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class CompressedCopperChargedEffect extends MobEffect {
    public CompressedCopperChargedEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xD58C4A);
        this.addAttributeModifier(
                Attributes.ARMOR,
                Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "effect.compressed_copper_charged.armor"),
                6.0,
                AttributeModifier.Operation.ADD_VALUE
        );
        this.addAttributeModifier(
                Attributes.ARMOR_TOUGHNESS,
                Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "effect.compressed_copper_charged.armor_toughness"),
                2.0,
                AttributeModifier.Operation.ADD_VALUE
        );
        this.addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "effect.compressed_copper_charged.damage"),
                10.0,
                AttributeModifier.Operation.ADD_VALUE
        );
        this.addAttributeModifier(
                Attributes.MAX_ABSORPTION,
                Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "effect.compressed_copper_charged.absorption"),
                80.0,
                AttributeModifier.Operation.ADD_VALUE
        );
        this.addAttributeModifier(
                Attributes.ATTACK_SPEED,
                Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "effect.compressed_copper_charged.attack_speed"),
                0.2,
                AttributeModifier.Operation.ADD_VALUE
        );
        this.addAttributeModifier(
                Attributes.ENTITY_INTERACTION_RANGE,
                Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "effect.compressed_copper_charged.entity_range"),
                0.4,
                AttributeModifier.Operation.ADD_VALUE
        );
        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "effect.compressed_copper_charged.movement_speed"),
                0.1,
                AttributeModifier.Operation.ADD_VALUE
        );
    }
}
