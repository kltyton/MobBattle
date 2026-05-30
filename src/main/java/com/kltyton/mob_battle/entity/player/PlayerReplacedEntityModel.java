package com.kltyton.mob_battle.entity.player;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class PlayerReplacedEntityModel<T extends Player & GeoAnimatable> extends GeoModel<T> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "player");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "player");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/player/player.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        return animations;
    }
}
