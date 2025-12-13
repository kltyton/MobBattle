package com.kltyton.mob_battle.entity.deepcreature;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.CustomBoneTextureGeoLayer;
import software.bernie.geckolib.util.ClientUtil;

public class DeepCreatureEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<DeepCreatureEntity, R> {
    private static final Identifier EAR = Identifier.of(Mob_battle.MOD_ID, "textures/entity/deep_creature/ear.png");
    public static final DataTicket<Integer> ENTITY_ID = DataTicket.create("entity_id", Integer.class);
    public static final DataTicket<Boolean> IS_CATCH = DataTicket.create("is_catch", Boolean.class);
    public DeepCreatureEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new DeepCreatureEntityModel());
        addRenderLayer(new CustomBoneTextureGeoLayer<>(this, "h_ear", EAR) {
            @Override
            protected RenderLayer getRenderType(R renderState, Identifier texture) {
                return RenderLayer.getEntityCutoutNoCull(texture);
            }
        });
    }
    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0f;   // 自定义翻转角度
    }
    @Override
    public int getPackedOverlay(DeepCreatureEntity animatable, Void relatedObject, float u, float partialTick) {
        if (animatable == null)
            return OverlayTexture.DEFAULT_UV;

        return OverlayTexture.packUv(
                OverlayTexture.getU(u),
                OverlayTexture.getV(false)   // ← 这里
        );
    }
    @Override
    public void renderFinal(R renderState, MatrixStack poseStack, BakedGeoModel model, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                            int packedLight, int packedOverlay, int renderColor) {
        poseStack.push();
        super.renderFinal(renderState, poseStack, model, bufferSource, buffer, packedLight, packedOverlay, renderColor);
        World world = ClientUtil.getLevel();
        PlayerEntity player = ClientUtil.getClientPlayer();
        if (world != null && renderState.hasGeckolibData(ENTITY_ID)) {
            Integer entityIdObj = renderState.getGeckolibData(ENTITY_ID);
            DeepCreatureEntity entity = (DeepCreatureEntity) world.getEntityById(entityIdObj);
            if (entity != null && entity.hasSkill()) {
                this.model.getBone("right_hand").ifPresent(bone -> {
                    Random rand = ClientUtil.getLevel().getRandom();
                    Vector3d pos = bone.getWorldPosition();
                    world.addParticleClient(
                            ParticleTypes.TRIAL_OMEN,
                            pos.x + rand.nextGaussian() * 0.25, pos.y + rand.nextGaussian() * 0.5, pos.z + rand.nextGaussian() * 0.25,
                            0.0, 0.0, 0.0
                    );
                });
                this.model.getBone("left_hand").ifPresent(bone -> {
                    Random rand = ClientUtil.getLevel().getRandom();
                    Vector3d pos = bone.getWorldPosition();
                    world.addParticleClient(
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
                    player.setPosition(pos.x, pos.y, pos.z);
                    player.setVelocity(Vec3d.ZERO);
                    player.velocityModified = true;
                });
            }
        }
        poseStack.pop();
    }

}
