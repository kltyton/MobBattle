package com.kltyton.mob_battle.mixin.client.render;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.accessor.IModEntityRenderState;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.utils.ArmorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow
    public abstract <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T entity);

    @Shadow
    public abstract <S extends EntityRenderState> EntityRenderer<?, ? super S> getRenderer(S state);

    @Shadow
    @Final
    private ItemModelResolver itemModelResolver;

    @Shadow
    @Final
    private Font font;

    @Unique
    private static final float BAR_WIDTH = 40.0F;
    @Unique
    private static final float BAR_HEIGHT = 6.0F;
    @Unique
    private static final int BACKGROUND_COLOR = 0x80000000;
    @Unique
    private static final int HEALTH_COLOR = 0xFF00FF00;
    @Unique
    private static final int TEXT_COLOR = -1;
    @Unique
    private static final float TEXT_SCALE = 0.02F;
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
    @Unique
    private final ItemStackRenderState mobBattle$markerItemRenderState = new ItemStackRenderState();

    //修复实体没有渲染器报错

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public <E extends Entity> void shouldRender(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (this.getRenderer(entity) == null) {
            Mob_battle.LOGGER.warn("[MobBattle][Render] Missing renderer for entity {}", entity.getClass().getName());
            /*
            Mob_battle.LOGGER.warn("实体 {} 没有渲染器", entity.getClass().getName());
            */
            cir.cancel();
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "submit", at = @At("TAIL"))
    private <S extends EntityRenderState> void mobBattle$submitCommonOverlays(
            S state,
            CameraRenderState cameraState,
            double x,
            double y,
            double z,
            PoseStack matrices,
            SubmitNodeCollector renderTasks,
            CallbackInfo ci
    ) {
        EntityRenderer<?, ? super S> renderer = this.getRenderer(state);
        if (renderer == null) {
            return;
        }

        Vec3 offset = renderer.getRenderOffset(state);
        matrices.pushPose();
        matrices.translate(x + offset.x(), y + offset.y(), z + offset.z());
        mobBattle$debugOverlayState(state);
        mobBattle$renderPigSpiritMark(state, matrices, renderTasks, cameraState);
        mobBattle$renderMarkerItem(state, matrices, renderTasks);
        mobBattle$renderHealthBar(state, matrices, renderTasks, cameraState);
        matrices.popPose();
    }

    @Unique
    private static long mobBattle$nextOverlayDebugLogMs;

    @Unique
    private static void mobBattle$debugOverlayState(EntityRenderState state) {
        IModEntityRenderState modState = (IModEntityRenderState) state;
        if (modState.getCompressedArmorMarkerType() == 0
                && modState.getPigSpiritMarkAmplifier() < 0
                && !modState.isHealthBarVisible()) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now < mobBattle$nextOverlayDebugLogMs) {
            return;
        }

        mobBattle$nextOverlayDebugLogMs = now + 2000L;
        Mob_battle.LOGGER.info(
                "[MobBattle][RenderOverlay] state={} markerMask={} pigMark={} healthVisible={} health={}/{} height={} light={} outline={}",
                state.getClass().getName(),
                modState.getCompressedArmorMarkerType(),
                modState.getPigSpiritMarkAmplifier(),
                modState.isHealthBarVisible(),
                modState.getHealthBarHealth(),
                modState.getHealthBarMaxHealth(),
                state.boundingBoxHeight,
                state.lightCoords,
                state.outlineColor
        );
    }

    @Unique
    private void mobBattle$renderMarkerItem(EntityRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        Item markerItem = null;
        int markerMask = ((IModEntityRenderState) state).getCompressedArmorMarkerType();
        if ((markerMask & NETHERITE_MARKER_MASK) != 0 && ArmorUtil.hasFullArmor(player, ModMaterial.COMPRESSED_NETHERITE_ARMOR_INSTANCE)) {
            markerItem = ModItems.COMPRESSED_NETHERITE_INGOT;
        } else if ((markerMask & DIAMOND_MARKER_MASK) != 0 && ArmorUtil.hasFullArmor(player, ModMaterial.COMPRESSED_DIAMOND_ARMOR_INSTANCE)) {
            markerItem = ModItems.COMPRESSED_DIAMOND;
        }
        if (markerItem == null) {
            return;
        }

        matrices.pushPose();
        matrices.translate(0.0F, state.boundingBoxHeight + 0.95F, 0.0F);
        matrices.mulPose(Axis.YP.rotationDegrees((state.ageInTicks * 8.0F) % 360.0F));
        matrices.scale(0.75F, 0.75F, 0.75F);
        this.itemModelResolver.updateForNonLiving(this.mobBattle$markerItemRenderState, new ItemStack(markerItem), ItemDisplayContext.GROUND, player);
        this.mobBattle$markerItemRenderState.submit(matrices, renderTasks, FULL_BRIGHT, OverlayTexture.NO_OVERLAY, state.outlineColor);
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

    @Unique
    private void mobBattle$renderHealthBar(EntityRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        IModEntityRenderState modState = (IModEntityRenderState) state;
        if (!modState.isHealthBarVisible()) {
            return;
        }

        float health = modState.getHealthBarHealth();
        float maxHealth = modState.getHealthBarMaxHealth();
        if (maxHealth <= 0.0F) {
            return;
        }

        matrices.pushPose();
        matrices.translate(0.0F, state.boundingBoxHeight + 0.65F, 0.0F);
        matrices.mulPose(cameraState.orientation);
        matrices.scale(-0.025F, -0.025F, 0.025F);

        float healthRatio = Math.max(0.0F, Math.min(1.0F, health / maxHealth));
        float filledWidth = BAR_WIDTH * healthRatio;
        float fillStart = BAR_WIDTH / 2.0F - filledWidth;
        if (filledWidth < BAR_WIDTH) {
            mobBattle$drawRectangle(matrices, renderTasks, -BAR_WIDTH / 2.0F, -BAR_HEIGHT / 2.0F, BAR_PLANE_Z, BAR_WIDTH - filledWidth, BAR_HEIGHT, BACKGROUND_COLOR);
        }
        if (filledWidth > 0.0F) {
            mobBattle$drawRectangle(matrices, renderTasks, fillStart, -BAR_HEIGHT / 2.0F, BAR_PLANE_Z, filledWidth, BAR_HEIGHT, HEALTH_COLOR);
        }
        matrices.popPose();
        mobBattle$renderHealthText(state, matrices, renderTasks, cameraState, health);
    }

    @Unique
    private void mobBattle$renderHealthText(EntityRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState, float health) {
        String healthText = String.format("%.1f", health);
        matrices.pushPose();
        matrices.translate(0.0F, state.boundingBoxHeight + 0.65F, 0.0F);
        matrices.mulPose(cameraState.orientation);
        matrices.translate(0.0F, 0.0F, BAR_PLANE_Z + 0.1);
        matrices.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);
        float x = -this.font.width(healthText) / 2.0F;
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
    private void mobBattle$drawRectangle(PoseStack matrices, SubmitNodeCollector renderTasks,
                                         float x, float y, float z, float width, float height, int color) {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        float alpha = (float) (color >> 24 & 255) / 255.0F;
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
