package com.kltyton.mob_battle.mixin.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.kltyton.mob_battle.entity.player.PlayerProxyRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EntityRenderers.class)
public class EntityRenderersMixin {
    @Inject(method = "reloadPlayerRenderers", at = @At("HEAD"), cancellable = true)
    private static void replacePlayerRenderers(
            EntityRendererFactory.Context ctx,
            CallbackInfoReturnable<Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity, ?>>> cir) {
        ImmutableMap.Builder<SkinTextures.Model, EntityRenderer<? extends PlayerEntity, ?>> builder = ImmutableMap.builder();
        builder.put(SkinTextures.Model.WIDE, new PlayerProxyRenderer<>(ctx, false));
        builder.put(SkinTextures.Model.SLIM, new PlayerProxyRenderer<>(ctx, true));

        cir.setReturnValue(builder.build());
    }
}
