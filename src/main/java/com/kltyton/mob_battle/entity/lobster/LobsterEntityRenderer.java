package com.kltyton.mob_battle.entity.lobster;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.constant.dataticket.DataTicket;

public class LobsterEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LobsterEntity, R> {
    public static final DataTicket<LobsterVariant> LOBSTER_VARIANT = DataTicket.create("lobster_variant", LobsterVariant.class);
    public LobsterEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new LobsterEntityModel());
    }
    @Override
    public void addRenderData(LobsterEntity animatable, Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(LOBSTER_VARIANT, animatable.getVariant());
    }

}
