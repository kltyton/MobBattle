package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.items.weapon.iceknife.IceKnifeItem;
import net.minecraft.world.item.ToolMaterial;

/** 冰刀领域物品注册入口；不改变共享 ModItems 兼容门面。 */
public final class IceKnifeItemRegistrar {
    public static final String ID = "ice_knife";
    public static IceKnifeItem ICE_KNIFE;
    private static boolean initialized;

    private IceKnifeItemRegistrar() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        ICE_KNIFE = RegistrySupport.registerItem(ID,
                new IceKnifeItem(RegistrySupport.registryBaseItemSettings(ID)
                        .sword(ToolMaterial.IRON, -1.0F, -2.4F)
                        .durability(IceKnifeItem.MAX_DURABILITY)
                        .stacksTo(1)),
                true,
                false);
        initialized = true;
    }
}
