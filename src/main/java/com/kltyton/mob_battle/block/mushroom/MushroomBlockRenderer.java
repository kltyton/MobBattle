package com.kltyton.mob_battle.block.mushroom;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MushroomBlockRenderer extends GeoBlockRenderer<MushroomBlockEntity> {
    public MushroomBlockRenderer(BlockEntityRendererFactory.Context ctx) {
        super(new MushroomBlockModel());
    }
}