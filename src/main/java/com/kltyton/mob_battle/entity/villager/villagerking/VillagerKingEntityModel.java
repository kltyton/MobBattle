package com.kltyton.mob_battle.entity.villager.villagerking;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class VillagerKingEntityModel extends GeoModel<VillagerKingEntity> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "villager_king");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "villager_king");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/villager_king/villager_king.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(VillagerKingEntity animatable) {
        return animations;
    }
}
