package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.items.tool.BaseAxe;
import com.kltyton.mob_battle.items.tool.BaseSword;
import com.kltyton.mob_battle.items.tool.bow.MeteoricoreBowItem;
import com.kltyton.mob_battle.items.tool.irongold.IronGoldSword;
import com.kltyton.mob_battle.items.tool.meteorite.MeteoriteSword;
import com.kltyton.mob_battle.items.tool.snipe.VsSnipe;
import com.kltyton.mob_battle.items.tool.moneygun.MoneyGunItem;
import com.kltyton.mob_battle.items.tool.sword.CompressedMarkedSword;
import com.kltyton.mob_battle.items.tool.sword.zijin.ZiJinSword;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * 武器与远程工具注册器，保持原始武器注册顺序和属性。
 *
 * <p>本类只负责构造并注册该领域物品；稳定公开字段仍由 {@link ModItems} 暴露。</p>
 */
public final class WeaponItemRegistrar {

    private WeaponItemRegistrar() {
    }

    /** 按兼容顺序注册武器与远程工具。 */
    public static void init() {
        ModItems.METEORICORE_AXE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "meteoricore_axe"),
                new BaseAxe(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "meteoricore_axe")
                        ))
                )
        );

        ModItems.METEORICORE_BOW = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "meteoricore_bow"),
                new MeteoricoreBowItem(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "meteoricore_bow")
                        ))
                )
        );

        ModItems.METEORICORE_SWORD = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "meteoricore_sword"),
                new MeteoriteSword(new Item.Properties()
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "meteoricore_sword")
                        ))
                )
        );
        ModItems.IRON_GOLD_SWORD = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_sword"),
                new IronGoldSword(new Item.Properties()
                        .sword(ModMaterial.IRON_GOLD_TOOL_MATERIAL, 84f, 1024)
                        .rarity(Rarity.EPIC)
                        .stacksTo(1)
                        .component(DataComponents.DEATH_PROTECTION, DeathProtection.TOTEM_OF_UNDYING)
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold_sword")
                        ))
                )
        );

        ModItems.EMERALD_DIAMOND_SWORD = RegistrySupport.registerItem("emerald_diamond_sword",
                RegistrySupport.registryBaseItemSettings("emerald_diamond_sword")
                        .rarity(Rarity.EPIC)
                        .sword(ModMaterial.EMERALD_DIAMOND_ALLOY_TOOL_MATERIAL, 149.0F, -2.4F)
                        .stacksTo(1)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE),
                false
        );

        ModItems.ZIJIN_SWORD = RegistrySupport.registerItem("zijin_sword",
                new ZiJinSword(
                        RegistrySupport.registryBaseItemSettings("zijin_sword")
                                .rarity(Rarity.EPIC)
                                .sword(ModMaterial.ZIJIN_ARMOR_TOOL_MATERIAL, 84, 0f)
                                .attributes(ItemAttributeModifiers.builder()
                                        .add(
                                                Attributes.ATTACK_DAMAGE,
                                                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 84.0 + ModMaterial.ZIJIN_ARMOR_TOOL_MATERIAL.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                                                EquipmentSlotGroup.MAINHAND
                                        )
                                        .add(
                                                Attributes.ATTACK_SPEED,
                                                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, 0.0, AttributeModifier.Operation.ADD_VALUE),
                                                EquipmentSlotGroup.MAINHAND
                                        )
                                        .add(
                                                Attributes.SWEEPING_DAMAGE_RATIO,
                                                new AttributeModifier(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "sweeping_zijin_sword"), 1.0, AttributeModifier.Operation.ADD_VALUE),
                                                EquipmentSlotGroup.MAINHAND
                                        )
                                        .build())
                                .stacksTo(1)
                                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                ),
                false
        );

        ModItems.COMPRESSED_COPPER_SWORD = RegistrySupport.registerCompressedSword(
                "compressed_copper_sword",
                new BaseSword(RegistrySupport.compressedSwordSettings("compressed_copper_sword", ModMaterial.COMPRESSED_COPPER_TOOL_MATERIAL, 15.0F, -2.2F))
        );
        ModItems.COMPRESSED_IRON_SWORD = RegistrySupport.registerCompressedSword(
                "compressed_iron_sword",
                new BaseSword(RegistrySupport.compressedSwordSettings("compressed_iron_sword", ModMaterial.COMPRESSED_IRON_TOOL_MATERIAL, 25.0F, -2.2F))
        );
        ModItems.COMPRESSED_GOLD_SWORD = RegistrySupport.registerCompressedSword(
                "compressed_gold_sword",
                new BaseSword(RegistrySupport.compressedSwordSettings("compressed_gold_sword", ModMaterial.COMPRESSED_GOLD_TOOL_MATERIAL, 30.0F, -2.3F))
        );
        ModItems.COMPRESSED_DIAMOND_SWORD = RegistrySupport.registerCompressedSword(
                "compressed_diamond_sword",
                new CompressedMarkedSword(RegistrySupport.compressedSwordSettings("compressed_diamond_sword", ModMaterial.COMPRESSED_DIAMOND_TOOL_MATERIAL, 68.0F, -2.0F), ModEffects.DIAMOND_MARK_ENTRY)
        );
        ModItems.COMPRESSED_NETHERITE_SWORD = RegistrySupport.registerCompressedSword(
                "compressed_netherite_sword",
                new CompressedMarkedSword(RegistrySupport.compressedSwordSettings("compressed_netherite_sword", ModMaterial.COMPRESSED_NETHERITE_TOOL_MATERIAL, 120.0F, -2.0F), ModEffects.NETHERITE_MARK_ENTRY)
        );

        ModItems.VS_SNIPE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "vs_snipe"),
                new VsSnipe(new Item.Properties()
                        .stacksTo(1).durability(465).component(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).enchantable(1)
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "vs_snipe")
                        ))
                )
        );
        ModItems.MONEY_GUN = RegistrySupport.registerItem(
                "money_gun",
                new MoneyGunItem(RegistrySupport.registryBaseItemSettings("money_gun").stacksTo(1)),
                false
        );
    }

}
