package com.kltyton.mob_battle.entity.piglingeneral;

import com.kltyton.mob_battle.entity.general.GeneralEntityModel;
import com.kltyton.mob_battle.network.packet.PiglinGeneralBonePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.util.ClientUtil;

import java.util.UUID;

public class PiglinGeneralEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<PiglinGeneralEntity, R> {
    public static final DataTicket<UUID> ENTITY_ID = DataTicket.create("piglin_general_entity_id", UUID.class);

    public PiglinGeneralEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new GeneralEntityModel<>("piglin_general", false, GeneralEntityModel.RenderTypes.CUTOUT));
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
        if (uuid == null || !(world.getEntity(uuid) instanceof PiglinGeneralEntity entity)) {
            return;
        }

        renderPassInfo.addBonePositionListener("sword_energy", (worldPos, modelPos, localPos) -> {
            if (worldPos == null) {
                return;
            }
            Vec3 swordEnergyPos = new Vec3(worldPos.x, worldPos.y, worldPos.z);
            entity.setSwordEnergyPos(swordEnergyPos);
            ClientPlayNetworking.send(new PiglinGeneralBonePayload(uuid, swordEnergyPos));
        });
    }

}
