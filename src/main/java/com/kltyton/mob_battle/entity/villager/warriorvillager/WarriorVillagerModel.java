package com.kltyton.mob_battle.entity.villager.warriorvillager;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class WarriorVillagerModel extends GeoModel<WarriorVillager> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "warrior_villager");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "warrior_villager");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/warrior_villager.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return this.model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return this.texture;
    }

    @Override
    public Identifier getAnimationResource(WarriorVillager animatable) {
        return this.animations;
    }
}
