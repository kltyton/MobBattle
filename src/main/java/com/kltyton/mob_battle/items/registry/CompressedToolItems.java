package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import java.util.ArrayList;
import java.util.List;

/** 压缩采掘装备独立材料表，避免改变已发布压缩剑的耐久和伤害。 */
public final class CompressedToolItems {
    public record Entry(String tier, String chineseMaterial, Item item, Item ingredient, TagKey<Item> repairTag, boolean axe) {}
    private static final List<Entry> ENTRIES = new ArrayList<>();
    public static Item COPPER_AXE;
    public static Item DIAMOND_PICKAXE;
    public static Item DIAMOND_AXE;
    public static Item NETHERITE_PICKAXE;
    public static Item NETHERITE_AXE;

    private CompressedToolItems() {}

    public static void init() {
        pair("copper", "铜", ModItems.COMPRESSED_COPPER_INGOT, ToolMaterial.COPPER, BlockTags.INCORRECT_FOR_IRON_TOOL, 1000, 6, 7.5F, 18, 6, -3.1F);
        pair("iron", "铁", ModItems.COMPRESSED_IRON_INGOT, ToolMaterial.IRON, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1500, 9, 8.5F, 30, 12, -3.1F);
        pair("gold", "金", ModItems.COMPRESSED_GOLD_INGOT, ToolMaterial.GOLD, BlockTags.INCORRECT_FOR_IRON_TOOL, 1000, 6, 12, 35, 12, -3.0F);
        pair("diamond", "钻石", ModItems.COMPRESSED_DIAMOND, ToolMaterial.DIAMOND, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 5000, 12, 9, 78, 14, -3.0F);
        pair("netherite", "下界合金", ModItems.COMPRESSED_NETHERITE_INGOT, ToolMaterial.NETHERITE, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 9000, 15, 11, 130, 9, -3.0F);
    }

    private static void pair(String tier, String chinese, Item ingredient, ToolMaterial base,
                             net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> miningTier,
                             int durability, float pickDamage, float pickSpeed, float axeDamage, float axeSpeed, float attackSpeed) {
        TagKey<Item> repair = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "repairs/compressed_" + tier + "_tools"));
        ToolMaterial pickMaterial = new ToolMaterial(miningTier, durability, pickSpeed, 0, base.enchantmentValue(), repair);
        ToolMaterial axeMaterial = new ToolMaterial(miningTier, durability, axeSpeed, 0, base.enchantmentValue(), repair);
        String pickId = "compressed_" + tier + "_pickaxe";
        String axeId = "compressed_" + tier + "_axe";
        Item.Properties pickSettings = RegistrySupport.registryBaseItemSettings(pickId).pickaxe(pickMaterial, pickDamage - 1, -2.8F);
        Item.Properties axeSettings = RegistrySupport.registryBaseItemSettings(axeId);
        if (tier.equals("netherite")) {
            pickSettings.fireResistant();
            axeSettings.fireResistant();
        }
        Item pick = RegistrySupport.registerItem(pickId, pickSettings, false);
        Item axe = RegistrySupport.registerItem(axeId, new AxeItem(axeMaterial, axeDamage - 1, attackSpeed, axeSettings), false);
        ENTRIES.add(new Entry(tier, chinese, pick, ingredient, repair, false));
        ENTRIES.add(new Entry(tier, chinese, axe, ingredient, repair, true));
        if (tier.equals("copper")) COPPER_AXE = axe;
        if (tier.equals("diamond")) { DIAMOND_PICKAXE = pick; DIAMOND_AXE = axe; }
        if (tier.equals("netherite")) { NETHERITE_PICKAXE = pick; NETHERITE_AXE = axe; }
    }

    public static List<Entry> entries() { return List.copyOf(ENTRIES); }
}
