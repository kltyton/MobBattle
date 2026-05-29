package com.kltyton.mob_battle.entity.skull.king;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class SkullKingEntityModel extends GeoModel<SkullKingEntity> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "skull_king");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "skull_king");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/skull/skull_king.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(SkullKingEntity animatable) {
        return animations;
    }
    @Override
    public void setCustomAnimations(AnimationState<SkullKingEntity> animationState) {
        super.setCustomAnimations(animationState);
        GeoBone head = getAnimationProcessor().getBone("Head");

        if (head != null) {
            float pitch = animationState.getDataOrDefault(DataTickets.ENTITY_PITCH, 0F);
            float yaw = animationState.getDataOrDefault(DataTickets.ENTITY_YAW, 0F);

            head.setRotX(pitch * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(-yaw * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
