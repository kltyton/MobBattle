package com.kltyton.mob_battle.entity.witherskeletonking;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class WitherSkeletonKingRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<WitherSkeletonKingEntity, R> {
    public static final DataTicket<Boolean> CAN_HALO = DataTicket.create("can_halo", Boolean.class);
    public WitherSkeletonKingRenderer(EntityRendererProvider.Context context) {
        super(context, new WitherSkeletonKingEntityModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
    @Override
    public void renderFinal(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                            int packedLight, int packedOverlay, int renderColor) {
        Boolean canHalo = renderState.getOrDefaultGeckolibData(CAN_HALO, false);
        this.model.getBone("quan").ifPresent(bone -> {
            bone.setHidden(canHalo == null || !canHalo);
        });
        super.renderFinal(renderState, poseStack, model, bufferSource, buffer, packedLight, packedOverlay, renderColor);

    }
}
