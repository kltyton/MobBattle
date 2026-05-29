package com.kltyton.mob_battle.block.nest;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class NestBlockRenderer extends GeoBlockRenderer<NestBlockEntity> {
    public NestBlockRenderer(BlockEntityRendererFactory.Context ctx) {
        super(new NestBlockModel());
    }
}
