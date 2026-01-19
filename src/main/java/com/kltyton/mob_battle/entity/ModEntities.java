package com.kltyton.mob_battle.entity;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.blueirongolem.BlueIronGolemEntity;
import com.kltyton.mob_battle.entity.bullet.BulletEntity;
import com.kltyton.mob_battle.entity.customfireball.CustomSuperBigFireballEntity;
import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import com.kltyton.mob_battle.entity.drone.attackdrone.AttackDroneEntity;
import com.kltyton.mob_battle.entity.drone.treatmentdrone.TreatmentDroneEntity;
import com.kltyton.mob_battle.entity.firewall.FireWallEntity;
import com.kltyton.mob_battle.entity.hiddeneye.HiddenEyeEntity;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import com.kltyton.mob_battle.entity.highbird.baby.HighbirdBabyEntity;
import com.kltyton.mob_battle.entity.highbird.egg.HighbirdEggEntity;
import com.kltyton.mob_battle.entity.highbird.teenage.HighbirdTeenageEntity;
import com.kltyton.mob_battle.entity.irongolem.VillagerIronGolemEntity;
import com.kltyton.mob_battle.entity.littleperson.archer.LittlePersonArcherEntity;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntity;
import com.kltyton.mob_battle.entity.littleperson.civilian.LittlePersonCivilianEntity;
import com.kltyton.mob_battle.entity.littleperson.giant.LittlePersonGiantEntity;
import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntity;
import com.kltyton.mob_battle.entity.littleperson.king.LittlePersonKingEntity;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.entity.meteorite.MeteoriteEntity;
import com.kltyton.mob_battle.entity.min.YoungMinEntity;
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
import com.kltyton.mob_battle.entity.voidcell.VoidCellEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.skill.WitherSkullBulletEntity;
import com.kltyton.mob_battle.entity.xunsheng.XunShengEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public class ModEntities {
    public static final RegistryKey<EntityType<?>> militia_warrior_villager = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"militia_warrior_villager"));
    public static final RegistryKey<EntityType<?>> militia_archer_villager = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"militia_archer_villager"));
    public static final RegistryKey<EntityType<?>> warrior_villager = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"warrior_villager"));
    public static final RegistryKey<EntityType<?>> archer_villager = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"archer_villager"));
    public static final RegistryKey<EntityType<?>> void_cell = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"void_cell"));
    public static final RegistryKey<EntityType<?>> xun_sheng= RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"xun_sheng"));
    public static final RegistryKey<EntityType<?>> deep_creature = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"deep_creature"));
    public static final RegistryKey<EntityType<?>> highbird_baby = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"highbird_baby"));
    public static final RegistryKey<EntityType<?>> highbird_teenage = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"highbird_teenage"));
    public static final RegistryKey<EntityType<?>> highbird_adulthood = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"highbird_adulthood"));
    public static final RegistryKey<EntityType<?>> highbird_egg = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"highbird_egg"));
    public static final RegistryKey<EntityType<?>> bigfireball = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"bigfireball"));
    public static final RegistryKey<EntityType<?>> meteorite = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"meteorite"));
    public static final RegistryKey<EntityType<?>> firewall = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"fairewall"));
    public static final RegistryKey<EntityType<?>> blue_iron_golem = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"blue_iron_golem"));
    public static final RegistryKey<EntityType<?>> sugar_man_scorpion = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"sugar_man_scorpion"));
    public static final RegistryKey<EntityType<?>> wither_skeleton_king = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"wither_skeleton_king"));
    public static final RegistryKey<EntityType<?>> vindicator_general = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"vindicator_general"));
    public static final RegistryKey<EntityType<?>> skull_king = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"skull_king"));
    public static final RegistryKey<EntityType<?>> skull_archer = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"skull_archer"));
    public static final RegistryKey<EntityType<?>> skull_warrior = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"skull_warrior"));
    public static final RegistryKey<EntityType<?>> skull_mage = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"skull_mage"));
    public static final RegistryKey<EntityType<?>> young_min = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"young_min"));
    public static final RegistryKey<EntityType<?>> hidden_eye = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"hidden_eye"));

    public static final RegistryKey<EntityType<?>> bullet = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"bullet"));
    public static final RegistryKey<EntityType<?>> wither_skull_bullet = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"wither_skull_bullet"));
    public static final RegistryKey<EntityType<?>> little_arrow = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"little_arrow"));
    public static final RegistryKey<EntityType<?>> villager_iron_golem = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"iron_golem"));
    public static final RegistryKey<EntityType<?>> villager_king = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"villager_king"));
    public static final RegistryKey<EntityType<?>> attack_drone =  RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"attack_drone"));
    public static final RegistryKey<EntityType<?>> treatment_drone = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"treatment_drone"));
    public static final RegistryKey<EntityType<?>> little_person_civilian = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"little_person_civilian"));
    public static final RegistryKey<EntityType<?>> little_person_militia = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"little_person_militia"));
    public static final RegistryKey<EntityType<?>> little_person_archer = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"little_person_archer"));
    public static final RegistryKey<EntityType<?>> little_person_giant = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"little_person_giant"));
    public static final RegistryKey<EntityType<?>> little_person_guard = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"little_person_guard"));
    public static final RegistryKey<EntityType<?>> little_person_king = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"little_person_king"));
    public static final EntityType<MilitiaWarriorVillager> MILITIA_WARRIOR_VILLAGER = FabricEntityType.Builder.createMob(MilitiaWarriorVillager::new, SpawnGroup.CREATURE,
                    (mob) -> mob.defaultAttributes(MilitiaArcherVillager::createVillagerAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MilitiaWarriorVillager::checkWarriorSpawnRules))
            .dimensions(0.6F, 1.95F)
            .maxTrackingRange(16)
            .build(militia_warrior_villager);
    public static final EntityType<MilitiaArcherVillager> MILITIA_ARCHER_VILLAGER = FabricEntityType.Builder.createMob(MilitiaArcherVillager::new, SpawnGroup.CREATURE,
                    (mob) -> mob.defaultAttributes(MilitiaArcherVillager::createVillagerAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                                    MilitiaArcherVillager::checkSnuffleSpawnRules))
            .dimensions(0.6F, 1.95F)
            .maxTrackingRange(16)
            .build(militia_archer_villager);
    public static final EntityType<WarriorVillager> WARRIOR_VILLAGER = FabricEntityType.Builder.createMob(WarriorVillager::new, SpawnGroup.CREATURE,
                    (mob) -> mob.defaultAttributes(() -> VillagerEntity.createVillagerAttributes()
                            .add(EntityAttributes.ATTACK_DAMAGE, 5.0D)
                            .add(EntityAttributes.ARMOR, 2.0D))
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WarriorVillager::checkWarriorSpawnRules))
            .dimensions(0.6F, 1.95F)
            .maxTrackingRange(16)
            .build(warrior_villager);
    public static final EntityType<ArcherVillager> ARCHER_VILLAGER = FabricEntityType.Builder.createMob(ArcherVillager::new, SpawnGroup.CREATURE,
                    (mob) -> mob.defaultAttributes(() -> ArcherVillager.createVillagerAttributes()
                            .add(EntityAttributes.ATTACK_DAMAGE, 3.0D)
                            .add(EntityAttributes.ARMOR, 1.0D))
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                                    ArcherVillager::checkSnuffleSpawnRules))
            .dimensions(0.6F, 1.95F)
            .maxTrackingRange(64)
            .build(archer_villager);
    public static final EntityType<VoidCellEntity> VOID_CELL = FabricEntityType.Builder.createMob(VoidCellEntity::new, SpawnGroup.CREATURE,
                    (mob) -> mob.defaultAttributes(VoidCellEntity::createVoidCellAttributes)
                                    .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                            Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(0.6F, 0.6F)
            .maxTrackingRange(16)
            .build(void_cell);

    public static final EntityType<XunShengEntity> XUN_SHENG = FabricEntityType.Builder.createMob(XunShengEntity::new, SpawnGroup.MONSTER,
                    (mob) -> mob.defaultAttributes(XunShengEntity::addAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            // 调整尺寸（监守者：宽0.9，高2.9）
            .dimensions(0.6F, 1.95F)
            // 调整追踪范围（监守者：50）
            .maxTrackingRange(50)
            .build(xun_sheng);
    public static final EntityType<DeepCreatureEntity> DEEP_CREATURE = FabricEntityType.Builder.createMob(DeepCreatureEntity::new, SpawnGroup.MONSTER,
                    (mob) -> mob.defaultAttributes(DeepCreatureEntity::createDeepCreatureAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(3.6F, 6.95F)
            .makeFireImmune()
            .maxTrackingRange(64)
            .build(deep_creature);
    public static final EntityType<WitherSkeletonKingEntity> WITHER_SKELETON_KING = FabricEntityType.Builder.createMob(WitherSkeletonKingEntity::new, SpawnGroup.MONSTER,
                    (mob) -> mob.defaultAttributes(WitherSkeletonKingEntity::addAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(1.0F, 3.15F)
            .maxTrackingRange(64)
            .makeFireImmune()
            .build(wither_skeleton_king);
    public static final EntityType<VindicatorGeneralEntity> VINDICATOR_GENERAL = FabricEntityType.Builder.createMob(VindicatorGeneralEntity::new, SpawnGroup.MONSTER,
                    (mob) -> mob.defaultAttributes(VindicatorGeneralEntity::addAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(1.0F, 3.15F)
            .maxTrackingRange(64)
            .makeFireImmune()
            .build(vindicator_general);
    public static final EntityType<SkullKingEntity> SKULL_KING = FabricEntityType.Builder.createMob(SkullKingEntity::new, SpawnGroup.MONSTER,
                    (mob) -> mob.defaultAttributes(SkullKingEntity::addAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(1.0F, 3.15F)
            .maxTrackingRange(64)
            .makeFireImmune()
            .build(skull_king);
    public static final EntityType<SkullArcherEntity> SKULL_ARCHER = FabricEntityType.Builder.createMob(SkullArcherEntity::new, SpawnGroup.MONSTER,
                    (mob) -> mob.defaultAttributes(SkullArcherEntity::addAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(0.75F, 1.8F)
            .maxTrackingRange(64)
            .makeFireImmune()
            .build(skull_archer);
    public static final EntityType<SkullWarriorEntity> SKULL_WARRIOR = FabricEntityType.Builder.createMob(SkullWarriorEntity::new, SpawnGroup.MONSTER,
                    (mob) -> mob.defaultAttributes(SkullWarriorEntity::addAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(0.6F, 1.95F)
            .maxTrackingRange(64)
            .makeFireImmune()
            .build(skull_warrior);
    public static final EntityType<SkullMageEntity> SKULL_MAGE = FabricEntityType.Builder.createMob(SkullMageEntity::new, SpawnGroup.MONSTER,
                    (mob) -> mob.defaultAttributes(SkullMageEntity::addAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(0.6F, 1.95F)
            .maxTrackingRange(64)
            .makeFireImmune()
            .build(skull_mage);
    public static final EntityType<YoungMinEntity> YOUNG_MIN = FabricEntityType.Builder.createMob(YoungMinEntity::new, SpawnGroup.CREATURE,
                    (mob) -> mob.defaultAttributes(YoungMinEntity::createAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(0.6F, 0.8F)
            .maxTrackingRange(16)
            .build(young_min);
    public static final EntityType<HiddenEyeEntity> HIDDEN_EYE = FabricEntityType.Builder.createMob(HiddenEyeEntity::new, SpawnGroup.CREATURE,
                (mob) -> mob.defaultAttributes(HiddenEyeEntity::createAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(0.6F, 1.2F)
            .maxTrackingRange(16)
            .build(hidden_eye);

    /* ↓↓↓ 在 ModEntities 类里新增 ↓↓↓ */
    public static final EntityType<HighbirdBabyEntity> HIGHBIRD_BABY = FabricEntityType.Builder.createMob(HighbirdBabyEntity::new, SpawnGroup.CREATURE,
            (mob) -> mob.defaultAttributes(HighbirdBabyEntity::createHighbirdAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(1.5F, 1.2F)   // 高脚鸟幼崽比玩家小一点
            .maxTrackingRange(16)
            .build(highbird_baby);
    /* ↓↓↓ 在 ModEntities 类里新增 ↓↓↓ */
    public static final EntityType<HighbirdEggEntity> HIGHBIRD_EGG = FabricEntityType.Builder.createMob(HighbirdEggEntity::new, SpawnGroup.CREATURE,
                    (mob) -> mob.defaultAttributes(HighbirdEggEntity::createHighbirdAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(0.7F, 0.8F)
            .maxTrackingRange(16)
            .build(highbird_egg);
    /* ↓↓↓ 在 ModEntities 类里新增 ↓↓↓ */
    public static final EntityType<HighbirdTeenageEntity> HIGHBIRD_TEENAGE = FabricEntityType.Builder.createMob(HighbirdTeenageEntity::new, SpawnGroup.CREATURE,
                    (mob) -> mob.defaultAttributes(HighbirdTeenageEntity::createHighbirdAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(1.5F, 4.5F)
            .maxTrackingRange(24)
            .build(highbird_teenage);
    public static final EntityType<HighbirdAdulthoodEntity> HIGHBIRD_ADULTHOOD = FabricEntityType.Builder.createMob(HighbirdAdulthoodEntity::new, SpawnGroup.CREATURE,
                    (mob) -> mob.defaultAttributes(HighbirdAdulthoodEntity::createHighbirdAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(1.5F, 4.5F)
            .maxTrackingRange(24)
            .build(highbird_adulthood);
    public static final EntityType<VillagerIronGolemEntity> VILLAGER_IRON_GOLEM_ENTITY = FabricEntityType.Builder.createMob(VillagerIronGolemEntity::new, SpawnGroup.MISC,
                    (mob) -> mob.defaultAttributes(VillagerIronGolemEntity::createIronGolemAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(1.4F, 2.7F)
            .maxTrackingRange(10)
            .build(villager_iron_golem);
    public static final EntityType<VillagerKingEntity> VILLAGER_KING_ENTITY = FabricEntityType.Builder.createMob(VillagerKingEntity::new, SpawnGroup.MISC,
                    (mob) -> mob.defaultAttributes(VillagerKingEntity::createVillagerKingAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(1.4F, 3.25F)
            .build(villager_king);
    public static final EntityType<WitherSkullBulletEntity> WITHER_SKULL_BULLET_ENTITY =
            EntityType.Builder.<WitherSkullBulletEntity>create(WitherSkullBulletEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(0.3125F, 0.3125F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
                    .build(wither_skull_bullet);
    public static final EntityType<BulletEntity> BULLET_ENTITY =
            EntityType.Builder.<BulletEntity>create(BulletEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(0.3125F, 0.3125F)
                    .eyeHeight(0.13F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(20)
                    .build(bullet);
    public static final EntityType<LittleArrowEntity> LITTLE_ARROW =
            EntityType.Builder.<LittleArrowEntity>create(LittleArrowEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(0.3125F, 0.3125F)
                    .eyeHeight(0.13F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(20)
                    .build(little_arrow);
    public static final EntityType<CustomSuperBigFireballEntity> BIG_CUSTOM_FIREBALL =
            EntityType.Builder.<CustomSuperBigFireballEntity>create(CustomSuperBigFireballEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(1.0F, 1.0F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
                    .build(bigfireball);
    public static final EntityType<MeteoriteEntity> METEORITE =
            EntityType.Builder.<MeteoriteEntity>create(MeteoriteEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(1.0F, 1.0F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
                    .build(meteorite);
    public static final EntityType<FireWallEntity> FIRE_WALL =
            EntityType.Builder.<FireWallEntity>create(FireWallEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(1f, 3f)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
                    .build(firewall);
    public static final EntityType<BlueIronGolemEntity> BLUE_IRON_GOLEM =
            FabricEntityType.Builder.createMob(BlueIronGolemEntity::new, SpawnGroup.MISC, (mob) -> mob.defaultAttributes(BlueIronGolemEntity::createIronGolemAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .dimensions(1.4F, 2.7F)
                    .maxTrackingRange(10)
                    .build(blue_iron_golem);
    public static final EntityType<SugarManScorpion> SUGAR_MAN_SCORPION =
            FabricEntityType.Builder.createMob(SugarManScorpion::new, SpawnGroup.MONSTER,
                    (mob) -> mob.defaultAttributes(SugarManScorpion::createSugarManScorpionAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(1.5F, 1.2F)
            .maxTrackingRange(16)
            .build(sugar_man_scorpion);

    public static final EntityType<AttackDroneEntity> ATTACK_DRONE =
            FabricEntityType.Builder.createMob(AttackDroneEntity::new, SpawnGroup.MISC,
                    (mob) -> mob.defaultAttributes(AttackDroneEntity::createDroneAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(0.7F, 0.3F)
            .maxTrackingRange(5)
            .build(attack_drone);
    public static final EntityType<TreatmentDroneEntity> TREATMENT_DRONE =
            FabricEntityType.Builder.createMob(TreatmentDroneEntity::new, SpawnGroup.MISC,
                    (mob) -> mob.defaultAttributes(TreatmentDroneEntity::createDroneAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(0.7F, 0.3F)
            .maxTrackingRange(5)
            .build(treatment_drone);
    public static final EntityType<LittlePersonCivilianEntity> LITTLE_PERSON_CIVILIAN =
            FabricEntityType.Builder.createMob(LittlePersonCivilianEntity::new, SpawnGroup.MISC,
                    (mob) -> mob.defaultAttributes(LittlePersonCivilianEntity::createLittlePersonCivilianAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(0.6F, 0.9F)
            .maxTrackingRange(40)
            .build(little_person_civilian);
    public static final EntityType<LittlePersonMilitiaEntity> LITTLE_PERSON_MILITIA =
            FabricEntityType.Builder.createMob(LittlePersonMilitiaEntity::new, SpawnGroup.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonMilitiaEntity::createLittlePersonMilitiaAttributes)
                                    .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                            Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .dimensions(0.6F, 0.9F)
                    .maxTrackingRange(40)
                    .build(little_person_militia);
    public static final EntityType<LittlePersonArcherEntity> LITTLE_PERSON_ARCHER =
            FabricEntityType.Builder.createMob(LittlePersonArcherEntity::new, SpawnGroup.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonArcherEntity::createLittlePersonArcherAttributes)
                                    .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                            Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .dimensions(0.6F, 0.9F)
                    .maxTrackingRange(40)
                    .build(little_person_archer);
    public static final EntityType<LittlePersonGiantEntity> LITTLE_PERSON_GIANT =
            FabricEntityType.Builder.createMob(LittlePersonGiantEntity::new, SpawnGroup.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonGiantEntity::createLittlePersonGiantAttributes)
                                    .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                            Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .dimensions(0.6F, 0.8F)
                    .maxTrackingRange(40)
                    .build(little_person_giant);
    public static final EntityType<LittlePersonGuardEntity> LITTLE_PERSON_GUARD =
            FabricEntityType.Builder.createMob(LittlePersonGuardEntity::new, SpawnGroup.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonGuardEntity::createLittlePersonGuardAttributes)
                                    .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                            Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .dimensions(0.6F, 0.9F)
                    .maxTrackingRange(40)
                    .build(little_person_guard);
    public static final EntityType<LittlePersonKingEntity> LITTLE_PERSON_KING =
            FabricEntityType.Builder.createMob(LittlePersonKingEntity::new, SpawnGroup.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonKingEntity::createLittlePersonKingAttributes)
                                    .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                            Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .dimensions(0.6F, 0.9F)
                    .maxTrackingRange(40)
                    .build(little_person_king);


    public static void init() {
        Registry.register(Registries.ENTITY_TYPE, militia_warrior_villager, MILITIA_WARRIOR_VILLAGER);
        Registry.register(Registries.ENTITY_TYPE, militia_archer_villager, MILITIA_ARCHER_VILLAGER);
        Registry.register(Registries.ENTITY_TYPE, warrior_villager, WARRIOR_VILLAGER);
        Registry.register(Registries.ENTITY_TYPE, archer_villager, ARCHER_VILLAGER);
        Registry.register(Registries.ENTITY_TYPE, void_cell, VOID_CELL);
        Registry.register(Registries.ENTITY_TYPE, xun_sheng, XUN_SHENG);
        Registry.register(Registries.ENTITY_TYPE, deep_creature, DEEP_CREATURE);
        Registry.register(Registries.ENTITY_TYPE, wither_skeleton_king, WITHER_SKELETON_KING);
        Registry.register(Registries.ENTITY_TYPE, vindicator_general, VINDICATOR_GENERAL);
        Registry.register(Registries.ENTITY_TYPE, skull_king, SKULL_KING);
        Registry.register(Registries.ENTITY_TYPE, skull_archer, SKULL_ARCHER);
        Registry.register(Registries.ENTITY_TYPE, skull_warrior, SKULL_WARRIOR);
        Registry.register(Registries.ENTITY_TYPE, skull_mage, SKULL_MAGE);
        Registry.register(Registries.ENTITY_TYPE, young_min, YOUNG_MIN);
        Registry.register(Registries.ENTITY_TYPE, hidden_eye, HIDDEN_EYE);
        Registry.register(Registries.ENTITY_TYPE, meteorite, METEORITE);
        Registry.register(Registries.ENTITY_TYPE, highbird_baby, HIGHBIRD_BABY);
        Registry.register(Registries.ENTITY_TYPE, highbird_egg, HIGHBIRD_EGG);
        Registry.register(Registries.ENTITY_TYPE, highbird_teenage, HIGHBIRD_TEENAGE);
        Registry.register(Registries.ENTITY_TYPE, highbird_adulthood, HIGHBIRD_ADULTHOOD);
        Registry.register(Registries.ENTITY_TYPE, bigfireball, BIG_CUSTOM_FIREBALL);
        Registry.register(Registries.ENTITY_TYPE, firewall, FIRE_WALL);
        Registry.register(Registries.ENTITY_TYPE, blue_iron_golem, BLUE_IRON_GOLEM);
        Registry.register(Registries.ENTITY_TYPE, sugar_man_scorpion, SUGAR_MAN_SCORPION);
        Registry.register(Registries.ENTITY_TYPE, wither_skull_bullet, WITHER_SKULL_BULLET_ENTITY);
        Registry.register(Registries.ENTITY_TYPE, bullet, BULLET_ENTITY);
        Registry.register(Registries.ENTITY_TYPE, little_arrow, LITTLE_ARROW);
        Registry.register(Registries.ENTITY_TYPE, villager_iron_golem, VILLAGER_IRON_GOLEM_ENTITY);
        Registry.register(Registries.ENTITY_TYPE, villager_king, VILLAGER_KING_ENTITY);
        Registry.register(Registries.ENTITY_TYPE, attack_drone, ATTACK_DRONE);
        Registry.register(Registries.ENTITY_TYPE, treatment_drone, TREATMENT_DRONE);
        Registry.register(Registries.ENTITY_TYPE, little_person_civilian, LITTLE_PERSON_CIVILIAN);
        Registry.register(Registries.ENTITY_TYPE, little_person_militia, LITTLE_PERSON_MILITIA);
        Registry.register(Registries.ENTITY_TYPE, little_person_archer, LITTLE_PERSON_ARCHER);
        Registry.register(Registries.ENTITY_TYPE, little_person_giant, LITTLE_PERSON_GIANT);
        Registry.register(Registries.ENTITY_TYPE, little_person_guard, LITTLE_PERSON_GUARD);
        Registry.register(Registries.ENTITY_TYPE, little_person_king, LITTLE_PERSON_KING);
    }
}

