package com.kltyton.mob_battle.entity.highbird.baby;

import com.kltyton.mob_battle.entity.xunsheng.XunShengEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class HighbirdBabyRender<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<HighbirdBabyEntity, R> {
    public HighbirdBabyRender(EntityRendererFactory.Context context) {
        super(context, new HighbirdBabyModel());
    }
}
