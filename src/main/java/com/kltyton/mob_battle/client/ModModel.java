package com.kltyton.mob_battle.client;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.cbot.SnowmanIceBlockModel;
import com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile.MissileEntityModel;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntityModel;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.StoneArrowEntityModel;
import com.kltyton.mob_battle.entity.littleperson.skillentity.ironmanbullet.IronManBulletEntityModel;
import com.kltyton.mob_battle.entity.littleperson.skillentity.laser.LaserEntityModel;
import com.kltyton.mob_battle.entity.littleperson.skillentity.poisonousbullet.PoisonousBulletEntityModel;
import com.kltyton.mob_battle.entity.littleperson.skillentity.spearbullet.SpearBulletEntityModel;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import com.kltyton.mob_battle.entity.bullet.GoldenTrailProjectileModel;

public class ModModel {
    public static final ModelLayerLocation LITTLE_ARROW = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "little_arrow"), "bb_main");
    public static final ModelLayerLocation STONE_ARROW = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "stone_arrow"), "bone");
    public static final ModelLayerLocation POISON_ARROW = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "poison_arrow"), "bb_main");
    public static final ModelLayerLocation IRON_MAN_BULLET = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_man_bullet"), "bb_main");
    public static final ModelLayerLocation SPEAR_BULLET = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "spear_bullet"), "bb_main");
    public static final ModelLayerLocation MISSILE = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "missile"), "bone");
    public static final ModelLayerLocation GOLDEN_TRAIL_PROJECTILE = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "golden_trail_projectile"), "bb_main");
    public static final ModelLayerLocation LASER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "laser"), "bb_main");
    public static final ModelLayerLocation SNOWMAN_ICE_BLOCK = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "snowman_ice_block"), "bone7");

    public static void init() {
        ModelLayerRegistry.registerModelLayer(LITTLE_ARROW, LittleArrowEntityModel::getTexturedModelData);
        ModelLayerRegistry.registerModelLayer(STONE_ARROW, StoneArrowEntityModel::getTexturedModelData);
        ModelLayerRegistry.registerModelLayer(MISSILE, MissileEntityModel::getTexturedModelData);
        ModelLayerRegistry.registerModelLayer(POISON_ARROW, PoisonousBulletEntityModel::getTexturedModelData);
        ModelLayerRegistry.registerModelLayer(IRON_MAN_BULLET, IronManBulletEntityModel::getTexturedModelData);
        ModelLayerRegistry.registerModelLayer(SPEAR_BULLET, SpearBulletEntityModel::getTexturedModelData);
        ModelLayerRegistry.registerModelLayer(GOLDEN_TRAIL_PROJECTILE, GoldenTrailProjectileModel::getTexturedModelData);
        ModelLayerRegistry.registerModelLayer(LASER, LaserEntityModel::getTexturedModelData);
        ModelLayerRegistry.registerModelLayer(SNOWMAN_ICE_BLOCK, SnowmanIceBlockModel::getTexturedModelData);
    }
}
