package com.kltyton.mob_battle.entity.lobster;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.constant.dataticket.DataTicket;

public class LobsterEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LobsterEntity, R> {
    public static final DataTicket<LobsterVariant> LOBSTER_VARIANT = DataTicket.create("lobster_variant", LobsterVariant.class);
    public LobsterEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new LobsterEntityModel());
    }
    @Override
    public void addRenderData(LobsterEntity animatable, Void relatedObject, R renderState) {
        renderState.addGeckolibData(LOBSTER_VARIANT, animatable.getVariant());
    }

}