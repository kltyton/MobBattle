package com.kltyton.mob_battle.entity.villager.warriorvillager;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class WarriorVillagerRenderer <R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<WarriorVillager, R> {
    public WarriorVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new WarriorVillagerModel());
    }
}
