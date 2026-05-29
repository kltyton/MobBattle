package com.kltyton.mob_battle.attributer;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.mixin.ClampedEntityAttributeAccessor;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModAttributer {

    public static void init() {
        overrideAttribute(EntityAttributes.MAX_HEALTH, 0.0, Double.MAX_VALUE);
        overrideAttribute(EntityAttributes.ARMOR, 0.0, Double.MAX_VALUE);
        overrideAttribute(EntityAttributes.ARMOR_TOUGHNESS, 0.0, Double.MAX_VALUE);
        overrideAttribute(EntityAttributes.ATTACK_DAMAGE, 0.0, Double.MAX_VALUE);
        overrideAttribute(EntityAttributes.ATTACK_KNOCKBACK, 0.0, Double.MAX_VALUE);
    }
    private static void overrideAttribute(RegistryEntry<EntityAttribute> attributeEntry, double newMin, double newMax) {
        if (attributeEntry == null) {
            Mob_battle.LOGGER.warn("属性条目为 null，正在跳过。");
            return;
        }

        EntityAttribute attribute = attributeEntry.value();
        if (!(attribute instanceof ClampedEntityAttribute)) {
            Mob_battle.LOGGER.warn("Attribute {} 不是 ClampedEntityAttribute，正在跳过。",
                    Registries.ATTRIBUTE.getId(attribute));
            return;
        }

        ClampedEntityAttribute clampedAttribute = (ClampedEntityAttribute) attribute;
        Identifier id = Registries.ATTRIBUTE.getId(clampedAttribute);
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
