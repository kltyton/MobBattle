package com.kltyton.mob_battle.entity;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.blueirongolem.BlueIronGolemEntity;
import com.kltyton.mob_battle.entity.bullet.BulletEntity;
import com.kltyton.mob_battle.entity.customfireball.CustomSuperBigFireballEntity;
import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import com.kltyton.mob_battle.entity.drone.attackdrone.AttackDroneEntity;
import com.kltyton.mob_battle.entity.drone.treatmentdrone.TreatmentDroneEntity;
import com.kltyton.mob_battle.entity.firewall.FireWallEntity;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import com.kltyton.mob_battle.entity.highbird.baby.HighbirdBabyEntity;
import com.kltyton.mob_battle.entity.highbird.egg.HighbirdEggEntity;
import com.kltyton.mob_battle.entity.highbird.teenage.HighbirdTeenageEntity;
import com.kltyton.mob_battle.entity.irongolem.VillagerIronGolemEntity;
import com.kltyton.mob_battle.entity.sugarmanscorpion.SugarManScorpion;
import com.kltyton.mob_battle.entity.villager.archervillager.ArcherVillager;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager;
import com.kltyton.mob_battle.entity.villager.villagerking.VillagerKingEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
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
    public static final RegistryKey<EntityType<?>> warrior_villager = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"warrior_villager"));
    public static final RegistryKey<EntityType<?>> archer_villager = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"archer_villager"));
    public static final RegistryKey<EntityType<?>> xun_sheng= RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"xun_sheng"));
    public static final RegistryKey<EntityType<?>> deep_creature = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"deep_creature"));
    public static final RegistryKey<EntityType<?>> highbird_baby = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"highbird_baby"));
    public static final RegistryKey<EntityType<?>> highbird_teenage = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"highbird_teenage"));
    public static final RegistryKey<EntityType<?>> highbird_adulthood = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"highbird_adulthood"));
    public static final RegistryKey<EntityType<?>> highbird_egg = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"highbird_egg"));
    public static final RegistryKey<EntityType<?>> bigfireball = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"bigfireball"));
    public static final RegistryKey<EntityType<?>> firewall = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"fairewall"));
    public static final RegistryKey<EntityType<?>> blue_iron_golem = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"blue_iron_golem"));
    public static final RegistryKey<EntityType<?>> sugar_man_scorpion = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"sugar_man_scorpion"));
    public static final RegistryKey<EntityType<?>> wither_skeleton_king = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"wither_skeleton_king"));
    public static final RegistryKey<EntityType<?>> bullet = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"bullet"));
    public static final RegistryKey<EntityType<?>> villgaer_iron_golem = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"iron_golem"));
    public static final RegistryKey<EntityType<?>> villager_king = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"villager_king"));
    public static final RegistryKey<EntityType<?>> attack_drone =  RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"attack_drone"));
    public static final RegistryKey<EntityType<?>> treatment_drone = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"treatment_drone"));
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
                    (mob) -> mob.defaultAttributes(() -> VillagerEntity.createVillagerAttributes()
                            .add(EntityAttributes.ATTACK_DAMAGE, 3.0D)
                            .add(EntityAttributes.ARMOR, 1.0D))
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                                    ArcherVillager::checkSnuffleSpawnRules))
            .dimensions(0.6F, 1.95F)
            .maxTrackingRange(16)
            .build(archer_villager);
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
            .maxTrackingRange(64)
            .build(deep_creature);
    public static final EntityType<WitherSkeletonKingEntity> WITHER_SKELETON_KING = FabricEntityType.Builder.createMob(WitherSkeletonKingEntity::new, SpawnGroup.MONSTER,
                    (mob) -> mob.defaultAttributes(WitherSkeletonKingEntity::addAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(1.0F, 3.15F)
            .maxTrackingRange(64)
            .build(wither_skeleton_king);
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
            .build(villgaer_iron_golem);
    public static final EntityType<VillagerKingEntity> VILLAGER_KING_ENTITY = FabricEntityType.Builder.createMob(VillagerKingEntity::new, SpawnGroup.MISC,
                    (mob) -> mob.defaultAttributes(VillagerKingEntity::createVillagerKingAttributes)
                            .spawnRestriction(SpawnLocationTypes.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .dimensions(1.4F, 3.25F)
            .build(villager_king);
    public static final EntityType<BulletEntity> BULLET_ENTITY =
            EntityType.Builder.<BulletEntity>create(BulletEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(0.3125F, 0.3125F)
                    .eyeHeight(0.13F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(20)
                    .build(bullet);
    public static final EntityType<CustomSuperBigFireballEntity> BIG_CUSTOM_FIREBALL =
            EntityType.Builder.<CustomSuperBigFireballEntity>create(CustomSuperBigFireballEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(1.0F, 1.0F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
                    .build(bigfireball);
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

    public static void init() {
        Registry.register(Registries.ENTITY_TYPE, warrior_villager, WARRIOR_VILLAGER);
        Registry.register(Registries.ENTITY_TYPE, archer_villager, ARCHER_VILLAGER);
        Registry.register(Registries.ENTITY_TYPE, xun_sheng, XUN_SHENG);
        Registry.register(Registries.ENTITY_TYPE, deep_creature, DEEP_CREATURE);
        Registry.register(Registries.ENTITY_TYPE, wither_skeleton_king, WITHER_SKELETON_KING);
        Registry.register(Registries.ENTITY_TYPE, highbird_baby, HIGHBIRD_BABY);
        Registry.register(Registries.ENTITY_TYPE, highbird_egg, HIGHBIRD_EGG);
        Registry.register(Registries.ENTITY_TYPE, highbird_teenage, HIGHBIRD_TEENAGE);
        Registry.register(Registries.ENTITY_TYPE, highbird_adulthood, HIGHBIRD_ADULTHOOD);
        Registry.register(Registries.ENTITY_TYPE, bigfireball, BIG_CUSTOM_FIREBALL);
        Registry.register(Registries.ENTITY_TYPE, firewall, FIRE_WALL);
        Registry.register(Registries.ENTITY_TYPE, blue_iron_golem, BLUE_IRON_GOLEM);
        Registry.register(Registries.ENTITY_TYPE, sugar_man_scorpion, SUGAR_MAN_SCORPION);
        Registry.register(Registries.ENTITY_TYPE, bullet, BULLET_ENTITY);
        Registry.register(Registries.ENTITY_TYPE, villgaer_iron_golem, VILLAGER_IRON_GOLEM_ENTITY);
        Registry.register(Registries.ENTITY_TYPE, villager_king, VILLAGER_KING_ENTITY);
        Registry.register(Registries.ENTITY_TYPE, attack_drone, ATTACK_DRONE);
        Registry.register(Registries.ENTITY_TYPE, treatment_drone, TREATMENT_DRONE);
    }
}

