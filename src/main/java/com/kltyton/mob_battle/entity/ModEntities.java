package com.kltyton.mob_battle.entity;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.blueirongolem.BlueIronGolemEntity;
import com.kltyton.mob_battle.entity.bullet.BulletEntity;
import com.kltyton.mob_battle.entity.bullet.GoldenBulletEntity;
import com.kltyton.mob_battle.entity.bullet.GoldenTrailProjectile;
import com.kltyton.mob_battle.entity.bullet.IceArrowEntity;
import com.kltyton.mob_battle.entity.customfireball.CustomSuperBigFireballEntity;
import com.kltyton.mob_battle.entity.cbot.Cbot002Entity;
import com.kltyton.mob_battle.entity.cbot.CbotSnowballEntity;
import com.kltyton.mob_battle.entity.cbot.SnowmanIceBlockEntity;
import com.kltyton.mob_battle.entity.customfireball.MagmaLobsterBigFireballEntity;
import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import com.kltyton.mob_battle.entity.drone.attackdrone.AttackDroneEntity;
import com.kltyton.mob_battle.entity.drone.treatmentdrone.TreatmentDroneEntity;
import com.kltyton.mob_battle.entity.evoker.SuperEvokerEntity;
import com.kltyton.mob_battle.entity.enhancedwither.EnhancedWitherEntity;
import com.kltyton.mob_battle.entity.firewall.FireWallEntity;
import com.kltyton.mob_battle.entity.flowerfairy.FlowerFairyEntity;
import com.kltyton.mob_battle.entity.hiddeneye.HiddenEyeEntity;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import com.kltyton.mob_battle.entity.highbird.baby.HighbirdBabyEntity;
import com.kltyton.mob_battle.entity.highbird.egg.HighbirdEggEntity;
import com.kltyton.mob_battle.entity.highbird.teenage.HighbirdTeenageEntity;
import com.kltyton.mob_battle.entity.irongolem.VillagerIronGolemEntity;
import com.kltyton.mob_battle.entity.irongolem.hulkbuster.HulkbusterEntity;
import com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile.MissileEntity;
import com.kltyton.mob_battle.entity.littleperson.archer.LittlePersonArcherEntity;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntity;
import com.kltyton.mob_battle.entity.littleperson.archer.soldier.LittlePersonSoldierArcherEntity;
import com.kltyton.mob_battle.entity.littleperson.civilian.LittlePersonCivilianEntity;
import com.kltyton.mob_battle.entity.littleperson.giant.LittlePersonGiantEntity;
import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntity;
import com.kltyton.mob_battle.entity.littleperson.king.LittlePersonKingEntity;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.entity.littleperson.militia.soldier.LittlePersonSoldierEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.*;
import com.kltyton.mob_battle.entity.littleperson.skillentity.ironmanbullet.IronManBulletEntity;
import com.kltyton.mob_battle.entity.lobster.LobsterEntity;
import com.kltyton.mob_battle.entity.lobster.MagmaLobsterEntity;
import com.kltyton.mob_battle.entity.meteorite.EnderDragonMeteoriteEntity;
import com.kltyton.mob_battle.entity.meteorite.MeteoriteEntity;
import com.kltyton.mob_battle.entity.min.YoungMinEntity;
import com.kltyton.mob_battle.entity.piglingeneral.PiglinGeneralEntity;
import com.kltyton.mob_battle.entity.misc.ModifiedDragonBreathCloud;
import com.kltyton.mob_battle.entity.misc.PoisonousBeachEntity;
import com.kltyton.mob_battle.entity.misc.shield.ShieldEntity;
import com.kltyton.mob_battle.entity.silencephantom.SilencePhantomEntity;
import com.kltyton.mob_battle.entity.silverfish.silverfish.*;
import com.kltyton.mob_battle.entity.skull.archer.SkullArcherEntity;
import com.kltyton.mob_battle.entity.skull.king.SkullKingEntity;
import com.kltyton.mob_battle.entity.skull.mage.SkullMageEntity;
import com.kltyton.mob_battle.entity.skull.warrior.SkullWarriorEntity;
import com.kltyton.mob_battle.entity.sugarmanscorpion.SugarManScorpion;
import com.kltyton.mob_battle.entity.villager.archervillager.ArcherVillager;
import com.kltyton.mob_battle.entity.villager.militia.MilitiaArcherVillager;
import com.kltyton.mob_battle.entity.villager.militia.MilitiaWarriorVillager;
import com.kltyton.mob_battle.entity.villager.villagerking.VillagerKingEntity;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager;
import com.kltyton.mob_battle.entity.vindicatorgeneral.VindicatorGeneralEntity;
import com.kltyton.mob_battle.entity.vindicatorgeneral.VindicatorGeneralAxeEntity;
import com.kltyton.mob_battle.entity.voidcell.VoidCellEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.skill.WitherSkullBulletEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.summon.DualBladeWitherSkeletonEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.summon.ShieldAxeWitherSkeletonEntity;
import com.kltyton.mob_battle.entity.xunsheng.XunShengEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.HashMap;
import java.util.Map;

