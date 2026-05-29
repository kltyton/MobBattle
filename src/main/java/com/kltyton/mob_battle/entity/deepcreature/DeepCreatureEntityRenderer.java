package com.kltyton.mob_battle.entity.deepcreature;

import com.kltyton.mob_battle.Mob_battle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.CustomBoneTextureGeoLayer;
import software.bernie.geckolib.util.ClientUtil;

public class DeepCreatureEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<DeepCreatureEntity, R> {
    private static final ResourceLocation EAR = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/deep_creature/ear.png");
    public static final DataTicket<Integer> ENTITY_ID = DataTicket.create("entity_id", Integer.class);
    public static final DataTicket<Boolean> IS_CATCH = DataTicket.create("is_catch", Boolean.class);
    public DeepCreatureEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DeepCreatureEntityModel());
        addRenderLayer(new CustomBoneTextureGeoLayer<>(this, "h_ear", EAR) {
            @Override
            protected RenderType getRenderType(R renderState, ResourceLocation texture) {
                return RenderType.entityCutoutNoCull(texture);
            }
        });
    }
    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0f;
    }
    @Override
    public int getPackedOverlay(DeepCreatureEntity animatable, Void relatedObject, float u, float partialTick) {
        if (animatable == null)
            return OverlayTexture.NO_OVERLAY;

        return OverlayTexture.pack(
                OverlayTexture.u(u),
                OverlayTexture.v(false)
        );
    }
    @Override
    public void renderFinal(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                            int packedLight, int packedOverlay, int renderColor) {
        poseStack.pushPose();
        super.renderFinal(renderState, poseStack, model, bufferSource, buffer, packedLight, packedOverlay, renderColor);
        Level world = ClientUtil.getLevel();
        Player player = ClientUtil.getClientPlayer();
        if (world != null && renderState.hasGeckolibData(ENTITY_ID)) {
            Integer entityIdObj = renderState.getGeckolibData(ENTITY_ID);
            DeepCreatureEntity entity = null;
            if (entityIdObj != null) entity = (DeepCreatureEntity) world.getEntity(entityIdObj);
            if (entity != null && entity.hasSkill()) {
                this.model.getBone("right_hand").ifPresent(bone -> {
                    RandomSource rand = ClientUtil.getLevel().getRandom();
                    Vector3d pos = bone.getWorldPosition();
                    world.addParticle(
                            ParticleTypes.TRIAL_OMEN,
                            pos.x + rand.nextGaussian() * 0.25, pos.y + rand.nextGaussian() * 0.5, pos.z + rand.nextGaussian() * 0.25,
                            0.0, 0.0, 0.0
                    );
                });
                this.model.getBone("left_hand").ifPresent(bone -> {
                    RandomSource rand = ClientUtil.getLevel().getRandom();
                    Vector3d pos = bone.getWorldPosition();
                    world.addParticle(
                            ParticleTypes.TRIAL_OMEN,
                            pos.x + rand.nextGaussian() * 0.25, pos.y + rand.nextGaussian() * 0.5, pos.z + rand.nextGaussian() * 0.25,
                            0.0, 0.0, 0.0
                    );
                });
            }
            if (renderState.hasGeckolibData(IS_CATCH) && entity.getGrabTargetId() != -1 && entity.getGrabTargetId() == player.getId()) {
                this.model.getBone("p_catch").ifPresent(bone -> {
                    Vector3d pos = bone.getWorldPosition().add(0,0 - 0.5,0);
                    if (!player.isAlive()) {
                        return;
                    }
                    if (pos.x() == 0 && pos.z() == 0) return;
                    player.setPos(pos.x, pos.y, pos.z);
                    player.setDeltaMovement(Vec3.ZERO);
                    player.hurtMarked = true;
                });
            }
        }
        poseStack.popPose();
    }

}
