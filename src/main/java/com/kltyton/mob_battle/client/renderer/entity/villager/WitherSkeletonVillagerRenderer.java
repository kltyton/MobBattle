package com.kltyton.mob_battle.client.renderer.entity.villager;

import com.kltyton.mob_battle.entity.villager.trading.WitherSkeletonVillagerEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.skeleton.SkeletonModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.Identifier;

/** 使用原版凋零骷髅模型和纹理渲染可交易村民。 */
public final class WitherSkeletonVillagerRenderer extends MobRenderer<
        WitherSkeletonVillagerEntity,
        SkeletonRenderState,
        SkeletonModel<SkeletonRenderState>> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/skeleton/wither_skeleton.png");

    public WitherSkeletonVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletonModel<>(context.bakeLayer(ModelLayers.WITHER_SKELETON)), 0.5F);
    }

    @Override
    public SkeletonRenderState createRenderState() {
        return new SkeletonRenderState();
    }

    @Override
    public Identifier getTextureLocation(SkeletonRenderState state) {
        return TEXTURE;
    }
}
