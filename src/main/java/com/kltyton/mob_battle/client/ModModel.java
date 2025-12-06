package com.kltyton.mob_battle.client;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntityModel;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModel {
    public static final EntityModelLayer LITTLE_ARROW = new EntityModelLayer(Identifier.of(Mob_battle.MOD_ID, "little_arrow"), "bb_main");

    public static void init() {
        EntityModelLayerRegistry.registerModelLayer(LITTLE_ARROW, LittleArrowEntityModel::getTexturedModelData);
    }
}
