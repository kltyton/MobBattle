package com.kltyton.mob_battle.block;

import com.kltyton.mob_battle.block.mushroom.MushroomBlockRenderer;
import com.kltyton.mob_battle.block.nest.NestBlockRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

@Environment(EnvType.CLIENT)
public class ModBlocksClient {
    public static void init() {
        BlockRenderLayerMap.putBlocks(
                ChunkSectionLayer.TRIPWIRE,
                ModBlocks.SCARECROW_BLOCK,
                ModBlocks.TARGET_BLOCK,
                ModBlocks.MACHINE_WORKTABLE_BLOCK,
                ModBlocks.NEST_BLOCK,
                ModBlocks.MUSHROOM_BLOCK
        );
        BlockEntityRenderers.register(ModBlockEntities.NEST_ENTITY, NestBlockRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.MUSHROOM_ENTITY, MushroomBlockRenderer::new);
    }
}
