package com.kltyton.mob_battle.mixin.client.render.entity.player;

import com.kltyton.mob_battle.entity.player.IGeoEntityAnimationTickInvoker;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

@Mixin(value = GeoEntityRenderer.class, remap = false)
public abstract class GeoEntityRendererAnimationTickMixin<T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends EntityRenderer<T, R> implements IGeoEntityAnimationTickInvoker<T> {
    protected GeoEntityRendererAnimationTickMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Shadow
    public abstract GeoModel<T> getGeoModel();

    @Override
    public void mobBattle$tickGeckoAnimations(T entity, float partialTick) {
        R renderState = this.createRenderState(entity, partialTick);
        this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(renderState));
    }
}
