package com.kltyton.mob_battle.entity.voidcell;

import com.kltyton.mob_battle.client.render.GeoRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

public class VoidCellEntityRenderer <R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<VoidCellEntity, R> {
    public VoidCellEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new VoidCellEntityModel());
        this.shadowRadius = 0.1f;
        this.withScale(0.28f);
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        GeoRenderUtil.applyHeadRotation(renderPassInfo, snapshots, "head", false);
    }

    @Nullable
    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucentEmissive(texture);
    }
}
