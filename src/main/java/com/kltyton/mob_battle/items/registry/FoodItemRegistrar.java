package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.food.ThousandBlossomedImmortalFruit;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

/**
 * 食物类物品注册器。
 *
 * <p>除保留的千瓣仙果外，本类集中注册直接食用物品，确保食物组件、食用副作用和
 * 物品注册 ID 在同一职责边界内可审计。效果组件使用 26.1.2 的原生消费组件，避免
 * 为食物行为增加额外事件或客户端专用代码。</p>
 */
public final class FoodItemRegistrar {

    private FoodItemRegistrar() {
    }

    public static void init() {
        //.useRemainder(THOUSAND_BLOSSOMED_IMMORTAL_FRUIT)
        ModItems.THOUSAND_BLOSSOMED_IMMORTAL_FRUIT = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "thousand_blossomed_immortal_fruit"),
                new ThousandBlossomedImmortalFruit(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).alwaysEdible().build()).useCooldown(60)
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "thousand_blossomed_immortal_fruit")))));
        ModItems.ROASTED_CARROT = RegistrySupport.registerItem("roasted_carrot",
                RegistrySupport.registryBaseItemSettings("roasted_carrot").food(
                        new FoodProperties.Builder().nutrition(5).saturationModifier(0.5F).build()
                )
        );
        ModItems.STRANGE_STEW = RegistrySupport.registerItem("strange_stew",
                RegistrySupport.registryBaseItemSettings("strange_stew")
                        .stacksTo(1)
                        .food(
                                new FoodProperties.Builder().nutrition(20).saturationModifier(0.1F).build(),
                                Consumables.defaultFood()
                                        .onConsume(new ApplyStatusEffectsConsumeEffect(
                                                new MobEffectInstance(MobEffects.HUNGER, 30 * 20, 1),
                                                0.2F
                                        ))
                                        .build()
                        )
                        .usingConvertsTo(Items.BOWL)
        );
        ModItems.LOBSTER_MAIN_COURSE = RegistrySupport.registerItem("lobster_main_course",
                RegistrySupport.registryBaseItemSettings("lobster_main_course")
                        .rarity(Rarity.UNCOMMON)
                        .food(
                                new FoodProperties.Builder().nutrition(20).saturationModifier(0.5F).build(),
                                Consumables.defaultFood()
                                        .onConsume(new ApplyStatusEffectsConsumeEffect(
                                                new MobEffectInstance(ModEffects.PROTEIN_ENTRY, 120 * 20, 1)
                                        ))
                                        .build()
                        )
        );
    }
}
