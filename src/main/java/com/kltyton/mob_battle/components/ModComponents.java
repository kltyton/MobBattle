package com.kltyton.mob_battle.components;

import com.kltyton.mob_battle.Mob_battle;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModComponents {
    public static ComponentType<Boolean> LOBSTER_TRANSFORMED;
    public static ComponentType<List<ItemStack>> BACKPACK_CONTENTS;

    private static final int MAX_BACKPACK_COMPONENT_SLOTS = 54 * 50;

    public static void init() {
        LOBSTER_TRANSFORMED = Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Identifier.of(Mob_battle.MOD_ID, "lobster_transformed"),
                ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
        );
        BACKPACK_CONTENTS = Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Identifier.of(Mob_battle.MOD_ID, "backpack_contents"),
                ComponentType.<List<ItemStack>>builder()
                        .codec(ItemStack.OPTIONAL_CODEC.listOf())
                        .packetCodec(ItemStack.OPTIONAL_PACKET_CODEC.collect(PacketCodecs.toList(MAX_BACKPACK_COMPONENT_SLOTS)))
                        .build()
        );
    }
}
