package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.consumable.CardiotonicInjectionItem;
import com.kltyton.mob_battle.items.control.LittlePersonScepterItem;
import com.kltyton.mob_battle.items.control.WoodenWhistleItem;
import com.kltyton.mob_battle.items.heartstone.LittleStoneItem;
import com.kltyton.mob_battle.items.teleport.EnderPurplePearlItem;
import com.kltyton.mob_battle.items.tool.LittlePersonToolItem;
import com.kltyton.mob_battle.items.tool.backpack.BackpackItem;
import com.kltyton.mob_battle.items.tool.sword.ChasingWindSwordItem;
import com.kltyton.mob_battle.items.tool.sword.ElementalSwordItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;

/**
 * 背包、消耗品、移动与控制装备注册器。
 *
 * <p>本类只负责构造并注册该领域物品；稳定公开字段仍由 {@link ModItems} 暴露。</p>
 */
public final class EquipmentItemRegistrar {

    private EquipmentItemRegistrar() {
    }

    /** 按兼容顺序注册通用装备、消耗品和控制工具。 */
    public static void init() {
        ModItems.SMALL_BACKPACK = RegistrySupport.registerItem("small_backpack", new BackpackItem(
                RegistrySupport.registryBaseItemSettings("small_backpack")
                        .rarity(Rarity.UNCOMMON)
                        .stacksTo(1),
                false)
        );

        ModItems.BIG_BACKPACK = RegistrySupport.registerItem("big_backpack", new BackpackItem(
                RegistrySupport.registryBaseItemSettings("big_backpack")
                        .rarity(Rarity.UNCOMMON)
                        .stacksTo(1),
                9)
        );

        ModItems.LARGE_BACKPACK = RegistrySupport.registerItem("large_backpack", new BackpackItem(
                RegistrySupport.registryBaseItemSettings("large_backpack")
                        .rarity(Rarity.RARE)
                        .stacksTo(1),
                true)
        );

        ModItems.CARDIOTONIC_INJECTION = RegistrySupport.registerItem("cardiotonic_injection", new CardiotonicInjectionItem(
                RegistrySupport.registryBaseItemSettings("cardiotonic_injection")
                        .stacksTo(1)),
                false
        );

        ModItems.ICE_ARROW_ITEM = RegistrySupport.registerItem("ice_arrow_item", RegistrySupport.registryBaseItemSettings("ice_arrow_item").stacksTo(64));
        ModItems.ENDER_PURPLE_PEARL = RegistrySupport.registerItem("ender_purple_pearl",
                new EnderPurplePearlItem(RegistrySupport.registryBaseItemSettings("ender_purple_pearl").stacksTo(20)));
        ModItems.ICE_SWORD = RegistrySupport.registerItem("ice_sword",
                new ElementalSwordItem(RegistrySupport.registryBaseItemSettings("ice_sword")
                        .durability(230)
                        .sword(ToolMaterial.IRON, 3.0F, -2.4F)
                        .stacksTo(1),
                        com.kltyton.mob_battle.entity.projectile.ElementalSwordProjectileEntity.ICE_SWORD,
                        Items.SNOWBALL,
                        1,
                        0));
        ModItems.FIRE_SWORD = RegistrySupport.registerItem("fire_sword",
                new ElementalSwordItem(RegistrySupport.registryBaseItemSettings("fire_sword")
                        .durability(210)
                        .sword(ToolMaterial.IRON, 3.0F, -2.4F)
                        .stacksTo(1),
                        com.kltyton.mob_battle.entity.projectile.ElementalSwordProjectileEntity.FIRE_SWORD,
                        Items.FIRE_CHARGE,
                        1,
                        0));
        ModItems.CHASING_WIND_SWORD = RegistrySupport.registerItem("chasing_wind_sword",
                new ChasingWindSwordItem(RegistrySupport.registryBaseItemSettings("chasing_wind_sword")
                        .durability(1600)
                        .sword(ToolMaterial.IRON, 33.0F, -2.4F)
                        .stacksTo(1)));
        ModItems.LITTLE_STONE = RegistrySupport.registerItem("little_stone", new LittleStoneItem(RegistrySupport.registryBaseItemSettings("little_stone").stacksTo(64)));
        ModItems.WOODEN_WHISTLE = RegistrySupport.registerItem("wooden_whistle", new WoodenWhistleItem(RegistrySupport.registryBaseItemSettings("wooden_whistle").durability(10).stacksTo(1)));
        ModItems.LITTLE_PERSON_TOOL = RegistrySupport.registerItem("little_person_tool",
                new LittlePersonToolItem(LittlePersonToolItem.applyAxePickaxeProperties(
                        RegistrySupport.registryBaseItemSettings("little_person_tool")
                                .durability(50))
                        .stacksTo(1)),
                true,
                false
        );
        ModItems.LITTLE_PERSON_SCEPTER = RegistrySupport.registerItem("little_person_scepter",
                new LittlePersonScepterItem(RegistrySupport.registryBaseItemSettings("little_person_scepter").durability(100).stacksTo(1)),
                true,
                false
        );
    }

}
