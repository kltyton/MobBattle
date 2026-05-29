package com.kltyton.mob_battle.entity.littleperson.skillentity.base;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BaseSkillLittlePersonEntityModel<T extends LivingEntity & LittlePersonEntity> extends GeoModel<T> {
    public String name;
    public boolean hasHand;
    public BaseSkillLittlePersonEntityModel(String name, boolean hasHand) {
        super();
        this.name = name;
        this.hasHand = hasHand;
    }
    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, name);
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/little_person/" + name + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, name);
    }
    @Override
    public void setCustomAnimations(AnimationState<T> animationState) {
        super.setCustomAnimations(animationState);
        if (hasHand) {
            GeoBone head = getAnimationProcessor().getBone("Head");

            if (head != null) {
                float pitch = animationState.getDataOrDefault(DataTickets.ENTITY_PITCH, 0F);
                float yaw = animationState.getDataOrDefault(DataTickets.ENTITY_YAW, 0F);

                head.setRotX(-pitch * Mth.DEG_TO_RAD);
                head.setRotY(-yaw * Mth.DEG_TO_RAD);
            }
        }
    }
}
