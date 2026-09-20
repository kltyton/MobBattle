package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.tool.bow.IceBowItem;
import net.minecraft.world.item.Rarity;

/**
 * 弓类物品注册器。
 *
 * <p>按原始源码语句顺序注册寒冰弓(位于原杂项区块,25000 耐久,EPIC)。
 * 陨铁弓与猪灵炮因原始语句位于武器区块,分别由 WeaponItemRegistrar 与
 * TechItemRegistrar 注册,此处不重复。</p>
 */
public final class BowItemRegistrar {

    private BowItemRegistrar() {
    }

    public static void init() {
        ModItems.ICE_BOW = RegistrySupport.registerItem("ice_bow",
                new IceBowItem(
                        RegistrySupport.registryBaseItemSettings("ice_bow")
                                .rarity(Rarity.EPIC)
                                .durability(25000)
                                .stacksTo(1)
                ),
                 false
        );
    }
}
