package com.kltyton.mob_battle.entity.villager.archervillager;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class ArcherVillagerModel extends GeoModel<ArcherVillager> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "archer_villager");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "archer_villager");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/archer_villager.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return this.model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return this.texture;
    }

    @Override
    public Identifier getAnimationResource(ArcherVillager animatable) {
        return this.animations;
    }
    @Override
    public void setCustomAnimations(AnimationState<ArcherVillager> animationState) {
        super.setCustomAnimations(animationState);
        GeoBone head = getAnimationProcessor().getBone("1");

        if (head != null) {
            float pitch = animationState.getData(DataTickets.ENTITY_PITCH);
            float yaw = animationState.getData(DataTickets.ENTITY_YAW);

            head.setRotX(-pitch * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(-yaw * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
