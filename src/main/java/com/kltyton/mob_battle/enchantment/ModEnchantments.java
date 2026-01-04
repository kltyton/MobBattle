package com.kltyton.mob_battle.enchantment;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    public static RegistryKey<Enchantment> MAGIC_PROTECTION;
    public static void init() {
        MAGIC_PROTECTION = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Mob_battle.MOD_ID, "magic_protection"));
    }
}
