package com.kltyton.mob_battle.mixin.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.kltyton.mob_battle.entity.player.PlayerProxyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.player.PlayerModelType;

@Mixin(EntityRenderers.class)
public class EntityRenderersMixin {
    @Inject(method = "createAvatarRenderers", at = @At("HEAD"), cancellable = true)
    private static void replacePlayerRenderers(
            EntityRendererProvider.Context ctx,
            CallbackInfoReturnable<Map<PlayerModelType, AvatarRenderer<?>>> cir) {
        ImmutableMap.Builder<PlayerModelType, AvatarRenderer<?>> builder = ImmutableMap.builder();
        builder.put(PlayerModelType.WIDE, new PlayerProxyRenderer<>(ctx, false));
        builder.put(PlayerModelType.SLIM, new PlayerProxyRenderer<>(ctx, true));

        cir.setReturnValue(builder.build());
    }
}
