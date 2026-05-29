package com.kltyton.mob_battle.entity.irongolem;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class VillagerIronGolemEntityModel extends GeoModel<VillagerIronGolemEntity> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "irongolem");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "irongolem");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/irongolem/irongolem.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(VillagerIronGolemEntity animatable) {
        return animations;
    }
    @Override
    public void setCustomAnimations(AnimationState<VillagerIronGolemEntity> animationState) {
        super.setCustomAnimations(animationState);
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            float pitch = animationState.getDataOrDefault(DataTickets.ENTITY_PITCH, 0F);
            float yaw = animationState.getDataOrDefault(DataTickets.ENTITY_YAW, 0F);

            head.setRotX(-pitch * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(-yaw * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}