package com.kltyton.mob_battle.entity.vindicatorgeneral;

import com.kltyton.mob_battle.entity.general.GeneralEntityModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class VindicatorGeneralAxeRenderer<R extends EntityRenderState & GeoRenderState>
        extends GeoEntityRenderer<VindicatorGeneralAxeEntity, R> {
    public VindicatorGeneralAxeRenderer(EntityRendererFactory.Context context) {
        super(context, new GeneralEntityModel<>("vindicator_general_axe", false, GeneralEntityModel.RenderTypes.CUTOUT));
    }
}
