package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.scroll.BigFireballScrollItem;
import com.kltyton.mob_battle.items.scroll.FireWallScrollItem;
import com.kltyton.mob_battle.items.scroll.FireballScrollItem;
import com.kltyton.mob_battle.items.scroll.FiremanScrollItem;
import com.kltyton.mob_battle.items.scroll.PurificationScrollItem;
import com.kltyton.mob_battle.items.scroll.SkullMageScrollItem;
import com.kltyton.mob_battle.items.scroll.SlownessScrollItem;
import com.kltyton.mob_battle.items.scroll.SuperBigFireballScrollItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

/**
 * 卷轴类物品注册器。
 *
 * <p>按原始源码语句顺序注册第一个连续区块的卷轴段:火焰弹、大火球、超级大火球、火人、
 * 迟缓、火墙、净化与骷髅法师卷轴(共 8 项)。前六项与后两项在重构前分别使用直接
 * {@code Registry.register} 与 {@code RegistrySupport.registerItem},保持逐字兼容。</p>
 */
public final class ScrollItemRegistrar {

    private ScrollItemRegistrar() {
    }

    public static void init() {
        ModItems.FIREBALL_SCROLL = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "fireball_scroll"),
                new FireballScrollItem(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "fireball_scroll")))));
        ModItems.BIG_FIREBALL_SCROLL = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "big_fireball_scroll"),
                new BigFireballScrollItem(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "big_fireball_scroll")))));
        ModItems.SUPER_BIG_FIREBALL_SCROLL = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "super_big_fireball_scroll"),
                new SuperBigFireballScrollItem(new Item.Properties().useCooldown(7)
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "super_big_fireball_scroll")))));

        ModItems.FIREMAN_SCROLL = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "fireman_scroll"),
                new FiremanScrollItem(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "fireman_scroll")))));
        ModItems.SLOWNESS_SCROLL = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "slowness_scroll"),
                new SlownessScrollItem(new Item.Properties().useCooldown(20)
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "slowness_scroll")))));
        ModItems.FIRE_WALL_SCROLL = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "fire_wall_scroll"),
                new FireWallScrollItem(new Item.Properties().useCooldown(35)
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "fire_wall_scroll")))));
        ModItems.PURIFICATION_SCROLL = RegistrySupport.registerItem("purification_scroll",
                new PurificationScrollItem(RegistrySupport.registryBaseItemSettings("purification_scroll")
                        .useCooldown(75))
        );
        ModItems.SKULL_MAGE_SCROLL = RegistrySupport.registerItem("skull_mage_scroll",
                new SkullMageScrollItem(RegistrySupport.registryBaseItemSettings("skull_mage_scroll")
                        .useCooldown(20))
        );
    }
}
