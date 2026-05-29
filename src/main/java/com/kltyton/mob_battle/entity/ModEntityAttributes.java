package com.kltyton.mob_battle.entity;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ModEntityAttributes {
    public static Holder<Attribute> DAMAGE_REDUCTION;
    public static Holder<Attribute> MAGIC_DAMAGE;

    public static void init() {
        DAMAGE_REDUCTION = Registry.registerForHolder(
                BuiltInRegistries.ATTRIBUTE,
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "generic.damage_reduction"),
                new RangedAttribute(
                        "attribute.name.generic.damage_reduction",
                        0.0, 0.0, 1.0
                ).setSyncable(true)
        );
        MAGIC_DAMAGE = Registry.registerForHolder(
                BuiltInRegistries.ATTRIBUTE,
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "generic.magic_damage"),
                new RangedAttribute(
                        "attribute.name.generic.magic_damage",
                        0.0, 0.0, Float.MAX_VALUE
                ).setSyncable(true)
        );
    }
}
