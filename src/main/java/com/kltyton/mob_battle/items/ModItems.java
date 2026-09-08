package com.kltyton.mob_battle.items;

import com.kltyton.mob_battle.items.armor.ModBaseArmorItem;
import com.kltyton.mob_battle.items.consumable.CardiotonicInjectionItem;
import com.kltyton.mob_battle.items.control.LittlePersonScepterItem;
import com.kltyton.mob_battle.items.control.MutualAttackStickItem;
import com.kltyton.mob_battle.items.control.WoodenWhistleItem;
import com.kltyton.mob_battle.items.food.ThousandBlossomedImmortalFruit;
import com.kltyton.mob_battle.items.heartstone.HeartStoneItem;
import com.kltyton.mob_battle.items.heartstone.LittleStoneItem;
import com.kltyton.mob_battle.items.incubation.IncubationEggItem;
import com.kltyton.mob_battle.items.leash.InvisibleUniversalLeadItem;
import com.kltyton.mob_battle.items.leash.UniversalLeadItem;
import com.kltyton.mob_battle.items.registry.ArmorItemRegistrar;
import com.kltyton.mob_battle.items.registry.BaseMaterialItems;
import com.kltyton.mob_battle.items.registry.BowItemRegistrar;
import com.kltyton.mob_battle.items.registry.CombatItemRegistrar;
import com.kltyton.mob_battle.items.registry.CurrencyItemRegistrar;
import com.kltyton.mob_battle.items.registry.DeviceItemRegistrar;
import com.kltyton.mob_battle.items.registry.EquipmentItemRegistrar;
import com.kltyton.mob_battle.items.registry.EquipmentUpgradeItemRegistrar;
import com.kltyton.mob_battle.items.registry.BiochemicalBladeRegistrar;
import com.kltyton.mob_battle.items.registry.LittlePersonShieldRegistrar;
import com.kltyton.mob_battle.items.registry.VehicleItemRegistrar;
import com.kltyton.mob_battle.items.registry.LittlePersonHammerRegistrar;
import com.kltyton.mob_battle.items.registry.IceKnifeItemRegistrar;
import com.kltyton.mob_battle.items.registry.FoodItemRegistrar;
import com.kltyton.mob_battle.items.registry.HeartStoneItemRegistrar;
import com.kltyton.mob_battle.items.registry.LobsterItemRegistrar;
import com.kltyton.mob_battle.items.registry.MaterialItemRegistrar;
import com.kltyton.mob_battle.items.registry.RegistrySupport;
import com.kltyton.mob_battle.items.registry.ScrollItemRegistrar;
import com.kltyton.mob_battle.items.registry.SnackItemRegistrar;
import com.kltyton.mob_battle.items.registry.SpawnEggItemRegistrar;
import com.kltyton.mob_battle.items.registry.ToolItemRegistrar;
import com.kltyton.mob_battle.items.registry.WeaponItemRegistrar;
import com.kltyton.mob_battle.items.scroll.*;
import com.kltyton.mob_battle.items.tool.MasterScepterItem;
import com.kltyton.mob_battle.items.tool.backpack.BackpackItem;
import com.kltyton.mob_battle.items.tool.snipe.VsSnipe;
import com.kltyton.mob_battle.items.shield.LittlePersonShieldItem;
import com.kltyton.mob_battle.items.weapon.biochemical.BiochemicalBladeItem;
import com.kltyton.mob_battle.items.weapon.littlepersonhammer.LittlePersonHammerItem;
import com.kltyton.mob_battle.items.weapon.iceknife.IceKnifeItem;
import com.kltyton.mob_battle.items.consumable.berryjuice.BerryJuiceItem;
import com.kltyton.mob_battle.items.consumable.berryjuice.BerryJuiceItems;
import com.kltyton.mob_battle.items.tool.sword.BloodKnifeItem;
import com.kltyton.mob_battle.items.tool.sword.ChasingWindSwordItem;
import com.kltyton.mob_battle.items.tool.sword.ElementalSwordItem;
import com.kltyton.mob_battle.items.tool.sword.FineKnifeItem;
import com.kltyton.mob_battle.items.tool.sword.IronManMissileLauncherItem;
import com.kltyton.mob_battle.items.tool.sword.PoisonKnifeItem;
import java.util.Map;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

