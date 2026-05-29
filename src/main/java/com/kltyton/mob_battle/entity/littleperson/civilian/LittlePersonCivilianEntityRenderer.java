package com.kltyton.mob_battle.entity.littleperson.civilian;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class LittlePersonCivilianEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LittlePersonCivilianEntity, R> {
    public LittlePersonCivilianEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new LittlePersonCivilianEntityModel());
    }
}
