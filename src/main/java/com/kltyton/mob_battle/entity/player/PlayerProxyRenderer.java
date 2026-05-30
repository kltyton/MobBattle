package com.kltyton.mob_battle.entity.player;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class PlayerProxyRenderer<T extends Player & GeoAnimatable, R extends AvatarRenderState & GeoRenderState> extends AvatarRenderer implements IGeoEntityAnimationTickInvoker<T> {
    private final PlayerReplacedEntityRenderer<T, R> playerRenderer;

    public PlayerProxyRenderer(EntityRendererProvider.Context context, boolean slim) {
        super(context, slim);
        this.playerRenderer = new PlayerReplacedEntityRenderer<>(context);
    }

    @Override
    public void submit(EntityRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        if (state instanceof AvatarRenderState avatarState && ((IPlayerStateAccessor) avatarState).isUsingGeckoLib()) {
            try {
                this.playerRenderer.submit((R) avatarState, matrices, renderTasks, cameraState);
            } catch (IllegalArgumentException e) {
                if (e.getMessage() != null && e.getMessage().contains("GeoRenderState")) {
                    super.submit(state, matrices, renderTasks, cameraState);
                } else {
                    throw e;
                }
            }
        } else {
            super.submit(state, matrices, renderTasks, cameraState);
        }
    }

    @Override
    public void extractRenderState(Entity entity, EntityRenderState renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        if (entity instanceof AbstractClientPlayer player && renderState instanceof AvatarRenderState avatarRenderState) {
            boolean usingGeckoLib = ((IPlayerEntityAccessor) player).isUsingGeckoLib();
            ((IPlayerStateAccessor) avatarRenderState).setUseGeckoLib(usingGeckoLib);
            if (usingGeckoLib) {
                this.playerRenderer.extractRenderState((T) player, (R) avatarRenderState, partialTick);
            }
        }
    }

    @Override
    protected boolean shouldShowName(Avatar animatable, double distToCameraSq) {
        if (animatable instanceof AbstractClientPlayer player && ((IPlayerEntityAccessor) player).isUsingGeckoLib()) {
            return this.playerRenderer.shouldShowName((T) player, distToCameraSq);
        }

        return super.shouldShowName(animatable, distToCameraSq);
    }

    @Override
    public void mobBattle$tickGeckoAnimations(T entity, float partialTick) {
        ((IGeoEntityAnimationTickInvoker<T>) this.playerRenderer).mobBattle$tickGeckoAnimations(entity, partialTick);
    }
}
