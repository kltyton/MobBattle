package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.gravity.AreaGravityDeviceItem;
import com.kltyton.mob_battle.items.tool.piglin.PiglinCannonItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Rarity;

/**
 * 机械设备与电子材料注册器。
 *
 * <p>本类只负责构造并注册该领域物品；稳定公开字段仍由 {@link ModItems} 暴露。</p>
 */
public final class DeviceItemRegistrar {

    private DeviceItemRegistrar() {
    }

    /** 注册重力设备、猪灵火炮与电子材料。 */
    public static void init() {
        ModItems.AREA_GRAVITY_DEVICE_ITEM = RegistrySupport.registerItem("area_gravity_device_item", new AreaGravityDeviceItem(
                RegistrySupport.registryBaseItemSettings("area_gravity_device_item")
                        .rarity(Rarity.RARE)
                        .stacksTo(1)
                        .useCooldown(70)
                        .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)

        ));
        ModItems.PIGLIN_CANNON = RegistrySupport.registerItem("piglin_cannon",
                new PiglinCannonItem(
                        RegistrySupport.registryBaseItemSettings("piglin_cannon")
                                .rarity(Rarity.RARE)
                                .stacksTo(1)
                                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                )
        );
        ModItems.WIRE = RegistrySupport.registerItem("wire");
        ModItems.ELECTRONIC_COMPONENTS = RegistrySupport.registerItem("electronic_components");
    }

}
