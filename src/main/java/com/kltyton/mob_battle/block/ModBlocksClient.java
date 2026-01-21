package com.kltyton.mob_battle.block;

import com.kltyton.mob_battle.block.mushroom.MushroomBlockRenderer;
import com.kltyton.mob_battle.block.nest.NestBlockRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public class ModBlocksClient {
    public static void init() {
        BlockRenderLayerMap.putBlocks(
                BlockRenderLayer.TRIPWIRE,
                ModBlocks.SCARECROW_BLOCK,
                ModBlocks.TARGET_BLOCK,
                ModBlocks.NEST_BLOCK,
                ModBlocks.MUSHROOM_BLOCK
        );
        BlockEntityRendererFactories.register(ModBlockEntities.NEST_ENTITY, NestBlockRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.MUSHROOM_ENTITY, MushroomBlockRenderer::new);
    }
}
