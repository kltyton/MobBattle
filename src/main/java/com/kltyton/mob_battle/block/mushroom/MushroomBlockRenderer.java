package com.kltyton.mob_battle.block.mushroom;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class MushroomBlockRenderer<R extends BlockEntityRenderState & GeoRenderState> extends GeoBlockRenderer<MushroomBlockEntity, R> {
    public MushroomBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx, new MushroomBlockModel());
    }
}
