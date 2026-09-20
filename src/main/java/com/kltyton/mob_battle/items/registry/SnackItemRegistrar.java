package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.components.ModConsumableComponents;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.world.food.FoodProperties;

/**
 * 小吃/饮品类物品注册器(第二段食物区块)。
 *
 * <p>按原始源码语句顺序注册熟高脚鸟蛋、奶酪与啤酒,全部经由
 * {@code RegistrySupport.registerItem} 注册。</p>
 */
public final class SnackItemRegistrar {

    private SnackItemRegistrar() {
    }

    public static void init() {
        ModItems.COOKED_HIGHBIRD_EGG = RegistrySupport.registerItem("cooked_highbird_egg",
                RegistrySupport.registryBaseItemSettings("cooked_highbird_egg").food(
                        new FoodProperties.Builder().nutrition(20).saturationModifier(20).alwaysEdible().build(),
                        ModConsumableComponents.COOKED_HIGHBIRD_EGG
                )
        );
        ModItems.CHEESE = RegistrySupport.registerItem("cheese",
                RegistrySupport.registryBaseItemSettings("cheese").food(
                        new FoodProperties.Builder().nutrition(1).saturationModifier(2.0F).build()
                )
        );
        ModItems.BEER = RegistrySupport.registerItem("beer",
                RegistrySupport.registryBaseItemSettings("beer").food(
                        new FoodProperties.Builder().nutrition(1).saturationModifier(1.0F).alwaysEdible().build(),
                        ModConsumableComponents.BEER
                )
        );
    }
}
