package com.kltyton.mob_battle.client.renderer.entitydomain;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.diamondgiant.DiamondGiantEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

/**
 * 钻石巨人 attack3 的客户端三维能量束。
 *
 * <p>核心和外辉复用原版信标光束管线，外围折线使用同一全亮纹理提交；
 * 不参与命中判定，也不创建长期实体，因此多个巨人同时释放时只增加少量顶点。</p>
 */
final class DiamondGiantLaserEffectRenderer {
    private static final Identifier LASER_TEXTURE = Identifier.fromNamespaceAndPath(
            Mob_battle.MOD_ID, "textures/particle/laser.png");
    private static final int FULL_BRIGHT = 15728880;
    private static final int BLADE_COUNT = 4;
    private static final int RIBBON_COUNT = 3;
    private static final int RIBBON_SEGMENTS = 40;
    private static final int RING_SEGMENTS = 16;
    private static final int CRYSTAL_FIN_COUNT = 24;
    private static final float TWO_PI = (float) (Math.PI * 2.0D);
    private static final int[] RIBBON_COLORS = {0xD000EFFF, 0xA060F7FF, 0xB0FFFFFF};

    private DiamondGiantLaserEffectRenderer() {
    }

    static void submit(PoseStack poseStack, SubmitNodeCollector renderTasks, Vec3 direction,
                       float eyeHeight, float ageInTicks, int visualPhase) {
        if (visualPhase == DiamondGiantEntity.ATTACK_3_VISUAL_IDLE || direction.lengthSqr() < 1.0E-8D) {
            return;
        }

        Vec3 normalized = direction.normalize();
        poseStack.pushPose();
        poseStack.translate(0.0F, eyeHeight * 0.92F, 0.0F);
        poseStack.mulPose(new Quaternionf().rotationTo(
                0.0F, 1.0F, 0.0F,
                (float) normalized.x, (float) normalized.y, (float) normalized.z));
        poseStack.translate(0.0F, 0.45F, 0.0F);

        float pulse = 0.92F + 0.08F * Mth.sin(ageInTicks * 1.7F);
        float chargeScale = visualPhase == DiamondGiantEntity.ATTACK_3_VISUAL_FIRING
                ? 1.35F : (0.85F + 0.15F * Mth.sin(ageInTicks * 0.65F));
        submitBladeStar(poseStack, renderTasks, ageInTicks, chargeScale, 0xE000DFFF);
        submitBladeStar(poseStack, renderTasks, -ageInTicks * 1.3F, chargeScale * 0.52F, 0xFFFFFFFF);
        submitPulseRings(poseStack, renderTasks, ageInTicks,
                visualPhase == DiamondGiantEntity.ATTACK_3_VISUAL_FIRING ? 1.45F : 1.0F);

        if (visualPhase == DiamondGiantEntity.ATTACK_3_VISUAL_FIRING) {
            submitBeam(poseStack, renderTasks, ageInTicks, pulse);
        }
        poseStack.popPose();
    }

    private static void submitBeam(PoseStack poseStack, SubmitNodeCollector renderTasks,
                                   float ageInTicks, float pulse) {
        int length = Mth.ceil(DiamondGiantEntity.ATTACK_3_LASER_LENGTH);
        float glowRadius = (float) (DiamondGiantEntity.ATTACK_3_LASER_RADIUS * 0.44D) * pulse;

        BeaconRenderer.submitBeaconBeam(
                poseStack, renderTasks, LASER_TEXTURE, 0.18F, ageInTicks,
                0, length, 0xA000CFFF, 0.65F * pulse, glowRadius);
        BeaconRenderer.submitBeaconBeam(
                poseStack, renderTasks, LASER_TEXTURE, 0.3F, ageInTicks * 1.8F,
                0, length, 0xFFFFFFFF, 0.2F * pulse, 0.55F * pulse);
        submitTwistingRibbons(poseStack, renderTasks, ageInTicks, length, pulse);
        submitCrystalFins(poseStack, renderTasks, ageInTicks, length, RenderTypes.dragonRays());
        submitCrystalFins(poseStack, renderTasks, ageInTicks, length, RenderTypes.dragonRaysDepth());

        poseStack.pushPose();
        poseStack.translate(0.0F, length, 0.0F);
        submitBladeStar(poseStack, renderTasks, ageInTicks * 2.4F, 1.7F * pulse, 0xC000EFFF);
        submitBladeStar(poseStack, renderTasks, -ageInTicks * 3.0F, 0.9F * pulse, 0xFFFFFFFF);
        poseStack.popPose();
    }

    private static void submitBladeStar(PoseStack poseStack, SubmitNodeCollector renderTasks,
                                        float ageInTicks, float scale, int color) {
        float spin = ageInTicks * 0.22F;
        renderTasks.submitCustomGeometry(poseStack, RenderTypes.beaconBeam(LASER_TEXTURE, true),
                (pose, buffer) -> {
                    for (int blade = 0; blade < BLADE_COUNT; blade++) {
                        float angle = spin + blade * TWO_PI / BLADE_COUNT;
                        float radialX = Mth.cos(angle);
                        float radialZ = Mth.sin(angle);
                        float tangentX = -radialZ;
                        float tangentZ = radialX;
                        addVertex(buffer, pose, radialX * 0.12F * scale, -0.35F * scale,
                                radialZ * 0.12F * scale, color, 0.5F, 1.0F);
                        addVertex(buffer, pose,
                                (radialX * 0.8F + tangentX * 0.32F) * scale, 0.7F * scale,
                                (radialZ * 0.8F + tangentZ * 0.32F) * scale, color, 0.0F, 0.55F);
                        addVertex(buffer, pose, radialX * 2.7F * scale, 2.8F * scale,
                                radialZ * 2.7F * scale, color, 0.5F, 0.0F);
                        addVertex(buffer, pose,
                                (radialX * 0.8F - tangentX * 0.32F) * scale, 0.7F * scale,
                                (radialZ * 0.8F - tangentZ * 0.32F) * scale, color, 1.0F, 0.55F);
                    }
                });
    }

