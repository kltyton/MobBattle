package com.kltyton.mob_battle.mixin.client.render.entity.player;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Mixin(value = GeoEntityRenderer.class, remap = false)
public interface IGeoEntityRendererAccessor<T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> {
}
