package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.cbot.Cbot002Entity;
import com.kltyton.mob_battle.entity.golem.ChestGolemEntity;
import com.kltyton.mob_battle.entity.golem.StrongMinEntity;
import com.kltyton.mob_battle.entity.piglingeneral.PiglinGeneralEntity;
import com.kltyton.mob_battle.entity.snowgolem.NewSnowGolemEntity;
import com.kltyton.mob_battle.entity.summon.SummonedVexEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.WitherSkeletonDogEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.monster.Vex;

/**
 * 受命令驱动、召唤或构造生成的战斗单位类型注册器。
 *
 * <p>字段声明顺序与原 {@code ModEntities} 完全一致。直接注册的实体仍在对应别名首次解析时注册；
 * 仅构造的实体继续由 {@code ModEntities.init()} 按历史顺序写入注册表。</p>
 */
public final class CommandedUnitEntityTypes {

    private CommandedUnitEntityTypes() {
    }

    public static final EntityType<Cbot002Entity> CBOT002 =
            FabricEntityType.Builder.createMob(Cbot002Entity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(Cbot002Entity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(1.2F, 3.5F)
                    .clientTrackingRange(40)
                    .build(ModEntities.cbot002);
    public static final EntityType<PiglinGeneralEntity> PIGLIN_GENERAL =
            FabricEntityType.Builder.createMob(PiglinGeneralEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(PiglinGeneralEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(1.2F, 3.0F)
                    .clientTrackingRange(64)
                    .fireImmune()
                    .build(ModEntities.piglin_general);
    public static final EntityType<WitherSkeletonDogEntity> WITHER_SKELETON_DOG = EntityRegistrySupport.registerEntityType(
            "wither_skeleton_dog",
            FabricEntityType.Builder.createMob(WitherSkeletonDogEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(WitherSkeletonDogEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.9F, 0.9F)
                    .clientTrackingRange(60)
                    .fireImmune(),
            true,
            true
    );
    public static final EntityType<SummonedVexEntity> SUMMONED_VEX = EntityRegistrySupport.registerEntityType(
            "summoned_vex",
            FabricEntityType.Builder.createMob(SummonedVexEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(Vex::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.4F, 0.8F)
                    .clientTrackingRange(8),
            false,
            false
    );
    public static final EntityType<NewSnowGolemEntity> NEW_SNOW_GOLEM = EntityRegistrySupport.registerEntityType(
            "new_snow_golem",
            FabricEntityType.Builder.createMob(NewSnowGolemEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(SnowGolem::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.7F, 1.9F)
                    .eyeHeight(1.7F)
                    .clientTrackingRange(10),
            true,
            false
    );
    public static final EntityType<ChestGolemEntity> CHEST_GOLEM = EntityRegistrySupport.registerEntityType(
            "chest_golem",
            FabricEntityType.Builder.createMob(ChestGolemEntity::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(ChestGolemEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(EntityType.IRON_GOLEM.getWidth(), EntityType.IRON_GOLEM.getHeight())
                    .eyeHeight(2.3F)
                    .clientTrackingRange(10),
            true,
            false
    );
    public static final EntityType<StrongMinEntity> STRONG_MIN = EntityRegistrySupport.registerEntityType(
            "strong_min",
            FabricEntityType.Builder.createMob(StrongMinEntity::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(StrongMinEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(1.4F, 2.7F)
                    .eyeHeight(2.3F)
                    .clientTrackingRange(10),
            true,
            false
    );
}
