package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.items.armor.ModBaseArmorItem;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.ArmorType;

/**
 * 护甲注册器。
 *
 * <p>按原始源码语句顺序注册第二个连续区块:地狱套、秘教套、铁金套、翠钻合金套、
 * 紫金套与压缩系列护甲(共 44 项)。压缩护甲经由共享层
 * {@link RegistrySupport#registerCompressedArmor} 注册。</p>
 */
public final class ArmorItemRegistrar {

    private ArmorItemRegistrar() {
    }

    public static void init() {
        //盔甲
        ModItems.HELL_HELMET_1 = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_helmet_1"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_1, ArmorType.HELMET)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_helmet_1")
                                ))
                )
        );
        ModItems.HELL_CHESTPLATE_1 = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_chestplate_1"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_1, ArmorType.CHESTPLATE)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_chestplate_1")
                                ))
                )
        );
        ModItems.HELL_LEGGINGS_1 = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_leggings_1"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_1, ArmorType.LEGGINGS)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_leggings_1")
                                ))
                )
        );
        ModItems.HELL_BOOTS_1 = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_boots_1"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_1, ArmorType.BOOTS)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_boots_1")
                                ))
                )
        );
        ModItems.HELL_HELMET_2 = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_helmet_2"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_2, ArmorType.HELMET)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_helmet_2")
                                ))
                )
        );
        ModItems.HELL_CHESTPLATE_2 = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_chestplate_2"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_2, ArmorType.CHESTPLATE)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_chestplate_2")
                                ))
                )
        );
        ModItems.HELL_LEGGINGS_2 = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_leggings_2"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_2, ArmorType.LEGGINGS)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_leggings_2")
                                ))
                )
        );
        ModItems.HELL_BOOTS_2 = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_boots_2"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.HELL_ARMOR_INSTANCE_2, ArmorType.BOOTS)
                                .durability(0).stacksTo(1)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_boots_2")
                                ))
                )
        );

        ModItems.ECREDCULTIST_HELMET = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_helmet"),
                new ModBaseArmorItem(new Item.Properties().humanoidArmor(ModMaterial.ECREDCULTIST_INSTANCE, ArmorType.HELMET)
                        .durability(1).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_helmet")
                        )),
                        ModMaterial.ECREDCULTIST_INSTANCE
                )
        );
        ModItems.ECREDCULTIST_CHESTPLATE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_chestplate"),
                new ModBaseArmorItem(new Item.Properties().humanoidArmor(ModMaterial.ECREDCULTIST_INSTANCE, ArmorType.CHESTPLATE)
                        .durability(1).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_chestplate")
                        )),
                        ModMaterial.ECREDCULTIST_INSTANCE
                )
        );
        ModItems.ECREDCULTIST_LEGGINGS = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_leggings"),
                new ModBaseArmorItem(new Item.Properties().humanoidArmor(ModMaterial.ECREDCULTIST_INSTANCE, ArmorType.LEGGINGS)
                        .durability(1).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_leggings")
                        )),
                        ModMaterial.ECREDCULTIST_INSTANCE
                )
        );
        ModItems.ECREDCULTIST_BOOTS = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_boots"),
                new ModBaseArmorItem(
                        new Item.Properties().humanoidArmor(ModMaterial.ECREDCULTIST_INSTANCE, ArmorType.BOOTS)
                        .durability(1).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist_boots")
                        )),
                        ModMaterial.ECREDCULTIST_INSTANCE
                )
        );
        ModItems.IRON_GOLD_HELMET = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_helmet"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.IRON_GOLD_INSTANCE, ArmorType.HELMET)
                                .rarity(Rarity.EPIC)
                                .durability(512).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_helmet")
                                ))
                )
        );
        ModItems.IRON_GOLD_CHESTPLATE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_chestplate"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.IRON_GOLD_INSTANCE, ArmorType.CHESTPLATE)
                                .rarity(Rarity.EPIC)
                                .durability(512).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_chestplate")
                                ))
                )
        );
        ModItems.IRON_GOLD_LEGGINGS = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_leggings"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.IRON_GOLD_INSTANCE, ArmorType.LEGGINGS)
                                .rarity(Rarity.EPIC)
                                .durability(512).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_leggings")
                                ))
                )
        );
        ModItems.IRON_GOLD_BOOTS = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_boots"),
                new Item(new Item.Properties().humanoidArmor(ModMaterial.IRON_GOLD_INSTANCE, ArmorType.BOOTS)
                                .rarity(Rarity.EPIC)
                                .durability(512).stacksTo(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                                .setId(ResourceKey.create(
                                        Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_boots")
                                ))
                )
        );
        // 翠钻合金套
        ModItems.EMERALD_DIAMOND_HELMET = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_helmet"),
                new Item(new Item.Properties()
                        .rarity(Rarity.EPIC)
                        .humanoidArmor(ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE, ArmorType.HELMET)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
                        .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_helmet")))
                )
        );

        ModItems.EMERALD_DIAMOND_CHESTPLATE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_chestplate"),
                new Item(new Item.Properties()
                        .rarity(Rarity.EPIC)
                        .humanoidArmor(ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE, ArmorType.CHESTPLATE)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
                        .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_chestplate")))
                )
        );

        ModItems.EMERALD_DIAMOND_LEGGINGS = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_leggings"),
                new Item(new Item.Properties()
                        .rarity(Rarity.EPIC)
                        .humanoidArmor(ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE, ArmorType.LEGGINGS)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
                        .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_leggings")))
                )
        );

        ModItems.EMERALD_DIAMOND_BOOTS = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_boots"),
                new Item(new Item.Properties()
                        .rarity(Rarity.EPIC)
                        .humanoidArmor(ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE, ArmorType.BOOTS)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
                        .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_boots")))
                )
        );

        ModItems.ZIJIN_HELMET = RegistrySupport.registerItem(
                "zijin_helmet",
                RegistrySupport.registryBaseItemSettings("zijin_helmet")
                        .rarity(Rarity.EPIC)
                        .humanoidArmor(ModMaterial.ZIJIN_ARMOR_INSTANCE, ArmorType.HELMET)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
        );

        ModItems.ZIJIN_CHESTPLATE = RegistrySupport.registerItem(
                "zijin_chestplate",
                RegistrySupport.registryBaseItemSettings("zijin_chestplate")
                        .rarity(Rarity.EPIC)
                        .humanoidArmor(ModMaterial.ZIJIN_ARMOR_INSTANCE, ArmorType.CHESTPLATE)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
        );

        ModItems.ZIJIN_LEGGINGS = RegistrySupport.registerItem(
                "zijin_leggings",
                RegistrySupport.registryBaseItemSettings("zijin_leggings")
                        .rarity(Rarity.EPIC)
                        .humanoidArmor(ModMaterial.ZIJIN_ARMOR_INSTANCE, ArmorType.LEGGINGS)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
        );

        ModItems.ZIJIN_BOOTS = RegistrySupport.registerItem("zijin_boots",
                RegistrySupport.registryBaseItemSettings("zijin_boots")
                        .rarity(Rarity.EPIC)
                        .humanoidArmor(ModMaterial.ZIJIN_ARMOR_INSTANCE, ArmorType.BOOTS)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                        .stacksTo(1)
        );

        // 注册工具和武器
        ModItems.COMPRESSED_COPPER_HELMET = RegistrySupport.registerCompressedArmor("compressed_copper_helmet", ModMaterial.COMPRESSED_COPPER_ARMOR_INSTANCE, ArmorType.HELMET, 2500, 5.0, 0.0);
        ModItems.COMPRESSED_COPPER_CHESTPLATE = RegistrySupport.registerCompressedArmor("compressed_copper_chestplate", ModMaterial.COMPRESSED_COPPER_ARMOR_INSTANCE, ArmorType.CHESTPLATE, 2500, 5.0, 0.0);
        ModItems.COMPRESSED_COPPER_LEGGINGS = RegistrySupport.registerCompressedArmor("compressed_copper_leggings", ModMaterial.COMPRESSED_COPPER_ARMOR_INSTANCE, ArmorType.LEGGINGS, 2500, 5.0, 0.0);
        ModItems.COMPRESSED_COPPER_BOOTS = RegistrySupport.registerCompressedArmor("compressed_copper_boots", ModMaterial.COMPRESSED_COPPER_ARMOR_INSTANCE, ArmorType.BOOTS, 2500, 5.0, 0.0);

        ModItems.COMPRESSED_IRON_HELMET = RegistrySupport.registerCompressedArmor("compressed_iron_helmet", ModMaterial.COMPRESSED_IRON_ARMOR_INSTANCE, ArmorType.HELMET, 5000, 6.0, 0.0);
        ModItems.COMPRESSED_IRON_CHESTPLATE = RegistrySupport.registerCompressedArmor("compressed_iron_chestplate", ModMaterial.COMPRESSED_IRON_ARMOR_INSTANCE, ArmorType.CHESTPLATE, 5000, 6.0, 0.0);
        ModItems.COMPRESSED_IRON_LEGGINGS = RegistrySupport.registerCompressedArmor("compressed_iron_leggings", ModMaterial.COMPRESSED_IRON_ARMOR_INSTANCE, ArmorType.LEGGINGS, 5000, 6.0, 0.0);
        ModItems.COMPRESSED_IRON_BOOTS = RegistrySupport.registerCompressedArmor("compressed_iron_boots", ModMaterial.COMPRESSED_IRON_ARMOR_INSTANCE, ArmorType.BOOTS, 5000, 6.0, 0.0);

        ModItems.COMPRESSED_GOLD_HELMET = RegistrySupport.registerCompressedArmor("compressed_gold_helmet", ModMaterial.COMPRESSED_GOLD_ARMOR_INSTANCE, ArmorType.HELMET, 4000, 5.0, 0.0);
        ModItems.COMPRESSED_GOLD_CHESTPLATE = RegistrySupport.registerCompressedArmor("compressed_gold_chestplate", ModMaterial.COMPRESSED_GOLD_ARMOR_INSTANCE, ArmorType.CHESTPLATE, 4000, 5.0, 0.0);
        ModItems.COMPRESSED_GOLD_LEGGINGS = RegistrySupport.registerCompressedArmor("compressed_gold_leggings", ModMaterial.COMPRESSED_GOLD_ARMOR_INSTANCE, ArmorType.LEGGINGS, 4000, 5.0, 0.0);
        ModItems.COMPRESSED_GOLD_BOOTS = RegistrySupport.registerCompressedArmor("compressed_gold_boots", ModMaterial.COMPRESSED_GOLD_ARMOR_INSTANCE, ArmorType.BOOTS, 4000, 5.0, 0.0);

        ModItems.COMPRESSED_DIAMOND_HELMET = RegistrySupport.registerCompressedArmor("compressed_diamond_helmet", ModMaterial.COMPRESSED_DIAMOND_ARMOR_INSTANCE, ArmorType.HELMET, 10000, 7.0, 0.0);
        ModItems.COMPRESSED_DIAMOND_CHESTPLATE = RegistrySupport.registerCompressedArmor("compressed_diamond_chestplate", ModMaterial.COMPRESSED_DIAMOND_ARMOR_INSTANCE, ArmorType.CHESTPLATE, 10000, 7.0, 1.0);
        ModItems.COMPRESSED_DIAMOND_LEGGINGS = RegistrySupport.registerCompressedArmor("compressed_diamond_leggings", ModMaterial.COMPRESSED_DIAMOND_ARMOR_INSTANCE, ArmorType.LEGGINGS, 10000, 7.0, 1.0);
        ModItems.COMPRESSED_DIAMOND_BOOTS = RegistrySupport.registerCompressedArmor("compressed_diamond_boots", ModMaterial.COMPRESSED_DIAMOND_ARMOR_INSTANCE, ArmorType.BOOTS, 10000, 7.0, 0.0);

        ModItems.COMPRESSED_NETHERITE_HELMET = RegistrySupport.registerCompressedArmor("compressed_netherite_helmet", ModMaterial.COMPRESSED_NETHERITE_ARMOR_INSTANCE, ArmorType.HELMET, 20000, 10.0, 0.0);
        ModItems.COMPRESSED_NETHERITE_CHESTPLATE = RegistrySupport.registerCompressedArmor("compressed_netherite_chestplate", ModMaterial.COMPRESSED_NETHERITE_ARMOR_INSTANCE, ArmorType.CHESTPLATE, 20000, 10.0, 0.0);
        ModItems.COMPRESSED_NETHERITE_LEGGINGS = RegistrySupport.registerCompressedArmor("compressed_netherite_leggings", ModMaterial.COMPRESSED_NETHERITE_ARMOR_INSTANCE, ArmorType.LEGGINGS, 20000, 10.0, 0.0);
        ModItems.COMPRESSED_NETHERITE_BOOTS = RegistrySupport.registerCompressedArmor("compressed_netherite_boots", ModMaterial.COMPRESSED_NETHERITE_ARMOR_INSTANCE, ArmorType.BOOTS, 20000, 10.0, 0.0);
    }
}
