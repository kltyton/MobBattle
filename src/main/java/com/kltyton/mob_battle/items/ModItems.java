package com.kltyton.mob_battle.items;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.items.misc.*;
import com.kltyton.mob_battle.items.scroll.*;
import com.kltyton.mob_battle.items.tool.BaseAxe;
import com.kltyton.mob_battle.items.tool.BaseBow;
import com.kltyton.mob_battle.items.tool.BaseSword;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {
    // 直接声明物品
    public static MutualAttackStickItem MUTUAL_ATTACK_STICK;
    public static UniversalLeadItem UNIVERSAL_LEAD;
    public static FireballScrollItem FIREBALL_SCROLL;
    public static BigFireballScrollItem BIG_FIREBALL_SCROLL;
    public static SuperBigFireballScrollItem SUPER_BIG_FIREBALL_SCROLL;
    public static FiremanScrollItem FIREMAN_SCROLL;
    public static SlownessScrollItem SLOWNESS_SCROLL;
    public static FireWallScrollItem FIRE_WALL_SCROLL;
    public static HeartStoneItem HEART_STONE;
    // 刷怪蛋声明
    public static SpawnEggItem HIGHBIRD_BABY_SPAWN_EGG;
    public static SpawnEggItem HIGHBIRD_TEENAGE_SPAWN_EGG;
    public static SpawnEggItem HIGHBIRD_EGG_SPAWN_EGG;
    public static SpawnEggItem HIGHBIRD_ADULT_SPAWN_EGG;
    public static SpawnEggItem XU_SHENG_SPAWN_EGG;
    public static SpawnEggItem ARCHER_VILLAGER_SPAWN_EGG;
    public static SpawnEggItem WARRIOR_VILLAGER_SPAWN_EGG;
    public static SpawnEggItem BLUE_IRON_GOLEM_SPAWN_EGG;
    public static SpawnEggItem SUGAR_MAN_SCORPION_SPAWN_EGG;
    public static IncubationEggItem INCUBATION_EGG;
    //盔甲
    public static Item HELL_HELMET_1;
    public static Item HELL_CHESTPLATE_1;
    public static Item HELL_LEGGINGS_1;
    public static Item HELL_BOOTS_1;
    public static Item HELL_HELMET_2;
    public static Item HELL_CHESTPLATE_2;
    public static Item HELL_LEGGINGS_2;
    public static Item HELL_BOOTS_2;
    //工具以及武器
    public static Item METEORICORE_AXE;
    public static Item METEORICORE_BOW;
    public static Item METEORICORE_SWORD;

    public static void init() {
        BaseItems.init();
        //注册物品
        MUTUAL_ATTACK_STICK = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "mutual_attack_stick"),
                new MutualAttackStickItem(new Item.Settings().maxCount(1)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "mutual_attack_stick")))));

        UNIVERSAL_LEAD = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "universal_lead"),
                new UniversalLeadItem(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "universal_lead")))));
        FIREBALL_SCROLL = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "fireball_scroll"),
                new FireballScrollItem(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "fireball_scroll")))));
        BIG_FIREBALL_SCROLL = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "big_fireball_scroll"),
                new BigFireballScrollItem(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "big_fireball_scroll")))));
        SUPER_BIG_FIREBALL_SCROLL = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "super_big_fireball_scroll"),
                new SuperBigFireballScrollItem(new Item.Settings().useCooldown(7)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "super_big_fireball_scroll")))));

        FIREMAN_SCROLL = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "fireman_scroll"),
                new FiremanScrollItem(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "fireman_scroll")))));
        SLOWNESS_SCROLL = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "slowness_scroll"),
                new SlownessScrollItem(new Item.Settings().useCooldown(20)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "slowness_scroll")))));
        FIRE_WALL_SCROLL = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "fire_wall_scroll"),
                new FireWallScrollItem(new Item.Settings().useCooldown(35)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "fire_wall_scroll")))));

        HEART_STONE = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "heart_stone"),
                new HeartStoneItem(new Item.Settings().maxCount(3)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "heart_stone")))));

        HELL_HELMET_1 = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_helmet_1"),
                new Item(new Item.Settings().armor(ModMaterial.HELL_ARMOR_INSTANCE_1, EquipmentType.HELMET)
                                .maxDamage(0).maxCount(1)
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_helmet_1")
                                ))
                )
        );
        HELL_CHESTPLATE_1 = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_chestplate_1"),
                new Item(new Item.Settings().armor(ModMaterial.HELL_ARMOR_INSTANCE_1, EquipmentType.CHESTPLATE)
                                .maxDamage(0).maxCount(1)
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_chestplate_1")
                                ))
                )
        );
        HELL_LEGGINGS_1 = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_leggings_1"),
                new Item(new Item.Settings().armor(ModMaterial.HELL_ARMOR_INSTANCE_1, EquipmentType.LEGGINGS)
                                .maxDamage(0).maxCount(1)
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_leggings_1")
                                ))
                )
        );
        HELL_BOOTS_1 = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_boots_1"),
                new Item(new Item.Settings().armor(ModMaterial.HELL_ARMOR_INSTANCE_1, EquipmentType.BOOTS)
                                .maxDamage(0).maxCount(1)
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_boots_1")
                                ))
                )
        );
        HELL_HELMET_2 = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_helmet_2"),
                new Item(new Item.Settings().armor(ModMaterial.HELL_ARMOR_INSTANCE_2, EquipmentType.HELMET)
                                .maxDamage(0).maxCount(1)
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_helmet_2")
                                ))
                )
        );
        HELL_CHESTPLATE_2 = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_chestplate_2"),
                new Item(new Item.Settings().armor(ModMaterial.HELL_ARMOR_INSTANCE_2, EquipmentType.CHESTPLATE)
                                .maxDamage(0).maxCount(1)
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_chestplate_2")
                                ))
                )
        );
        HELL_LEGGINGS_2 = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_leggings_2"),
                new Item(new Item.Settings().armor(ModMaterial.HELL_ARMOR_INSTANCE_2, EquipmentType.LEGGINGS)
                                .maxDamage(0).maxCount(1)
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_leggings_2")
                                ))
                )
        );
        HELL_BOOTS_2 = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_boots_2"),
                new Item(new Item.Settings().armor(ModMaterial.HELL_ARMOR_INSTANCE_2, EquipmentType.BOOTS)
                                .maxDamage(0).maxCount(1)
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "hell_boots_2")
                                ))
                )
        );
        // 注册工具和武器
        METEORICORE_AXE = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "meteoricore_axe"),
                new BaseAxe(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "meteoricore_axe")
                        ))
                )
        );

        METEORICORE_BOW = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "meteoricore_bow"),
                new BaseBow(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "meteoricore_bow")
                        ))
                )
        );

        METEORICORE_SWORD = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "meteoricore_sword"),
                new BaseSword(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "meteoricore_sword")
                        ))
                )
        );
        // 注册高脚鸟刷怪蛋
        HIGHBIRD_BABY_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "highbird_baby_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.HIGHBIRD_BABY,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "highbird_baby_spawn_egg")
                                ))
                )
        );

        HIGHBIRD_TEENAGE_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "highbird_teenage_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.HIGHBIRD_TEENAGE,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "highbird_teenage_spawn_egg")
                                ))
                )
        );
        HIGHBIRD_ADULT_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "highbird_adulthood_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.HIGHBIRD_ADULTHOOD,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "highbird_adulthood_spawn_egg")
                                ))
                )
        );

        HIGHBIRD_EGG_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "highbird_egg_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.HIGHBIRD_EGG,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "highbird_egg_spawn_egg")
                                ))
                )
        );

        XU_SHENG_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "xun_sheng_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.XUN_SHENG,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "xun_sheng_spawn_egg")
                                ))
                )
        );
        ARCHER_VILLAGER_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "archer_villager_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.ARCHER_VILLAGER,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "archer_villager_spawn_egg")
                                ))
                )
        );
        WARRIOR_VILLAGER_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "warrior_villager_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.WARRIOR_VILLAGER,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "warrior_villager_spawn_egg")
                                ))
                )
        );
        BLUE_IRON_GOLEM_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "blue_iron_golem_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.BLUE_IRON_GOLEM,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "blue_iron_golem_spawn_egg")
                                ))
                )
        );
        SUGAR_MAN_SCORPION_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "sugar_man_scorpion_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.SUGAR_MAN_SCORPION,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "sugar_man_scorpion_spawn_egg")
                                ))
                )
        );

        INCUBATION_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "incubation_egg"),
                new IncubationEggItem(
                        ModEntities.HIGHBIRD_EGG,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "incubation_egg")
                                ))
                )
        );
    }
}
