package com.kltyton.mob_battle.entity.general;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class GeneralEntityModel<T extends GeoAnimatable> extends GeoModel<T> {
    public String name;
    public boolean hasHand;
    public GeneralEntityModel(String name, boolean hasHand) {
        super();
        this.name = name;
        this.hasHand = hasHand;
    }
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.of(Mob_battle.MOD_ID, name);
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Identifier.of(Mob_battle.MOD_ID, "textures/entity/" + name + "/" + name + ".png");
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        return Identifier.of(Mob_battle.MOD_ID, name);
    }
    @Override
    public void setCustomAnimations(AnimationState<T> animationState) {
        super.setCustomAnimations(animationState);
        if (hasHand) {
            GeoBone head = getAnimationProcessor().getBone("Head");

            if (head != null) {
                float pitch = animationState.getDataOrDefault(DataTickets.ENTITY_PITCH, 0F);
                float yaw = animationState.getDataOrDefault(DataTickets.ENTITY_YAW, 0F);

                head.setRotX(-pitch * MathHelper.RADIANS_PER_DEGREE);
                head.setRotY(-yaw * MathHelper.RADIANS_PER_DEGREE);
            }
        }
    }
}
