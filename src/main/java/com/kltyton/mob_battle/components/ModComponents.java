package com.kltyton.mob_battle.components;

import com.kltyton.mob_battle.Mob_battle;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModComponents {
    public static ComponentType<Boolean> LOBSTER_TRANSFORMED;
    public static void init() {
        LOBSTER_TRANSFORMED = Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Identifier.of(Mob_battle.MOD_ID, "lobster_transformed"),
                ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
        );
    }
}
