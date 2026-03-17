package com.kltyton.mob_battle.items;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.components.ModConsumableComponents;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.items.armor.ModBaseArmorItem;
import com.kltyton.mob_battle.items.food.MagmaLobsterItemMod;
import com.kltyton.mob_battle.items.food.ThousandBlossomedImmortalFruit;
import com.kltyton.mob_battle.items.misc.*;
import com.kltyton.mob_battle.items.scroll.*;
import com.kltyton.mob_battle.items.tool.BaseAxe;
import com.kltyton.mob_battle.items.tool.MasterScepterItem;
import com.kltyton.mob_battle.items.tool.backpack.BackpackItem;
import com.kltyton.mob_battle.items.tool.bow.IceBowItem;
import com.kltyton.mob_battle.items.tool.bow.MeteoricoreBowItem;
import com.kltyton.mob_battle.items.tool.irongold.IronGoldSword;
import com.kltyton.mob_battle.items.tool.meteorite.MeteoriteSword;
import com.kltyton.mob_battle.items.tool.piglin.PiglinCannonItem;
import com.kltyton.mob_battle.items.tool.snipe.VsSnipe;
import com.kltyton.mob_battle.items.tool.sword.FineKnifeItem;
import com.kltyton.mob_battle.items.tool.sword.zijin.ZiJinSword;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Unit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModItems {
    public static final Map<String, Item> ITEMS = new HashMap<>();
    public static final Map<String, SpawnEggItem> SPAWN_EGG_ITEMS = new HashMap<>();
    public static final Map<String, Item> GENERATED_ITEMS = new HashMap<>();


    // 杂项物品
    public static MutualAttackStickItem MUTUAL_ATTACK_STICK;
    public static MasterScepterItem MASTER_SCEPTER;
    public static UniversalLeadItem UNIVERSAL_LEAD;
    public static InvisibleUniversalLeadItem INVISIBLE_UNIVERSAL_LEAD;
    public static FireballScrollItem FIREBALL_SCROLL;
    public static BigFireballScrollItem BIG_FIREBALL_SCROLL;
    public static SuperBigFireballScrollItem SUPER_BIG_FIREBALL_SCROLL;
    public static FiremanScrollItem FIREMAN_SCROLL;
    public static SlownessScrollItem SLOWNESS_SCROLL;
    public static FireWallScrollItem FIRE_WALL_SCROLL;
    public static SummonVexBookItem WARLOCK_BOOK;
    public static SummonVexBookItem GRAND_SUMMON_BOOK;
    public static GuardianSealItem GUARDIAN_SEAL;
    public static GuardianSealItem FILLING_SEAL;
    public static FineKnifeItem FINE_KNIFE;
    public static BackpackItem SMALL_BACKPACK;
    public static BackpackItem LARGE_BACKPACK;
    public static Item ICE_ARROW_ITEM;

    public static HeartStoneItem HEART_STONE;
    public static ThousandBlossomedImmortalFruit THOUSAND_BLOSSOMED_IMMORTAL_FRUIT;
    public static Item LOBSTER_MAIN_COURSE;
    public static Item COOKED_HIGHBIRD_EGG;

    // 压缩材料
    public static Item COMPRESSED_COPPER_INGOT;
    public static Item COMPRESSED_IRON_INGOT;
    public static Item COMPRESSED_GOLD_INGOT;
    public static Item COMPRESSED_DIAMOND;
    public static Item COMPRESSED_NETHERITE_INGOT;
    public static Item COMPRESSED_REDSTONE;
    public static Item COMPRESSED_LAPIS_LAZULI;

    // 龙虾系列
    public static Item LOBSTER;
    public static Item MAGMA_LOBSTER;
    public static Item OBSIDIAN_LOBSTER;
    public static Item BURST_OBSIDIAN_LOBSTER;

    // 弓
    public static Item ICE_BOW;
    public static Item PIGLIN_CANNON;

    // 刷怪蛋
    public static SpawnEggItem HIGHBIRD_BABY_SPAWN_EGG;
    public static SpawnEggItem HIGHBIRD_TEENAGE_SPAWN_EGG;
    public static SpawnEggItem HIGHBIRD_EGG_SPAWN_EGG;
    public static SpawnEggItem HIGHBIRD_ADULTHOOD_SPAWN_EGG;
    public static SpawnEggItem XUN_SHENG_SPAWN_EGG;
    public static SpawnEggItem DEEP_CREATURE_SPAWN_EGG;
    public static SpawnEggItem WITHER_SKELETON_KING_SPAWN_EGG;
    public static SpawnEggItem VILLAGER_KING_SPAWN_EGG;
    public static SpawnEggItem ARCHER_VILLAGER_SPAWN_EGG;
    public static SpawnEggItem WARRIOR_VILLAGER_SPAWN_EGG;
    public static SpawnEggItem MILITIA_ARCHER_VILLAGER_SPAWN_EGG;
    public static SpawnEggItem MILITIA_WARRIOR_VILLAGER_SPAWN_EGG;
    public static SpawnEggItem BLUE_IRON_GOLEM_SPAWN_EGG;
    public static SpawnEggItem SUGAR_MAN_SCORPION_SPAWN_EGG;
    public static SpawnEggItem IRON_GOLEM_SPAWN_EGG;
    public static SpawnEggItem LITTLE_PERSON_CIVILIAN_SPAWN_EGG;
    public static SpawnEggItem LITTLE_PERSON_MILITIA_SPAWN_EGG;
    public static SpawnEggItem LITTLE_PERSON_ARCHER_SPAWN_EGG;
    public static SpawnEggItem LITTLE_PERSON_GIANT_SPAWN_EGG;
    public static SpawnEggItem LITTLE_PERSON_GUARD_SPAWN_EGG;
    public static SpawnEggItem LITTLE_PERSON_KING_SPAWN_EGG;
    public static SpawnEggItem SKULL_KING_SPAWN_EGG;
    public static SpawnEggItem SKULL_ARCHER_SPAWN_EGG;
    public static SpawnEggItem SKULL_WARRIOR_SPAWN_EGG;
    public static SpawnEggItem SKULL_MAGE_SPAWN_EGG;
    public static SpawnEggItem VOID_CELL_SPAWN_EGG;
    public static SpawnEggItem YOUNG_MIN_SPAWN_EGG;
    public static SpawnEggItem HIDDEN_EYE_SPAWN_EGG;
    public static SpawnEggItem VINDICATOR_GENERAL_SPAWN_EGG;
    public static SpawnEggItem HULKBUSTER_SPAWN_EGG;
    public static SpawnEggItem SILENCE_PHANTOM_SPAWN_EGG;
    public static SpawnEggItem COAL_SILVERFISH_SPAWN_EGG;

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
    public static Item IRON_GOLD_HELMET;
    public static Item IRON_GOLD_CHESTPLATE;
    public static Item IRON_GOLD_LEGGINGS;
    public static Item IRON_GOLD_BOOTS;
    // 翠钻合金套
    public static Item EMERALD_DIAMOND_HELMET;
    public static Item EMERALD_DIAMOND_CHESTPLATE;
    public static Item EMERALD_DIAMOND_LEGGINGS;
    public static Item EMERALD_DIAMOND_BOOTS;

    public static ModBaseArmorItem ECREDCULTIST_HELMET;
    public static ModBaseArmorItem ECREDCULTIST_CHESTPLATE;
    public static ModBaseArmorItem ECREDCULTIST_LEGGINGS;
    public static ModBaseArmorItem ECREDCULTIST_BOOTS;

    public static Item ZIJIN_HELMET;
    public static Item ZIJIN_CHESTPLATE;
    public static Item ZIJIN_LEGGINGS;
    public static Item ZIJIN_BOOTS;
    //工具以及武器
    public static Item METEORICORE_AXE;
    public static Item METEORICORE_BOW;
    public static Item METEORICORE_SWORD;
    public static Item IRON_GOLD_SWORD;
    public static Item EMERALD_DIAMOND_SWORD;
    public static Item ZIJIN_SWORD;
    public static Item IRON_GOLD_SWORD_DAMAGED;

    public static VsSnipe VS_SNIPE;
    public static Item TRAIN_BULLET;
    public static Item AREA_GRAVITY_DEVICE_ITEM;
    public static Item WIRE;
    public static Item ELECTRONIC_COMPONENTS;

    public static CardiotonicInjectionItem CARDIOTONIC_INJECTION;

    public static void init() {
        BaseItems.init();
        //注册物品
        MUTUAL_ATTACK_STICK = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "mutual_attack_stick"),
                new MutualAttackStickItem(new Item.Settings().maxCount(1)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "mutual_attack_stick")))));
        MASTER_SCEPTER = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "master_scepter"),
                new MasterScepterItem(new Item.Settings().maxCount(1).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "master_scepter")))));
        UNIVERSAL_LEAD = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "universal_lead"),
                new UniversalLeadItem(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "universal_lead")))));
        INVISIBLE_UNIVERSAL_LEAD = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "invisible_universal_lead"),
                new InvisibleUniversalLeadItem(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "invisible_universal_lead")))));
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
        //.useRemainder(THOUSAND_BLOSSOMED_IMMORTAL_FRUIT)
        THOUSAND_BLOSSOMED_IMMORTAL_FRUIT = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "thousand_blossomed_immortal_fruit"),
                new ThousandBlossomedImmortalFruit(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.3F).alwaysEdible().build()).useCooldown(60)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "thousand_blossomed_immortal_fruit")))));
        LOBSTER_MAIN_COURSE = registerItem("lobster_main_course",
                registryBaseItemSettings("lobster_main_course").food(
                        new FoodComponent.Builder().nutrition(10).saturationModifier(0.3F).build()
                )
        );
        COMPRESSED_COPPER_INGOT = registerItem("compressed_copper_ingot");
        COMPRESSED_IRON_INGOT = registerItem("compressed_iron_ingot");
        COMPRESSED_GOLD_INGOT = registerItem("compressed_gold_ingot");
        COMPRESSED_DIAMOND = registerItem("compressed_diamond");
        COMPRESSED_NETHERITE_INGOT = registerItem("compressed_netherite_ingot");
        COMPRESSED_REDSTONE = registerItem("compressed_redstone");
        COMPRESSED_LAPIS_LAZULI = registerItem("compressed_lapis_lazuli");

        // 龙虾系列
        LOBSTER = registerItem("lobster",
                registryBaseItemSettings("lobster").food(
                        new FoodComponent.Builder()
                                .nutrition(2)
                                .saturationModifier(4.0F)
                                .alwaysEdible()
                                .build(),
                        ModConsumableComponents.LOBSTER
                )
        );
        // 岩浆龙虾：
        // 1. 吃下着火
        // 2. 扔到水里变黑曜石龙虾并播放冷却音效
        MAGMA_LOBSTER = registerItem("magma_lobster",
                new MagmaLobsterItemMod(
                        registryBaseItemSettings("magma_lobster").food(
                                new FoodComponent.Builder()
                                        .nutrition(3)
                                        .saturationModifier(4.0F)
                                        .alwaysEdible()
                                        .build(),
                                ModConsumableComponents.MAGMA_LOBSTER
                        )
                )
        );

        // 黑曜石龙虾：
        // 右键当盾牌，1500耐久，不能附魔
        OBSIDIAN_LOBSTER = registerItem("obsidian_lobster",
                new ShieldItem(
                        registryBaseItemSettings("obsidian_lobster")
                                .maxCount(1)
                                .maxDamage(1500)
                                .component(DataComponentTypes.BLOCKS_ATTACKS,
                                        new BlocksAttacksComponent(
                                                0.25F,
                                                1.0F,
                                                List.of(new BlocksAttacksComponent.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                                                new BlocksAttacksComponent.ItemDamage(3.0F, 1.0F, 1.0F),
                                                Optional.of(DamageTypeTags.BYPASSES_SHIELD),
                                                Optional.of(SoundEvents.ITEM_SHIELD_BLOCK),
                                                Optional.of(SoundEvents.ITEM_SHIELD_BREAK)
                                        )
                                )
                                .component(DataComponentTypes.BREAK_SOUND, SoundEvents.ITEM_SHIELD_BREAK)
                )
        );

        // 爆开的黑曜石龙虾
        BURST_OBSIDIAN_LOBSTER = registerItem("burst_obsidian_lobster",
                registryBaseItemSettings("burst_obsidian_lobster").food(
                        new FoodComponent.Builder()
                                .nutrition(6)
                                .saturationModifier(8.0F)
                                .alwaysEdible()
                                .build(),
                        ModConsumableComponents.BURST_OBSIDIAN_LOBSTER
                )
        );

        // 寒冰弓
        ICE_BOW = registerItem("ice_bow",
                new IceBowItem(
                        registryBaseItemSettings("ice_bow")
                                .rarity(Rarity.EPIC)
                                .maxDamage(25000)
                                .maxCount(1)
                ),
                 false
        );

        COOKED_HIGHBIRD_EGG = registerItem("cooked_highbird_egg",
                registryBaseItemSettings("cooked_highbird_egg").food(
                        new FoodComponent.Builder().nutrition(20).saturationModifier(20).alwaysEdible().build(),
                        ModConsumableComponents.COOKED_HIGHBIRD_EGG
                )
        );

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

        ECREDCULTIST_HELMET = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "ecredcultist_helmet"),
                new ModBaseArmorItem(new Item.Settings().armor(ModMaterial.ECREDCULTIST_INSTANCE, EquipmentType.HELMET)
                        .maxDamage(1).maxCount(1).component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "ecredcultist_helmet")
                        )),
                        ModMaterial.ECREDCULTIST_INSTANCE
                )
        );
        ECREDCULTIST_CHESTPLATE = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "ecredcultist_chestplate"),
                new ModBaseArmorItem(new Item.Settings().armor(ModMaterial.ECREDCULTIST_INSTANCE, EquipmentType.CHESTPLATE)
                        .maxDamage(1).maxCount(1).component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "ecredcultist_chestplate")
                        )),
                        ModMaterial.ECREDCULTIST_INSTANCE
                )
        );
        ECREDCULTIST_LEGGINGS = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "ecredcultist_leggings"),
                new ModBaseArmorItem(new Item.Settings().armor(ModMaterial.ECREDCULTIST_INSTANCE, EquipmentType.LEGGINGS)
                        .maxDamage(1).maxCount(1).component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "ecredcultist_leggings")
                        )),
                        ModMaterial.ECREDCULTIST_INSTANCE
                )
        );
        ECREDCULTIST_BOOTS = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "ecredcultist_boots"),
                new ModBaseArmorItem(
                        new Item.Settings().armor(ModMaterial.ECREDCULTIST_INSTANCE, EquipmentType.BOOTS)
                        .maxDamage(1).maxCount(1).component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "ecredcultist_boots")
                        )),
                        ModMaterial.ECREDCULTIST_INSTANCE
                )
        );
        IRON_GOLD_HELMET = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_helmet"),
                new Item(new Item.Settings().armor(ModMaterial.IRON_GOLD_INSTANCE, EquipmentType.HELMET)
                                .maxDamage(512).maxCount(1).component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_helmet")
                                ))
                )
        );
        IRON_GOLD_CHESTPLATE = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_chestplate"),
                new Item(new Item.Settings().armor(ModMaterial.IRON_GOLD_INSTANCE, EquipmentType.CHESTPLATE)
                                .maxDamage(512).maxCount(1).component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_chestplate")
                                ))
                )
        );
        IRON_GOLD_LEGGINGS = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_leggings"),
                new Item(new Item.Settings().armor(ModMaterial.IRON_GOLD_INSTANCE, EquipmentType.LEGGINGS)
                                .maxDamage(512).maxCount(1).component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_leggings")
                                ))
                )
        );
        IRON_GOLD_BOOTS = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_boots"),
                new Item(new Item.Settings().armor(ModMaterial.IRON_GOLD_INSTANCE, EquipmentType.BOOTS)
                                .maxDamage(512).maxCount(1).component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_boots")
                                ))
                )
        );
        // 翠钻合金套
        EMERALD_DIAMOND_HELMET = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "emerald_diamond_helmet"),
                new Item(new Item.Settings()
                        .armor(ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE, EquipmentType.HELMET)
                        .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                        .maxCount(1)
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "emerald_diamond_helmet")))
                )
        );

        EMERALD_DIAMOND_CHESTPLATE = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "emerald_diamond_chestplate"),
                new Item(new Item.Settings()
                        .armor(ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE, EquipmentType.CHESTPLATE)
                        .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                        .maxCount(1)
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "emerald_diamond_chestplate")))
                )
        );

        EMERALD_DIAMOND_LEGGINGS = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "emerald_diamond_leggings"),
                new Item(new Item.Settings()
                        .armor(ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE, EquipmentType.LEGGINGS)
                        .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                        .maxCount(1)
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "emerald_diamond_leggings")))
                )
        );

        EMERALD_DIAMOND_BOOTS = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "emerald_diamond_boots"),
                new Item(new Item.Settings()
                        .armor(ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE, EquipmentType.BOOTS)
                        .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                        .maxCount(1)
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "emerald_diamond_boots")))
                )
        );

        ZIJIN_HELMET = registerItem(
                "zijin_helmet",
                registryBaseItemSettings("zijin_helmet")
                        .armor(ModMaterial.ZIJIN_ARMOR_INSTANCE, EquipmentType.HELMET)
                        .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                        .maxCount(1)
        );

        ZIJIN_CHESTPLATE = registerItem(
                "zijin_chestplate",
                registryBaseItemSettings("zijin_chestplate")
                        .armor(ModMaterial.ZIJIN_ARMOR_INSTANCE, EquipmentType.CHESTPLATE)
                        .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                        .maxCount(1)
        );

        ZIJIN_LEGGINGS = registerItem(
                "zijin_leggings",
                registryBaseItemSettings("zijin_leggings")
                        .armor(ModMaterial.ZIJIN_ARMOR_INSTANCE, EquipmentType.LEGGINGS)
                        .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                        .maxCount(1)
        );

        ZIJIN_BOOTS = registerItem("zijin_boots",
                registryBaseItemSettings("zijin_boots")
                        .armor(ModMaterial.ZIJIN_ARMOR_INSTANCE, EquipmentType.BOOTS)
                        .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                        .maxCount(1)
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
                new MeteoricoreBowItem(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "meteoricore_bow")
                        ))
                )
        );

        METEORICORE_SWORD = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "meteoricore_sword"),
                new MeteoriteSword(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "meteoricore_sword")
                        ))
                )
        );
        IRON_GOLD_SWORD = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_sword"),
                new IronGoldSword(new Item.Settings()
                        .sword(ModMaterial.IRON_GOLD_TOOL_MATERIAL, 84f, 1024)
                        .maxCount(1)
                        .component(DataComponentTypes.DEATH_PROTECTION, DeathProtectionComponent.TOTEM_OF_UNDYING)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_sword")
                        ))
                )
        );

        IRON_GOLD_SWORD_DAMAGED = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_sword_damaged"),
                new IronGoldSword(new Item.Settings()
                        .sword(ModMaterial.IRON_GOLD_TOOL_MATERIAL, 0f,-2.2f)
                        .maxCount(1)
                        .maxDamage(0)
                        .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_sword_damaged")
                        ))
                )
        );

        EMERALD_DIAMOND_SWORD = registerItem("emerald_diamond_sword",
                registryBaseItemSettings("emerald_diamond_sword")
                        .sword(ModMaterial.EMERALD_DIAMOND_ALLOY_TOOL_MATERIAL, 149, -2f)
                        .maxCount(1)
                        .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE),
                false
        );

        ZIJIN_SWORD = registerItem("zijin_sword",
                new ZiJinSword(
                        registryBaseItemSettings("zijin_sword")
                        .sword(ModMaterial.ZIJIN_ARMOR_TOOL_MATERIAL, 99, 0f)
                        .maxCount(1)
                        .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                ),
                false
        );

        VS_SNIPE = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "vs_snipe"),
                new VsSnipe(new Item.Settings()
                        .maxCount(1).maxDamage(465).component(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT).enchantable(1)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "vs_snipe")
                        ))
                )
        );
        SKULL_KING_SPAWN_EGG = registerSpawnEggItem(ModEntities.SKULL_KING, "skull_king_spawn_egg");
        SKULL_ARCHER_SPAWN_EGG = registerSpawnEggItem(ModEntities.SKULL_ARCHER, "skull_archer_spawn_egg");
        SKULL_WARRIOR_SPAWN_EGG = registerSpawnEggItem(ModEntities.SKULL_WARRIOR, "skull_warrior_spawn_egg");
        SKULL_MAGE_SPAWN_EGG = registerSpawnEggItem(ModEntities.SKULL_MAGE, "skull_mage_spawn_egg");
        VOID_CELL_SPAWN_EGG = registerSpawnEggItem(ModEntities.VOID_CELL, "void_cell_spawn_egg");
        YOUNG_MIN_SPAWN_EGG = registerSpawnEggItem(ModEntities.YOUNG_MIN, "young_min_spawn_egg");
        HIDDEN_EYE_SPAWN_EGG = registerSpawnEggItem(ModEntities.HIDDEN_EYE, "hidden_eye_spawn_egg");
        // 注册高脚鸟刷怪蛋
        HIGHBIRD_BABY_SPAWN_EGG = registerSpawnEggItem(ModEntities.HIGHBIRD_BABY, "highbird_baby_spawn_egg");
        HIGHBIRD_TEENAGE_SPAWN_EGG = registerSpawnEggItem(ModEntities.HIGHBIRD_TEENAGE, "highbird_teenage_spawn_egg");
        HIGHBIRD_ADULTHOOD_SPAWN_EGG = registerSpawnEggItem(ModEntities.HIGHBIRD_ADULTHOOD, "highbird_adulthood_spawn_egg");
        HIGHBIRD_EGG_SPAWN_EGG = registerSpawnEggItem(ModEntities.HIGHBIRD_EGG, "highbird_egg_spawn_egg");
        XUN_SHENG_SPAWN_EGG = registerSpawnEggItem(ModEntities.XUN_SHENG, "xun_sheng_spawn_egg");
        DEEP_CREATURE_SPAWN_EGG = registerSpawnEggItem(ModEntities.DEEP_CREATURE, "deep_creature_spawn_egg");
        WITHER_SKELETON_KING_SPAWN_EGG = registerSpawnEggItem(ModEntities.WITHER_SKELETON_KING, "wither_skeleton_king_spawn_egg");
        MILITIA_ARCHER_VILLAGER_SPAWN_EGG = registerSpawnEggItem(ModEntities.MILITIA_ARCHER_VILLAGER, "militia_archer_villager_spawn_egg");
        MILITIA_WARRIOR_VILLAGER_SPAWN_EGG = registerSpawnEggItem(ModEntities.MILITIA_WARRIOR_VILLAGER, "militia_warrior_villager_spawn_egg");
        ARCHER_VILLAGER_SPAWN_EGG = registerSpawnEggItem(ModEntities.ARCHER_VILLAGER, "archer_villager_spawn_egg");
        WARRIOR_VILLAGER_SPAWN_EGG = registerSpawnEggItem(ModEntities.WARRIOR_VILLAGER, "warrior_villager_spawn_egg");
        BLUE_IRON_GOLEM_SPAWN_EGG = registerSpawnEggItem(ModEntities.BLUE_IRON_GOLEM, "blue_iron_golem_spawn_egg");
        SUGAR_MAN_SCORPION_SPAWN_EGG = registerSpawnEggItem(ModEntities.SUGAR_MAN_SCORPION, "sugar_man_scorpion_spawn_egg");
        IRON_GOLEM_SPAWN_EGG = registerSpawnEggItem(ModEntities.VILLAGER_IRON_GOLEM_ENTITY, "iron_golem_spawn_egg");
        LITTLE_PERSON_CIVILIAN_SPAWN_EGG = registerSpawnEggItem(ModEntities.LITTLE_PERSON_CIVILIAN, "little_person_civilian_spawn_egg");
        LITTLE_PERSON_MILITIA_SPAWN_EGG = registerSpawnEggItem(ModEntities.LITTLE_PERSON_MILITIA, "little_person_militia_spawn_egg");
        LITTLE_PERSON_ARCHER_SPAWN_EGG = registerSpawnEggItem(ModEntities.LITTLE_PERSON_ARCHER, "little_person_archer_spawn_egg");
        LITTLE_PERSON_GIANT_SPAWN_EGG = registerSpawnEggItem(ModEntities.LITTLE_PERSON_GIANT, "little_person_giant_spawn_egg");
        LITTLE_PERSON_GUARD_SPAWN_EGG = registerSpawnEggItem(ModEntities.LITTLE_PERSON_GUARD, "little_person_guard_spawn_egg");
        LITTLE_PERSON_KING_SPAWN_EGG = registerSpawnEggItem(ModEntities.LITTLE_PERSON_KING, "little_person_king_spawn_egg");
        VILLAGER_KING_SPAWN_EGG = registerSpawnEggItem(ModEntities.VILLAGER_KING_ENTITY, "villager_king_spawn_egg");
        VINDICATOR_GENERAL_SPAWN_EGG = registerSpawnEggItem(ModEntities.VINDICATOR_GENERAL, "vindicator_general_spawn_egg");

        HULKBUSTER_SPAWN_EGG = registerSpawnEggItem(ModEntities.HULKBUSTER, "hulkbuster_spawn_egg");
        SILENCE_PHANTOM_SPAWN_EGG = registerSpawnEggItem(ModEntities.SILENCE_PHANTOM, "silence_phantom_spawn_egg");
        COAL_SILVERFISH_SPAWN_EGG = registerSpawnEggItem(ModEntities.COAL_SILVERFISH, "coal_silverfish_spawn_egg");

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

        WARLOCK_BOOK = registerItem("warlock_book", new SummonVexBookItem(
                registryBaseItemSettings("warlock_book")
                        .useCooldown(15)
                        .maxDamage(150)
                        .maxCount(1),
                3, 0, 0)
        );

        GRAND_SUMMON_BOOK = registerItem("grand_summon_book", new SummonVexBookItem(
                registryBaseItemSettings("grand_summon_book")
                        .rarity(Rarity.RARE)
                        .useCooldown(20)
                        .maxDamage(150)
                        .maxCount(1),
                10, 5, 10)
        );

        GUARDIAN_SEAL = registerItem("guardian_seal", new GuardianSealItem(
                registryBaseItemSettings("guardian_seal")
                        .useCooldown(2000)
                        .maxCount(1),
                false)
        );

        FILLING_SEAL = registerItem("filling_seal", new GuardianSealItem(
                registryBaseItemSettings("filling_seal")
                        .maxCount(1),
                true)
        );

        FINE_KNIFE = registerItem("fine_knife", new FineKnifeItem(
                registryBaseItemSettings("fine_knife")
                        .maxDamage(200)
                        .sword(ToolMaterial.IRON, 0, 0)
                        .maxCount(1)),
                false
        );

        SMALL_BACKPACK = registerItem("small_backpack", new BackpackItem(
                registryBaseItemSettings("small_backpack")
                        .rarity(Rarity.UNCOMMON)
                        .maxCount(1),
                false)
        );

        LARGE_BACKPACK = registerItem("large_backpack", new BackpackItem(
                registryBaseItemSettings("large_backpack")
                        .rarity(Rarity.RARE)
                        .maxCount(1),
                true)
        );

        CARDIOTONIC_INJECTION = registerItem("cardiotonic_injection", new CardiotonicInjectionItem(
                registryBaseItemSettings("cardiotonic_injection")
                        .maxCount(1)),
                false
        );

        ICE_ARROW_ITEM = registerItem("ice_arrow_item", registryBaseItemSettings("ice_arrow_item").maxCount(64));
        TRAIN_BULLET = registerItem("train_bullet", registryBaseItemSettings("train_bullet").maxCount(64));
        AREA_GRAVITY_DEVICE_ITEM = registerItem("area_gravity_device_item", new AreaGravityDeviceItem(
                registryBaseItemSettings("area_gravity_device_item")
                        .maxCount(1)
                        .useCooldown(70)
                        .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)

        ));
        PIGLIN_CANNON = registerItem("piglin_cannon",
                new PiglinCannonItem(
                        registryBaseItemSettings("piglin_cannon")
                                .rarity(Rarity.RARE)
                                .maxCount(1)
                                .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                )
        );
        WIRE = registerItem("wire");
        ELECTRONIC_COMPONENTS = registerItem("electronic_components");
        ModEntities.SPAWN_EGG_ENTITIES.forEach((id, entityType) -> {
            @SuppressWarnings("unchecked")
            EntityType<? extends MobEntity> mobType = (EntityType<? extends MobEntity>) entityType;
            registerSpawnEggItem(mobType, id);
        });
    }

    public static Item.Settings registryBaseItemSettings(String id) {
        Identifier itemId = Identifier.of(Mob_battle.MOD_ID, id);
        return new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, itemId));
    }
    public static Item registerItem(String id) {
        return registerItem(id, registryBaseItemSettings(id));
    }
    public static <T extends Item> T registerItem(String id, T item) {
        return registerItem(id, item, true);
    }
    public static Item registerItem(String id, Item.Settings settings) {
        return registerItem(id, settings, true);
    }
    public static Item registerItem(String id, Item.Settings settings, boolean isGenerated) {
        return registerItem(id, settings, true, isGenerated);
    }
    public static Item registerItem(String id, Item.Settings settings, boolean registerGroup, boolean isGenerated) {
        return registerItem(id, new Item(settings), registerGroup, isGenerated);
    }
    public static <T extends Item> T registerItem(String id, T item, boolean isGenerated) {
        return registerItem(id, item, true, isGenerated);
    }
    public static <T extends Item> T registerItem(String id, T item, boolean registerGroup, boolean isGenerated) {
        Identifier itemId = Identifier.of(Mob_battle.MOD_ID, id);
        T registered = Registry.register(
                Registries.ITEM,
                itemId,
                item
        );
        if (registerGroup) ITEMS.put(id, registered);
        if (isGenerated) GENERATED_ITEMS.put(id, registered);
        return registered;
    }
    public static SpawnEggItem registerSpawnEggItem(EntityType<? extends MobEntity> entityType, String id) {
        SpawnEggItem item = registerItem(id, new SpawnEggItem(entityType, registryBaseItemSettings(id)), false, false);
        SPAWN_EGG_ITEMS.put(id, item);
        return item;
    }
}
