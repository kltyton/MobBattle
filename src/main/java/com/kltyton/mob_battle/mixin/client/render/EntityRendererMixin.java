package com.kltyton.mob_battle.mixin.client.render;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.accessor.IEffectMarker;
import com.kltyton.mob_battle.accessor.ILead;
import com.kltyton.mob_battle.accessor.ILeadRenderData;
import com.kltyton.mob_battle.accessor.IModEntityRenderState;
import com.kltyton.mob_battle.client.render.SubmitRenderUtil;
import com.kltyton.mob_battle.config.MobBattleConfig;
import com.kltyton.mob_battle.entity.drone.DroneEntity;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.utils.ArmorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Shadow public abstract Font getFont();

    @Shadow @Final protected EntityRenderDispatcher entityRenderDispatcher;
    @Unique
    private static final float BAR_WIDTH = 40.0F;
    @Unique
    private static final float BAR_HEIGHT = 6.0F;
    @Unique
    private static final int BACKGROUND_COLOR = 0x80000000; // 半透明黑色
    @Unique
    private static final int HEALTH_COLOR = 0xFF00FF00;     // 绿色
    @Unique
    private static final float TEXT_SCALE = 0.02F;  // 文本缩放比例
    @Unique
    private static final int TEXT_COLOR = -1; // 白色文本
    @Unique
    private static final float BAR_PLANE_Z = 0.0F;
    @Unique
    private static final int DIAMOND_MARKER_MASK = 1;
    @Unique
    private static final int NETHERITE_MARKER_MASK = 2;
    @Unique
    private static final int FULL_BRIGHT = LightCoordsUtil.FULL_BRIGHT;
    @Unique
    private static final Identifier PIG_SPIRIT_MARK_TEXTURE = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/mob_effect/pig_spirit_mark.png");

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
    private ItemModelResolver itemModelManager;
    @Unique
    private BlockModelResolver blockModelResolver;
    @Unique
    private final BlockModelRenderState iceBlockRenderState = new BlockModelRenderState();
    @Unique
    private final ItemStackRenderState markerItemRenderState = new ItemStackRenderState();
    @Inject(method = "<init>", at = @At("RETURN"))
    private void initSkillManager(EntityRendererProvider.Context context, CallbackInfo ci) {
        itemModelManager = context.getItemModelResolver();
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
            SubmitRenderUtil.submitBlock(this.blockModelResolver, this.iceBlockRenderState, Blocks.PACKED_ICE.defaultBlockState(),
                    matrices, renderTasks, state.lightCoords, state.outlineColor);
            matrices.popPose();
        }
    }
    private void mobBattle$renderMarkerItem(EntityRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState, CallbackInfo ci) {
        mobBattle$renderPigSpiritMark(state, matrices, renderTasks, cameraState);
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        Item markerItem = null;
        int markerMask = ((IModEntityRenderState) state).getCompressedArmorMarkerType();
        if ((markerMask & NETHERITE_MARKER_MASK) != 0 && ArmorUtil.hasFullArmor(player, ModMaterial.COMPRESSED_NETHERITE_ARMOR_INSTANCE)) {
            markerItem = ModItems.COMPRESSED_NETHERITE_INGOT;
        } else if ((markerMask & DIAMOND_MARKER_MASK) != 0 && ArmorUtil.hasFullArmor(player, ModMaterial.COMPRESSED_DIAMOND_ARMOR_INSTANCE)) {
            markerItem = ModItems.COMPRESSED_DIAMOND;
        }
        if (markerItem == null) return;

        matrices.pushPose();
        matrices.translate(0.0F, state.boundingBoxHeight + 0.95F, 0.0F);
        matrices.mulPose(Axis.YP.rotationDegrees((state.ageInTicks * 8.0F) % 360.0F));
        matrices.scale(0.75F, 0.75F, 0.75F);
        itemModelManager.updateForNonLiving(markerItemRenderState, new ItemStack(markerItem), ItemDisplayContext.GROUND, player);
        markerItemRenderState.submit(matrices, renderTasks, FULL_BRIGHT, OverlayTexture.NO_OVERLAY, state.outlineColor);
        matrices.popPose();
    }
    @Unique
    private void mobBattle$renderPigSpiritMark(EntityRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        int amplifier = ((IModEntityRenderState) state).getPigSpiritMarkAmplifier();
        if (amplifier < 0) {
            return;
        }

        matrices.pushPose();
        matrices.translate(0.0F, state.boundingBoxHeight + 1.15F, 0.0F);
        matrices.mulPose(cameraState.orientation);
        matrices.scale(0.025F, -0.025F, 0.025F);
        mobBattle$drawPigSpiritMarkIcon(matrices, renderTasks, FULL_BRIGHT, -15.0F, -8.0F, 16.0F, 16.0F);
        renderTasks.submitText(
                matrices,
                4.0F,
                3.0F,
                Component.literal(String.valueOf(amplifier + 1)).getVisualOrderText(),
                false,
                Font.DisplayMode.NORMAL,
                FULL_BRIGHT,
                TEXT_COLOR,
                0,
                state.outlineColor
        );
        matrices.popPose();
    }
    private void renderHealthBar(EntityRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState, CallbackInfo ci) {
        IModEntityRenderState modState = (IModEntityRenderState) state;
        if (!modState.isHealthBarVisible()) return;

        float health = modState.getHealthBarHealth();
        float maxHealth = modState.getHealthBarMaxHealth();
        if (maxHealth <= 0.0F) return;

        float yOffset = state.boundingBoxHeight + 0.65F;
        matrices.pushPose();
        matrices.translate(0, yOffset, 0);
        matrices.mulPose(cameraState.orientation);
        matrices.scale(-0.025F, -0.025F, 0.025F);

        float healthRatio = Math.max(0.0F, Math.min(1.0F, health / maxHealth));
        float filledWidth = BAR_WIDTH * healthRatio;
        float fillStart = BAR_WIDTH / 2.0F - filledWidth;
        if (filledWidth < BAR_WIDTH) {
            drawRectangle(matrices, renderTasks, -BAR_WIDTH / 2, -BAR_HEIGHT / 2, BAR_PLANE_Z, BAR_WIDTH - filledWidth, BAR_HEIGHT, BACKGROUND_COLOR);
        }
        if (filledWidth > 0.0F) {
            drawRectangle(matrices, renderTasks, fillStart, -BAR_HEIGHT / 2.0F, BAR_PLANE_Z, filledWidth, BAR_HEIGHT, HEALTH_COLOR);
        }
        matrices.popPose();
        renderHealthText(state, matrices, renderTasks, cameraState, health);
    }
    @Unique
    protected void renderHealthText(EntityRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState, float health) {
        String healthText = String.format("%.1f", health);
        matrices.pushPose();
        matrices.translate(0.0F, state.boundingBoxHeight + 0.65F, 0.0F);
        matrices.mulPose(cameraState.orientation);
        matrices.translate(0.0F, 0.0F, BAR_PLANE_Z);
        matrices.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);
        Font textRenderer = this.getFont();
        float x = -textRenderer.width(healthText) / 2.0F;
        renderTasks.submitText(
                matrices,
                x,
                -4.0F,
                Component.literal(healthText).getVisualOrderText(),
                false,
                Font.DisplayMode.NORMAL,
                FULL_BRIGHT,
                TEXT_COLOR,
                0,
                state.outlineColor
        );
        matrices.popPose();
    }

    @Unique
    private void drawRectangle(PoseStack matrices, SubmitNodeCollector renderTasks,
                               float x, float y, float z,float width, float height, int color) {
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        renderTasks.submitCustomGeometry(matrices, net.minecraft.client.renderer.rendertype.RenderTypes.debugQuads(), (pose, buffer) -> {
            Matrix4f matrix = pose.pose();
            buffer.addVertex(matrix, x, y + height, z).setColor(red, green, blue, alpha);
            buffer.addVertex(matrix, x + width, y + height, z).setColor(red, green, blue, alpha);
            buffer.addVertex(matrix, x + width, y, z).setColor(red, green, blue, alpha);
            buffer.addVertex(matrix, x, y, z).setColor(red, green, blue, alpha);
        });
    }

    @Unique
    private void mobBattle$drawPigSpiritMarkIcon(PoseStack matrices, SubmitNodeCollector renderTasks, int light,
                                                float x, float y, float width, float height) {
        renderTasks.submitCustomGeometry(matrices, net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(PIG_SPIRIT_MARK_TEXTURE), (pose, buffer) -> {
            Matrix4f matrix = pose.pose();
            float z = 0.0F;
            buffer.addVertex(matrix, x, y + height, z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, 1.0F);
            buffer.addVertex(matrix, x + width, y + height, z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, 1.0F);
            buffer.addVertex(matrix, x + width, y, z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, 1.0F);
            buffer.addVertex(matrix, x, y, z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, 1.0F);
        });
    }
}
