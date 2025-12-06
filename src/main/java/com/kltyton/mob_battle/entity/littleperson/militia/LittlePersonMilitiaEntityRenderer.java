package com.kltyton.mob_battle.entity.littleperson.militia;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class LittlePersonMilitiaEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LittlePersonMilitiaEntity, R> {
    public LittlePersonMilitiaEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new LittlePersonMilitiaEntityModel());
    }
}
