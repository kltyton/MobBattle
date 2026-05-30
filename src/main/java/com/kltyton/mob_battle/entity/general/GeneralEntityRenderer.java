package com.kltyton.mob_battle.entity.general;

import com.kltyton.mob_battle.client.render.GeoRenderUtil;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

public class GeneralEntityRenderer<T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    public GeneralEntityRenderer(EntityRendererProvider.Context context, String name, boolean hasHand, GeneralEntityModel.RenderTypes renderType) {
        super(context, new GeneralEntityModel<>(name, hasHand, renderType));
    }
    public GeneralEntityRenderer(EntityRendererProvider.Context context, String name, boolean hasHand) {
        super(context, new GeneralEntityModel<>(name, hasHand, GeneralEntityModel.RenderTypes.CUTOUT));
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        GeoRenderUtil.applyHeadRotation(renderPassInfo, snapshots, "head", true);
    }
}
