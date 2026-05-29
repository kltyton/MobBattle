package com.kltyton.mob_battle.items.itemgroup;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.enchantment.ModEnchantments;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.items.misc.BaseItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final RegistryKey<ItemGroup> MOB_BATTLE_GROUP_KEY =
            RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(Mob_battle.MOD_ID, "main"));

    public static final ItemGroup MOB_BATTLE_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.BIG_FIREBALL_SCROLL))
            .displayName(Text.translatable("itemGroup.mob_battle.main"))
            .entries((context, entries) -> {
                RegistryWrapper.Impl<Enchantment> lookup = context.lookup().getOrThrow(RegistryKeys.ENCHANTMENT);

                addToolsAndScrolls(entries);
                addSpawnEggs(entries);
                entries.add(ModItems.INCUBATION_EGG);
                addArmor(entries, lookup);
                addWeapons(entries);
                addFood(entries);
                addMaterials(entries);
                addBlocks(entries);
            })
            .build();

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, MOB_BATTLE_GROUP_KEY, MOB_BATTLE_GROUP);
    }

    private static void addToolsAndScrolls(ItemGroup.Entries entries) {
        addEntries(entries,
                ModItems.MUTUAL_ATTACK_STICK,
                ModItems.MASTER_SCEPTER,
                ModItems.UNIVERSAL_LEAD,
                ModItems.INVISIBLE_UNIVERSAL_LEAD,
                ModItems.FIREBALL_SCROLL,
                ModItems.BIG_FIREBALL_SCROLL,
                ModItems.SUPER_BIG_FIREBALL_SCROLL,
                ModItems.FIREMAN_SCROLL,
                ModItems.SLOWNESS_SCROLL,
                ModItems.FIRE_WALL_SCROLL,
                ModItems.PURIFICATION_SCROLL,
                ModItems.WARLOCK_BOOK,
                ModItems.GRAND_SUMMON_BOOK,
                ModItems.GUARDIAN_SEAL,
                ModItems.FILLING_SEAL,
                ModItems.FINE_KNIFE,
                ModItems.SMALL_BACKPACK,
                ModItems.LARGE_BACKPACK,
                ModItems.HEART_STONE,
                ModItems.CARDIOTONIC_INJECTION,
                ModItems.AREA_GRAVITY_DEVICE_ITEM
        );
    }

    private static void addSpawnEggs(ItemGroup.Entries entries) {
        addEntries(entries,
                ModItems.HIGHBIRD_EGG_SPAWN_EGG,
                ModItems.HIGHBIRD_BABY_SPAWN_EGG,
                ModItems.HIGHBIRD_TEENAGE_SPAWN_EGG,
                ModItems.HIGHBIRD_ADULTHOOD_SPAWN_EGG,
                spawnEgg("lobster_entity"),
                spawnEgg("magma_lobster_entity"),
                ModItems.XUN_SHENG_SPAWN_EGG,
                ModItems.DEEP_CREATURE_SPAWN_EGG,
                ModItems.WITHER_SKELETON_KING_SPAWN_EGG,
                spawnEgg("enhanced_wither"),
                ModItems.VINDICATOR_GENERAL_SPAWN_EGG,
                ModItems.HULKBUSTER_SPAWN_EGG,
                spawnEgg("super_evoker"),
                ModItems.VOID_CELL_SPAWN_EGG,
                ModItems.YOUNG_MIN_SPAWN_EGG,
                ModItems.HIDDEN_EYE_SPAWN_EGG,
                ModItems.SILENCE_PHANTOM_SPAWN_EGG,
                ModItems.SKULL_KING_SPAWN_EGG,
                ModItems.SKULL_ARCHER_SPAWN_EGG,
                ModItems.SKULL_WARRIOR_SPAWN_EGG,
                ModItems.SKULL_MAGE_SPAWN_EGG,
                ModItems.ARCHER_VILLAGER_SPAWN_EGG,
                ModItems.WARRIOR_VILLAGER_SPAWN_EGG,
                ModItems.MILITIA_ARCHER_VILLAGER_SPAWN_EGG,
                ModItems.MILITIA_WARRIOR_VILLAGER_SPAWN_EGG,
                ModItems.VILLAGER_KING_SPAWN_EGG,
                ModItems.BLUE_IRON_GOLEM_SPAWN_EGG,
                ModItems.IRON_GOLEM_SPAWN_EGG,
                ModItems.SUGAR_MAN_SCORPION_SPAWN_EGG,
                ModItems.COAL_SILVERFISH_SPAWN_EGG,
                spawnEgg("ruili_silverfish"),
                spawnEgg("drill_silverfish"),
                spawnEgg("poisonous_silverfish"),
                spawnEgg("load_silverfish"),
                spawnEgg("long_whip_silverfish"),
                spawnEgg("flower_fairy"),
                ModItems.LITTLE_PERSON_CIVILIAN_SPAWN_EGG,
                ModItems.LITTLE_PERSON_MILITIA_SPAWN_EGG,
                ModItems.LITTLE_PERSON_ARCHER_SPAWN_EGG,
                ModItems.LITTLE_PERSON_GIANT_SPAWN_EGG,
                ModItems.LITTLE_PERSON_GUARD_SPAWN_EGG,
                ModItems.LITTLE_PERSON_KING_SPAWN_EGG,
                spawnEgg("little_person_soldier"),
                spawnEgg("little_person_soldier_archer"),
                spawnEgg("poisonous_slash"),
                spawnEgg("cyborg"),
                spawnEgg("iron_man"),
                spawnEgg("iron_man_true"),
                spawnEgg("tai_lin"),
                spawnEgg("french_sphere_flow"),
                spawnEgg("wild_man"),
                spawnEgg("wild_boar"),
                spawnEgg("magic_man"),
                spawnEgg("heaven_crippled_feet"),
                spawnEgg("bloody_blade"),
                spawnEgg("human_shield"),
                spawnEgg("human_hammer"),
                spawnEgg("sex_entity"),
                spawnEgg("cbot002"),
                spawnEgg("piglin_general"),
                spawnEgg("angel_cyborg"),
                spawnEgg("living_ghost"),
                spawnEgg("scattered_demon"),
                spawnEgg("ninja"),
                spawnEgg("laser_man"),
                spawnEgg("blood_man"),
                spawnEgg("ice_man"),
                spawnEgg("wither_skeleton_dog")
        );
    }

    private static void addArmor(ItemGroup.Entries entries, RegistryWrapper.Impl<Enchantment> lookup) {
        addEntries(entries,
                ModItems.HELL_HELMET_1,
                ModItems.HELL_CHESTPLATE_1,
                ModItems.HELL_LEGGINGS_1,
                ModItems.HELL_BOOTS_1,
                ModItems.HELL_HELMET_2,
                ModItems.HELL_CHESTPLATE_2,
                ModItems.HELL_LEGGINGS_2,
                ModItems.HELL_BOOTS_2
        );

        addEntries(entries,
                createIronGoldArmor(ModItems.IRON_GOLD_HELMET, "iron_gold_helmet", EquipmentType.HELMET, lookup),
                createIronGoldArmor(ModItems.IRON_GOLD_CHESTPLATE, "iron_gold_chestplate", EquipmentType.CHESTPLATE, lookup),
                createIronGoldArmor(ModItems.IRON_GOLD_LEGGINGS, "iron_gold_leggings", EquipmentType.LEGGINGS, lookup),
                createIronGoldArmor(ModItems.IRON_GOLD_BOOTS, "iron_gold_boots", EquipmentType.BOOTS, lookup)
        );

        addEntries(entries,
                createZiJinArmor(ModItems.ZIJIN_HELMET, "zijin_helmet", EquipmentType.HELMET, lookup),
                createZiJinArmor(ModItems.ZIJIN_CHESTPLATE, "zijin_chestplate", EquipmentType.CHESTPLATE, lookup),
                createZiJinArmor(ModItems.ZIJIN_LEGGINGS, "zijin_leggings", EquipmentType.LEGGINGS, lookup),
                createZiJinArmor(ModItems.ZIJIN_BOOTS, "zijin_boots", EquipmentType.BOOTS, lookup)
        );

        addEntries(entries,
                createEmeraldDiamondArmor(ModItems.EMERALD_DIAMOND_HELMET, "emerald_diamond_helmet", EquipmentType.HELMET, lookup, 5, 2),
                createEmeraldDiamondArmor(ModItems.EMERALD_DIAMOND_CHESTPLATE, "emerald_diamond_chestplate", EquipmentType.CHESTPLATE, lookup, 6, 3),
                createEmeraldDiamondArmor(ModItems.EMERALD_DIAMOND_LEGGINGS, "emerald_diamond_leggings", EquipmentType.LEGGINGS, lookup, 6, 2),
                createEmeraldDiamondArmor(ModItems.EMERALD_DIAMOND_BOOTS, "emerald_diamond_boots", EquipmentType.BOOTS, lookup, 5, 2),
                ModItems.ECREDCULTIST_HELMET,
                ModItems.ECREDCULTIST_CHESTPLATE,
                ModItems.ECREDCULTIST_LEGGINGS,
                ModItems.ECREDCULTIST_BOOTS
        );

        addEntries(entries,
                ModItems.COMPRESSED_IRON_HELMET,
                ModItems.COMPRESSED_IRON_CHESTPLATE,
                ModItems.COMPRESSED_IRON_LEGGINGS,
                ModItems.COMPRESSED_IRON_BOOTS,
                ModItems.COMPRESSED_GOLD_HELMET,
                ModItems.COMPRESSED_GOLD_CHESTPLATE,
                ModItems.COMPRESSED_GOLD_LEGGINGS,
                ModItems.COMPRESSED_GOLD_BOOTS,
                ModItems.COMPRESSED_DIAMOND_HELMET,
                ModItems.COMPRESSED_DIAMOND_CHESTPLATE,
                ModItems.COMPRESSED_DIAMOND_LEGGINGS,
                ModItems.COMPRESSED_DIAMOND_BOOTS,
                ModItems.COMPRESSED_NETHERITE_HELMET,
                ModItems.COMPRESSED_NETHERITE_CHESTPLATE,
                ModItems.COMPRESSED_NETHERITE_LEGGINGS,
                ModItems.COMPRESSED_NETHERITE_BOOTS
        );
    }

    private static void addWeapons(ItemGroup.Entries entries) {
        addEntries(entries,
                ModItems.METEORICORE_AXE,
                ModItems.METEORICORE_BOW,
                ModItems.METEORICORE_SWORD,
                createSword(ModItems.IRON_GOLD_SWORD, ModMaterial.IRON_GOLD_TOOL_MATERIAL, "iron_gold_sword"),
                createSword(ModItems.EMERALD_DIAMOND_SWORD, ModMaterial.IRON_GOLD_TOOL_MATERIAL, "emerald_diamond_sword"),
                createSword(ModItems.ZIJIN_SWORD, ModMaterial.ZIJIN_ARMOR_TOOL_MATERIAL, "zijin_sword"),
                ModItems.COMPRESSED_IRON_SWORD,
                ModItems.COMPRESSED_GOLD_SWORD,
                ModItems.COMPRESSED_DIAMOND_SWORD,
                ModItems.COMPRESSED_NETHERITE_SWORD,
                ModItems.ICE_BOW,
                ModItems.VS_SNIPE,
                ModItems.PIGLIN_CANNON,
                ModItems.ICE_ARROW_ITEM,
                ModItems.TRAIN_BULLET
        );
    }

    private static void addFood(ItemGroup.Entries entries) {
        addEntries(entries,
                ModItems.THOUSAND_BLOSSOMED_IMMORTAL_FRUIT,
                ModItems.LOBSTER,
                ModItems.MAGMA_LOBSTER,
                ModItems.OBSIDIAN_LOBSTER,
                ModItems.BURST_OBSIDIAN_LOBSTER,
                ModItems.LOBSTER_MAIN_COURSE,
                ModItems.COOKED_HIGHBIRD_EGG,
                ModItems.CHEESE,
                ModItems.BEER
        );
    }

    private static void addMaterials(ItemGroup.Entries entries) {
        addEntries(entries,
                baseItem("strong_obsidian"),
                baseItem("fire_red"),
                baseItem("blue_ice"),
                baseItem("emerald_diamond"),
                baseItem("iron_gold"),
                baseItem("energy"),
                baseItem("sorcerer_stone"),
                baseItem("lj"),
                baseItem("fire_crystal"),
                baseItem("ice_crystal"),
                baseItem("desert_crystal"),
                ModItems.COMPRESSED_COPPER_INGOT,
                ModItems.COMPRESSED_IRON_INGOT,
                ModItems.COMPRESSED_GOLD_INGOT,
                ModItems.COMPRESSED_DIAMOND,
                ModItems.COMPRESSED_NETHERITE_INGOT,
                ModItems.COMPRESSED_REDSTONE,
                ModItems.COMPRESSED_LAPIS_LAZULI,
                ModItems.WIRE,
                ModItems.ELECTRONIC_COMPONENTS
        );
    }

    private static void addBlocks(ItemGroup.Entries entries) {
        addEntries(entries,
                ModBlocks.SCARECROW_BLOCK,
                ModBlocks.TARGET_BLOCK,
                ModBlocks.MACHINE_WORKTABLE_BLOCK,
                ModBlocks.NEST_BLOCK,
                ModBlocks.MUSHROOM_BLOCK,
                ModBlocks.COMPRESSED_IRON_BLOCK,
                ModBlocks.COMPRESSED_GOLD_BLOCK,
                ModBlocks.COMPRESSED_DIAMOND_BLOCK,
                ModBlocks.COMPRESSED_NETHERITE_BLOCK
        );
    }

    private static ItemStack createIronGoldArmor(
            Item item,
            String id,
            EquipmentType type,
            RegistryWrapper.Impl<Enchantment> lookup
    ) {
        ItemStack stack = new ItemStack(item);
        enchant(stack, lookup, Enchantments.PROTECTION, 5);
        enchant(stack, lookup, ModEnchantments.MAGIC_PROTECTION, 2);
        if (type == EquipmentType.CHESTPLATE) {
            enchant(stack, lookup, Enchantments.FIRE_PROTECTION, 1);
        }
        ModMaterial.createArmor(stack, ModMaterial.IRON_GOLD_INSTANCE, id, type);
        return stack;
    }

    private static ItemStack createEmeraldDiamondArmor(
            Item item,
            String id,
            EquipmentType type,
            RegistryWrapper.Impl<Enchantment> lookup,
            int protection,
            int magicProtection
    ) {
        ItemStack stack = new ItemStack(item);
        enchant(stack, lookup, Enchantments.PROTECTION, protection);
        enchant(stack, lookup, ModEnchantments.MAGIC_PROTECTION, magicProtection);
        ModMaterial.createArmor(stack, ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE, id, type);
        return stack;
    }

    private static ItemStack createZiJinArmor(
            Item item,
            String id,
            EquipmentType type,
            RegistryWrapper.Impl<Enchantment> lookup
    ) {
        ItemStack stack = new ItemStack(item);
        enchant(stack, lookup, Enchantments.PROTECTION, 5);
        enchant(stack, lookup, ModEnchantments.MAGIC_PROTECTION, 2);
        ModMaterial.createArmor(stack, ModMaterial.ZIJIN_ARMOR_INSTANCE, id, type);
        return stack;
    }

    private static void enchant(
            ItemStack stack,
            RegistryWrapper.Impl<Enchantment> lookup,
            RegistryKey<Enchantment> enchantment,
            int level
    ) {
        stack.addEnchantment(lookup.getOrThrow(enchantment), level);
    }

    private static ItemStack createSword(Item item, net.minecraft.item.ToolMaterial material, String id) {
        ItemStack stack = new ItemStack(item);
        ModMaterial.createSword(stack, material, id, AttributeModifierSlot.MAINHAND);
        return stack;
    }

    private static Item baseItem(String id) {
        return BaseItems.ITEMS.get(id);
    }

    private static SpawnEggItem spawnEgg(String id) {
        return ModItems.SPAWN_EGG_ITEMS.get(id);
    }

    private static void addEntries(ItemGroup.Entries entries, Object... entriesToAdd) {
        for (Object entry : entriesToAdd) {
            if (entry instanceof ItemStack stack) {
                if (!stack.isEmpty()) {
                    entries.add(stack);
                }
            } else if (entry instanceof ItemConvertible item) {
                entries.add(item);
            }
        }
    }
}
