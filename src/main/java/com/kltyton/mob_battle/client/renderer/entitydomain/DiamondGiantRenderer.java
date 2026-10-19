package com.kltyton.mob_battle.client.renderer.entitydomain;

import com.geckolib.renderer.base.RenderPassInfo;
import com.kltyton.mob_battle.entity.diamondgiant.DiamondGiantEntity;
import com.kltyton.mob_battle.entity.general.GeneralEntityModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.mesdag.particlestorm.api.geckolib.GeckoLibHelper;

/** 钻石巨人的 GeckoLib renderer；模型、动画和纹理均使用 diamond_giant 资源命名。 */
@Environment(EnvType.CLIENT)
public final class DiamondGiantRenderer<R extends LivingEntityRenderState & GeoRenderState>
        extends GeoEntityRenderer<DiamondGiantEntity, R> {
    public DiamondGiantRenderer(EntityRendererProvider.Context context) {
        super(context, new GeneralEntityModel<>("diamond_giant", false, GeneralEntityModel.RenderTypes.CUTOUT));
    }

    @Override
    public void preRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        super.preRenderPass(renderPassInfo, renderTasks);
        GeckoLibHelper.attachLocatorListeners(renderPassInfo.renderState(), renderPassInfo);
    }

}
