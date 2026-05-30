package com.kltyton.mob_battle.block;

import com.kltyton.mob_battle.block.mushroom.MushroomBlockRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

@Environment(EnvType.CLIENT)
public class ModBlocksClient {
    public static void init() {
        BlockEntityRenderers.register(ModBlockEntities.MUSHROOM_ENTITY, MushroomBlockRenderer::new);
    }
}
