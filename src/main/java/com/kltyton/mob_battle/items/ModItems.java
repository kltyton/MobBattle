package com.kltyton.mob_battle.items;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.components.ModConsumableComponents;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.items.armor.ModBaseArmorItem;
import com.kltyton.mob_battle.items.food.ThousandBlossomedImmortalFruit;
import com.kltyton.mob_battle.items.misc.*;
import com.kltyton.mob_battle.items.scroll.*;
import com.kltyton.mob_battle.items.tool.BaseAxe;
import com.kltyton.mob_battle.items.tool.BaseBow;
import com.kltyton.mob_battle.items.tool.MasterScepterItem;
import com.kltyton.mob_battle.items.tool.irongold.IronGoldSword;
import com.kltyton.mob_battle.items.tool.meteorite.MeteoriteSword;
import com.kltyton.mob_battle.items.tool.snipe.VsSnipe;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

import java.util.HashMap;
import java.util.Map;

public class ModItems {
    public static final Map<String, Item> ITEMS = new HashMap<>();
    public static final Map<String, SpawnEggItem> SPAWN_EGG_ITEMS = new HashMap<>();

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
    public static HeartStoneItem HEART_STONE;
    public static ThousandBlossomedImmortalFruit THOUSAND_BLOSSOMED_IMMORTAL_FRUIT;
    public static Item LOBSTER_MAIN_COURSE;
    public static Item COOKED_HIGHBIRD_EGG;
    // 刷怪蛋
    public static SpawnEggItem HIGHBIRD_BABY_SPAWN_EGG;
    public static SpawnEggItem HIGHBIRD_TEENAGE_SPAWN_EGG;
    public static SpawnEggItem HIGHBIRD_EGG_SPAWN_EGG;
    public static SpawnEggItem HIGHBIRD_ADULT_SPAWN_EGG;
    public static SpawnEggItem XU_SHENG_SPAWN_EGG;
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

    public static ModBaseArmorItem ECREDCULTIST_HELMET;
    public static ModBaseArmorItem ECREDCULTIST_CHESTPLATE;
    public static ModBaseArmorItem ECREDCULTIST_LEGGINGS;
    public static ModBaseArmorItem ECREDCULTIST_BOOTS;

    //工具以及武器
    public static Item METEORICORE_AXE;
    public static Item METEORICORE_BOW;
    public static Item METEORICORE_SWORD;
    public static Item IRON_GOLD_SWORD;
    public static Item IRON_GOLD_SWORD_DAMAGED;

