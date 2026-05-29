package com.kltyton.mob_battle.block.nest;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class NestBlockRenderer extends GeoBlockRenderer<NestBlockEntity> {
    public NestBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        super(new NestBlockModel());
    }
}
