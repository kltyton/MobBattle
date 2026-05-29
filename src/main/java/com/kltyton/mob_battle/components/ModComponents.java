package com.kltyton.mob_battle.components;

import com.kltyton.mob_battle.Mob_battle;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ModComponents {
    public static DataComponentType<Boolean> LOBSTER_TRANSFORMED;
    public static DataComponentType<List<ItemStack>> BACKPACK_CONTENTS;

    private static final int MAX_BACKPACK_COMPONENT_SLOTS = 54 * 50;

    public static void init() {
        LOBSTER_TRANSFORMED = Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "lobster_transformed"),
                DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build()
        );
        BACKPACK_CONTENTS = Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "backpack_contents"),
                DataComponentType.<List<ItemStack>>builder()
                        .persistent(ItemStack.OPTIONAL_CODEC.listOf())
                        .networkSynchronized(ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(MAX_BACKPACK_COMPONENT_SLOTS)))
                        .build()
        );
    }
}
