package com.kltyton.mob_battle.client;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.client.render.ModPiglinBruteRenderer;
import com.kltyton.mob_battle.entity.blueirongolem.BlueIronGolemEntityRenderer;
import com.kltyton.mob_battle.entity.bullet.BulletEntityRenderer;
import com.kltyton.mob_battle.entity.bullet.GoldenBulletEntityRenderer;
import com.kltyton.mob_battle.entity.bullet.GoldenTrailProjectileRenderer;
import com.kltyton.mob_battle.entity.bullet.IceArrowEntityRenderer;
import com.kltyton.mob_battle.entity.cbot.SnowmanIceBlockRenderer;
import com.kltyton.mob_battle.entity.cbot.CbotSnowballRenderer;
import com.kltyton.mob_battle.entity.customfireball.render.CustomSuperBigFireballEntityRenderer;
import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntityRenderer;
import com.kltyton.mob_battle.entity.drone.attackdrone.AttackDroneEntityRenderer;
import com.kltyton.mob_battle.entity.drone.treatmentdrone.TreatmentDroneEntityRenderer;
import com.kltyton.mob_battle.entity.firewall.FireWallEntityRenderer;
import com.kltyton.mob_battle.entity.general.GeneralEntityModel;
import com.kltyton.mob_battle.entity.general.GeneralEntityOnlyOneSkill;
import com.kltyton.mob_battle.entity.general.GeneralEntityRenderer;
import com.kltyton.mob_battle.entity.golem.ChestGolemRenderer;
import com.kltyton.mob_battle.entity.golem.StrongMinRenderer;
import com.kltyton.mob_battle.entity.hiddeneye.HiddenEyeEntityRenderer;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntityRenderer;
import com.kltyton.mob_battle.entity.highbird.baby.HighbirdBabyEntityRenderer;
import com.kltyton.mob_battle.entity.highbird.egg.HighbirdEggEntityRenderer;
import com.kltyton.mob_battle.entity.highbird.teenage.HighbirdTeenageEntityRenderer;
import com.kltyton.mob_battle.entity.irongolem.VillagerIronGolemEntityRenderer;
import com.kltyton.mob_battle.entity.irongolem.hulkbuster.HulkbusterEntityRenderer;
import com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile.MissileEntityRenderer;
import com.kltyton.mob_battle.entity.littleperson.archer.LittlePersonArcherEntityRenderer;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntityRender;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.StoneArrowEntityRender;
import com.kltyton.mob_battle.entity.littleperson.civilian.LittlePersonCivilianEntityRenderer;
import com.kltyton.mob_battle.entity.littleperson.civilian.LittlePersonWorkerEntityRenderer;
import com.kltyton.mob_battle.entity.littleperson.giant.LittlePersonGiantEntityRenderer;
import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntityRenderer;
import com.kltyton.mob_battle.entity.littleperson.king.LittlePersonKingEntityRenderer;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntityRenderer;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntityRenderer;
import com.kltyton.mob_battle.entity.littleperson.skillentity.ironmanbullet.IronManBulletEntityRenderer;
import com.kltyton.mob_battle.entity.littleperson.skillentity.laser.LaserEntityRenderer;
import com.kltyton.mob_battle.entity.littleperson.skillentity.LaserManEntityRenderer;
import com.kltyton.mob_battle.entity.littleperson.skillentity.poisonousbullet.PoisonousBulletEntityRenderer;
import com.kltyton.mob_battle.entity.littleperson.skillentity.requestedprojectile.KnifeProjectileModel;
import com.kltyton.mob_battle.entity.littleperson.skillentity.requestedprojectile.SevenHarvestBulletModel;
import com.kltyton.mob_battle.entity.littleperson.skillentity.requestedprojectile.SkeletonHeadProjectileModel;
import com.kltyton.mob_battle.entity.littleperson.skillentity.requestedprojectile.TexturedSkillProjectileRenderer;
import com.kltyton.mob_battle.entity.littleperson.skillentity.spearbullet.SpearBulletEntityRenderer;
import com.kltyton.mob_battle.entity.lobster.LobsterEntityRenderer;
import com.kltyton.mob_battle.entity.lobster.MagmaLobsterEntityRenderer;
import com.kltyton.mob_battle.entity.meteorite.MeteoriteEntityRender;
import com.kltyton.mob_battle.entity.min.YoungMinEntityRenderer;
import com.kltyton.mob_battle.entity.piglingeneral.PiglinGeneralEntityRenderer;
import com.kltyton.mob_battle.entity.skull.archer.SkullArcherEntityRenderer;
import com.kltyton.mob_battle.entity.skull.king.SkullKingEntityRenderer;
import com.kltyton.mob_battle.entity.skull.mage.SkullMageEntityRenderer;
import com.kltyton.mob_battle.entity.skull.warrior.SkullWarriorEntityRenderer;
import com.kltyton.mob_battle.entity.silverfish.silverfish.AngrySilverfishRenderer;
import com.kltyton.mob_battle.entity.sugarmanscorpion.SugarManScorpionRenderer;
import com.kltyton.mob_battle.entity.villager.archervillager.ArcherVillagerRenderer;
import com.kltyton.mob_battle.entity.villager.militia.MilitiaArcherVillagerRenderer;
import com.kltyton.mob_battle.entity.villager.militia.MilitiaWarriorVillagerRenderer;
import com.kltyton.mob_battle.entity.villager.villagerking.VillagerKingEntityRenderer;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillagerRenderer;
import com.kltyton.mob_battle.entity.vindicatorgeneral.VindicatorGeneralEntityRenderer;
import com.kltyton.mob_battle.entity.vindicatorgeneral.VindicatorGeneralAxeRenderer;
import com.kltyton.mob_battle.entity.voidcell.VoidCellEntityRenderer;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingRenderer;
import com.kltyton.mob_battle.entity.witherskeletonking.skill.WitherSkullBulletEntityRenderer;
import com.kltyton.mob_battle.entity.xunsheng.XunShengEntityRenderer;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.world.entity.EntityType;

