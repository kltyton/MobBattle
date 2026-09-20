package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.world.item.Rarity;

/**
 * 压缩材料类物品注册器。
 *
 * <p>按原始源码语句顺序注册压缩铜锭、压缩铁锭、压缩金锭、压缩钻石、压缩下界合金锭、
 * 压缩红石与压缩青金石(共 7 项),统一稀有度为 UNCOMMON,经由
 * {@code RegistrySupport.registerItem} 注册。</p>
 */
public final class MaterialItemRegistrar {

    private MaterialItemRegistrar() {
    }

    public static void init() {
        ModItems.COMPRESSED_COPPER_INGOT = RegistrySupport.registerItem("compressed_copper_ingot", RegistrySupport.registryBaseItemSettings("compressed_copper_ingot").rarity(Rarity.UNCOMMON));
        ModItems.COMPRESSED_IRON_INGOT = RegistrySupport.registerItem("compressed_iron_ingot", RegistrySupport.registryBaseItemSettings("compressed_iron_ingot").rarity(Rarity.UNCOMMON));
        ModItems.COMPRESSED_GOLD_INGOT = RegistrySupport.registerItem("compressed_gold_ingot", RegistrySupport.registryBaseItemSettings("compressed_gold_ingot").rarity(Rarity.UNCOMMON));
        ModItems.COMPRESSED_DIAMOND = RegistrySupport.registerItem("compressed_diamond", RegistrySupport.registryBaseItemSettings("compressed_diamond").rarity(Rarity.UNCOMMON));
        ModItems.COMPRESSED_NETHERITE_INGOT = RegistrySupport.registerItem("compressed_netherite_ingot", RegistrySupport.registryBaseItemSettings("compressed_netherite_ingot").rarity(Rarity.UNCOMMON));
        ModItems.COMPRESSED_REDSTONE = RegistrySupport.registerItem("compressed_redstone", RegistrySupport.registryBaseItemSettings("compressed_redstone").rarity(Rarity.UNCOMMON));
        ModItems.COMPRESSED_LAPIS_LAZULI = RegistrySupport.registerItem("compressed_lapis_lazuli", RegistrySupport.registryBaseItemSettings("compressed_lapis_lazuli").rarity(Rarity.UNCOMMON));
    }
}
