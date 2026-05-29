package com.kltyton.mob_battle.attributer;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.mixin.ClampedEntityAttributeAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
            Mob_battle.LOGGER.warn("属性条目为 null，正在跳过。");
            return;
        }

        Attribute attribute = attributeEntry.value();
        if (!(attribute instanceof RangedAttribute)) {
            Mob_battle.LOGGER.warn("Attribute {} 不是 ClampedEntityAttribute，正在跳过。",
                    BuiltInRegistries.ATTRIBUTE.getKey(attribute));
            return;
        }

        RangedAttribute clampedAttribute = (RangedAttribute) attribute;
        ResourceLocation id = BuiltInRegistries.ATTRIBUTE.getKey(clampedAttribute);
        if (id == null) return;

        try {
            ClampedEntityAttributeAccessor accessor = (ClampedEntityAttributeAccessor) clampedAttribute;
            accessor.setMinValue(newMin);
            accessor.setMaxValue(newMax);
        } catch (ClassCastException e) {
            Mob_battle.LOGGER.error("无法覆盖属性 {}", id, e);
        }
    }
}
