package com.kltyton.mob_battle.attributer;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ModAttributer {
    public static void init() {
        overrideAttribute(Attributes.MAX_HEALTH, 0.0, Double.MAX_VALUE);
        overrideAttribute(Attributes.ARMOR, 0.0, Double.MAX_VALUE);
        overrideAttribute(Attributes.ARMOR_TOUGHNESS, 0.0, Double.MAX_VALUE);
        overrideAttribute(Attributes.ATTACK_DAMAGE, 0.0, Double.MAX_VALUE);
        overrideAttribute(Attributes.ATTACK_KNOCKBACK, 0.0, Double.MAX_VALUE);
    }

    private static void overrideAttribute(Holder<Attribute> attributeEntry, double newMin, double newMax) {
        if (attributeEntry == null) {
            Mob_battle.LOGGER.warn("Attribute holder is null, skipping range override.");
            return;
        }

        Attribute attribute = attributeEntry.value();
        if (!(attribute instanceof RangedAttribute clampedAttribute)) {
            Mob_battle.LOGGER.warn("Attribute {} is not RangedAttribute, skipping range override.",
                    BuiltInRegistries.ATTRIBUTE.getKey(attribute));
            return;
        }

        Identifier id = BuiltInRegistries.ATTRIBUTE.getKey(clampedAttribute);
        if (id == null) {
            return;
        }

        clampedAttribute.minValue = newMin;
        clampedAttribute.maxValue = newMax;
    }
}
