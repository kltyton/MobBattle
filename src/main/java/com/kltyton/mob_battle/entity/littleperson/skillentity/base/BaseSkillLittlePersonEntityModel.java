package com.kltyton.mob_battle.entity.littleperson.skillentity.base;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class BaseSkillLittlePersonEntityModel<T extends LivingEntity & LittlePersonEntity> extends GeoModel<T> {
    public String name;
    public boolean hasHand;
    public BaseSkillLittlePersonEntityModel(String name, boolean hasHand) {
        super();
        this.name = name;
        this.hasHand = hasHand;
    }
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, name);
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/little_person/" + name + ".png");
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        return Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, name);
    }
}
