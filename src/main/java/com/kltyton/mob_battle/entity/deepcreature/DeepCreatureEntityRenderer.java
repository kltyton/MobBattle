package com.kltyton.mob_battle.entity.deepcreature;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.CustomBoneTextureGeoLayer;
import com.geckolib.util.ClientUtil;

public class DeepCreatureEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<DeepCreatureEntity, R> {
    private static final Identifier EAR = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/deep_creature/ear.png");
    public static final DataTicket<Integer> ENTITY_ID = DataTicket.create("entity_id", Integer.class);
    public static final DataTicket<Boolean> IS_CATCH = DataTicket.create("is_catch", Boolean.class);
    public DeepCreatureEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DeepCreatureEntityModel());
        withRenderLayer(new CustomBoneTextureGeoLayer<>(this, "h_ear", EAR) {
            @Override
            protected RenderType getRenderType(R renderState, Identifier texture) {
                return net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(texture);
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
    public void preRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        super.preRenderPass(renderPassInfo, renderTasks);

        R renderState = renderPassInfo.renderState();
        Level world = ClientUtil.getLevel();
        Player player = ClientUtil.getClientPlayer();
        if (world == null || player == null || !renderState.hasGeckolibData(ENTITY_ID)) {
            return;
        }

        Integer entityId = renderState.getGeckolibData(ENTITY_ID);
        if (entityId == null || !(world.getEntity(entityId) instanceof DeepCreatureEntity entity)) {
            return;
        }

        if (entity.hasSkill()) {
            renderPassInfo.addBonePositionListener("right_hand", (worldPos, modelPos, localPos) -> {
                if (worldPos == null) {
                    return;
                }
                RandomSource rand = world.getRandom();
                world.addParticle(
                        ParticleTypes.TRIAL_OMEN,
                        worldPos.x + rand.nextGaussian() * 0.25,
                        worldPos.y + rand.nextGaussian() * 0.5,
                        worldPos.z + rand.nextGaussian() * 0.25,
                        0.0, 0.0, 0.0
                );
            });
            renderPassInfo.addBonePositionListener("left_hand", (worldPos, modelPos, localPos) -> {
                if (worldPos == null) {
                    return;
                }
                RandomSource rand = world.getRandom();
                world.addParticle(
                        ParticleTypes.TRIAL_OMEN,
                        worldPos.x + rand.nextGaussian() * 0.25,
                        worldPos.y + rand.nextGaussian() * 0.5,
                        worldPos.z + rand.nextGaussian() * 0.25,
                        0.0, 0.0, 0.0
                );
            });
        }

        if (renderState.hasGeckolibData(IS_CATCH) && entity.getGrabTargetId() != -1 && entity.getGrabTargetId() == player.getId()) {
            renderPassInfo.addBonePositionListener("p_catch", (worldPos, modelPos, localPos) -> {
                if (worldPos == null || !player.isAlive()) {
                    return;
                }
                Vec3 pos = worldPos.add(0, -0.5, 0);
                if (pos.x() == 0 && pos.z() == 0) {
                    return;
                }
                player.setPos(pos.x, pos.y, pos.z);
                player.setDeltaMovement(Vec3.ZERO);
                player.hurtMarked = true;
            });
        }
    }

}
