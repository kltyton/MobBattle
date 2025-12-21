package com.kltyton.mob_battle.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;

@Environment(EnvType.CLIENT)
public class ModBlocksClient {
    public static void init() {
        BlockRenderLayerMap.putBlocks(BlockRenderLayer.CUTOUT, ModBlocks.SCARECROW_BLOCK, ModBlocks.TARGET_BLOCK);
    }
}
