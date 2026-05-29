package com.kltyton.mob_battle.items;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.components.ModComponents;
import com.kltyton.mob_battle.components.ModConsumableComponents;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.items.armor.ModBaseArmorItem;
import com.kltyton.mob_battle.items.food.MagmaLobsterItemMod;
import com.kltyton.mob_battle.items.food.ThousandBlossomedImmortalFruit;
import com.kltyton.mob_battle.items.misc.*;
import com.kltyton.mob_battle.items.tool.BaseSword;
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
import com.kltyton.mob_battle.items.tool.sword.CompressedMarkedSword;
import com.kltyton.mob_battle.items.tool.sword.FineKnifeItem;
import com.kltyton.mob_battle.items.tool.sword.zijin.ZiJinSword;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
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
    public static PurificationScrollItem PURIFICATION_SCROLL;
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
    public static Item CHEESE;
    public static Item BEER;

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
    public static Item COMPRESSED_IRON_HELMET;
    public static Item COMPRESSED_IRON_CHESTPLATE;
    public static Item COMPRESSED_IRON_LEGGINGS;
    public static Item COMPRESSED_IRON_BOOTS;
    public static Item COMPRESSED_GOLD_HELMET;
    public static Item COMPRESSED_GOLD_CHESTPLATE;
    public static Item COMPRESSED_GOLD_LEGGINGS;
    public static Item COMPRESSED_GOLD_BOOTS;
    public static Item COMPRESSED_DIAMOND_HELMET;
    public static Item COMPRESSED_DIAMOND_CHESTPLATE;
    public static Item COMPRESSED_DIAMOND_LEGGINGS;
    public static Item COMPRESSED_DIAMOND_BOOTS;
    public static Item COMPRESSED_NETHERITE_HELMET;
    public static Item COMPRESSED_NETHERITE_CHESTPLATE;
    public static Item COMPRESSED_NETHERITE_LEGGINGS;
    public static Item COMPRESSED_NETHERITE_BOOTS;
    //工具以及武器
    public static Item METEORICORE_AXE;
    public static Item METEORICORE_BOW;
    public static Item METEORICORE_SWORD;
    public static Item IRON_GOLD_SWORD;
    public static Item EMERALD_DIAMOND_SWORD;
    public static Item ZIJIN_SWORD;
    public static Item COMPRESSED_IRON_SWORD;
    public static Item COMPRESSED_GOLD_SWORD;
    public static Item COMPRESSED_DIAMOND_SWORD;
    public static Item COMPRESSED_NETHERITE_SWORD;
    public static VsSnipe VS_SNIPE;
    public static Item TRAIN_BULLET;
    public static Item AREA_GRAVITY_DEVICE_ITEM;
    public static Item WIRE;
    public static Item ELECTRONIC_COMPONENTS;

    public static CardiotonicInjectionItem CARDIOTONIC_INJECTION;

    public static void init() {
        BaseItems.init();
        //注册物品
        MUTUAL_ATTACK_STICK = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "mutual_attack_stick"),
                new MutualAttackStickItem(new Item.Properties().stacksTo(1)
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "mutual_attack_stick")))));
        MASTER_SCEPTER = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "master_scepter"),
                new MasterScepterItem(new Item.Properties().stacksTo(1).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "master_scepter")))));
        UNIVERSAL_LEAD = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "universal_lead"),
                new UniversalLeadItem(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "universal_lead")))));
        INVISIBLE_UNIVERSAL_LEAD = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "invisible_universal_lead"),
                new InvisibleUniversalLeadItem(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "invisible_universal_lead")))));
        FIREBALL_SCROLL = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "fireball_scroll"),
                new FireballScrollItem(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "fireball_scroll")))));
        BIG_FIREBALL_SCROLL = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "big_fireball_scroll"),
                new BigFireballScrollItem(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "big_fireball_scroll")))));
        SUPER_BIG_FIREBALL_SCROLL = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "super_big_fireball_scroll"),
                new SuperBigFireballScrollItem(new Item.Properties().useCooldown(7)
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "super_big_fireball_scroll")))));

        FIREMAN_SCROLL = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "fireman_scroll"),
                new FiremanScrollItem(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "fireman_scroll")))));
        SLOWNESS_SCROLL = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "slowness_scroll"),
                new SlownessScrollItem(new Item.Properties().useCooldown(20)
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "slowness_scroll")))));
        FIRE_WALL_SCROLL = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "fire_wall_scroll"),
                new FireWallScrollItem(new Item.Properties().useCooldown(35)
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "fire_wall_scroll")))));
        PURIFICATION_SCROLL = registerItem("purification_scroll",
                new PurificationScrollItem(registryBaseItemSettings("purification_scroll")
                        .useCooldown(75))
        );
        //.useRemainder(THOUSAND_BLOSSOMED_IMMORTAL_FRUIT)
        THOUSAND_BLOSSOMED_IMMORTAL_FRUIT = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "thousand_blossomed_immortal_fruit"),
                new ThousandBlossomedImmortalFruit(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).alwaysEdible().build()).useCooldown(60)
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "thousand_blossomed_immortal_fruit")))));
        LOBSTER_MAIN_COURSE = registerItem("lobster_main_course",
                registryBaseItemSettings("lobster_main_course").food(
                        new FoodProperties.Builder().nutrition(10).saturationModifier(0.3F).build()
                )
        );
        COMPRESSED_COPPER_INGOT = registerItem("compressed_copper_ingot", registryBaseItemSettings("compressed_copper_ingot").rarity(Rarity.UNCOMMON));
        COMPRESSED_IRON_INGOT = registerItem("compressed_iron_ingot", registryBaseItemSettings("compressed_iron_ingot").rarity(Rarity.UNCOMMON));
        COMPRESSED_GOLD_INGOT = registerItem("compressed_gold_ingot", registryBaseItemSettings("compressed_gold_ingot").rarity(Rarity.UNCOMMON));
        COMPRESSED_DIAMOND = registerItem("compressed_diamond", registryBaseItemSettings("compressed_diamond").rarity(Rarity.UNCOMMON));
        COMPRESSED_NETHERITE_INGOT = registerItem("compressed_netherite_ingot", registryBaseItemSettings("compressed_netherite_ingot").rarity(Rarity.UNCOMMON));
        COMPRESSED_REDSTONE = registerItem("compressed_redstone", registryBaseItemSettings("compressed_redstone").rarity(Rarity.UNCOMMON));
        COMPRESSED_LAPIS_LAZULI = registerItem("compressed_lapis_lazuli", registryBaseItemSettings("compressed_lapis_lazuli").rarity(Rarity.UNCOMMON));

        // 龙虾系列
        LOBSTER = registerItem("lobster",
                registryBaseItemSettings("lobster").food(
                        new FoodProperties.Builder()
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
                                new FoodProperties.Builder()
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
                new ObsidianLobsterItem(
                        registryBaseItemSettings("obsidian_lobster")
                                .stacksTo(1)
                                .durability(1500)
                                .component(DataComponents.BLOCKS_ATTACKS,
                                        new BlocksAttacks(
                                                0.25F,
                                                1.0F,
                                                List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                                                new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                                                Optional.of(DamageTypeTags.BYPASSES_SHIELD),
                                                Optional.of(SoundEvents.SHIELD_BLOCK),
                                                Optional.of(SoundEvents.SHIELD_BREAK)
                                        )
                                )
                                .component(ModComponents.LOBSTER_TRANSFORMED, false)
                                .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK)
                )
        );

        // 爆开的黑曜石龙虾
        BURST_OBSIDIAN_LOBSTER = registerItem("burst_obsidian_lobster",
                registryBaseItemSettings("burst_obsidian_lobster").food(
                        new FoodProperties.Builder()
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
                                .durability(25000)
                                .stacksTo(1)
                ),
                 false
        );

        COOKED_HIGHBIRD_EGG = registerItem("cooked_highbird_egg",
                registryBaseItemSettings("cooked_highbird_egg").food(
                        new FoodProperties.Builder().nutrition(20).saturationModifier(20).alwaysEdible().build(),
                        ModConsumableComponents.COOKED_HIGHBIRD_EGG
                )
        );
        CHEESE = registerItem("cheese",
                registryBaseItemSettings("cheese").food(
                        new FoodProperties.Builder().nutrition(1).saturationModifier(2.0F).build()
                )
        );
        BEER = registerItem("beer",
                registryBaseItemSettings("beer").food(
                        new FoodProperties.Builder().nutrition(1).saturationModifier(1.0F).alwaysEdible().build(),
                        ModConsumableComponents.BEER
                )
        );

        HEART_STONE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "heart_stone"),
                new HeartStoneItem(new Item.Properties().stacksTo(3)
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "heart_stone")))));

        HELL_HELMET_1 = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_helmet_1"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_1, ArmorType.HELMET)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_helmet_1")
                                ))
                )
        );
        HELL_CHESTPLATE_1 = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_chestplate_1"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_1, ArmorType.CHESTPLATE)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_chestplate_1")
                                ))
                )
        );
        HELL_LEGGINGS_1 = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_leggings_1"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_1, ArmorType.LEGGINGS)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_leggings_1")
                                ))
                )
        );
        HELL_BOOTS_1 = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_boots_1"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_1, ArmorType.BOOTS)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_boots_1")
                                ))
                )
        );
        HELL_HELMET_2 = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_helmet_2"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_2, ArmorType.HELMET)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_helmet_2")
                                ))
                )
        );
        HELL_CHESTPLATE_2 = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_chestplate_2"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_2, ArmorType.CHESTPLATE)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_chestplate_2")
                                ))
                )
        );
        HELL_LEGGINGS_2 = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_leggings_2"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_2, ArmorType.LEGGINGS)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_leggings_2")
                                ))
                )
        );
        HELL_BOOTS_2 = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_boots_2"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_2, ArmorType.BOOTS)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_boots_2")
                                ))
                )
        );

        ECREDCULTIST_HELMET = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_helmet"),
                new ModBaseArmorItem(new Item.Properties().humanoidArmor(ModMaterial.ECREDCULTIST_INSTANCE, ArmorType.HELMET)
                        .durability(1).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_helmet")
                        )),
                        ModMaterial.ECREDCULTIST_INSTANCE
                )
        );
        ECREDCULTIST_CHESTPLATE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_chestplate"),
                new ModBaseArmorItem(new Item.Properties().humanoidArmor(ModMaterial.ECREDCULTIST_INSTANCE, ArmorType.CHESTPLATE)
                        .durability(1).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_chestplate")
                        )),
                        ModMaterial.ECREDCULTIST_INSTANCE
                )
        );
        ECREDCULTIST_LEGGINGS = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_leggings"),
                new ModBaseArmorItem(new Item.Properties().humanoidArmor(ModMaterial.ECREDCULTIST_INSTANCE, ArmorType.LEGGINGS)
                        .durability(1).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_leggings")
                        )),
                        ModMaterial.ECREDCULTIST_INSTANCE
                )
        );
        ECREDCULTIST_BOOTS = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_boots"),
                new ModBaseArmorItem(
                        new Item.Properties().humanoidArmor(ModMaterial.ECREDCULTIST_INSTANCE, ArmorType.BOOTS)
                        .durability(1).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_boots")
                        )),
                        ModMaterial.ECREDCULTIST_INSTANCE
                )
        );
        IRON_GOLD_HELMET = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_helmet"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.IRON_GOLD_INSTANCE, ArmorType.HELMET)
                                .durability(512).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_helmet")
                                ))
                )
        );
        IRON_GOLD_CHESTPLATE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_chestplate"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.IRON_GOLD_INSTANCE, ArmorType.CHESTPLATE)
                                .durability(512).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_chestplate")
                                ))
                )
        );
        IRON_GOLD_LEGGINGS = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_leggings"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.IRON_GOLD_INSTANCE, ArmorType.LEGGINGS)
                                .durability(512).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_leggings")
                                ))
                )
        );
        IRON_GOLD_BOOTS = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_boots"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.IRON_GOLD_INSTANCE, ArmorType.BOOTS)
                                .durability(512).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_boots")
                                ))
                )
        );
        // 翠钻合金套
        EMERALD_DIAMOND_HELMET = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_helmet"),
                new Item(new Item.Properties()
                        .humanoidArmor(ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE, ArmorType.HELMET)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
                        .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_helmet")))
                )
        );

        EMERALD_DIAMOND_CHESTPLATE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_chestplate"),
                new Item(new Item.Properties()
                        .humanoidArmor(ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE, ArmorType.CHESTPLATE)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
                        .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_chestplate")))
                )
        );

        EMERALD_DIAMOND_LEGGINGS = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_leggings"),
                new Item(new Item.Properties()
                        .humanoidArmor(ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE, ArmorType.LEGGINGS)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
                        .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_leggings")))
                )
        );

        EMERALD_DIAMOND_BOOTS = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_boots"),
                new Item(new Item.Properties()
                        .humanoidArmor(ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE, ArmorType.BOOTS)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
                        .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_boots")))
                )
        );

        ZIJIN_HELMET = registerItem(
                "zijin_helmet",
                registryBaseItemSettings("zijin_helmet")
                        .humanoidArmor(ModMaterial.ZIJIN_ARMOR_INSTANCE, ArmorType.HELMET)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
        );

        ZIJIN_CHESTPLATE = registerItem(
                "zijin_chestplate",
                registryBaseItemSettings("zijin_chestplate")
                        .humanoidArmor(ModMaterial.ZIJIN_ARMOR_INSTANCE, ArmorType.CHESTPLATE)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
        );

        ZIJIN_LEGGINGS = registerItem(
                "zijin_leggings",
                registryBaseItemSettings("zijin_leggings")
                        .humanoidArmor(ModMaterial.ZIJIN_ARMOR_INSTANCE, ArmorType.LEGGINGS)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
        );

        ZIJIN_BOOTS = registerItem("zijin_boots",
                registryBaseItemSettings("zijin_boots")
                        .humanoidArmor(ModMaterial.ZIJIN_ARMOR_INSTANCE, ArmorType.BOOTS)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
        );

        // 注册工具和武器
        COMPRESSED_IRON_HELMET = registerCompressedArmor("compressed_iron_helmet", ModMaterial.COMPRESSED_IRON_ARMOR_INSTANCE, ArmorType.HELMET, 5000, 6.0, 0.0);
        COMPRESSED_IRON_CHESTPLATE = registerCompressedArmor("compressed_iron_chestplate", ModMaterial.COMPRESSED_IRON_ARMOR_INSTANCE, ArmorType.CHESTPLATE, 5000, 6.0, 0.0);
        COMPRESSED_IRON_LEGGINGS = registerCompressedArmor("compressed_iron_leggings", ModMaterial.COMPRESSED_IRON_ARMOR_INSTANCE, ArmorType.LEGGINGS, 5000, 6.0, 0.0);
        COMPRESSED_IRON_BOOTS = registerCompressedArmor("compressed_iron_boots", ModMaterial.COMPRESSED_IRON_ARMOR_INSTANCE, ArmorType.BOOTS, 5000, 6.0, 0.0);

        COMPRESSED_GOLD_HELMET = registerCompressedArmor("compressed_gold_helmet", ModMaterial.COMPRESSED_GOLD_ARMOR_INSTANCE, ArmorType.HELMET, 4000, 5.0, 0.0);
        COMPRESSED_GOLD_CHESTPLATE = registerCompressedArmor("compressed_gold_chestplate", ModMaterial.COMPRESSED_GOLD_ARMOR_INSTANCE, ArmorType.CHESTPLATE, 4000, 5.0, 0.0);
        COMPRESSED_GOLD_LEGGINGS = registerCompressedArmor("compressed_gold_leggings", ModMaterial.COMPRESSED_GOLD_ARMOR_INSTANCE, ArmorType.LEGGINGS, 4000, 5.0, 0.0);
        COMPRESSED_GOLD_BOOTS = registerCompressedArmor("compressed_gold_boots", ModMaterial.COMPRESSED_GOLD_ARMOR_INSTANCE, ArmorType.BOOTS, 4000, 5.0, 0.0);

        COMPRESSED_DIAMOND_HELMET = registerCompressedArmor("compressed_diamond_helmet", ModMaterial.COMPRESSED_DIAMOND_ARMOR_INSTANCE, ArmorType.HELMET, 10000, 7.0, 0.0);
        COMPRESSED_DIAMOND_CHESTPLATE = registerCompressedArmor("compressed_diamond_chestplate", ModMaterial.COMPRESSED_DIAMOND_ARMOR_INSTANCE, ArmorType.CHESTPLATE, 10000, 7.0, 1.0);
        COMPRESSED_DIAMOND_LEGGINGS = registerCompressedArmor("compressed_diamond_leggings", ModMaterial.COMPRESSED_DIAMOND_ARMOR_INSTANCE, ArmorType.LEGGINGS, 10000, 7.0, 1.0);
        COMPRESSED_DIAMOND_BOOTS = registerCompressedArmor("compressed_diamond_boots", ModMaterial.COMPRESSED_DIAMOND_ARMOR_INSTANCE, ArmorType.BOOTS, 10000, 7.0, 0.0);

        COMPRESSED_NETHERITE_HELMET = registerCompressedArmor("compressed_netherite_helmet", ModMaterial.COMPRESSED_NETHERITE_ARMOR_INSTANCE, ArmorType.HELMET, 20000, 10.0, 0.0);
        COMPRESSED_NETHERITE_CHESTPLATE = registerCompressedArmor("compressed_netherite_chestplate", ModMaterial.COMPRESSED_NETHERITE_ARMOR_INSTANCE, ArmorType.CHESTPLATE, 20000, 10.0, 0.0);
        COMPRESSED_NETHERITE_LEGGINGS = registerCompressedArmor("compressed_netherite_leggings", ModMaterial.COMPRESSED_NETHERITE_ARMOR_INSTANCE, ArmorType.LEGGINGS, 20000, 10.0, 0.0);
        COMPRESSED_NETHERITE_BOOTS = registerCompressedArmor("compressed_netherite_boots", ModMaterial.COMPRESSED_NETHERITE_ARMOR_INSTANCE, ArmorType.BOOTS, 20000, 10.0, 0.0);

        METEORICORE_AXE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "meteoricore_axe"),
                new BaseAxe(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "meteoricore_axe")
                        ))
                )
        );

        METEORICORE_BOW = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "meteoricore_bow"),
                new MeteoricoreBowItem(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "meteoricore_bow")
                        ))
                )
        );

        METEORICORE_SWORD = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "meteoricore_sword"),
                new MeteoriteSword(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "meteoricore_sword")
                        ))
                )
        );
        IRON_GOLD_SWORD = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_sword"),
                new IronGoldSword(new Item.Properties()
                        .sword(ModMaterial.IRON_GOLD_TOOL_MATERIAL, 84f, 1024)
                        .stacksTo(1)
                        .component(DataComponents.DEATH_PROTECTION, DeathProtection.TOTEM_OF_UNDYING)
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_sword")
                        ))
                )
        );

        EMERALD_DIAMOND_SWORD = registerItem("emerald_diamond_sword",
                registryBaseItemSettings("emerald_diamond_sword")
                        .sword(ModMaterial.EMERALD_DIAMOND_ALLOY_TOOL_MATERIAL, 119, -2f)
                        .stacksTo(1)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE),
                false
        );

        ZIJIN_SWORD = registerItem("zijin_sword",
                new ZiJinSword(
                        registryBaseItemSettings("zijin_sword")
                        .sword(ModMaterial.ZIJIN_ARMOR_TOOL_MATERIAL, 84, 0f)
                        .stacksTo(1)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                ),
                false
        );

        COMPRESSED_IRON_SWORD = registerCompressedSword(
                "compressed_iron_sword",
                new BaseSword(compressedSwordSettings("compressed_iron_sword", ModMaterial.COMPRESSED_IRON_TOOL_MATERIAL, 25.0F, -2.2F))
        );
        COMPRESSED_GOLD_SWORD = registerCompressedSword(
                "compressed_gold_sword",
                new BaseSword(compressedSwordSettings("compressed_gold_sword", ModMaterial.COMPRESSED_GOLD_TOOL_MATERIAL, 30.0F, -2.3F))
        );
        COMPRESSED_DIAMOND_SWORD = registerCompressedSword(
                "compressed_diamond_sword",
                new CompressedMarkedSword(compressedSwordSettings("compressed_diamond_sword", ModMaterial.COMPRESSED_DIAMOND_TOOL_MATERIAL, 68.0F, -2.0F), ModEffects.DIAMOND_MARK_ENTRY)
        );
        COMPRESSED_NETHERITE_SWORD = registerCompressedSword(
                "compressed_netherite_sword",
                new CompressedMarkedSword(compressedSwordSettings("compressed_netherite_sword", ModMaterial.COMPRESSED_NETHERITE_TOOL_MATERIAL, 120.0F, -2.0F), ModEffects.NETHERITE_MARK_ENTRY)
        );

        VS_SNIPE = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "vs_snipe"),
                new VsSnipe(new Item.Properties()
                        .stacksTo(1).durability(465).component(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).enchantable(1)
                        .setId(ResourceKey.create(
                                Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "vs_snipe")
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

        INCUBATION_EGG = Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "incubation_egg"),
                new IncubationEggItem(
                        ModEntities.HIGHBIRD_EGG,
                        new Item.Properties()
                                .setId(ResourceKey.create(
                                        Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "incubation_egg")
                                ))
                )
        );

        WARLOCK_BOOK = registerItem("warlock_book", new SummonVexBookItem(
                registryBaseItemSettings("warlock_book")
                        .useCooldown(15)
                        .durability(150)
                        .stacksTo(1),
                3, 0, 0)
        );

        GRAND_SUMMON_BOOK = registerItem("grand_summon_book", new SummonVexBookItem(
                registryBaseItemSettings("grand_summon_book")
                        .rarity(Rarity.RARE)
                        .useCooldown(20)
                        .durability(150)
                        .stacksTo(1),
                10, 5, 10)
        );

        GUARDIAN_SEAL = registerItem("guardian_seal", new GuardianSealItem(
                registryBaseItemSettings("guardian_seal")
                        .useCooldown(2000)
                        .stacksTo(1),
                false)
        );

        FILLING_SEAL = registerItem("filling_seal", new GuardianSealItem(
                registryBaseItemSettings("filling_seal")
                        .stacksTo(1),
                true)
        );

        FINE_KNIFE = registerItem("fine_knife", new FineKnifeItem(
                registryBaseItemSettings("fine_knife")
                        .durability(200)
                        .sword(ToolMaterial.IRON, 0, 0)
                        .stacksTo(1)),
                false
        );

        SMALL_BACKPACK = registerItem("small_backpack", new BackpackItem(
                registryBaseItemSettings("small_backpack")
                        .rarity(Rarity.UNCOMMON)
                        .stacksTo(1),
                false)
        );

        LARGE_BACKPACK = registerItem("large_backpack", new BackpackItem(
                registryBaseItemSettings("large_backpack")
                        .rarity(Rarity.RARE)
                        .stacksTo(1),
                true)
        );

        CARDIOTONIC_INJECTION = registerItem("cardiotonic_injection", new CardiotonicInjectionItem(
                registryBaseItemSettings("cardiotonic_injection")
                        .stacksTo(1)),
                false
        );

        ICE_ARROW_ITEM = registerItem("ice_arrow_item", registryBaseItemSettings("ice_arrow_item").stacksTo(64));
        TRAIN_BULLET = registerItem("train_bullet", registryBaseItemSettings("train_bullet").stacksTo(64));
        AREA_GRAVITY_DEVICE_ITEM = registerItem("area_gravity_device_item", new AreaGravityDeviceItem(
                registryBaseItemSettings("area_gravity_device_item")
                        .rarity(Rarity.RARE)
                        .stacksTo(1)
                        .useCooldown(70)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)

        ));
        PIGLIN_CANNON = registerItem("piglin_cannon",
                new PiglinCannonItem(
                        registryBaseItemSettings("piglin_cannon")
                                .rarity(Rarity.RARE)
                                .stacksTo(1)
                                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                )
        );
        WIRE = registerItem("wire");
        ELECTRONIC_COMPONENTS = registerItem("electronic_components");
        ModEntities.SPAWN_EGG_ENTITIES.forEach((id, entityType) -> {
            @SuppressWarnings("unchecked")
            EntityType<? extends Mob> mobType = (EntityType<? extends Mob>) entityType;
            registerSpawnEggItem(mobType, id);
        });
    }

    public static Item.Properties registryBaseItemSettings(String id) {
        ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, id);
        return new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, itemId));
    }
    private static Item registerCompressedArmor(String id, ArmorMaterial material, ArmorType type, int durability, double maxHealth, double extraToughness) {
        EquipmentSlotGroup slot = EquipmentSlotGroup.bySlot(type.getSlot());
        ItemAttributeModifiers attributes = material.createAttributes(type)
                .withModifierAdded(
                        Attributes.MAX_HEALTH,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "health_" + id), maxHealth, AttributeModifier.Operation.ADD_VALUE),
                        slot
                );
        if (extraToughness > 0.0) {
            attributes = attributes.withModifierAdded(
                    Attributes.ARMOR_TOUGHNESS,
                    new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "armor_toughness_" + id), extraToughness, AttributeModifier.Operation.ADD_VALUE),
                    slot
            );
        }
        return registerItem(
                id,
                new ModBaseArmorItem(
                        registryBaseItemSettings(id)
                        .humanoidArmor(material, type)
                        .attributes(attributes)
                        .durability(durability)
                        .stacksTo(1),
                        material,
                        false
                )
        );
    }
    private static Item.Properties compressedSwordSettings(String id, ToolMaterial material, float attackDamage, float attackSpeed) {
        ItemAttributeModifiers attributes = ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage + material.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.SWEEPING_DAMAGE_RATIO,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "sweeping_" + id), 1.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
        return registryBaseItemSettings(id)
                .sword(material, attackDamage, attackSpeed)
                .attributes(attributes)
                .stacksTo(1);
    }
    private static Item registerCompressedSword(String id, Item item) {
        return registerItem(id, item, true, false);
    }
    public static Item registerItem(String id) {
        return registerItem(id, registryBaseItemSettings(id));
    }
    public static <T extends Item> T registerItem(String id, T item) {
        return registerItem(id, item, true);
    }
    public static Item registerItem(String id, Item.Properties settings) {
        return registerItem(id, settings, true);
    }
    public static Item registerItem(String id, Item.Properties settings, boolean isGenerated) {
        return registerItem(id, settings, true, isGenerated);
    }
    public static Item registerItem(String id, Item.Properties settings, boolean registerGroup, boolean isGenerated) {
        return registerItem(id, new Item(settings), registerGroup, isGenerated);
    }
    public static <T extends Item> T registerItem(String id, T item, boolean isGenerated) {
        return registerItem(id, item, true, isGenerated);
    }
    public static <T extends Item> T registerItem(String id, T item, boolean registerGroup, boolean isGenerated) {
        ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, id);
        T registered = Registry.register(
                BuiltInRegistries.ITEM,
                itemId,
                item
        );
        if (registerGroup) ITEMS.put(id, registered);
        if (isGenerated) GENERATED_ITEMS.put(id, registered);
        return registered;
    }
    public static SpawnEggItem registerSpawnEggItem(EntityType<? extends Mob> entityType, String id) {
        SpawnEggItem item = registerItem(id, new SpawnEggItem(entityType, registryBaseItemSettings(id)), false, false);
        SPAWN_EGG_ITEMS.put(id, item);
        return item;
    }
}
