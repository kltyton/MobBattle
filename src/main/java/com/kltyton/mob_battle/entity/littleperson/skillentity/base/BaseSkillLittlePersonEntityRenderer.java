package com.kltyton.mob_battle.entity.littleperson.skillentity.base;

import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BaseSkillLittlePersonEntityRenderer<T extends LivingEntity & LittlePersonEntity, R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    public BaseSkillLittlePersonEntityRenderer(EntityRendererFactory.Context context, String name, boolean hasHand) {
        super(context, new BaseSkillLittlePersonEntityModel<>(name, hasHand));
    }
    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0f;
    }
    @Override
    public int getPackedOverlay(T animatable, Void relatedObject, float u, float partialTick) {
        if (animatable == null)
            return OverlayTexture.DEFAULT_UV;
        return OverlayTexture.packUv(
                OverlayTexture.getU(u),
                OverlayTexture.getV(animatable.hurtTime > 0)
        );
    }
}