    public static VsSnipe VS_SNIPE;

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
                new Item.Settings().food(
                        new FoodComponent.Builder().nutrition(10).saturationModifier(0.3F).build()
                )
        );

        COOKED_HIGHBIRD_EGG = registerItem("cooked_highbird_egg",
                new Item.Settings().food(
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
                new MeteoriteSword(new Item.Settings()
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "meteoricore_sword")
                        ))
                )
        );
        IRON_GOLD_SWORD = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_sword"),
                new IronGoldSword(new Item.Settings()
                        .sword(ModMaterial.IRON_GOLD_TOOL_MATERIAL, 49f,-2.2f).maxCount(1)
                        .component(DataComponentTypes.DEATH_PROTECTION, DeathProtectionComponent.TOTEM_OF_UNDYING)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_sword")
                        ))
                )
        );
        IRON_GOLD_SWORD_DAMAGED = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_sword_damaged"),
                new IronGoldSword(new Item.Settings().sword(ModMaterial.IRON_GOLD_TOOL_MATERIAL, 0f,-2.2f).maxCount(1).maxDamage(0).component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_sword_damaged")
                        ))
                )
        );
        VS_SNIPE = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "vs_snipe"),
                new VsSnipe(new Item.Settings()
                        .maxCount(1).maxDamage(465).component(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT).enchantable(1)
                        .registryKey(RegistryKey.of(
                                RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "vs_snipe")
                        ))
                )
        );
        SKULL_KING_SPAWN_EGG = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "skull_king_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.SKULL_KING,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "skull_king_spawn_egg")
                                ))
                )
        );
        SKULL_ARCHER_SPAWN_EGG = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "skull_archer_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.SKULL_ARCHER,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "skull_archer_spawn_egg")
                                ))
                )
        );
        SKULL_WARRIOR_SPAWN_EGG = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "skull_warrior_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.SKULL_WARRIOR,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "skull_warrior_spawn_egg")
                                ))
                )
        );
        SKULL_MAGE_SPAWN_EGG = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "skull_mage_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.SKULL_MAGE,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "skull_mage_spawn_egg")
                                ))
                )
        );
        VOID_CELL_SPAWN_EGG = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "void_cell_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.VOID_CELL,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "void_cell_spawn_egg")
                                ))
                )
        );
        YOUNG_MIN_SPAWN_EGG = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "young_min_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.YOUNG_MIN,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "young_min_spawn_egg")
                                ))
                )
        );
        HIDDEN_EYE_SPAWN_EGG = Registry.register(Registries.ITEM, Identifier.of(Mob_battle.MOD_ID, "hidden_eye_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.HIDDEN_EYE,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "hidden_eye_spawn_egg")
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
        DEEP_CREATURE_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "deep_creature_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.DEEP_CREATURE,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "deep_creature_spawn_egg")
                                ))
                )
        );
        WITHER_SKELETON_KING_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "wither_skeleton_king_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.WITHER_SKELETON_KING,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "wither_skeleton_king_spawn_egg")
                                ))
                )
        );
        MILITIA_ARCHER_VILLAGER_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "militia_archer_villager_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.MILITIA_ARCHER_VILLAGER,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "militia_archer_villager_spawn_egg")
                                ))
                )
        );
        MILITIA_WARRIOR_VILLAGER_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "militia_warrior_villager_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.MILITIA_WARRIOR_VILLAGER,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "militia_warrior_villager_spawn_egg")
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
        IRON_GOLEM_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "iron_golem_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.VILLAGER_IRON_GOLEM_ENTITY,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_golem_spawn_egg")
                                ))
                )
        );
        LITTLE_PERSON_CIVILIAN_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "little_person_civilian_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.LITTLE_PERSON_CIVILIAN,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "little_person_civilian_spawn_egg")
                                ))
                )
        );
        LITTLE_PERSON_MILITIA_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "little_person_militia_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.LITTLE_PERSON_MILITIA,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "little_person_militia_spawn_egg")
                                ))
                )
        );
        LITTLE_PERSON_ARCHER_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "little_person_archer_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.LITTLE_PERSON_ARCHER,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "little_person_archer_spawn_egg")
                                ))
                )
        );
        LITTLE_PERSON_GIANT_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "little_person_giant_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.LITTLE_PERSON_GIANT,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "little_person_giant_spawn_egg")
                                ))
                )
        );
        LITTLE_PERSON_GUARD_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "little_person_guard_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.LITTLE_PERSON_GUARD,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "little_person_guard_spawn_egg")
                                ))
                )
        );
        LITTLE_PERSON_KING_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "little_person_king_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.LITTLE_PERSON_KING,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "little_person_king_spawn_egg")
                                ))
                )
        );
        VILLAGER_KING_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "villager_king_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.VILLAGER_KING_ENTITY,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "villager_king_spawn_egg")
                                ))
                )
        );
        VINDICATOR_GENERAL_SPAWN_EGG = Registry.register(Registries.ITEM,
                Identifier.of(Mob_battle.MOD_ID, "vindicator_general_spawn_egg"),
                new SpawnEggItem(
                        ModEntities.VINDICATOR_GENERAL,
                        new Item.Settings()
                                .registryKey(RegistryKey.of(
                                        RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "vindicator_general_spawn_egg")
                                ))
                )
        );
        HULKBUSTER_SPAWN_EGG = registerSpawnEggItem(ModEntities.HULKBUSTER, "hulkbuster_spawn_egg");
        SILENCE_PHANTOM_SPAWN_EGG = registerSpawnEggItem(ModEntities.SILENCE_PHANTOM, "silence_phantom_spawn_egg");
        COAL_SILVERFISH_SPAWN_EGG = registerSpawnEggItem(ModEntities.COAL_SILVERFISH, "coal_silverfish_spawn_egg");

        ModEntities.SPAWN_EGG_ENTITIES.forEach((id, entityType) -> {
            @SuppressWarnings("unchecked")
            EntityType<? extends MobEntity> mobType = (EntityType<? extends MobEntity>) entityType;
            registerSpawnEggItem(mobType, id);
        });
    }
    private static Item registerItem(String id) {
        Identifier itemId = Identifier.of(Mob_battle.MOD_ID, id);
        Item.Settings settings = new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, itemId));
        Item item = Registry.register(Registries.ITEM, itemId, new Item(settings));
        ITEMS.put(id, item);
        return item;
    }
    public static Item registerItem(String id, Item.Settings settings) {
        Identifier itemId = Identifier.of(Mob_battle.MOD_ID, id);
        Item.Settings finalsettings = settings
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, itemId));
        Item item = Registry.register(
                Registries.ITEM,
                itemId,
                new Item(finalsettings)
        );
        ITEMS.put(id, item);
        return item;
    }
    public static SpawnEggItem registerSpawnEggItem(EntityType<? extends MobEntity> entityType, String id) {
        Identifier itemId = Identifier.of(Mob_battle.MOD_ID, id);
        Item.Settings finalsettings = new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, itemId));
        SpawnEggItem item = Registry.register(
                Registries.ITEM,
                itemId,
                new SpawnEggItem(
                        entityType,
                        finalsettings
                )
        );
        SPAWN_EGG_ITEMS.put(id, item);
        return item;
    }
}
