package com.kltyton.mob_battle.entity.general;

import com.kltyton.mob_battle.client.render.GeoRenderUtil;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class GeneralEntityRenderer<T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    private final boolean hasHand;
    private final GeneralEntityModel.RenderTypes renderType;

    public GeneralEntityRenderer(EntityRendererProvider.Context context, String name, boolean hasHand, GeneralEntityModel.RenderTypes renderType) {
        super(context, new GeneralEntityModel<>(name, hasHand, renderType));
        this.hasHand = hasHand;
        this.renderType = renderType;
    }
    public GeneralEntityRenderer(EntityRendererProvider.Context context, String name, boolean hasHand) {
        this(context, name, hasHand, GeneralEntityModel.RenderTypes.CUTOUT);
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        if (this.hasHand) {
            GeoRenderUtil.applyHeadRotation(renderPassInfo, snapshots, "Head", true);
        }
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return switch (this.renderType) {
            case TRANSLUCENT -> RenderTypes.entityTranslucentEmissive(texture);
            case CUTOUT -> RenderTypes.entityCutout(texture);
        };
    }
}
