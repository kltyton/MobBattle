package com.kltyton.mob_battle.client;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile.MissileEntityModel;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntityModel;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.StoneArrowEntityModel;
import com.kltyton.mob_battle.entity.littleperson.skillentity.ironmanbullet.IronManBulletEntityModel;
import com.kltyton.mob_battle.entity.littleperson.skillentity.poisonousbullet.PoisonousBulletEntityModel;
import com.kltyton.mob_battle.entity.littleperson.skillentity.spearbullet.SpearBulletEntityModel;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModel {
    public static final EntityModelLayer LITTLE_ARROW = new EntityModelLayer(Identifier.of(Mob_battle.MOD_ID, "little_arrow"), "bb_main");
    public static final EntityModelLayer STONE_ARROW = new EntityModelLayer(Identifier.of(Mob_battle.MOD_ID, "stone_arrow"), "bone");
    public static final EntityModelLayer POISON_ARROW = new EntityModelLayer(Identifier.of(Mob_battle.MOD_ID, "poison_arrow"), "bb_main");
    public static final EntityModelLayer IRON_MAN_BULLET = new EntityModelLayer(Identifier.of(Mob_battle.MOD_ID, "iron_man_bullet"), "bb_main");
    public static final EntityModelLayer SPEAR_BULLET = new EntityModelLayer(Identifier.of(Mob_battle.MOD_ID, "spear_bullet"), "bb_main");
    public static final EntityModelLayer MISSILE = new EntityModelLayer(Identifier.of(Mob_battle.MOD_ID, "missile"), "bone");

    public static void init() {
        EntityModelLayerRegistry.registerModelLayer(LITTLE_ARROW, LittleArrowEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(STONE_ARROW, StoneArrowEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MISSILE, MissileEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(POISON_ARROW, PoisonousBulletEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(IRON_MAN_BULLET, IronManBulletEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(SPEAR_BULLET, SpearBulletEntityModel::getTexturedModelData);
    }
}
