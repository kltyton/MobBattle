package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import com.kltyton.mob_battle.entity.highbird.baby.HighbirdBabyEntity;
import com.kltyton.mob_battle.entity.highbird.egg.HighbirdEggEntity;
import com.kltyton.mob_battle.entity.highbird.teenage.HighbirdTeenageEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * 高脚鸟实体（highbird 家族）的类型构造领域类。
 *
 * <p>集中保存 4 个高脚鸟生命周期实体的真实构造表达式：幼鸟、蛋、少年鸟与成年鸟。
 * 字段声明顺序与原 ModEntities 中的声明顺序完全一致。本类字段使用
 * {@code .build(ModEntities.xxx)} 仅构造类型，实际注册由 {@code ModEntities.init()}
 * 按历史时机和顺序执行。</p>
 *
 * <p><b>别名与加载顺序契约：</b>本类必须经由 ModEntities 的别名字段（HIGHBIRD_BABY 等）
 * 首次触发静态初始化；本类不注册、不填充任何 Map，因此不改变注册顺序语义。</p>
 */
public final class HighbirdEntityTypes {

    private HighbirdEntityTypes() {
    }

    public static final EntityType<HighbirdBabyEntity> HIGHBIRD_BABY =
            FabricEntityType.Builder.createMob(HighbirdBabyEntity::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(HighbirdBabyEntity::createHighbirdAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(1.5F, 1.2F)   // 保留幼鸟碰撞箱：宽 1.5、高 1.2。
                    .clientTrackingRange(16)
                    .build(ModEntities.highbird_baby);
    public static final EntityType<HighbirdEggEntity> HIGHBIRD_EGG =
            FabricEntityType.Builder.createMob(HighbirdEggEntity::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(HighbirdEggEntity::createHighbirdAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.7F, 0.8F)
                    .clientTrackingRange(16)
                    .build(ModEntities.highbird_egg);
    public static final EntityType<HighbirdTeenageEntity> HIGHBIRD_TEENAGE =
            FabricEntityType.Builder.createMob(HighbirdTeenageEntity::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(HighbirdTeenageEntity::createHighbirdAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(1.5F, 4.5F)
                    .clientTrackingRange(24)
                    .build(ModEntities.highbird_teenage);
    public static final EntityType<HighbirdAdulthoodEntity> HIGHBIRD_ADULTHOOD =
            FabricEntityType.Builder.createMob(HighbirdAdulthoodEntity::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(HighbirdAdulthoodEntity::createHighbirdAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(1.5F, 4.5F)
                    .clientTrackingRange(24)
                    .build(ModEntities.highbird_adulthood);
}
