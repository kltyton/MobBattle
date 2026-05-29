package com.kltyton.mob_battle.mixin.client.render;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.accessor.IEffectMarker;
import com.kltyton.mob_battle.accessor.ILead;
import com.kltyton.mob_battle.accessor.ILeadRenderData;
import com.kltyton.mob_battle.accessor.IModEntityRenderState;
import com.kltyton.mob_battle.entity.drone.DroneEntity;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.utils.ArmorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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
    private static final int DIAMOND_MARKER_MASK = 1;
    @Unique
    private static final int NETHERITE_MARKER_MASK = 2;
    @Unique
    private static final ResourceLocation PIG_SPIRIT_MARK_TEXTURE = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/mob_effect/pig_spirit_mark.png");
    @Unique
    private LivingEntity targetEntity;

    @Inject(
            method = "extractRenderState",
            at = @At("HEAD")
    )
    private void onUpdateRenderState(Entity entity, EntityRenderState state, float tickProgress, CallbackInfo ci) {
        if (entity instanceof LivingEntity livingEntity) {
            this.targetEntity = livingEntity;

            IEffectMarker marker = (IEffectMarker) livingEntity;
            IModEntityRenderState modState = (IModEntityRenderState) state;

            modState.setCompressedArmorMarkerType(marker.mobBattle$getCompressedArmorMarkerType());
            modState.setPigSpiritMarkAmplifier(marker.mobBattle$getPigSpiritMarkAmplifier());
        } else {
            this.targetEntity = null;

            IModEntityRenderState modState = (IModEntityRenderState) state;
            modState.setCompressedArmorMarkerType(0);
            modState.setPigSpiritMarkAmplifier(-1);
        }
    }
    @Inject(
            method = "extractRenderState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;yRot(F)Lnet/minecraft/world/phys/Vec3;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void onUpdateLeashDatas(Entity livingEntity, EntityRenderState state, float tickProgress, CallbackInfo ci, boolean bl, Leashable leashable, Entity entity2, float g, Vec3 vec3d, BlockPos blockPos, BlockPos blockPos2, int i, int j, int k, int l, boolean bl2, int m, float h, Vec3 vec3d2, Vec3[] vec3ds, Vec3[] vec3ds2, int o, EntityRenderState.LeashState leashData) {
        if (leashData == null) return;
        if (livingEntity instanceof LivingEntity livingEntity1) {
           if (((ILead)livingEntity1).getIsInvisibleUniversalLeadEnyity()) {
               ((ILeadRenderData)leashData).setShouldRender(false);
            }
        }

    }
    @Inject(
            method = "extractRenderState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", ordinal = 2), locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void onUpdateLeashDatas_2(Entity livingEntity, EntityRenderState state, float tickProgress, CallbackInfo ci, boolean bl, Leashable leashable, Entity entity2, float g, Vec3 vec3d, BlockPos blockPos, BlockPos blockPos2, int i, int j, int k, int l, boolean bl2, int m, Vec3 vec3d3, EntityRenderState.LeashState leashData2) {
        if (leashData2 == null) return;
        if (livingEntity instanceof LivingEntity livingEntity1) {
            if (((ILead)livingEntity1).getIsInvisibleUniversalLeadEnyity()) {
                ((ILeadRenderData)leashData2).setShouldRender(false);
            }
        }

    }

    @Inject(
            method = "extractRenderState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isInvisible()Z")
    )
    private void onUpdateRenderStateTrueInvisible(Entity livingEntity, EntityRenderState livingEntityRenderState, float f, CallbackInfo ci) {
        ((IModEntityRenderState)livingEntityRenderState).setTrueInvisible(((IModEntityRenderState)livingEntity).isTrueInvisible());
    }

    @Inject(method = "renderLeash", at = @At("HEAD"), cancellable = true)
    private static void renderLeash(PoseStack matrices, MultiBufferSource vertexConsumers, EntityRenderState.LeashState leashData, CallbackInfo ci) {
        if (!((ILeadRenderData)leashData).shouldRender()) {
            ci.cancel();
        }
    }
    @Unique
    private BlockRenderDispatcher blockRenderManager;
    @Unique
    private ItemModelResolver itemModelManager;
    @Unique
    private final ItemStackRenderState markerItemRenderState = new ItemStackRenderState();
    @Inject(method = "<init>", at = @At("RETURN"))
    private void initSkillManager(EntityRendererProvider.Context context, CallbackInfo ci) {
        blockRenderManager = context.getBlockRenderDispatcher();
        itemModelManager = context.getItemModelResolver();
    }
    @Inject(
            method = "render",
            at = @At("RETURN")
    )
    private void renderIce(EntityRenderState state, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        int amplifier = ((IModEntityRenderState)state).getIceAmplifier();
        if (amplifier >= 5) {
            matrices.pushPose();
            matrices.translate(-0.28F, 0.7F, -0.28F);
            matrices.scale(0.7F, 0.7F, 0.7F);

            blockRenderManager.renderSingleBlock(
                    Blocks.PACKED_ICE.defaultBlockState(),
                    matrices,
                    vertexConsumers,
                    light,
                    OverlayTexture.NO_OVERLAY
            );
            matrices.popPose();
        }
    }
    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void mobBattle$renderMarkerItem(EntityRenderState state, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        mobBattle$renderPigSpiritMark(state, matrices, vertexConsumers, light);
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
        markerItemRenderState.render(matrices, vertexConsumers, light, OverlayTexture.NO_OVERLAY);
        matrices.popPose();
    }
    @Unique
    private void mobBattle$renderPigSpiritMark(EntityRenderState state, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        int amplifier = ((IModEntityRenderState) state).getPigSpiritMarkAmplifier();
        if (amplifier < 0) {
            return;
        }
        String text = String.valueOf(amplifier + 1);
        matrices.pushPose();
        matrices.translate(0.0F, state.boundingBoxHeight + 1.15F, 0.0F);
        matrices.mulPose(this.entityRenderDispatcher.cameraOrientation());
        matrices.scale(0.025F, -0.025F, 0.025F);
        mobBattle$drawPigSpiritMarkIcon(matrices, vertexConsumers, light, -15.0F, -8.0F, 16.0F, 16.0F);
        Matrix4f matrix4f = matrices.last().pose();
        this.getFont().drawInBatch(
                text,
                4.0F,
                3.0F,
                TEXT_COLOR,
                false,
                matrix4f,
                vertexConsumers,
                Font.DisplayMode.NORMAL,
                0,
                light
        );
        matrices.popPose();
    }
    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void renderHealthBar(EntityRenderState state, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (targetEntity == null) return;
        boolean canNotRender = player == null || !(player.isCreative() || player.isSpectator()) || targetEntity == player;
        if (targetEntity instanceof DroneEntity drone) {
            if (drone.getOwner() == player) canNotRender = false;
        }
        // 仅创造模式可见检查
        if (canNotRender) return;
        //if (targetEntity.isInvisibleTo(player)) return;
        // 计算血条位置（基于实体碰撞箱）
        float yOffset = targetEntity.getBbHeight() + 0.65F;
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        matrices.pushPose();
        // 将血条定位到实体头顶
        matrices.translate(0, yOffset, 0);
        // 保持血条始终面向摄像机
        matrices.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
        matrices.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        // 缩放控制血条大小
        matrices.scale(-0.025F, -0.025F, 0.025F);
        // 计算生命值比例
        float healthRatio = targetEntity.getHealth() / targetEntity.getMaxHealth();
        float filledWidth = BAR_WIDTH * healthRatio;

        // 绘制背景条
        drawRectangle(matrices, vertexConsumers,
                -BAR_WIDTH/2, -BAR_HEIGHT/2, 0.02f,
                BAR_WIDTH, BAR_HEIGHT,
                BACKGROUND_COLOR);

        // 绘制当前血量条
        drawRectangle(matrices, vertexConsumers,
                -BAR_WIDTH/2, -BAR_HEIGHT/2, 0.01f,
                filledWidth, BAR_HEIGHT,
                HEALTH_COLOR);
        matrices.popPose();
        renderHealthText(matrices, vertexConsumers, light);
    }
    @Unique
    protected void renderHealthText(PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        String healthText = String.format("%.1f", targetEntity.getHealth());
        float yOffset = targetEntity.getBbHeight() + 0.65F;
        matrices.pushPose();
        matrices.translate(0, yOffset, 0);
        matrices.mulPose(this.entityRenderDispatcher.cameraOrientation());
        matrices.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);
        Matrix4f matrix4f = matrices.last().pose();
        Font textRenderer = this.getFont();
        float f = -textRenderer.width(healthText) / 2.0F;
        float y = -4F; // 微调垂直位置
        textRenderer.drawInBatch(
                healthText,
                f,
                y,
                TEXT_COLOR,
                false,
                matrix4f,
                vertexConsumers,
                Font.DisplayMode.NORMAL,
                0,
                light
        );

        matrices.popPose();
    }

    @Unique
    private void drawRectangle(PoseStack matrices, MultiBufferSource vertexConsumers,
                               float x, float y, float z,float width, float height, int color) {
        PoseStack.Pose entry = matrices.last();
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        float alpha = (float)(color >> 24 & 255) / 255.0F;

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderType.debugQuads());

        // 构建四边形顶点数据
        buffer.addVertex(entry.pose(), x, y + height, z)
                .setColor(red, green, blue, alpha);
        buffer.addVertex(entry.pose(), x + width, y + height, z)
                .setColor(red, green, blue, alpha);
        buffer.addVertex(entry.pose(), x + width, y, z)
                .setColor(red, green, blue, alpha);
        buffer.addVertex(entry.pose(), x, y, z)
                .setColor(red, green, blue, alpha);
    }

    @Unique
    private void mobBattle$drawPigSpiritMarkIcon(PoseStack matrices, MultiBufferSource vertexConsumers, int light,
                                                float x, float y, float width, float height) {
        Matrix4f matrix = matrices.last().pose();
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(PIG_SPIRIT_MARK_TEXTURE));
        float z = 0.0F;
        buffer.addVertex(matrix, x, y + height, z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, 1.0F);
        buffer.addVertex(matrix, x + width, y + height, z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, 1.0F);
        buffer.addVertex(matrix, x + width, y, z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, 1.0F);
        buffer.addVertex(matrix, x, y, z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, 1.0F);
    }
}