public class ModEntities {
    public static Map<String, EntityType<?>> SPAWN_EGG_ENTITIES = new HashMap<>();
    public static Map<String, EntityType<?>> GENERAL_RENDERERS = new HashMap<>();
    public static Map<String, EntityType<?>> LITTLE_PERSON_ENTITIES = new HashMap<>();
    public static final ResourceKey<EntityType<?>> militia_warrior_villager = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"militia_warrior_villager"));
    public static final ResourceKey<EntityType<?>> militia_archer_villager = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"militia_archer_villager"));
    public static final ResourceKey<EntityType<?>> warrior_villager = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"warrior_villager"));
    public static final ResourceKey<EntityType<?>> archer_villager = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"archer_villager"));
    public static final ResourceKey<EntityType<?>> void_cell = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"void_cell"));
    public static final ResourceKey<EntityType<?>> xun_sheng= ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"xun_sheng"));
    public static final ResourceKey<EntityType<?>> deep_creature = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"deep_creature"));
    public static final ResourceKey<EntityType<?>> highbird_baby = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"highbird_baby"));
    public static final ResourceKey<EntityType<?>> highbird_teenage = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"highbird_teenage"));
    public static final ResourceKey<EntityType<?>> highbird_adulthood = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"highbird_adulthood"));
    public static final ResourceKey<EntityType<?>> highbird_egg = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"highbird_egg"));
    public static final ResourceKey<EntityType<?>> bigfireball = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"bigfireball"));
    public static final ResourceKey<EntityType<?>> meteorite = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"meteorite"));
    public static final ResourceKey<EntityType<?>> missile = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"missile"));
    public static final ResourceKey<EntityType<?>> firewall = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"fairewall"));
    public static final ResourceKey<EntityType<?>> blue_iron_golem = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"blue_iron_golem"));
    public static final ResourceKey<EntityType<?>> sugar_man_scorpion = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"sugar_man_scorpion"));
    public static final ResourceKey<EntityType<?>> wither_skeleton_king = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"wither_skeleton_king"));
    public static final ResourceKey<EntityType<?>> vindicator_general = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"vindicator_general"));
    public static final ResourceKey<EntityType<?>> vindicator_general_axe = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"vindicator_general_axe"));
    public static final ResourceKey<EntityType<?>> hulkbuster = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"hulkbuster"));
    public static final ResourceKey<EntityType<?>> skull_king = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"skull_king"));
    public static final ResourceKey<EntityType<?>> skull_archer = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"skull_archer"));
    public static final ResourceKey<EntityType<?>> skull_warrior = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"skull_warrior"));
    public static final ResourceKey<EntityType<?>> skull_mage = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"skull_mage"));
    public static final ResourceKey<EntityType<?>> young_min = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"young_min"));
    public static final ResourceKey<EntityType<?>> hidden_eye = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"hidden_eye"));
    public static final ResourceKey<EntityType<?>> silence_phantom = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"silence_phantom"));
    public static final ResourceKey<EntityType<?>> coal_silverfish = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"coal_silverfish"));
    public static final ResourceKey<EntityType<?>> enhanced_wither = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"enhanced_wither"));
    public static final ResourceKey<EntityType<?>> dual_blade_wither_skeleton = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"dual_blade_wither_skeleton"));
    public static final ResourceKey<EntityType<?>> shield_axe_wither_skeleton = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"shield_axe_wither_skeleton"));
    public static final ResourceKey<EntityType<?>> cbot002 = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"cbot002"));
    public static final ResourceKey<EntityType<?>> cbot_snowball = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"cbot_snowball"));
    public static final ResourceKey<EntityType<?>> piglin_general = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"piglin_general"));
    public static final ResourceKey<EntityType<?>> wither_skeleton_dog = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"wither_skeleton_dog"));
    public static final ResourceKey<EntityType<?>> laser = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"laser"));
    public static final ResourceKey<EntityType<?>> blood_sword_energy = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"blood_sword_energy"));
    public static final ResourceKey<EntityType<?>> ice_sword_energy = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"ice_sword_energy"));
    public static final ResourceKey<EntityType<?>> ice_bomb = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"ice_bomb"));
    public static final ResourceKey<EntityType<?>> ice_fangs = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"ice_fangs"));
    public static final ResourceKey<EntityType<?>> ninja_clone = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"ninja_clone"));

    public static final ResourceKey<EntityType<?>> bullet = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"bullet"));
    public static final ResourceKey<EntityType<?>> wither_skull_bullet = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"wither_skull_bullet"));
    public static final ResourceKey<EntityType<?>> iron_man_bullet = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"iron_man_bullet"));
    public static final ResourceKey<EntityType<?>> little_arrow = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"little_arrow"));
    public static final ResourceKey<EntityType<?>> stone_arrow = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"stone_arrow"));
    public static final ResourceKey<EntityType<?>> poison_arrow = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"poison_arrow"));
    public static final ResourceKey<EntityType<?>> spear_bullet = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"spear_bullet"));
    public static final ResourceKey<EntityType<?>> villager_iron_golem = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"iron_golem"));
    public static final ResourceKey<EntityType<?>> villager_king = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"villager_king"));
    public static final ResourceKey<EntityType<?>> attack_drone =  ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"attack_drone"));
    public static final ResourceKey<EntityType<?>> treatment_drone = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"treatment_drone"));
    public static final ResourceKey<EntityType<?>> little_person_civilian = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"little_person_civilian"));
    public static final ResourceKey<EntityType<?>> little_person_militia = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"little_person_militia"));
    public static final ResourceKey<EntityType<?>> little_person_archer = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"little_person_archer"));
    public static final ResourceKey<EntityType<?>> little_person_giant = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"little_person_giant"));
    public static final ResourceKey<EntityType<?>> little_person_guard = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"little_person_guard"));
    public static final ResourceKey<EntityType<?>> little_person_king = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"little_person_king"));
    public static final EntityType<MilitiaWarriorVillager> MILITIA_WARRIOR_VILLAGER = FabricEntityType.Builder.createMob(MilitiaWarriorVillager::new, MobCategory.CREATURE,
                    (mob) -> mob.defaultAttributes(MilitiaArcherVillager::createVillagerAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MilitiaWarriorVillager::checkWarriorSpawnRules))
            .sized(0.6F, 1.95F)
            .clientTrackingRange(16)
            .build(militia_warrior_villager);
    public static final EntityType<MilitiaArcherVillager> MILITIA_ARCHER_VILLAGER = FabricEntityType.Builder.createMob(MilitiaArcherVillager::new, MobCategory.CREATURE,
                    (mob) -> mob.defaultAttributes(MilitiaArcherVillager::createVillagerAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                    MilitiaArcherVillager::checkSnuffleSpawnRules))
            .sized(0.6F, 1.95F)
            .clientTrackingRange(16)
            .build(militia_archer_villager);
    public static final EntityType<WarriorVillager> WARRIOR_VILLAGER = FabricEntityType.Builder.createMob(WarriorVillager::new, MobCategory.CREATURE,
                    (mob) -> mob.defaultAttributes(() -> Villager.createAttributes()
                            .add(Attributes.ATTACK_DAMAGE, 5.0D)
                            .add(Attributes.ARMOR, 2.0D))
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WarriorVillager::checkWarriorSpawnRules))
            .sized(0.6F, 1.95F)
            .clientTrackingRange(16)
            .build(warrior_villager);
    public static final EntityType<ArcherVillager> ARCHER_VILLAGER = FabricEntityType.Builder.createMob(ArcherVillager::new, MobCategory.CREATURE,
                    (mob) -> mob.defaultAttributes(() -> ArcherVillager.createVillagerAttributes()
                            .add(Attributes.ATTACK_DAMAGE, 3.0D)
                            .add(Attributes.ARMOR, 1.0D))
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                    ArcherVillager::checkSnuffleSpawnRules))
            .sized(0.6F, 1.95F)
            .clientTrackingRange(64)
            .build(archer_villager);
    public static final EntityType<VoidCellEntity> VOID_CELL = FabricEntityType.Builder.createMob(VoidCellEntity::new, MobCategory.CREATURE,
                    (mob) -> mob.defaultAttributes(VoidCellEntity::createVoidCellAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(0.6F, 0.6F)
            .clientTrackingRange(16)
            .build(void_cell);

    public static final EntityType<XunShengEntity> XUN_SHENG = FabricEntityType.Builder.createMob(XunShengEntity::new, MobCategory.MONSTER,
                    (mob) -> mob.defaultAttributes(XunShengEntity::addAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            // 调整尺寸（监守者：宽0.9，高2.9）
            .sized(0.6F, 1.95F)
            // 调整追踪范围（监守者：50）
            .clientTrackingRange(50)
            .build(xun_sheng);
    public static final EntityType<DeepCreatureEntity> DEEP_CREATURE = FabricEntityType.Builder.createMob(DeepCreatureEntity::new, MobCategory.MONSTER,
                    (mob) -> mob.defaultAttributes(DeepCreatureEntity::createDeepCreatureAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(3.6F, 6.95F)
            .fireImmune()
            .clientTrackingRange(64)
            .build(deep_creature);
    public static final EntityType<WitherSkeletonKingEntity> WITHER_SKELETON_KING = FabricEntityType.Builder.createMob(WitherSkeletonKingEntity::new, MobCategory.MONSTER,
                    (mob) -> mob.defaultAttributes(WitherSkeletonKingEntity::addAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(1.0F, 3.15F)
            .clientTrackingRange(64)
            .fireImmune()
            .build(wither_skeleton_king);
    public static final EntityType<VindicatorGeneralEntity> VINDICATOR_GENERAL = FabricEntityType.Builder.createMob(VindicatorGeneralEntity::new, MobCategory.MONSTER,
                    (mob) -> mob.defaultAttributes(VindicatorGeneralEntity::addAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(1.0F, 3.15F)
            .clientTrackingRange(64)
            .fireImmune()
            .build(vindicator_general);
    public static final EntityType<HulkbusterEntity> HULKBUSTER = FabricEntityType.Builder.createMob(HulkbusterEntity::new, MobCategory.MONSTER,
                    (mob) -> mob.defaultAttributes(HulkbusterEntity::addAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(1.0F, 3.15F)
            .clientTrackingRange(64)
            .build(hulkbuster);

    public static final EntityType<SkullKingEntity> SKULL_KING = FabricEntityType.Builder.createMob(SkullKingEntity::new, MobCategory.MONSTER,
                    (mob) -> mob.defaultAttributes(SkullKingEntity::addAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(1.0F, 3.15F)
            .clientTrackingRange(64)
            .fireImmune()
            .build(skull_king);
    public static final EntityType<SkullArcherEntity> SKULL_ARCHER = FabricEntityType.Builder.createMob(SkullArcherEntity::new, MobCategory.MONSTER,
                    (mob) -> mob.defaultAttributes(SkullArcherEntity::addAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(0.75F, 1.8F)
            .clientTrackingRange(64)
            .fireImmune()
            .build(skull_archer);
    public static final EntityType<SkullWarriorEntity> SKULL_WARRIOR = FabricEntityType.Builder.createMob(SkullWarriorEntity::new, MobCategory.MONSTER,
                    (mob) -> mob.defaultAttributes(SkullWarriorEntity::addAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(0.6F, 1.95F)
            .clientTrackingRange(64)
            .fireImmune()
            .build(skull_warrior);
    public static final EntityType<SkullMageEntity> SKULL_MAGE = FabricEntityType.Builder.createMob(SkullMageEntity::new, MobCategory.MONSTER,
                    (mob) -> mob.defaultAttributes(SkullMageEntity::addAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(0.6F, 1.95F)
            .clientTrackingRange(64)
            .fireImmune()
            .build(skull_mage);
    public static final EntityType<YoungMinEntity> YOUNG_MIN = FabricEntityType.Builder.createMob(YoungMinEntity::new, MobCategory.CREATURE,
                    (mob) -> mob.defaultAttributes(YoungMinEntity::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(0.6F, 0.8F)
            .clientTrackingRange(16)
            .build(young_min);
    public static final EntityType<HiddenEyeEntity> HIDDEN_EYE = FabricEntityType.Builder.createMob(HiddenEyeEntity::new, MobCategory.CREATURE,
                (mob) -> mob.defaultAttributes(HiddenEyeEntity::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(0.6F, 1.2F)
            .clientTrackingRange(16)
            .build(hidden_eye);

    /* ↓↓↓ 在 ModEntities 类里新增 ↓↓↓ */
    public static final EntityType<HighbirdBabyEntity> HIGHBIRD_BABY = FabricEntityType.Builder.createMob(HighbirdBabyEntity::new, MobCategory.CREATURE,
            (mob) -> mob.defaultAttributes(HighbirdBabyEntity::createHighbirdAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(1.5F, 1.2F)   // 高脚鸟幼崽比玩家小一点
            .clientTrackingRange(16)
            .build(highbird_baby);
    /* ↓↓↓ 在 ModEntities 类里新增 ↓↓↓ */
    public static final EntityType<HighbirdEggEntity> HIGHBIRD_EGG = FabricEntityType.Builder.createMob(HighbirdEggEntity::new, MobCategory.CREATURE,
                    (mob) -> mob.defaultAttributes(HighbirdEggEntity::createHighbirdAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(0.7F, 0.8F)
            .clientTrackingRange(16)
            .build(highbird_egg);
    /* ↓↓↓ 在 ModEntities 类里新增 ↓↓↓ */
    public static final EntityType<HighbirdTeenageEntity> HIGHBIRD_TEENAGE = FabricEntityType.Builder.createMob(HighbirdTeenageEntity::new, MobCategory.CREATURE,
                    (mob) -> mob.defaultAttributes(HighbirdTeenageEntity::createHighbirdAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(1.5F, 4.5F)
            .clientTrackingRange(24)
            .build(highbird_teenage);
    public static final EntityType<HighbirdAdulthoodEntity> HIGHBIRD_ADULTHOOD = FabricEntityType.Builder.createMob(HighbirdAdulthoodEntity::new, MobCategory.CREATURE,
                    (mob) -> mob.defaultAttributes(HighbirdAdulthoodEntity::createHighbirdAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(1.5F, 4.5F)
            .clientTrackingRange(24)
            .build(highbird_adulthood);
    public static final EntityType<VillagerIronGolemEntity> VILLAGER_IRON_GOLEM_ENTITY = FabricEntityType.Builder.createMob(VillagerIronGolemEntity::new, MobCategory.MISC,
                    (mob) -> mob.defaultAttributes(VillagerIronGolemEntity::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(1.4F, 2.7F)
            .clientTrackingRange(10)
            .build(villager_iron_golem);
    public static final EntityType<VillagerKingEntity> VILLAGER_KING_ENTITY = FabricEntityType.Builder.createMob(VillagerKingEntity::new, MobCategory.MISC,
                    (mob) -> mob.defaultAttributes(VillagerKingEntity::createVillagerKingAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(1.4F, 3.25F)
            .build(villager_king);
    public static final EntityType<WitherSkullBulletEntity> WITHER_SKULL_BULLET_ENTITY =
            EntityType.Builder.<WitherSkullBulletEntity>of(WitherSkullBulletEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(wither_skull_bullet);

    public static final EntityType<IronManBulletEntity> IRON_MAN_BULLET_ENTITY =
            EntityType.Builder.<IronManBulletEntity>of(IronManBulletEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(iron_man_bullet);
    public static final EntityType<BulletEntity> BULLET_ENTITY =
            EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125F, 0.3125F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(bullet);
    public static final EntityType<LittleArrowEntity> LITTLE_ARROW =
            EntityType.Builder.<LittleArrowEntity>of(LittleArrowEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125F, 0.3125F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(little_arrow);
    public static final EntityType<LittleArrowEntity> STONE_ARROW =
            EntityType.Builder.<LittleArrowEntity>of(LittleArrowEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125F, 0.3125F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(stone_arrow);
    public static final EntityType<LittleArrowEntity> POISON_ARROW =
            EntityType.Builder.<LittleArrowEntity>of(LittleArrowEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125F, 0.3125F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(poison_arrow);
    public static final EntityType<LittleArrowEntity> SPEAR_BULLET =
            EntityType.Builder.<LittleArrowEntity>of(LittleArrowEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125F, 0.3125F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(spear_bullet);
    public static final EntityType<CustomSuperBigFireballEntity> BIG_CUSTOM_FIREBALL =
            EntityType.Builder.<CustomSuperBigFireballEntity>of(CustomSuperBigFireballEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(bigfireball);
    public static final EntityType<MeteoriteEntity> METEORITE =
            EntityType.Builder.<MeteoriteEntity>of(MeteoriteEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(meteorite);
    public static final EntityType<MissileEntity> MISSILE =
            EntityType.Builder.<MissileEntity>of(MissileEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.5f, 0.5f)
                    .eyeHeight(0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(missile);
    public static final EntityType<FireWallEntity> FIRE_WALL =
            EntityType.Builder.<FireWallEntity>of(FireWallEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(1f, 3f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(firewall);
    public static final EntityType<BlueIronGolemEntity> BLUE_IRON_GOLEM =
            FabricEntityType.Builder.createMob(BlueIronGolemEntity::new, MobCategory.MISC, (mob) -> mob.defaultAttributes(BlueIronGolemEntity::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(1.4F, 2.7F)
                    .clientTrackingRange(10)
                    .build(blue_iron_golem);
    public static final EntityType<SugarManScorpion> SUGAR_MAN_SCORPION =
            FabricEntityType.Builder.createMob(SugarManScorpion::new, MobCategory.MONSTER,
                    (mob) -> mob.defaultAttributes(SugarManScorpion::createSugarManScorpionAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(1.5F, 1.2F)
            .clientTrackingRange(16)
            .build(sugar_man_scorpion);


    public static final EntityType<AttackDroneEntity> ATTACK_DRONE =
            FabricEntityType.Builder.createMob(AttackDroneEntity::new, MobCategory.MISC,
                    (mob) -> mob.defaultAttributes(AttackDroneEntity::createDroneAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(0.7F, 0.3F)
            .clientTrackingRange(5)
            .build(attack_drone);
    public static final EntityType<SilencePhantomEntity> SILENCE_PHANTOM =
            FabricEntityType.Builder.createMob(SilencePhantomEntity::new, MobCategory.MONSTER,
                    (mob) -> mob.defaultAttributes(SilencePhantomEntity::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.9F, 0.5F)
                    .eyeHeight(0.175F)
                    .passengerAttachments(0.3375F)
                    .ridingOffset(-0.125F)
                    .clientTrackingRange(8)
                    .fireImmune()
                    .build(silence_phantom);
    public static final EntityType<CoalSilverfishEntity> COAL_SILVERFISH =
            FabricEntityType.Builder.createMob(CoalSilverfishEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(CoalSilverfishEntity::createAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.4F, 0.3F)
                    .eyeHeight(0.13F)
                    .passengerAttachments(0.2375F)
                    .clientTrackingRange(8)
                    .build(coal_silverfish);
    public static final EntityType<EnhancedWitherEntity> ENHANCED_WITHER =
            FabricEntityType.Builder.createMob(EnhancedWitherEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(EnhancedWitherEntity::createEnhancedWitherAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.9F, 3.5F)
                    .clientTrackingRange(64)
                    .fireImmune()
                    .build(enhanced_wither);
    public static final EntityType<DualBladeWitherSkeletonEntity> DUAL_BLADE_WITHER_SKELETON = createEntityType(
            "dual_blade_wither_skeleton",
            FabricEntityType.Builder.createMob(DualBladeWitherSkeletonEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(DualBladeWitherSkeletonEntity::createAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.7F, 2.4F)
                    .clientTrackingRange(64)
                    .fireImmune(),
            true,
            false
    );
    public static final EntityType<ShieldAxeWitherSkeletonEntity> SHIELD_AXE_WITHER_SKELETON = createEntityType(
            "shield_axe_wither_skeleton",
            FabricEntityType.Builder.createMob(ShieldAxeWitherSkeletonEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(ShieldAxeWitherSkeletonEntity::createAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.8F, 2.4F)
                    .clientTrackingRange(64)
                    .fireImmune(),
            true,
            false
    );
    public static final EntityType<Cbot002Entity> CBOT002 =
            FabricEntityType.Builder.createMob(Cbot002Entity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(Cbot002Entity::createAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(1.2F, 3.5F)
                    .clientTrackingRange(40)
                    .build(cbot002);
    public static final EntityType<PiglinGeneralEntity> PIGLIN_GENERAL =
            FabricEntityType.Builder.createMob(PiglinGeneralEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(PiglinGeneralEntity::createAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(1.2F, 3.0F)
                    .clientTrackingRange(64)
                    .fireImmune()
                    .build(piglin_general);
    public static final EntityType<WitherSkeletonDogEntity> WITHER_SKELETON_DOG = createEntityType(
            "wither_skeleton_dog",
            FabricEntityType.Builder.createMob(WitherSkeletonDogEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(WitherSkeletonDogEntity::createAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.9F, 0.9F)
                    .clientTrackingRange(60)
                    .fireImmune(),
            true,
            true
    );
    public static final EntityType<LiruiSilverfishEntity> LIRUI_SILVERFISH = createEntityType(
            "ruili_silverfish",
            FabricEntityType.Builder.createMob(LiruiSilverfishEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(LiruiSilverfishEntity::createAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.4F, 0.3F)
                    .eyeHeight(0.13F)
                    .passengerAttachments(0.2375F)
                    .clientTrackingRange(8),
            true,
            true
    );
    public static final EntityType<DrillSilverfishEntity> DRILL_SILVERFISH = createEntityType(
            "drill_silverfish",
            FabricEntityType.Builder.createMob(DrillSilverfishEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(DrillSilverfishEntity::createAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.4F, 0.3F)
                    .eyeHeight(0.13F)
                    .passengerAttachments(0.2375F)
                    .clientTrackingRange(8),
            true,
            true
    );
    public static final EntityType<PoisonousSilverfishEntity> POISONOUS_SILVERFISH = createEntityType(
            "poisonous_silverfish",
            FabricEntityType.Builder.createMob(PoisonousSilverfishEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(PoisonousSilverfishEntity::createAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.4F, 0.3F)
                    .eyeHeight(0.13F)
                    .passengerAttachments(0.2375F)
                    .clientTrackingRange(8),
            true,
            true
    );
    public static final EntityType<LoadSilverfishEntity> LOAD_SILVERFISH = createEntityType(
            "load_silverfish",
            FabricEntityType.Builder.createMob(LoadSilverfishEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(LoadSilverfishEntity::createAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.4F, 0.3F)
                    .eyeHeight(0.13F)
                    .passengerAttachments(0.2375F)
                    .clientTrackingRange(8),
            true,
            true
    );
    public static final EntityType<LongWhipSilverfishEntity> LONG_WHIP_SILVERFISH = createEntityType(
            "long_whip_silverfish",
            FabricEntityType.Builder.createMob(LongWhipSilverfishEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(LongWhipSilverfishEntity::createAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(1F, 1F)
                    .eyeHeight(0.13F)
                    .passengerAttachments(0.2375F)
                    .clientTrackingRange(8),
            true,
            true
    );
    public static final EntityType<FlowerFairyEntity> FLOWER_FAIRY = createEntityType(
            "flower_fairy",
            FabricEntityType.Builder.createMob(FlowerFairyEntity::new, MobCategory.CREATURE,

                            (mob) -> mob.defaultAttributes(FlowerFairyEntity::createAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))

                    .sized(0.4F, 0.4F)
                    .eyeHeight(0.3F)
                    .clientTrackingRange(10),
            true,
            true
    );
    public static final EntityType<SuperEvokerEntity> SUPER_EVOKER  = createEntityType(
            "super_evoker",
            FabricEntityType.Builder.createMob(SuperEvokerEntity::new, MobCategory.MONSTER,

            (mob) -> mob.defaultAttributes(SuperEvokerEntity::createSuperEvokerAttributes)
                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))

                    .sized(0.6F, 1.95F)
                    .passengerAttachments(2.0F)
                    .ridingOffset(-0.6F)
                    .clientTrackingRange(8),
            true,
            false
    );
    public static final EntityType<TreatmentDroneEntity> TREATMENT_DRONE =
            FabricEntityType.Builder.createMob(TreatmentDroneEntity::new, MobCategory.MISC,
                    (mob) -> mob.defaultAttributes(TreatmentDroneEntity::createDroneAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(0.7F, 0.3F)
            .clientTrackingRange(5)
            .build(treatment_drone);
    public static final EntityType<LittlePersonCivilianEntity> LITTLE_PERSON_CIVILIAN =
            FabricEntityType.Builder.createMob(LittlePersonCivilianEntity::new, MobCategory.MISC,
                    (mob) -> mob.defaultAttributes(LittlePersonCivilianEntity::createLittlePersonCivilianAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(0.6F, 0.9F)
            .clientTrackingRange(40)
            .build(little_person_civilian);
    public static final EntityType<LittlePersonMilitiaEntity> LITTLE_PERSON_MILITIA =
            FabricEntityType.Builder.createMob(LittlePersonMilitiaEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonMilitiaEntity::createLittlePersonMilitiaAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
                    .build(little_person_militia);
    public static final EntityType<LittlePersonArcherEntity> LITTLE_PERSON_ARCHER =
            FabricEntityType.Builder.createMob(LittlePersonArcherEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonArcherEntity::createLittlePersonArcherAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
                    .build(little_person_archer);
    public static final EntityType<LittlePersonGiantEntity> LITTLE_PERSON_GIANT =
            FabricEntityType.Builder.createMob(LittlePersonGiantEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonGiantEntity::createLittlePersonGiantAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.8F)
                    .clientTrackingRange(40)
                    .build(little_person_giant);
    public static final EntityType<LittlePersonGuardEntity> LITTLE_PERSON_GUARD =
            FabricEntityType.Builder.createMob(LittlePersonGuardEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonGuardEntity::createLittlePersonGuardAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
                    .build(little_person_guard);
    public static final EntityType<LittlePersonKingEntity> LITTLE_PERSON_KING = FabricEntityType.Builder.createMob(
            LittlePersonKingEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonKingEntity::createLittlePersonKingAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
                    .build(little_person_king);
    public static final EntityType<PoisonousSlashEntity> POISONOUS_SLASH = createLittlePersonEntityType(
            "poisonous_slash",
            FabricEntityType.Builder.createMob(
                    PoisonousSlashEntity::new, MobCategory.MISC,
                    (mob) -> mob.defaultAttributes(PoisonousSlashEntity::createLittlePersonAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(0.6F, 0.9F)
            .clientTrackingRange(40)
    );
    public static final EntityType<CyborgEntity> CYBORG = createLittlePersonEntityType(
            "cyborg",
            FabricEntityType.Builder.createMob(
                            CyborgEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(CyborgEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<IronManEntity> IRON_MAN = createLittlePersonEntityType(
            "iron_man",
            FabricEntityType.Builder.createMob(
                            IronManEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(IronManEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<IronManTrueEntity> IRON_MAN_TRUE = createLittlePersonEntityType(
            "iron_man_true",
            FabricEntityType.Builder.createMob(
                            IronManTrueEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(IronManTrueEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<TaiLinEntity> TAI_LIN = createLittlePersonEntityType(
            "tai_lin",
            FabricEntityType.Builder.createMob(
                            TaiLinEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(TaiLinEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<FrenchSphereFlowEntity> FRENCH_SPHERE_FLOW = createLittlePersonEntityType(
            "french_sphere_flow",
            FabricEntityType.Builder.createMob(
                            FrenchSphereFlowEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(FrenchSphereFlowEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<WildManEntity> WILD_MAN = createLittlePersonEntityType(
            "wild_man",
            FabricEntityType.Builder.createMob(
                            WildManEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(WildManEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<WildBoarEntity> WILD_BOAR = createLittlePersonEntityType(
            "wild_boar",
            FabricEntityType.Builder.createMob(
                            WildBoarEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(WildBoarEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<MagicManEntity> MAGIC_MAN = createLittlePersonEntityType(
            "magic_man",
            FabricEntityType.Builder.createMob(
                            MagicManEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(MagicManEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<HeavenCrippledFeetEntity> HEAVEN_CRIPPLED_FEET = createLittlePersonEntityType(
            "heaven_crippled_feet",
            FabricEntityType.Builder.createMob(
                            HeavenCrippledFeetEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(HeavenCrippledFeetEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );

    public static final EntityType<BloodyBladeEntity> BLOODY_BLADE = createLittlePersonEntityType(
            "bloody_blade",
            FabricEntityType.Builder.createMob(
                            BloodyBladeEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(BloodyBladeEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<HumanShieldEntity> HUMAN_SHIELD = createLittlePersonEntityType(
            "human_shield",
            FabricEntityType.Builder.createMob(
                            HumanShieldEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(HumanShieldEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );

    public static final EntityType<HumanHammerEntity> HUMAN_HAMMER = createLittlePersonEntityType(
            "human_hammer",
            FabricEntityType.Builder.createMob(
                            HumanHammerEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(HumanHammerEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<LittlePersonSoldierEntity> LITTLE_PERSON_SOLDIER = createLittlePersonEntityType(
            "little_person_soldier",
            FabricEntityType.Builder.createMob(
                            LittlePersonSoldierEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonSoldierEntity::createLittlePersonMilitiaAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<LittlePersonSoldierArcherEntity> LITTLE_PERSON_SOLDIER_ARCHER = createLittlePersonEntityType(
            "little_person_soldier_archer",
            FabricEntityType.Builder.createMob(
                            LittlePersonSoldierArcherEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonSoldierArcherEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<SexEntity> SEX_ENTITY = createLittlePersonEntityType(
            "sex_entity",
            FabricEntityType.Builder.createMob(
                            SexEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(SexEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<AngelCyborgEntity> ANGEL_CYBORG = createLittlePersonEntityType(
            "angel_cyborg",
            FabricEntityType.Builder.createMob(
                            AngelCyborgEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(AngelCyborgEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<LivingGhostEntity> LIVING_GHOST = createLittlePersonEntityType(
            "living_ghost",
            FabricEntityType.Builder.createMob(
                            LivingGhostEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LivingGhostEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<ScatteredDemonEntity> SCATTERED_DEMON = createLittlePersonEntityType(
            "scattered_demon",
            FabricEntityType.Builder.createMob(
                            ScatteredDemonEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(ScatteredDemonEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<NinjaEntity> NINJA = createLittlePersonEntityType(
            "ninja",
            FabricEntityType.Builder.createMob(
                            NinjaEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(NinjaEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<LaserManEntity> LASER_MAN = createLittlePersonEntityType(
            "laser_man",
            FabricEntityType.Builder.createMob(
                            LaserManEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LaserManEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<BloodManEntity> BLOOD_MAN = createLittlePersonEntityType(
            "blood_man",
            FabricEntityType.Builder.createMob(
                            BloodManEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(BloodManEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<IceManEntity> ICE_MAN = createLittlePersonEntityType(
            "ice_man",
            FabricEntityType.Builder.createMob(
                            IceManEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(IceManEntity::createLittlePersonAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
    );
    public static final EntityType<SkillProjectileEntity> LASER = createEntityType(
            "laser",
            EntityType.Builder.<SkillProjectileEntity>of(SkillProjectileEntity::new, MobCategory.MISC)
                    .sized(0.35F, 0.35F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SkillProjectileEntity> BLOOD_SWORD_ENERGY = createEntityType(
            "blood_sword_energy",
            EntityType.Builder.<SkillProjectileEntity>of(SkillProjectileEntity::new, MobCategory.MISC)
                    .sized(0.8F, 0.8F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SkillProjectileEntity> ICE_SWORD_ENERGY = createEntityType(
            "ice_sword_energy",
            EntityType.Builder.<SkillProjectileEntity>of(SkillProjectileEntity::new, MobCategory.MISC)
                    .sized(0.8F, 0.8F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SkillProjectileEntity> ICE_BOMB = createEntityType(
            "ice_bomb",
            EntityType.Builder.<SkillProjectileEntity>of(SkillProjectileEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SnowmanIceBlockEntity> SNOWMAN_ICE_BLOCK = createEntityType(
            "snowman_ice_block",
            EntityType.Builder.<SnowmanIceBlockEntity>of(SnowmanIceBlockEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.9F, 0.9F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<CbotSnowballEntity> CBOT_SNOWBALL = createEntityType(
            "cbot_snowball",
            EntityType.Builder.<CbotSnowballEntity>of(CbotSnowballEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<VindicatorGeneralAxeEntity> VINDICATOR_GENERAL_AXE = createEntityType(
            "vindicator_general_axe",
            EntityType.Builder.<VindicatorGeneralAxeEntity>of(VindicatorGeneralAxeEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.6F, 0.6F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SkillVisualEntity> ICE_FANGS = createEntityType(
            "ice_fangs",
            EntityType.Builder.<SkillVisualEntity>of(SkillVisualEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SkillVisualEntity> NINJA_CLONE = createEntityType(
            "ninja_clone",
            EntityType.Builder.<SkillVisualEntity>of(SkillVisualEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );



    public static final EntityType<ShieldEntity> SHIELD = createEntityType(
            "shield_force_field",
            EntityType.Builder.of(ShieldEntity::new, MobCategory.MISC)
                    .sized(5.0f, 5.0f) // 最终碰撞箱大小
                    .clientTrackingRange(10)
                    .updateInterval(1),
            false,
            false
    );

    public static final EntityType<PoisonousBeachEntity> POISONOUS_BEACH = createEntityType(
            "poisonous_beach",
            EntityType.Builder.of(PoisonousBeachEntity::new, MobCategory.MISC)
                    .sized(1.0f, 0.0f) // 最终碰撞箱大小
                    .clientTrackingRange(10)
                    .updateInterval(1),
            false,
            true
    );

    public static final EntityType<ModifiedDragonBreathCloud> MODIFIED_DRAGON_BREATH_CLOUD = createEntityType(
            "modified_dragon_breath_cloud",
            EntityType.Builder.<ModifiedDragonBreathCloud>of(ModifiedDragonBreathCloud::new, MobCategory.MISC)
                    .noLootTable()
                    .fireImmune()
                    .sized(6.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE),
            false,
            false
    );
    public static final EntityType<EnderDragonMeteoriteEntity> ENDER_DRAGON_METEORITE = createEntityType(
            "ender_dragon_meteorite",
            EntityType.Builder.<EnderDragonMeteoriteEntity>of(EnderDragonMeteoriteEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4)
                    .updateInterval(10),
            false,
            false
    );

    public static final EntityType<MagmaLobsterBigFireballEntity> MAGMA_LOBBER_BIG_FIREBALL = createEntityType(
            "magma_lobber_big_fireball",
            EntityType.Builder.<MagmaLobsterBigFireballEntity>of(MagmaLobsterBigFireballEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4)
                    .updateInterval(10),
            false,
            false
    );
    public static final EntityType<IceArrowEntity> ICE_ARROW = createEntityType(
            "ice_arrow",
            EntityType.Builder.<IceArrowEntity>of(IceArrowEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.5F, 0.5F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20),
            false,
            false
    );
    public static final EntityType<GoldenTrailProjectile> GOLDEN_TRAIL_PROJECTILE = createEntityType(
            "golden_trail_projectile",
            EntityType.Builder.<GoldenTrailProjectile>of(GoldenTrailProjectile::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.5F, 0.5F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20),
            false,
            false
    );
    public static final EntityType<GoldenBulletEntity> GOLDEN_BULLET = createEntityType(
            "golden_bullet",
            EntityType.Builder.<GoldenBulletEntity>of(GoldenBulletEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.35F, 0.35F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20),
            false,
            false
    );
    public static final EntityType<LobsterEntity> LOBSTER = createEntityType(
            "lobster_entity",
            FabricEntityType.Builder.createMob(LobsterEntity::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(LobsterEntity::createAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                            (type, world, reason, pos, random) -> false))
                    .sized(2F, 0.7F)
                    .eyeHeight(0.25F)
                    .clientTrackingRange(8),
            true,
            false
    );
    public static final EntityType<MagmaLobsterEntity> MAGMA_LOBSTER = createEntityType(
            "magma_lobster_entity",
            FabricEntityType.Builder.createMob(MagmaLobsterEntity::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(MagmaLobsterEntity::createAttributes)
                                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                            (type, world, reason, pos, random) -> false))
                    .sized(2F, 0.7F)
                    .eyeHeight(0.25F)
                    .clientTrackingRange(8)
                    .fireImmune(),
            true,
            false
    );
    static {
        SPAWN_EGG_ENTITIES.put("enhanced_wither", ENHANCED_WITHER);
        SPAWN_EGG_ENTITIES.put("dual_blade_wither_skeleton", DUAL_BLADE_WITHER_SKELETON);
        SPAWN_EGG_ENTITIES.put("shield_axe_wither_skeleton", SHIELD_AXE_WITHER_SKELETON);
        SPAWN_EGG_ENTITIES.put("cbot002", CBOT002);
        SPAWN_EGG_ENTITIES.put("piglin_general", PIGLIN_GENERAL);
        GENERAL_RENDERERS.put("cbot002", CBOT002);
        GENERAL_RENDERERS.put("piglin_general", PIGLIN_GENERAL);
    }
    public static void init() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, militia_warrior_villager, MILITIA_WARRIOR_VILLAGER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, militia_archer_villager, MILITIA_ARCHER_VILLAGER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, warrior_villager, WARRIOR_VILLAGER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, archer_villager, ARCHER_VILLAGER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, void_cell, VOID_CELL);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, xun_sheng, XUN_SHENG);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, deep_creature, DEEP_CREATURE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, wither_skeleton_king, WITHER_SKELETON_KING);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, vindicator_general, VINDICATOR_GENERAL);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, skull_king, SKULL_KING);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, skull_archer, SKULL_ARCHER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, skull_warrior, SKULL_WARRIOR);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, skull_mage, SKULL_MAGE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, young_min, YOUNG_MIN);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, hidden_eye, HIDDEN_EYE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, meteorite, METEORITE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, highbird_baby, HIGHBIRD_BABY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, highbird_egg, HIGHBIRD_EGG);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, highbird_teenage, HIGHBIRD_TEENAGE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, highbird_adulthood, HIGHBIRD_ADULTHOOD);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, bigfireball, BIG_CUSTOM_FIREBALL);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, firewall, FIRE_WALL);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, blue_iron_golem, BLUE_IRON_GOLEM);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, sugar_man_scorpion, SUGAR_MAN_SCORPION);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, wither_skull_bullet, WITHER_SKULL_BULLET_ENTITY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, bullet, BULLET_ENTITY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, little_arrow, LITTLE_ARROW);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, stone_arrow, STONE_ARROW);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, poison_arrow, POISON_ARROW);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, hulkbuster, HULKBUSTER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, missile, MISSILE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, coal_silverfish, COAL_SILVERFISH);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, silence_phantom, SILENCE_PHANTOM);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, enhanced_wither, ENHANCED_WITHER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, cbot002, CBOT002);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, piglin_general, PIGLIN_GENERAL);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, iron_man_bullet, IRON_MAN_BULLET_ENTITY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, spear_bullet, SPEAR_BULLET);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, villager_iron_golem, VILLAGER_IRON_GOLEM_ENTITY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, villager_king, VILLAGER_KING_ENTITY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, attack_drone, ATTACK_DRONE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, treatment_drone, TREATMENT_DRONE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, little_person_civilian, LITTLE_PERSON_CIVILIAN);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, little_person_militia, LITTLE_PERSON_MILITIA);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, little_person_archer, LITTLE_PERSON_ARCHER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, little_person_giant, LITTLE_PERSON_GIANT);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, little_person_guard, LITTLE_PERSON_GUARD);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, little_person_king, LITTLE_PERSON_KING);
    }
    public static <T extends Entity> EntityType<T> createEntityType(String name, EntityType.Builder<T> builder, boolean registerSpawnEgg, boolean generalRenderer) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, name));
        EntityType<T> type = builder.build(key);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
        if (registerSpawnEgg) SPAWN_EGG_ENTITIES.put(name, type);
        if (generalRenderer) GENERAL_RENDERERS.put(name, type);
        return type;
    }
    public static <T extends Mob> EntityType<T> createLittlePersonEntityType(String name, EntityType.Builder<T> builder) {
        EntityType<T> type = createEntityType(name, builder, true, false);
        LITTLE_PERSON_ENTITIES.put(name, type);
        return type;
    }
}

