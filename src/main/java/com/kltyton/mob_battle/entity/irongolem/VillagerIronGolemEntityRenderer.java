package com.kltyton.mob_battle.entity.irongolem;

import com.kltyton.mob_battle.client.render.GeoRenderUtil;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

public class VillagerIronGolemEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<VillagerIronGolemEntity, R> {
    public VillagerIronGolemEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerIronGolemEntityModel());
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        GeoRenderUtil.applyHeadRotation(renderPassInfo, snapshots, "head", true);
    }
}
