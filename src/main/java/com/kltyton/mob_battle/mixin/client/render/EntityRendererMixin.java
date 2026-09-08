package com.kltyton.mob_battle.mixin.client.render;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.accessor.IEffectMarker;
import com.kltyton.mob_battle.accessor.ILead;
import com.kltyton.mob_battle.accessor.ILeadRenderData;
import com.kltyton.mob_battle.accessor.IModEntityRenderState;
import com.kltyton.mob_battle.client.render.RenderSubmission;
import com.kltyton.mob_battle.config.MobBattleConfig;
import com.kltyton.mob_battle.entity.drone.DroneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Inject(
            method = "extractRenderState",
            at = @At("HEAD")
    )
    private void onUpdateRenderState(Entity entity, EntityRenderState state, float tickProgress, CallbackInfo ci) {
        IModEntityRenderState modState = (IModEntityRenderState) state;
        if (entity instanceof LivingEntity livingEntity) {
            IEffectMarker marker = (IEffectMarker) livingEntity;

            modState.setCompressedArmorMarkerType(marker.mobBattle$getCompressedArmorMarkerType());
            modState.setPigSpiritMarkAmplifier(marker.mobBattle$getPigSpiritMarkAmplifier());
            mobBattle$copyHealthBarState(livingEntity, modState);
            mobBattle$debugCopiedRenderState(entity, modState);
        } else {
            modState.setCompressedArmorMarkerType(0);
            modState.setPigSpiritMarkAmplifier(-1);
            modState.setHealthBarVisible(false);
            modState.setHealthBarHealth(0.0F);
            modState.setHealthBarMaxHealth(1.0F);
        }
    }

    @Unique
    private void mobBattle$copyHealthBarState(LivingEntity livingEntity, IModEntityRenderState modState) {
        LocalPlayer player = Minecraft.getInstance().player;
        boolean shouldRender = player != null && (player.isCreative() || player.isSpectator()) && livingEntity != player;
        if (player != null && livingEntity instanceof DroneEntity drone && drone.getOwner() == player) {
            shouldRender = true;
        }

        modState.setHealthBarVisible(shouldRender);
        modState.setHealthBarHealth(livingEntity.getHealth());
        modState.setHealthBarMaxHealth(Math.max(1.0F, livingEntity.getMaxHealth()));
    }

    @Unique
    private static long mobBattle$nextRenderStateDebugLogMs;

    @Unique
    private static void mobBattle$debugCopiedRenderState(Entity entity, IModEntityRenderState modState) {
        if (!MobBattleConfig.isDebugLoggingEnabled()) {
            return;
        }

        if (modState.getCompressedArmorMarkerType() == 0
                && modState.getPigSpiritMarkAmplifier() < 0
                && !modState.isHealthBarVisible()) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now < mobBattle$nextRenderStateDebugLogMs) {
            return;
        }

        mobBattle$nextRenderStateDebugLogMs = now + 2000L;
        Mob_battle.LOGGER.info(
                "[MobBattle][RenderState] entity={} id={} class={} markerMask={} pigMark={} healthVisible={} health={}/{}",
                entity.getType(),
                entity.getId(),
                entity.getClass().getName(),
                modState.getCompressedArmorMarkerType(),
                modState.getPigSpiritMarkAmplifier(),
                modState.isHealthBarVisible(),
                modState.getHealthBarHealth(),
                modState.getHealthBarMaxHealth()
        );
    }

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void mobBattle$updateLeashVisibility(Entity entity, EntityRenderState state, float tickProgress, CallbackInfo ci) {
        if (state.leashStates == null) return;
        boolean shouldRender = !(entity instanceof LivingEntity livingEntity && ((ILead) livingEntity).getIsInvisibleUniversalLeadEnyity());
        for (EntityRenderState.LeashState leashState : state.leashStates) {
            ((ILeadRenderData) leashState).setShouldRender(shouldRender);
        }
    }

    @Inject(
            method = "extractRenderState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isInvisible()Z")
    )
    private void onUpdateRenderStateTrueInvisible(Entity livingEntity, EntityRenderState livingEntityRenderState, float f, CallbackInfo ci) {
        ((IModEntityRenderState)livingEntityRenderState).setTrueInvisible(((IModEntityRenderState)livingEntity).isTrueInvisible());
    }

    @Inject(method = "submit", at = @At("HEAD"))
    private void mobBattle$removeHiddenLeashStates(EntityRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState, CallbackInfo ci) {
        if (state.leashStates == null) return;
        state.leashStates.removeIf(leashData -> !((ILeadRenderData) leashData).shouldRender());
        if (state.leashStates.isEmpty()) {
            state.leashStates = null;
        }
    }
    @Unique
    private BlockModelResolver blockModelResolver;
    @Unique
    private final BlockModelRenderState iceBlockRenderState = new BlockModelRenderState();
    @Inject(method = "<init>", at = @At("RETURN"))
    private void initSkillManager(EntityRendererProvider.Context context, CallbackInfo ci) {
        blockModelResolver = context.getBlockModelResolver();
    }
    @Inject(
            method = "submit",
            at = @At("RETURN")
    )
    private void renderIce(EntityRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState, CallbackInfo ci) {
        int amplifier = ((IModEntityRenderState)state).getIceAmplifier();
        if (amplifier >= 5) {
            matrices.pushPose();
            matrices.translate(-0.28F, 0.7F, -0.28F);
            matrices.scale(0.7F, 0.7F, 0.7F);
            RenderSubmission.submitBlock(this.blockModelResolver, this.iceBlockRenderState, Blocks.PACKED_ICE.defaultBlockState(),
                    matrices, renderTasks, state.lightCoords, state.outlineColor);
            matrices.popPose();
        }
    }
}
