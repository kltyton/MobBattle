package com.kltyton.mob_battle.entity.piglingeneral;

import com.kltyton.mob_battle.entity.general.GeneralEntityModel;
import com.kltyton.mob_battle.network.packet.PiglinGeneralBonePayload;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.util.ClientUtil;

import java.util.UUID;

public class PiglinGeneralEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<PiglinGeneralEntity, R> {
    public static final DataTicket<UUID> ENTITY_ID = DataTicket.create("piglin_general_entity_id", UUID.class);

    public PiglinGeneralEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new GeneralEntityModel<>("piglin_general", false, GeneralEntityModel.RenderTypes.CUTOUT));
    }

    @Override
    public void renderFinal(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                            int packedLight, int packedOverlay, int renderColor) {
        Level world = ClientUtil.getLevel();
        if (world != null && renderState.hasGeckolibData(ENTITY_ID)) {
            UUID uuid = renderState.getGeckolibData(ENTITY_ID);
            if (world.getEntity(uuid) instanceof PiglinGeneralEntity entity) {
                this.model.getBone("sword_energy").ifPresent(bone -> {
                    Vector3d pos = bone.getWorldPosition();
                    Vec3 swordEnergyPos = new Vec3(pos.x, pos.y, pos.z);
                    entity.setSwordEnergyPos(swordEnergyPos);
                    ClientPlayNetworking.send(new PiglinGeneralBonePayload(uuid, swordEnergyPos));
                });
            }
        }
        super.renderFinal(renderState, poseStack, model, bufferSource, buffer, packedLight, packedOverlay, renderColor);
    }
}