public class ModEntityRenderer {
    public static void init() {
/*        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            @SuppressWarnings({"unchecked"})
            FeatureRendererContext<LivingEntityRenderState, EntityModel<LivingEntityRenderState>> featureContext = (FeatureRendererContext<LivingEntityRenderState, EntityModel<LivingEntityRenderState>>) entityRenderer;
            registrationHelper.register(new IceChestBlockFeatureRenderer<>(featureContext));
        });*/
        EntityRenderers.register(ModEntities.MILITIA_WARRIOR_VILLAGER, MilitiaWarriorVillagerRenderer::new);
        EntityRenderers.register(ModEntities.MILITIA_ARCHER_VILLAGER, MilitiaArcherVillagerRenderer::new);
        EntityRenderers.register(ModEntities.WARRIOR_VILLAGER, WarriorVillagerRenderer::new);
        EntityRenderers.register(ModEntities.ARCHER_VILLAGER, ArcherVillagerRenderer::new);
        EntityRenderers.register(ModEntities.XUN_SHENG, XunShengEntityRenderer::new);
        EntityRenderers.register(ModEntities.DEEP_CREATURE, DeepCreatureEntityRenderer::new);
        EntityRenderers.register(ModEntities.WITHER_SKELETON_KING, WitherSkeletonKingRenderer::new);
        EntityRenderers.register(ModEntities.VINDICATOR_GENERAL, VindicatorGeneralEntityRenderer::new);
        EntityRenderers.register(ModEntities.VINDICATOR_GENERAL_AXE, VindicatorGeneralAxeRenderer::new);
        EntityRenderers.register(ModEntities.HULKBUSTER, HulkbusterEntityRenderer::new);
        EntityRenderers.register(ModEntities.SKULL_KING, SkullKingEntityRenderer::new);
        EntityRenderers.register(ModEntities.SKULL_ARCHER, SkullArcherEntityRenderer::new);
        EntityRenderers.register(ModEntities.SKULL_WARRIOR, SkullWarriorEntityRenderer::new);
        EntityRenderers.register(ModEntities.SKULL_MAGE, SkullMageEntityRenderer::new);
        EntityRenderers.register(ModEntities.NEW_SKULL_MAGE, ctx -> new BaseSkillLittlePersonEntityRenderer<>(ctx, "new_skull_mage", false));
        EntityRenderers.register(ModEntities.SUMMONED_SKELETON, SkeletonRenderer::new);
        EntityRenderers.register(ModEntities.SUMMONED_VEX, VexRenderer::new);
        EntityRenderers.register(ModEntities.YOUNG_MIN, YoungMinEntityRenderer::new);
        EntityRenderers.register(ModEntities.HIDDEN_EYE, HiddenEyeEntityRenderer::new);
        EntityRenderers.register(ModEntities.HIGHBIRD_BABY, HighbirdBabyEntityRenderer::new);
        EntityRenderers.register(ModEntities.HIGHBIRD_EGG, HighbirdEggEntityRenderer::new);
        EntityRenderers.register(ModEntities.HIGHBIRD_TEENAGE, HighbirdTeenageEntityRenderer::new);
        EntityRenderers.register(ModEntities.HIGHBIRD_ADULTHOOD, HighbirdAdulthoodEntityRenderer::new);
        EntityRenderers.register(ModEntities.BIG_CUSTOM_FIREBALL, CustomSuperBigFireballEntityRenderer::new);
        EntityRenderers.register(ModEntities.METEORITE, MeteoriteEntityRender::new);
        EntityRenderers.register(ModEntities.FIRE_WALL, FireWallEntityRenderer::new);
        EntityRenderers.register(ModEntities.BLUE_IRON_GOLEM, BlueIronGolemEntityRenderer::new);
        EntityRenderers.register(ModEntities.SUGAR_MAN_SCORPION, SugarManScorpionRenderer::new);
        EntityRenderers.register(ModEntities.BULLET_ENTITY, BulletEntityRenderer::new);
        EntityRenderers.register(ModEntities.WITHER_SKULL_BULLET_ENTITY, WitherSkullBulletEntityRenderer::new);
        EntityRenderers.register(ModEntities.VILLAGER_IRON_GOLEM_ENTITY, VillagerIronGolemEntityRenderer::new);
        EntityRenderers.register(ModEntities.VILLAGER_KING_ENTITY, VillagerKingEntityRenderer::new);
        EntityRenderers.register(ModEntities.ATTACK_DRONE, AttackDroneEntityRenderer::new);
        EntityRenderers.register(ModEntities.TREATMENT_DRONE, TreatmentDroneEntityRenderer::new);
        EntityRenderers.register(ModEntities.LITTLE_PERSON_CIVILIAN, LittlePersonCivilianEntityRenderer::new);
        EntityRenderers.register(ModEntities.LITTLE_PERSON_WORKER, LittlePersonWorkerEntityRenderer::new);
        EntityRenderers.register(ModEntities.LITTLE_PERSON_MILITIA, LittlePersonMilitiaEntityRenderer::new);
        EntityRenderers.register(ModEntities.LITTLE_PERSON_ARCHER, LittlePersonArcherEntityRenderer::new);
        EntityRenderers.register(ModEntities.LITTLE_PERSON_GIANT, LittlePersonGiantEntityRenderer::new);
        EntityRenderers.register(ModEntities.LITTLE_ARROW, LittleArrowEntityRender::new);
        EntityRenderers.register(ModEntities.STONE_ARROW, StoneArrowEntityRender::new);
        EntityRenderers.register(ModEntities.POISON_ARROW, PoisonousBulletEntityRenderer::new);
        EntityRenderers.register(ModEntities.SPEAR_BULLET, SpearBulletEntityRenderer::new);
        EntityRenderers.register(ModEntities.IRON_MAN_BULLET_ENTITY, IronManBulletEntityRenderer::new);
        EntityRenderers.register(ModEntities.LITTLE_PERSON_GUARD, LittlePersonGuardEntityRenderer::new);
        EntityRenderers.register(ModEntities.LITTLE_PERSON_KING, LittlePersonKingEntityRenderer::new);
        EntityRenderers.register(ModEntities.VOID_CELL, VoidCellEntityRenderer::new);
        EntityRenderers.register(ModEntities.MISSILE, MissileEntityRenderer::new);
        EntityRenderers.register(ModEntities.ICE_ARROW, IceArrowEntityRenderer::new);
        EntityRenderers.register(ModEntities.GOLDEN_TRAIL_PROJECTILE, GoldenTrailProjectileRenderer::new);
        EntityRenderers.register(ModEntities.GOLDEN_BULLET, GoldenBulletEntityRenderer::new);
        EntityRenderers.register(ModEntities.LITTLE_STONE_PROJECTILE, ThrownItemRenderer::new);
        EntityRenderers.register(ModEntities.ELEMENTAL_SWORD_PROJECTILE, ThrownItemRenderer::new);
        EntityRenderers.register(ModEntities.SNOWMAN_ICE_BLOCK, SnowmanIceBlockRenderer::new);
        EntityRenderers.register(ModEntities.CBOT_SNOWBALL, CbotSnowballRenderer::new);
        EntityRenderers.register(ModEntities.NEW_SNOW_GOLEM, SnowGolemRenderer::new);
        EntityRenderers.register(ModEntities.CHEST_GOLEM, ChestGolemRenderer::new);
        EntityRenderers.register(ModEntities.STRONG_MIN, StrongMinRenderer::new);
        EntityRenderers.register(ModEntities.PIGLIN_GENERAL, PiglinGeneralEntityRenderer::new);
        EntityRenderers.register(ModEntities.MODIFIED_DRAGON_BREATH_CLOUD, NoopRenderer::new);
        EntityRenderers.register(ModEntities.COAL_SILVERFISH, ctx -> new GeneralEntityRenderer<>(ctx, "coal_silverfish", false));
        EntityRenderers.register(ModEntities.ANGRY_SILVERFISH, ctx -> new GeneralEntityRenderer<>(ctx, "angry_silverfish", false));
        EntityRenderers.register(ModEntities.BOW_ZOMBIE_MOD, ZombieRenderer::new);
        EntityRenderers.register(ModEntities.PIGLIN_BRUTE_SPEAR_MOD, ModPiglinBruteRenderer::new);
        EntityRenderers.register(ModEntities.SILENCE_PHANTOM, ctx -> new GeneralEntityRenderer<>(ctx, "silence_phantom", false));
        EntityRenderers.register(ModEntities.SHIELD, ctx -> new GeneralEntityRenderer<>(ctx, "shield_force_field", false, GeneralEntityModel.RenderTypes.TRANSLUCENT));
        EntityRenderers.register(ModEntities.SUPER_EVOKER, EvokerRenderer::new);
        EntityRenderers.register(ModEntities.LOBSTER, LobsterEntityRenderer::new);
        EntityRenderers.register(ModEntities.MAGMA_LOBSTER, MagmaLobsterEntityRenderer::new);
        EntityRenderers.register(ModEntities.ENDER_DRAGON_METEORITE, ctx -> new GeneralEntityRenderer<>(ctx, "ender_dragon_meteorite", false));
        EntityRenderers.register(ModEntities.MAGMA_LOBBER_BIG_FIREBALL, ctx -> new GeneralEntityRenderer<>(ctx, "magma_lobber_big_fireball", false));
        EntityRenderers.register(ModEntities.ENHANCED_WITHER, WitherBossRenderer::new);
        EntityRenderers.register(ModEntities.DUAL_BLADE_WITHER_SKELETON, ctx -> new GeneralEntityRenderer<>(ctx, "dual_blade_wither_skeleton", false));
        EntityRenderers.register(ModEntities.SHIELD_AXE_WITHER_SKELETON, ctx -> new GeneralEntityRenderer<>(ctx, "shield_axe_wither_skeleton", false));
        EntityRenderers.register(ModEntities.LASER, LaserEntityRenderer::new);
        EntityRenderers.register(ModEntities.SEVEN_HARVEST_BULLET,
                ctx -> new TexturedSkillProjectileRenderer(ctx, ModModel.SEVEN_HARVEST_BULLET,
                        TexturedSkillProjectileRenderer.texture("seven_harvest_bullet"), 1.0F, SevenHarvestBulletModel.class));
        EntityRenderers.register(ModEntities.SEVEN_HARVEST_EXPLOSIVE_BULLET,
                ctx -> new TexturedSkillProjectileRenderer(ctx, ModModel.SEVEN_HARVEST_EXPLOSIVE_BULLET,
                        TexturedSkillProjectileRenderer.texture("seven_harvest_explosive_bullet"), 1.0F, SevenHarvestBulletModel.class));
        EntityRenderers.register(ModEntities.KNIFE_PROJECTILE,
                ctx -> new TexturedSkillProjectileRenderer(ctx, ModModel.KNIFE_PROJECTILE,
                        TexturedSkillProjectileRenderer.texture("knife_projectile"), 1.0F, KnifeProjectileModel.class));
        EntityRenderers.register(ModEntities.SKELETON_HEAD_PROJECTILE,
                ctx -> new TexturedSkillProjectileRenderer(ctx, ModModel.SKELETON_HEAD_PROJECTILE,
                        TexturedSkillProjectileRenderer.texture("skeleton_head_projectile"), 1.0F, SkeletonHeadProjectileModel.class));
        EntityRenderers.register(ModEntities.LASER_MAN, LaserManEntityRenderer::new);
        EntityRenderers.register(ModEntities.BLOOD_SWORD_ENERGY, ctx -> new GeneralEntityRenderer<>(ctx, "blood_sword_energy", false, GeneralEntityModel.RenderTypes.TRANSLUCENT));
        EntityRenderers.register(ModEntities.NINJA_CLONE, ctx -> new GeneralEntityRenderer<>(ctx, "ninja_clone", false));
        EntityRenderers.register(ModEntities.ICE_SWORD_ENERGY, ctx -> new GeneralEntityRenderer<>(ctx, "ice_sword_energy", false, GeneralEntityModel.RenderTypes.TRANSLUCENT));
        EntityRenderers.register(ModEntities.ICE_BOMB, ctx -> new GeneralEntityRenderer<>(ctx, "ice_bomb", false, GeneralEntityModel.RenderTypes.TRANSLUCENT));
        EntityRenderers.register(ModEntities.ICE_FANGS, ctx -> new GeneralEntityRenderer<>(ctx, "ice_fangs", false, GeneralEntityModel.RenderTypes.TRANSLUCENT));

        ModEntities.LITTLE_PERSON_ENTITIES.forEach((id, entityType) -> {
            if ("laser_man".equals(id)) {
                return;
            }
            @SuppressWarnings("unchecked")
            EntityType<? extends BaseSkillLittlePersonEntity> mobType = (EntityType<? extends BaseSkillLittlePersonEntity>) entityType;
            EntityRenderers.register(mobType, ctx -> new BaseSkillLittlePersonEntityRenderer<>(ctx, id, false));
        });
        ModEntities.GENERAL_RENDERERS.forEach((id, entityType) -> {
            if ("piglin_general".equals(id)) {
                return;
            }
            @SuppressWarnings("unchecked")
            EntityType<? extends GeneralEntityOnlyOneSkill<?>> mobType = (EntityType<? extends GeneralEntityOnlyOneSkill<?>>) entityType;
            EntityRenderers.register(mobType, ctx -> new GeneralEntityRenderer<>(ctx, id, false));
        });
    }
}
