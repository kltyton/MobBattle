package com.kltyton.mob_battle.block.berryjuice;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 甜浆果汁方块领域注册器。
 *
 * <p>甜浆果汁炼药锅不注册 BlockItem；它是由空炼药锅和玻璃瓶交互产生的
 * 液体状态方块，破坏后的获取行为由主线程接入时按项目资源策略补齐。</p>
 */
public final class BerryJuiceBlocks {

    public static BerryJuiceCauldronBlock BERRY_JUICE_CAULDRON;

    private BerryJuiceBlocks() {
    }

    public static void init() {
        Identifier id = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "berry_juice_cauldron");
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        BERRY_JUICE_CAULDRON = Registry.register(
                BuiltInRegistries.BLOCK,
                key,
                new BerryJuiceCauldronBlock(
                        BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON).setId(key)
                )
        );
    }
}
