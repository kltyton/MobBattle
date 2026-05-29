package com.kltyton.mob_battle.entity.villager.villagerking;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class VillagerKingEntityModel extends GeoModel<VillagerKingEntity> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "villager_king");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "villager_king");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/villager_king/villager_king.png");

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
