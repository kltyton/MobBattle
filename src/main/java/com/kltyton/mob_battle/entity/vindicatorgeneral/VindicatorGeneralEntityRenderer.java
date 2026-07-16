package com.kltyton.mob_battle.entity.vindicatorgeneral;

import com.kltyton.mob_battle.client.render.ClientGeckoParticleEffects;
import com.kltyton.mob_battle.client.render.GeoRenderUtil;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.util.ClientUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class VindicatorGeneralEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<VindicatorGeneralEntity, R> {

    public VindicatorGeneralEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new VindicatorGeneralEntityModel());
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        GeoRenderUtil.applyHeadRotation(renderPassInfo, snapshots, "Head", false);
    }

    @Override
    public void preRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        super.preRenderPass(renderPassInfo, renderTasks);

        VindicatorGeneralEntity entity = getEntity(renderPassInfo);
        if (entity == null) {
            return;
        }

        ClientGeckoParticleEffects.trackLocator(renderPassInfo, entity, "vindicator_locator");
        ClientGeckoParticleEffects.trackLocator(renderPassInfo, entity, "vindicator_locator2");
        ClientGeckoParticleEffects.trackLocator(renderPassInfo, entity, "vindicator_locator3");
        ClientGeckoParticleEffects.trackLocator(renderPassInfo, entity, "vindicator_locator4");
        ClientGeckoParticleEffects.trackLocator(renderPassInfo, entity, "vindicator_locator5");
    }

    private VindicatorGeneralEntity getEntity(RenderPassInfo<R> renderPassInfo) {
        Level world = ClientUtil.getLevel();
        Long instanceId = renderPassInfo.getGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID);
        if (world == null || instanceId == null) {
            return null;
        }

        Entity entity = world.getEntity(instanceId.intValue());
        return entity instanceof VindicatorGeneralEntity vindicatorGeneral ? vindicatorGeneral : null;
    }
}
