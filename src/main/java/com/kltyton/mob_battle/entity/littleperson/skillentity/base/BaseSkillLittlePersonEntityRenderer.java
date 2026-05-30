package com.kltyton.mob_battle.entity.littleperson.skillentity.base;

import com.kltyton.mob_battle.client.render.GeoRenderUtil;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

public class BaseSkillLittlePersonEntityRenderer<T extends LivingEntity & LittlePersonEntity, R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    public BaseSkillLittlePersonEntityRenderer(EntityRendererProvider.Context context, String name, boolean hasHand) {
        super(context, new BaseSkillLittlePersonEntityModel<>(name, hasHand));
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        GeoRenderUtil.applyHeadRotation(renderPassInfo, snapshots, "head", true);
    }

    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0f;
    }
    @Override
    public int getPackedOverlay(T animatable, Void relatedObject, float u, float partialTick) {
        if (animatable == null)
            return OverlayTexture.NO_OVERLAY;
        return OverlayTexture.pack(
                OverlayTexture.u(u),
                OverlayTexture.v(animatable.hurtTime > 0)
        );
    }
}
