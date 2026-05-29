package com.kltyton.mob_battle.block.mushroom;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MushroomBlockRenderer extends GeoBlockRenderer<MushroomBlockEntity> {
    public MushroomBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        super(new MushroomBlockModel());
    }
}