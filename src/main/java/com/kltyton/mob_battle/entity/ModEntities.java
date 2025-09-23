package com.kltyton.mob_battle.entity;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.customfireball.CustomSuperBigFireballEntity;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import com.kltyton.mob_battle.entity.highbird.baby.HighbirdBabyEntity;
import com.kltyton.mob_battle.entity.highbird.egg.HighbirdEggEntity;
import com.kltyton.mob_battle.entity.highbird.teenage.HighbirdTeenageEntity;
import com.kltyton.mob_battle.entity.villager.archervillager.ArcherVillager;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager;
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
    public static final RegistryKey<EntityType<?>> highbird_baby = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"highbird_baby"));
    public static final RegistryKey<EntityType<?>> highbird_teenage = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"highbird_teenage"));
    public static final RegistryKey<EntityType<?>> highbird_adulthood = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"highbird_adulthood"));
    public static final RegistryKey<EntityType<?>> highbird_egg = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"highbird_egg"));

    public static final RegistryKey<EntityType<?>> bigfireball = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Mob_battle.MOD_ID,"bigfireball"));
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
    /* ↓↓↓ 在 ModEntities 类里新增 ↓↓↓ */
    public static final EntityType<CustomSuperBigFireballEntity> BIG_CUSTOM_FIREBALL =
            EntityType.Builder.<CustomSuperBigFireballEntity>create(CustomSuperBigFireballEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(1.0F, 1.0F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
            .build(bigfireball);


    public static void init() {
        Registry.register(Registries.ENTITY_TYPE, warrior_villager, WARRIOR_VILLAGER);
        Registry.register(Registries.ENTITY_TYPE, archer_villager, ARCHER_VILLAGER);
        Registry.register(Registries.ENTITY_TYPE, xun_sheng, XUN_SHENG);
        Registry.register(Registries.ENTITY_TYPE, highbird_baby, HIGHBIRD_BABY);
        Registry.register(Registries.ENTITY_TYPE, highbird_egg, HIGHBIRD_EGG);
        Registry.register(Registries.ENTITY_TYPE, highbird_teenage, HIGHBIRD_TEENAGE);
        Registry.register(Registries.ENTITY_TYPE, highbird_adulthood, HIGHBIRD_ADULTHOOD);
        Registry.register(Registries.ENTITY_TYPE, bigfireball, BIG_CUSTOM_FIREBALL);
    }
}

