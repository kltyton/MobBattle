package com.kltyton.mob_battle.entity.vindicatorgeneral;

import com.kltyton.mob_battle.entity.general.GeneralEntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class VindicatorGeneralAxeRenderer<R extends EntityRenderState & GeoRenderState>
        extends GeoEntityRenderer<VindicatorGeneralAxeEntity, R> {
    public VindicatorGeneralAxeRenderer(EntityRendererProvider.Context context) {
        super(context, new GeneralEntityModel<>("vindicator_general_axe", false, GeneralEntityModel.RenderTypes.CUTOUT));
    }
}
