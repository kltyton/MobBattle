package com.kltyton.mob_battle.mixin.client.render.entity.player;

import com.kltyton.mob_battle.entity.player.IGeoEntityAnimationTickInvoker;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

@Mixin(value = GeoEntityRenderer.class, remap = false)
public abstract class GeoEntityRendererAnimationTickMixin<T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends EntityRenderer<T, R> implements IGeoEntityAnimationTickInvoker<T> {
    protected GeoEntityRendererAnimationTickMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Shadow
    protected abstract R createBaseRenderState(T entity);

    @Shadow
    public abstract GeoModel<T> getGeoModel();

    @Override
    @SuppressWarnings("unchecked")
    public void mobBattle$tickGeckoAnimations(T entity, float partialTick) {
        R renderState = createBaseRenderState(entity);
        extractRenderState(entity, renderState, partialTick);
        getGeoModel().getBakedModel(getGeoModel().getModelResource(renderState));
        getGeoModel().handleAnimations(((GeoRenderer<T, Void, R>) this).createAnimationState(renderState));
    }
}
