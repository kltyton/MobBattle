package com.kltyton.mob_battle.items.consumable.berryjuice;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import com.kltyton.mob_battle.items.registry.RegistrySupport;

/**
 * 甜浆果汁物品领域注册器。
 *
 * <p>这里保留全部饮用契约：2 点营养、3 点实际饱和度、始终可饮用、Sugar I
 * 持续 60 秒，以及饮用后由原版使用余物组件返回玻璃瓶。初始化必须在
 * {@link ModEffects#init()} 之后执行，以确保 Sugar 效果 Holder 已存在。</p>
 */
public final class BerryJuiceItems {

    public static BerryJuiceItem BERRY_JUICE;

    private BerryJuiceItems() {
    }

    public static void init() {
        Identifier id = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "berry_juice");
        BERRY_JUICE = RegistrySupport.registerItem(
                "berry_juice",
                new BerryJuiceItem(
                        new Item.Properties()
                                .setId(ResourceKey.create(Registries.ITEM, id))
                                .food(
                                        new FoodProperties.Builder()
                                                .nutrition(2)
                                                .saturationModifier(0.75F)
                                                .alwaysEdible()
                                                .build(),
                                        Consumables.defaultDrink()
                                                .onConsume(new ApplyStatusEffectsConsumeEffect(
                                                        new MobEffectInstance(ModEffects.SUGAR_ENTRY, 60 * 20, 0)
                                                ))
                                                .build()
                                )
                                .usingConvertsTo(Items.GLASS_BOTTLE)
                ),
                true
        );
    }
}
