package com.kltyton.mob_battle.mixin.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.kltyton.mob_battle.entity.player.PlayerProxyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.player.Player;

@Mixin(EntityRenderers.class)
public class EntityRenderersMixin {
    @Inject(method = "createPlayerRenderers", at = @At("HEAD"), cancellable = true)
    private static void replacePlayerRenderers(
            EntityRendererProvider.Context ctx,
            CallbackInfoReturnable<Map<PlayerSkin.Model, EntityRenderer<? extends Player, ?>>> cir) {
        ImmutableMap.Builder<PlayerSkin.Model, EntityRenderer<? extends Player, ?>> builder = ImmutableMap.builder();
        builder.put(PlayerSkin.Model.WIDE, new PlayerProxyRenderer<>(ctx, false));
        builder.put(PlayerSkin.Model.SLIM, new PlayerProxyRenderer<>(ctx, true));

        cir.setReturnValue(builder.build());
    }
}
