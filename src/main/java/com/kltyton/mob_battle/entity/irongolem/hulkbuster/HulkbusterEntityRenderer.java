package com.kltyton.mob_battle.entity.irongolem.hulkbuster;

import com.kltyton.mob_battle.network.packet.HulkbusterEntityPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.util.ClientUtil;

import java.util.UUID;

public class HulkbusterEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<HulkbusterEntity, R> {
    public HulkbusterEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new HulkbusterEntityModel());
    }
    public static final DataTicket<UUID> ENTITY_ID = DataTicket.create("entity_id", UUID.class);
    public static final DataTicket<Boolean> SYNC_CATCH = DataTicket.create("sync_catch", Boolean.class);

    @Override
    public void renderFinal(R renderState, MatrixStack poseStack, BakedGeoModel model, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                            int packedLight, int packedOverlay, int renderColor) {
        Boolean canHalo = renderState.getOrDefaultGeckolibData(SYNC_CATCH, false);
        this.model.getBone("yan").ifPresent(bone -> bone.setHidden(canHalo == null || !canHalo));
        World world = ClientUtil.getLevel();
        if (world != null && renderState.hasGeckolibData(ENTITY_ID)) {
            UUID uuid = renderState.getGeckolibData(ENTITY_ID);
            Boolean syncCatch = renderState.getOrDefaultGeckolibData(SYNC_CATCH, false);
            HulkbusterEntity entity = (HulkbusterEntity) world.getEntity(uuid);
            if (syncCatch != null && syncCatch) {
                this.model.getBone("right_muzzle").ifPresent(bone -> {
                    Vector3d pos = bone.getWorldPosition();
                    if (entity != null) {
                        Vec3d minecraft_pos = new Vec3d(pos.x, pos.y, pos.z);
                        entity.rightMuzzle = minecraft_pos;
                        ClientPlayNetworking.send(new HulkbusterEntityPayload(uuid, minecraft_pos, "right_muzzle"));
                    }
                });
                this.model.getBone("left_muzzle").ifPresent(bone -> {
                    Vector3d pos = bone.getWorldPosition();
                    Vec3d minecraft_pos = new Vec3d(pos.x, pos.y, pos.z);
                    if (entity != null) {
                        entity.leftMuzzle = minecraft_pos;
                        ClientPlayNetworking.send(new HulkbusterEntityPayload(uuid, minecraft_pos, "left_muzzle"));
                    }
                });
            }
        }
        super.renderFinal(renderState, poseStack, model, bufferSource, buffer, packedLight, packedOverlay, renderColor);
    }
}
