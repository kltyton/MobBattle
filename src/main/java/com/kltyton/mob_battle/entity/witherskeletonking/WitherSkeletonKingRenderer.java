package com.kltyton.mob_battle.entity.witherskeletonking;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class WitherSkeletonKingRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<WitherSkeletonKingEntity, R> {
    public static final DataTicket<Boolean> CAN_HALO = DataTicket.create("can_halo", Boolean.class);
    public WitherSkeletonKingRenderer(EntityRendererFactory.Context context) {
        super(context, new WitherSkeletonKingEntityModel());
    }
    @Override
    public void renderFinal(R renderState, MatrixStack poseStack, BakedGeoModel model, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                            int packedLight, int packedOverlay, int renderColor) {
        Boolean canHalo = renderState.getOrDefaultGeckolibData(CAN_HALO, false);
        this.model.getBone("quan").ifPresent(bone -> {
            bone.setHidden(canHalo == null || !canHalo);
        });
        super.renderFinal(renderState, poseStack, model, bufferSource, buffer, packedLight, packedOverlay, renderColor);

    }
}
