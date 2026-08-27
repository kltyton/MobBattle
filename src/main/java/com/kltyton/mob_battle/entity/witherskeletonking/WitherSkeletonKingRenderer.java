package com.kltyton.mob_battle.entity.witherskeletonking;

import com.kltyton.mob_battle.client.render.ClientGeckoParticleEffects;
import com.kltyton.mob_battle.client.render.GeoRenderTransforms;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import com.geckolib.util.ClientUtil;

public class WitherSkeletonKingRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<WitherSkeletonKingEntity, R> {
    public static final DataTicket<Boolean> CAN_HALO = DataTicket.create("can_halo", Boolean.class);
    public WitherSkeletonKingRenderer(EntityRendererProvider.Context context) {
        super(context, new WitherSkeletonKingEntityModel());
        this.withRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        GeoRenderTransforms.applyHeadRotation(renderPassInfo, snapshots, "Head", false);

        Boolean canHalo = renderPassInfo.renderState().getOrDefaultGeckolibData(CAN_HALO, false);
        snapshots.ifPresent("quan", snapshot -> {
            boolean hidden = canHalo == null || !canHalo;
            snapshot.skipRender(hidden).skipChildrenRender(hidden);
        });
    }

    @Override
    public void preRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        super.preRenderPass(renderPassInfo, renderTasks);

        WitherSkeletonKingEntity entity = getEntity(renderPassInfo);
        if (entity == null) {
            return;
        }

        ClientGeckoParticleEffects.trackLocator(renderPassInfo, entity, "wither_skeleton_king_locator");
        ClientGeckoParticleEffects.trackLocator(renderPassInfo, entity, "wither_king_locator", "locator");
        ClientGeckoParticleEffects.trackLocator(renderPassInfo, entity, "wither_king_locator2", "locator2");
        ClientGeckoParticleEffects.trackLocator(renderPassInfo, entity, "wither_king_locator3", "locator3");
        ClientGeckoParticleEffects.trackLocator(renderPassInfo, entity, "wither_king_locator4", "locator4");
        ClientGeckoParticleEffects.trackLocator(renderPassInfo, entity, "wither_king_locator5", "locator5");
        ClientGeckoParticleEffects.trackLocator(renderPassInfo, entity, "wither_king_locator6", "locator6");
    }

    private WitherSkeletonKingEntity getEntity(RenderPassInfo<R> renderPassInfo) {
        Level world = ClientUtil.getLevel();
        Long instanceId = renderPassInfo.getGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID);
        if (world == null || instanceId == null) {
            return null;
        }

        Entity entity = world.getEntity(instanceId.intValue());
        return entity instanceof WitherSkeletonKingEntity witherSkeletonKing ? witherSkeletonKing : null;
    }
}
