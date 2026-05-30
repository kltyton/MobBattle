package com.kltyton.mob_battle.entity.littleperson.militia;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class LittlePersonMilitiaEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LittlePersonMilitiaEntity, R> {
    public LittlePersonMilitiaEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new LittlePersonMilitiaEntityModel());
    }
}
