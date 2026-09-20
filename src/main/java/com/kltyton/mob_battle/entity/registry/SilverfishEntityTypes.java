package com.kltyton.mob_battle.entity.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import com.kltyton.mob_battle.entity.silverfish.silverfish.AngrySilverfishEntity;
import com.kltyton.mob_battle.entity.silverfish.silverfish.DrillSilverfishEntity;
import com.kltyton.mob_battle.entity.silverfish.silverfish.LiruiSilverfishEntity;
import com.kltyton.mob_battle.entity.silverfish.silverfish.LoadSilverfishEntity;
import com.kltyton.mob_battle.entity.silverfish.silverfish.LongWhipSilverfishEntity;
import com.kltyton.mob_battle.entity.silverfish.silverfish.PoisonousSilverfishEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.SpawnPlacementTypes;

/**
 * 蠹虫变体实体类型注册器。
 *
 * <p>字段声明顺序与原 {@code ModEntities} 完全一致。直接注册的实体仍在对应别名首次解析时注册；
 * 仅构造的实体继续由 {@code ModEntities.init()} 按历史顺序写入注册表。</p>
 */
public final class SilverfishEntityTypes {

    private SilverfishEntityTypes() {
    }

    public static final EntityType<LiruiSilverfishEntity> LIRUI_SILVERFISH = EntityRegistrySupport.registerEntityType(
            "ruili_silverfish",
            FabricEntityType.Builder.createMob(LiruiSilverfishEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(LiruiSilverfishEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.4F, 0.3F)
                    .eyeHeight(0.13F)
                    .passengerAttachments(0.2375F)
                    .clientTrackingRange(8),
            true,
            true
    );
    public static final EntityType<DrillSilverfishEntity> DRILL_SILVERFISH = EntityRegistrySupport.registerEntityType(
            "drill_silverfish",
            FabricEntityType.Builder.createMob(DrillSilverfishEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(DrillSilverfishEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.4F, 0.3F)
                    .eyeHeight(0.13F)
                    .passengerAttachments(0.2375F)
                    .clientTrackingRange(8),
            true,
            true
    );
    public static final EntityType<PoisonousSilverfishEntity> POISONOUS_SILVERFISH = EntityRegistrySupport.registerEntityType(
            "poisonous_silverfish",
            FabricEntityType.Builder.createMob(PoisonousSilverfishEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(PoisonousSilverfishEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.4F, 0.3F)
                    .eyeHeight(0.13F)
                    .passengerAttachments(0.2375F)
                    .clientTrackingRange(8),
            true,
            true
    );
    public static final EntityType<LoadSilverfishEntity> LOAD_SILVERFISH = EntityRegistrySupport.registerEntityType(
            "load_silverfish",
            FabricEntityType.Builder.createMob(LoadSilverfishEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(LoadSilverfishEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.4F, 0.3F)
                    .eyeHeight(0.13F)
                    .passengerAttachments(0.2375F)
                    .clientTrackingRange(8),
            true,
            true
    );
    public static final EntityType<LongWhipSilverfishEntity> LONG_WHIP_SILVERFISH = EntityRegistrySupport.registerEntityType(
            "long_whip_silverfish",
            FabricEntityType.Builder.createMob(LongWhipSilverfishEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(LongWhipSilverfishEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(1F, 1F)
                    .eyeHeight(0.13F)
                    .passengerAttachments(0.2375F)
                    .clientTrackingRange(8),
            true,
            true
    );
    public static final EntityType<AngrySilverfishEntity> ANGRY_SILVERFISH = EntityRegistrySupport.registerEntityType(
            "angry_silverfish",
            FabricEntityType.Builder.createMob(AngrySilverfishEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(AngrySilverfishEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.4F, 0.3F)
                    .eyeHeight(0.13F)
                    .passengerAttachments(0.2375F)
                    .clientTrackingRange(8),
            true,
            false
    );
}
