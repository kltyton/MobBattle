package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.world.item.Items;

/**
 * 货币与弹药材料注册器。
 *
 * <p>本类只负责构造并注册该领域物品；稳定公开字段仍由 {@link ModItems} 暴露。</p>
 */
public final class CurrencyItemRegistrar {

    private CurrencyItemRegistrar() {
    }

    /** 注册货币、容器货币与列车弹药。 */
    public static void init() {
        ModItems.ILLAGER_CURRENCY = RegistrySupport.registerItem("illager_currency");
        ModItems.NIBI = RegistrySupport.registerItem("nibi");
        ModItems.NIBI_BAG = RegistrySupport.registerItem("nibi_bag");
        ModItems.NIBI_BOX = RegistrySupport.registerItem("nibi_box", RegistrySupport.registryBaseItemSettings("nibi_box").craftRemainder(Items.CHEST));
        ModItems.TRAIN_BULLET = RegistrySupport.registerItem("train_bullet", RegistrySupport.registryBaseItemSettings("train_bullet").stacksTo(64));
    }

}
