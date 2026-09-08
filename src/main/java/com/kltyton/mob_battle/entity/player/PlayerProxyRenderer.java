package com.kltyton.mob_battle.entity.player;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.items.armor.support.ArmorSetRules;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.base.GeoRenderState;
import com.zigythebird.playeranim.accessors.IAnimatedAvatar;
import com.zigythebird.playeranim.accessors.IAvatarAnimationState;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.player.Player;

public class PlayerProxyRenderer<T extends Player & ClientAvatarEntity & GeoAnimatable,
        R extends AvatarRenderState & GeoRenderState> extends AvatarRenderer<T>
        implements IGeoEntityAnimationTickInvoker<T> {
    private final PlayerReplacedEntityRenderer<T, R> playerRenderer;
    private AvatarAnimManager fallbackAnimationManager;
    private boolean vanillaBodyFallback;

    public PlayerProxyRenderer(EntityRendererProvider.Context context, boolean slim) {
        super(context, slim);
        this.playerRenderer = new PlayerReplacedEntityRenderer<>(context);
    }

    @Override
    public void submit(AvatarRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        ensureAnimationManager(state);
        if (((IPlayerStateAccessor) state).isUsingGeckoLib()) {
            try {
                this.playerRenderer.submit((R) state, matrices, renderTasks, cameraState);
            } catch (IllegalArgumentException e) {
                if (e.getMessage() != null && e.getMessage().contains("GeoRenderState")) {
                    this.vanillaBodyFallback = true;
                    try {
                        super.submit(state, matrices, renderTasks, cameraState);
                    } finally {
                        this.vanillaBodyFallback = false;
                    }
                    return;
                } else {
                    throw e;
                }
            }
            super.submit(state, matrices, renderTasks, cameraState);
            return;
        }
        super.submit(state, matrices, renderTasks, cameraState);
    }

    @Override
    protected RenderType getRenderType(
            AvatarRenderState state,
            boolean isBodyVisible,
            boolean forceTransparent,
            boolean appearGlowing
    ) {
        if (((IPlayerStateAccessor) state).isUsingGeckoLib() && !this.vanillaBodyFallback) {
            return null;
        }
        return super.getRenderType(state, isBodyVisible, forceTransparent, appearGlowing);
    }

    private void ensureAnimationManager(AvatarRenderState state) {
        IAvatarAnimationState animationState = (IAvatarAnimationState) state;
        if (animationState.playerAnimLib$getAnimManager() != null) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        if (client.level != null && client.level.getEntity(state.id) instanceof IAnimatedAvatar animatedAvatar) {
            animationState.playerAnimLib$setAnimManager(animatedAvatar.playerAnimLib$getAnimManager());
            return;
        }
        if (client.player != null) {
            if (this.fallbackAnimationManager == null
                    || this.fallbackAnimationManager.getAvatar() != client.player) {
                this.fallbackAnimationManager = new AvatarAnimManager(client.player);
            }
            animationState.playerAnimLib$setAnimManager(this.fallbackAnimationManager);
        }
    }

    @Override
    public void extractRenderState(T entity, AvatarRenderState renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        if (entity instanceof AbstractClientPlayer player) {
            AvatarAnimManager animationManager = ((IAnimatedAvatar) player).playerAnimLib$getAnimManager();
            animationManager.setTickDelta(partialTick);
            ((IAvatarAnimationState) renderState).playerAnimLib$setAnimManager(animationManager);
            boolean usingGeckoLib = ((IPlayerEntityAccessor) player).isUsingGeckoLib();
            IPlayerStateAccessor playerState = (IPlayerStateAccessor) renderState;
            playerState.setUseGeckoLib(usingGeckoLib);
            playerState.setCompressedCopperPower(hasCompressedCopperPower(player));
            if (usingGeckoLib) {
                this.playerRenderer.extractRenderState((T) player, (R) renderState, partialTick);
            }
        }
    }

    private static boolean hasCompressedCopperPower(AbstractClientPlayer player) {
        return player.hasEffect(ModEffects.COMPRESSED_COPPER_CHARGED_ENTRY)
                && ArmorSetRules.hasFullArmor(player, ModMaterial.COMPRESSED_COPPER_ARMOR_INSTANCE);
    }

    @Override
    public void mobBattle$tickGeckoAnimations(T entity, float partialTick) {
        ((IGeoEntityAnimationTickInvoker<T>) this.playerRenderer).mobBattle$tickGeckoAnimations(entity, partialTick);
    }
}
