package com.kltyton.mob_battle.entity.irongolem.hulkbuster;

import com.kltyton.mob_battle.client.render.GeoRenderUtil;
import com.kltyton.mob_battle.network.packet.HulkbusterEntityPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import com.geckolib.util.ClientUtil;

import java.util.UUID;

public class HulkbusterEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<HulkbusterEntity, R> {
    public HulkbusterEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new HulkbusterEntityModel());
        this.withRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
    public static final DataTicket<UUID> ENTITY_ID = DataTicket.create("entity_id", UUID.class);
    public static final DataTicket<Boolean> SYNC_CATCH = DataTicket.create("sync_catch", Boolean.class);

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        GeoRenderUtil.applyHeadRotation(renderPassInfo, snapshots, "Head", false);

        Boolean syncCatch = renderPassInfo.renderState().getOrDefaultGeckolibData(SYNC_CATCH, false);
        snapshots.ifPresent("yan", snapshot -> {
            boolean hidden = syncCatch == null || !syncCatch;
            snapshot.skipRender(hidden).skipChildrenRender(hidden);
        });
    }

    @Override
    public void preRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        super.preRenderPass(renderPassInfo, renderTasks);

        R renderState = renderPassInfo.renderState();
        Level world = ClientUtil.getLevel();
        if (world == null || !renderState.hasGeckolibData(ENTITY_ID)) {
            return;
        }

        UUID uuid = renderState.getGeckolibData(ENTITY_ID);
        Boolean syncCatch = renderState.getOrDefaultGeckolibData(SYNC_CATCH, false);
        if (uuid == null || !Boolean.TRUE.equals(syncCatch) || !(world.getEntity(uuid) instanceof HulkbusterEntity entity)) {
            return;
        }

        renderPassInfo.addBonePositionListener("right_muzzle", (worldPos, modelPos, localPos) -> {
            if (worldPos == null) {
                return;
            }
            Vec3 pos = new Vec3(worldPos.x, worldPos.y, worldPos.z);
            entity.rightMuzzle = pos;
            ClientPlayNetworking.send(new HulkbusterEntityPayload(uuid, pos, "right_muzzle"));
        });
        renderPassInfo.addBonePositionListener("left_muzzle", (worldPos, modelPos, localPos) -> {
            if (worldPos == null) {
                return;
            }
            Vec3 pos = new Vec3(worldPos.x, worldPos.y, worldPos.z);
            entity.leftMuzzle = pos;
            ClientPlayNetworking.send(new HulkbusterEntityPayload(uuid, pos, "left_muzzle"));
        });
    }

}
