package com.kltyton.mob_battle.client.renderer.entitydomain;

import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.RenderPassInfo;
import com.kltyton.mob_battle.entity.diamondgiant.DiamondGiantEntity;
import com.kltyton.mob_battle.entity.general.GeneralEntityModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.mesdag.particlestorm.api.geckolib.GeckoLibHelper;

/** 钻石巨人的 GeckoLib renderer；模型、动画和纹理均使用 diamond_giant 资源命名。 */
@Environment(EnvType.CLIENT)
public final class DiamondGiantRenderer<R extends LivingEntityRenderState & GeoRenderState>
        extends GeoEntityRenderer<DiamondGiantEntity, R> {
    private static final DataTicket<Integer> ATTACK_3_VISUAL_PHASE = DataTickets.create(
            "diamond_giant_attack3_visual_phase", Integer.class);
    private static final DataTicket<Vec3> ATTACK_3_DIRECTION = DataTickets.create(
            "diamond_giant_attack3_direction", Vec3.class);

    public DiamondGiantRenderer(EntityRendererProvider.Context context) {
        super(context, new GeneralEntityModel<>("diamond_giant", false, GeneralEntityModel.RenderTypes.CUTOUT));
    }

    @Override
    public void preRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        super.preRenderPass(renderPassInfo, renderTasks);
        GeckoLibHelper.attachLocatorListeners(renderPassInfo.renderState(), renderPassInfo);
    }

    @Override
    public void extractRenderState(DiamondGiantEntity entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(ATTACK_3_VISUAL_PHASE, entity.getAttack3VisualPhase());
        renderState.addGeckolibData(ATTACK_3_DIRECTION, entity.getViewVector(partialTick));
    }

    @Override
    public void submit(R renderState, PoseStack poseStack, SubmitNodeCollector renderTasks,
                       CameraRenderState cameraState) {
        super.submit(renderState, poseStack, renderTasks, cameraState);
        DiamondGiantLaserEffectRenderer.submit(
                poseStack,
                renderTasks,
                renderState.getOrDefaultGeckolibData(ATTACK_3_DIRECTION, Vec3.ZERO),
                renderState.eyeHeight,
                renderState.ageInTicks,
                renderState.getOrDefaultGeckolibData(
                        ATTACK_3_VISUAL_PHASE, DiamondGiantEntity.ATTACK_3_VISUAL_IDLE));
    }
}
