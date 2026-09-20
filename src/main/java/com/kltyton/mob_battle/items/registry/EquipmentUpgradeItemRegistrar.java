package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.world.item.SmithingTemplateItem;

/**
 * 锻造台升级耗材注册器。
 */
public final class EquipmentUpgradeItemRegistrar {
    private EquipmentUpgradeItemRegistrar() {
    }

    public static void init() {
        ModItems.ADVANCED_SMITHING_TEMPLATE = RegistrySupport.registerItem(
                "advanced_smithing_template",
                SmithingTemplateItem.createNetheriteUpgradeTemplate(
                        RegistrySupport.registryBaseItemSettings("advanced_smithing_template")
                )
        );
    }
}
