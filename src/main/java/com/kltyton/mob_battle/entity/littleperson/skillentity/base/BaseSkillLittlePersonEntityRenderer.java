package com.kltyton.mob_battle.entity.littleperson.skillentity.base;

import com.kltyton.mob_battle.client.render.GeoRenderUtil;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.utils.GeoAnimationUtil;
import com.geckolib.constant.dataticket.DataTicket;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

public class BaseSkillLittlePersonEntityRenderer<T extends LivingEntity & LittlePersonEntity, R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    private static final DataTicket<Boolean> HAS_DEATH_ANIMATION =
            DataTicket.create("mob_battle_has_death_animation", Boolean.class);

    public BaseSkillLittlePersonEntityRenderer(EntityRendererProvider.Context context, String name, boolean hasHand) {
        super(context, new BaseSkillLittlePersonEntityModel<>(name, hasHand));
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        GeoRenderUtil.applyHeadRotation(renderPassInfo, snapshots, "head", true);
    }

    @Override
    public void addRenderData(T animatable, Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(HAS_DEATH_ANIMATION, GeoAnimationUtil.hasDeathAnimation(animatable));
    }

    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return renderState.getOrDefaultGeckolibData(HAS_DEATH_ANIMATION, false)
                ? 0.0F
                : super.getDeathMaxRotation(renderState);
    }

    @Override
    public int getPackedOverlay(T animatable, Void relatedObject, float u, float partialTick) {
        if (animatable == null)
            return OverlayTexture.NO_OVERLAY;
        boolean hasDeathAnimation = GeoAnimationUtil.hasDeathAnimation(animatable);
        boolean showRedOverlay = animatable.isDeadOrDying()
                ? !hasDeathAnimation
                : animatable.hurtTime > 0;
        return OverlayTexture.pack(
                OverlayTexture.u(u),
                OverlayTexture.v(showRedOverlay)
        );
    }
}
