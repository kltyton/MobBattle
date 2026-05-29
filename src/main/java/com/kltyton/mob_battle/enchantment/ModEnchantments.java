package com.kltyton.mob_battle.enchantment;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {
    public static ResourceKey<Enchantment> MAGIC_PROTECTION;
    public static void init() {
        MAGIC_PROTECTION = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "magic_protection"));
    }
}