    private static void submitPulseRings(PoseStack poseStack, SubmitNodeCollector renderTasks,
                                         float ageInTicks, float scale) {
        renderTasks.submitCustomGeometry(poseStack, RenderTypes.beaconBeam(LASER_TEXTURE, true),
                (pose, buffer) -> {
                    for (int ring = 0; ring < 3; ring++) {
                        float radius = (1.15F + ring * 0.55F
                                + 0.12F * Mth.sin(ageInTicks * 0.8F + ring)) * scale;
                        float halfWidth = 0.09F * scale;
                        float y = 0.25F + ring * 0.72F;
                        float spin = ageInTicks * (ring % 2 == 0 ? 0.18F : -0.22F) + ring * 0.4F;
                        int color = ring == 1 ? 0xE0FFFFFF : 0xC000EFFF;
                        for (int segment = 0; segment < RING_SEGMENTS; segment++) {
                            float angle0 = spin + segment * TWO_PI / RING_SEGMENTS;
                            float angle1 = spin + (segment + 1) * TWO_PI / RING_SEGMENTS;
                            addRibbonVertex(buffer, pose, angle0, radius - halfWidth, y, color, 0.0F, 0.0F);
                            addRibbonVertex(buffer, pose, angle0, radius + halfWidth, y, color, 1.0F, 0.0F);
                            addRibbonVertex(buffer, pose, angle1, radius + halfWidth, y, color, 1.0F, 1.0F);
                            addRibbonVertex(buffer, pose, angle1, radius - halfWidth, y, color, 0.0F, 1.0F);
                        }
                    }
                });
    }

    private static void submitTwistingRibbons(PoseStack poseStack, SubmitNodeCollector renderTasks,
                                               float ageInTicks, float length, float pulse) {
        float segmentLength = length / RIBBON_SEGMENTS;
        float spin = ageInTicks * 0.34F;
        renderTasks.submitCustomGeometry(poseStack, RenderTypes.beaconBeam(LASER_TEXTURE, true),
                (pose, buffer) -> {
                    for (int ribbon = 0; ribbon < RIBBON_COUNT; ribbon++) {
                        float ribbonOffset = ribbon * TWO_PI / RIBBON_COUNT;
                        int color = RIBBON_COLORS[ribbon];
                        for (int segment = 0; segment < RIBBON_SEGMENTS; segment++) {
                            float y0 = segment * segmentLength;
                            float y1 = (segment + 1) * segmentLength;
                            float angle0 = spin + ribbonOffset + segment * 0.72F;
                            float angle1 = spin + ribbonOffset + (segment + 1) * 0.72F;
                            float radius0 = (1.25F + ((segment & 1) == 0 ? 0.7F : 0.15F)) * pulse;
                            float radius1 = (1.25F + ((segment & 1) == 0 ? 0.15F : 0.7F)) * pulse;
                            addRibbonVertex(buffer, pose, angle0 - 0.1F, radius0, y0, color, 0.0F, y0 / 6.0F);
                            addRibbonVertex(buffer, pose, angle0 + 0.1F, radius0, y0, color, 1.0F, y0 / 6.0F);
                            addRibbonVertex(buffer, pose, angle1 + 0.1F, radius1, y1, color, 1.0F, y1 / 6.0F);
                            addRibbonVertex(buffer, pose, angle1 - 0.1F, radius1, y1, color, 0.0F, y1 / 6.0F);
                        }
                    }
                });
    }

    private static void submitCrystalFins(PoseStack poseStack, SubmitNodeCollector renderTasks,
                                          float ageInTicks, float length, RenderType renderType) {
        renderTasks.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
            float spacing = (length - 8.0F) / CRYSTAL_FIN_COUNT;
            for (int fin = 0; fin < CRYSTAL_FIN_COUNT; fin++) {
                float y = 4.0F + (fin + 0.5F) * spacing;
                float angle = ageInTicks * 0.31F + fin * 1.37F;
                float radialX = Mth.cos(angle);
                float radialZ = Mth.sin(angle);
                float reach = 2.2F + (fin % 4) * 0.42F;
                float halfLength = 0.75F + (fin % 3) * 0.28F;
                buffer.addVertex(pose, radialX * 0.5F, y - halfLength, radialZ * 0.5F)
                        .setColor(0xE0FFFFFF);
                buffer.addVertex(pose, radialX * reach, y, radialZ * reach)
                        .setColor(0xB000DFFF);
                buffer.addVertex(pose, radialX * 0.5F, y + halfLength, radialZ * 0.5F)
                        .setColor(0xE0FFFFFF);
            }
        });
    }

    private static void addRibbonVertex(VertexConsumer buffer, PoseStack.Pose pose, float angle,
                                        float radius, float y, int color, float u, float v) {
        addVertex(buffer, pose, Mth.cos(angle) * radius, y, Mth.sin(angle) * radius, color, u, v);
    }

    private static void addVertex(VertexConsumer buffer, PoseStack.Pose pose,
                                  float x, float y, float z, int color, float u, float v) {
        buffer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(FULL_BRIGHT)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
