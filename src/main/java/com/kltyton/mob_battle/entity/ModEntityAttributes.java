package com.kltyton.mob_battle.entity;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEntityAttributes {
    // 推荐方式：直接定义为 RegistryEntry
    public static RegistryEntry<EntityAttribute> DAMAGE_REDUCTION;

    public static void init() {
        DAMAGE_REDUCTION = Registry.registerReference(
                Registries.ATTRIBUTE,
                Identifier.of(Mob_battle.MOD_ID, "generic.damage_reduction"),
                new ClampedEntityAttribute(
                        "attribute.name.generic.damage_reduction",
                        0.0, 0.0, 1.0
                ).setTracked(true)
        );
    }
}
