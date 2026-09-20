package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.leash.InvisibleUniversalLeadItem;
import com.kltyton.mob_battle.items.control.MutualAttackStickItem;
import com.kltyton.mob_battle.items.leash.UniversalLeadItem;
import com.kltyton.mob_battle.items.tool.MasterScepterItem;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

/**
 * 工具/道具类物品注册器。
 *
 * <p>按原始源码语句顺序注册第一个连续区块的前四项:互击棍、权杖、普通牵引绳与隐形牵引绳。
 * 重构前这些物品使用直接 {@code Registry.register} 注册,不写入 ITEMS/GENERATED_ITEMS,
 * 此处保持逐字兼容。</p>
 */
public final class ToolItemRegistrar {

    private ToolItemRegistrar() {
    }

    public static void init() {
        ModItems.MUTUAL_ATTACK_STICK = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "mutual_attack_stick"),
                new MutualAttackStickItem(new Item.Properties().stacksTo(1)
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "mutual_attack_stick")))));
        ModItems.MASTER_SCEPTER = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "master_scepter"),
                new MasterScepterItem(new Item.Properties().stacksTo(1).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "master_scepter")))));
        ModItems.UNIVERSAL_LEAD = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "universal_lead"),
                new UniversalLeadItem(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "universal_lead")))));
        ModItems.INVISIBLE_UNIVERSAL_LEAD = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "invisible_universal_lead"),
                new InvisibleUniversalLeadItem(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "invisible_universal_lead")))));
    }
}