/**
 * 物品注册兼容门面。
 *
 * <p>公开字段和注册方法保持原签名，实际构造与分类注册由 {@code items.registry}
 * 下的领域注册器完成。{@link #init()} 只编排稳定的历史顺序，避免把新业务重新堆回本类。</p>
 */
public class ModItems {
    /** 创造模式物品组使用的稳定物品目录。 */
    public static final Map<String, Item> ITEMS = RegistrySupport.ITEMS;
    /** 刷怪蛋物品目录。 */
    public static final Map<String, SpawnEggItem> SPAWN_EGG_ITEMS = RegistrySupport.SPAWN_EGG_ITEMS;
    /** 需要由 Datagen 生成标准模型的物品目录。 */
    public static final Map<String, Item> GENERATED_ITEMS = RegistrySupport.GENERATED_ITEMS;

    // 工具、卷轴、战斗与通用装备的兼容字段
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
    public static SkullMageScrollItem SKULL_MAGE_SCROLL;
    public static SummonVexBookItem WARLOCK_BOOK;
    public static SummonVexBookItem GRAND_SUMMON_BOOK;
    public static GuardianSealItem GUARDIAN_SEAL;
    public static GuardianSealItem FILLING_SEAL;
    public static FineKnifeItem FINE_KNIFE;
    public static PoisonKnifeItem POISON_KNIFE;
    public static BloodKnifeItem BLOOD_KNIFE;
    public static IronManMissileLauncherItem IRON_MAN_MISSILE_LAUNCHER;
    public static BackpackItem SMALL_BACKPACK;
    public static BackpackItem BIG_BACKPACK;
    public static BackpackItem LARGE_BACKPACK;
    public static Item ICE_ARROW_ITEM;
    public static Item ENDER_PURPLE_PEARL;
    public static ElementalSwordItem ICE_SWORD;
    public static ElementalSwordItem FIRE_SWORD;
    public static ChasingWindSwordItem CHASING_WIND_SWORD;
    public static LittleStoneItem LITTLE_STONE;
    public static WoodenWhistleItem WOODEN_WHISTLE;
    public static Item LITTLE_PERSON_TOOL;
    public static LittlePersonShieldItem LITTLE_PERSON_SHIELD;
    public static BiochemicalBladeItem BIOCHEMICAL_BLADE;
    public static Item OBSIDIAN_BOAT;
    public static LittlePersonHammerItem LITTLE_PERSON_HAMMER;
    public static IceKnifeItem ICE_KNIFE;
    public static LittlePersonScepterItem LITTLE_PERSON_SCEPTER;
    public static Item ILLAGER_CURRENCY;
    public static Item NIBI;
    public static Item NIBI_BAG;
    public static Item NIBI_BOX;

    public static HeartStoneItem HEART_STONE;
    public static ThousandBlossomedImmortalFruit THOUSAND_BLOSSOMED_IMMORTAL_FRUIT;
    public static Item ROASTED_CARROT;
    public static Item STRANGE_STEW;
    public static BerryJuiceItem BERRY_JUICE;
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
    public static Item ADVANCED_SMITHING_TEMPLATE;

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
    public static SpawnEggItem PIGLIN_BRUTE_SPEAR_USE_SPAWN_EGG;
    public static SpawnEggItem PIGLIN_BRUTE_SPEAR_MELEE_SPAWN_EGG;
    public static SpawnEggItem PIGLIN_BRUTE_BOW_SPAWN_EGG;
    public static SpawnEggItem PIGLIN_BRUTE_CROSSBOW_SPAWN_EGG;
    public static SpawnEggItem BOW_ZOMBIE_SPAWN_EGG;
    public static SpawnEggItem PIGLIN_BRUTE_SPEAR_MOD_SPAWN_EGG;
    public static SpawnEggItem BOW_ZOMBIE_MOD_SPAWN_EGG;

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
    public static Item COMPRESSED_COPPER_HELMET;
    public static Item COMPRESSED_COPPER_CHESTPLATE;
    public static Item COMPRESSED_COPPER_LEGGINGS;
    public static Item COMPRESSED_COPPER_BOOTS;
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
    public static Item COMPRESSED_COPPER_SWORD;
    public static Item COMPRESSED_IRON_SWORD;
    public static Item COMPRESSED_GOLD_SWORD;
    public static Item COMPRESSED_DIAMOND_SWORD;
    public static Item COMPRESSED_NETHERITE_SWORD;
    public static VsSnipe VS_SNIPE;
    public static Item MONEY_GUN;
    public static Item TRAIN_BULLET;
    public static Item AREA_GRAVITY_DEVICE_ITEM;
    public static Item WIRE;
    public static Item ELECTRONIC_COMPONENTS;

