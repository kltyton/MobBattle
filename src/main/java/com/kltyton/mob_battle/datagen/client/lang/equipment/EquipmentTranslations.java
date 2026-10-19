package com.kltyton.mob_battle.datagen.client.lang.equipment;

import com.kltyton.mob_battle.items.registry.CompressedToolItems;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder;
import net.minecraft.core.registries.BuiltInRegistries;

/** 压缩采掘工具和“有”套装共用的双语装备文案。 */
public final class EquipmentTranslations {
    private EquipmentTranslations() {}

    public static void add(TranslationBuilder builder, boolean chinese) {
        for (String slot : new String[]{"helmet", "chestplate", "leggings", "boots"}) {
            builder.add("item.mob_battle.presence_" + slot, chinese ? "有" : "Presence");
        }
        for (var tool : CompressedToolItems.entries()) {
            String material = switch (tool.tier()) {
                case "copper" -> "Copper";
                case "iron" -> "Iron";
                case "gold" -> "Gold";
                case "diamond" -> "Diamond";
                case "netherite" -> "Netherite";
                default -> throw new IllegalArgumentException("Unknown compressed material: " + tool.tier());
            };
            builder.add(BuiltInRegistries.ITEM.getKey(tool.item()).toLanguageKey("item"),
                    chinese ? "压缩" + tool.chineseMaterial() + (tool.axe() ? "斧" : "镐")
                            : "Compressed " + material + (tool.axe() ? " Axe" : " Pickaxe"));
        }
        builder.add("entity.mob_battle.living_block", chinese ? "活方块" : "Living Block");
        builder.add("entity.mob_battle.presence_orbit_block", chinese ? "环绕方块" : "Orbiting Block");
        builder.add("entity.mob_battle.presence_block_projectile", chinese ? "飞掷方块" : "Thrown Block");
        builder.add("hud.mob_battle.presence_mode", chinese ? "模式：%s | 环绕：%s/5" : "Mode: %s | Blocks: %s/5");
        builder.add("hud.mob_battle.presence_aggressive", chinese ? "狂暴" : "Aggressive");
        builder.add("hud.mob_battle.presence_neutral", chinese ? "中立" : "Neutral");
        builder.add("hud.mob_battle.presence_cooldown", chinese ? "C：%s秒 | 潜行+Z：唤醒方块" : "C: %ss | Sneak+Z: awaken block");
        builder.add("message.mob_battle.presence_hold_block", chinese ? "主手持有方块，再潜行按 Z 唤醒" : "Hold a block in your main hand, then sneak and press Z");
        builder.add("message.mob_battle.presence_unbreakable", chinese ? "无法唤醒不可破坏的方块" : "Unbreakable blocks cannot be awakened");
        builder.add("message.mob_battle.presence_health_needed", chinese ? "需要预留 %s 点最大生命，并至少保留 1 点" : "Reserve %s maximum health while keeping at least 1");
        builder.add("message.mob_battle.presence_no_space", chinese ? "前方没有足够空间唤醒方块" : "There is not enough space ahead to awaken the block");
    }
}
