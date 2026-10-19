package com.kltyton.mob_battle.enchantment;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.enchantment.effect.FallingThunderEnchantmentEffect;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantment;

/** 初始化附魔资源键和数据驱动的实体效果类型。 */
public class ModEnchantments {
    public static ResourceKey<Enchantment> MAGIC_PROTECTION;
    public static ResourceKey<Enchantment> FALLING_THUNDER;
    public static void init() {
        MAGIC_PROTECTION = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "magic_protection"));
        FALLING_THUNDER = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "falling_thunder"));
        Registry.register(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE,
                Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "falling_thunder"),
                FallingThunderEnchantmentEffect.CODEC);
    }
}