    public static CardiotonicInjectionItem CARDIOTONIC_INJECTION;

    /**
     * 按历史顺序初始化全部物品。
     *
     * <p>注册顺序属于兼容契约：显式刷怪蛋会触发实体类型初始化，动态刷怪蛋必须最后执行。</p>
     */
    public static void init() {
        BaseMaterialItems.init();
        ToolItemRegistrar.init();
        ScrollItemRegistrar.init();
        FoodItemRegistrar.init();
        BerryJuiceItems.init();
        BERRY_JUICE = BerryJuiceItems.BERRY_JUICE;
        MaterialItemRegistrar.init();
        EquipmentUpgradeItemRegistrar.init();
        LobsterItemRegistrar.init();
        BowItemRegistrar.init();
        SnackItemRegistrar.init();
        HeartStoneItemRegistrar.init();
        ArmorItemRegistrar.init();
        WeaponItemRegistrar.init();
        LittlePersonShieldRegistrar.init();
        BiochemicalBladeRegistrar.init();
        VehicleItemRegistrar.init();
        LittlePersonHammerRegistrar.init();
        LITTLE_PERSON_HAMMER = LittlePersonHammerRegistrar.LITTLE_PERSON_HAMMER;
        IceKnifeItemRegistrar.init();
        ICE_KNIFE = IceKnifeItemRegistrar.ICE_KNIFE;
        LITTLE_PERSON_SHIELD = LittlePersonShieldRegistrar.LITTLE_PERSON_SHIELD;
        BIOCHEMICAL_BLADE = BiochemicalBladeRegistrar.BIOCHEMICAL_BLADE;
        SpawnEggItemRegistrar.initExplicit();
        CombatItemRegistrar.init();
        EquipmentItemRegistrar.init();
        CurrencyItemRegistrar.init();
        DeviceItemRegistrar.init();
        SpawnEggItemRegistrar.initDynamic();
    }

    /** 构造带稳定注册 ID 的物品属性；保留旧公开入口供外部扩展调用。 */
    public static Item.Properties registryBaseItemSettings(String id) {
        return RegistrySupport.registryBaseItemSettings(id);
    }

    /** 注册普通物品；保留旧公开入口。 */
    public static Item registerItem(String id) {
        return RegistrySupport.registerItem(id);
    }

    /** 注册自定义物品实例；保留旧公开入口。 */
    public static <T extends Item> T registerItem(String id, T item) {
        return RegistrySupport.registerItem(id, item);
    }

    /** 按属性注册物品；保留旧公开入口。 */
    public static Item registerItem(String id, Item.Properties settings) {
        return RegistrySupport.registerItem(id, settings);
    }

    /** 按属性注册物品并控制模型生成；保留旧公开入口。 */
    public static Item registerItem(String id, Item.Properties settings, boolean isGenerated) {
        return RegistrySupport.registerItem(id, settings, isGenerated);
    }

    /** 按属性注册物品并分别控制物品组与模型生成；保留旧公开入口。 */
    public static Item registerItem(String id, Item.Properties settings, boolean registerGroup, boolean isGenerated) {
        return RegistrySupport.registerItem(id, settings, registerGroup, isGenerated);
    }

    /** 注册自定义物品并控制模型生成；保留旧公开入口。 */
    public static <T extends Item> T registerItem(String id, T item, boolean isGenerated) {
        return RegistrySupport.registerItem(id, item, isGenerated);
    }

    /** 注册自定义物品并分别控制物品组与模型生成；保留旧公开入口。 */
    public static <T extends Item> T registerItem(String id, T item, boolean registerGroup, boolean isGenerated) {
        return RegistrySupport.registerItem(id, item, registerGroup, isGenerated);
    }

    /** 注册刷怪蛋；保留旧公开入口。 */
    public static SpawnEggItem registerSpawnEggItem(EntityType<? extends Mob> entityType, String id) {
        return RegistrySupport.registerSpawnEggItem(entityType, id);
    }
}
