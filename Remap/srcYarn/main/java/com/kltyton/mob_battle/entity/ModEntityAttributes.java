package com.kltyton.mob_battle.entity;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEntityAttributes {
    public static RegistryEntry<EntityAttribute> DAMAGE_REDUCTION;
    public static RegistryEntry<EntityAttribute> MAGIC_DAMAGE;

    public static void init() {
        DAMAGE_REDUCTION = Registry.registerReference(
                Registries.ATTRIBUTE,
                Identifier.of(Mob_battle.MOD_ID, "generic.damage_reduction"),
                new ClampedEntityAttribute(
                        "attribute.name.generic.damage_reduction",
                        0.0, 0.0, 1.0
                ).setTracked(true)
        );
        MAGIC_DAMAGE = Registry.registerReference(
                Registries.ATTRIBUTE,
                Identifier.of(Mob_battle.MOD_ID, "generic.magic_damage"),
                new ClampedEntityAttribute(
                        "attribute.name.generic.magic_damage",
                        0.0, 0.0, Float.MAX_VALUE
                ).setTracked(true)
        );
    }
}
