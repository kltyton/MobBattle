package com.kltyton.mob_battle.entity.lobster;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.constant.dataticket.DataTicket;

public class LobsterEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LobsterEntity, R> {
    public static final DataTicket<LobsterVariant> LOBSTER_VARIANT = DataTicket.create("lobster_variant", LobsterVariant.class);
    public LobsterEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new LobsterEntityModel());
    }
    @Override
    public void addRenderData(LobsterEntity animatable, Void relatedObject, R renderState) {
        renderState.addGeckolibData(LOBSTER_VARIANT, animatable.getVariant());
    }

}