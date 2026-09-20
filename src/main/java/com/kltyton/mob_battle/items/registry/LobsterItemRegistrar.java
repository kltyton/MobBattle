package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.components.ModComponents;
import com.kltyton.mob_battle.components.ModConsumableComponents;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.food.MagmaLobsterItemMod;
import com.kltyton.mob_battle.items.food.ObsidianLobsterItem;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.BlocksAttacks;

/**
 * 龙虾系列物品注册器。
 *
 * <p>按原始源码语句顺序注册龙虾、岩浆龙虾、黑曜石龙虾与爆开的黑曜石龙虾。
 * 黑曜石龙虾带有盾牌(1500 耐久)与龙虾形态转换组件,为独立玩法系列,其余经由
 * {@code RegistrySupport.registerItem} 注册。</p>
 */
public final class LobsterItemRegistrar {

    private LobsterItemRegistrar() {
    }

    public static void init() {
        ModItems.LOBSTER = RegistrySupport.registerItem("lobster",
                RegistrySupport.registryBaseItemSettings("lobster").food(
                        new FoodProperties.Builder()
                                .nutrition(8)
                                .saturationModifier(0.8F)
                                .alwaysEdible()
                                .build(),
                        ModConsumableComponents.LOBSTER
                )
        );
        // 岩浆龙虾：
        // 1. 吃下着火
        // 2. 扔到水里变黑曜石龙虾并播放冷却音效
        ModItems.MAGMA_LOBSTER = RegistrySupport.registerItem("magma_lobster",
                new MagmaLobsterItemMod(
                        RegistrySupport.registryBaseItemSettings("magma_lobster").food(
                                new FoodProperties.Builder()
                                        .nutrition(10)
                                        .saturationModifier(0.6F)
                                        .alwaysEdible()
                                        .build(),
                                ModConsumableComponents.MAGMA_LOBSTER
                        )
                )
        );

        // 黑曜石龙虾：
        // 右键当盾牌，1500耐久，不能附魔
        ModItems.OBSIDIAN_LOBSTER = RegistrySupport.registerItem("obsidian_lobster",
                new ObsidianLobsterItem(
                        RegistrySupport.registryBaseItemSettings("obsidian_lobster")
                                .stacksTo(1)
                                .durability(1500)
                                .component(DataComponents.BLOCKS_ATTACKS,
                                        new BlocksAttacks(
                                                0.25F,
                                                1.0F,
                                                List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                                                new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                                                Optional.empty(),
                                                Optional.of(SoundEvents.SHIELD_BLOCK),
                                                Optional.of(SoundEvents.SHIELD_BREAK)
                                        )
                                )
                                .component(ModComponents.LOBSTER_TRANSFORMED, false)
                                .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK)
                )
        );

        // 爆开的黑曜石龙虾
        ModItems.BURST_OBSIDIAN_LOBSTER = RegistrySupport.registerItem("burst_obsidian_lobster",
                RegistrySupport.registryBaseItemSettings("burst_obsidian_lobster").food(
                        new FoodProperties.Builder()
                                .nutrition(10)
                                .saturationModifier(0.6F)
                                .alwaysEdible()
                                .build(),
                        ModConsumableComponents.BURST_OBSIDIAN_LOBSTER
                )
        );
    }
}
