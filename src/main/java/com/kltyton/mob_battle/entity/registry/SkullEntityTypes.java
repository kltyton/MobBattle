package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.skull.archer.SkullArcherEntity;
import com.kltyton.mob_battle.entity.skull.king.SkullKingEntity;
import com.kltyton.mob_battle.entity.skull.mage.SkullMageEntity;
import com.kltyton.mob_battle.entity.skull.mage.SummonedSkeletonEntity;
import com.kltyton.mob_battle.entity.skull.warrior.SkullWarriorEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * 骷髅族实体（skull 家族）的类型构造领域类。
 *
 * <p>集中保存 5 个骷髅族实体的真实构造表达式：骷髅王、骷髅弓箭手、骷髅战士、
 * 骷髅法师与召唤骷髅。字段声明顺序与原 ModEntities 中的声明顺序完全一致。
 * 本类字段使用 {@code .build(ModEntities.xxx)} 仅构造类型，实际注册由
 * {@code ModEntities.init()} 按历史时机和顺序执行。</p>
 *
 * <p><b>别名与加载顺序契约：</b>本类必须经由 ModEntities 的别名字段（SKULL_KING 等）
 * 首次触发静态初始化；本类不注册、不填充任何 Map，因此不改变注册顺序语义。</p>
 */
public final class SkullEntityTypes {

    private SkullEntityTypes() {
    }

    public static final EntityType<SkullKingEntity> SKULL_KING =
            FabricEntityType.Builder.createMob(SkullKingEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(SkullKingEntity::addAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(1.0F, 3.15F)
                    .clientTrackingRange(64)
                    .fireImmune()
                    .build(ModEntities.skull_king);
    public static final EntityType<SkullArcherEntity> SKULL_ARCHER =
            FabricEntityType.Builder.createMob(SkullArcherEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(SkullArcherEntity::addAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.75F, 1.8F)
                    .clientTrackingRange(64)
                    .fireImmune()
                    .build(ModEntities.skull_archer);
    public static final EntityType<SkullWarriorEntity> SKULL_WARRIOR =
            FabricEntityType.Builder.createMob(SkullWarriorEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(SkullWarriorEntity::addAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(64)
                    .fireImmune()
                    .build(ModEntities.skull_warrior);
    public static final EntityType<SkullMageEntity> SKULL_MAGE =
            FabricEntityType.Builder.createMob(SkullMageEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(SkullMageEntity::addAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(64)
                    .fireImmune()
                    .build(ModEntities.skull_mage);
    public static final EntityType<SummonedSkeletonEntity> SUMMONED_SKELETON =
            FabricEntityType.Builder.createMob(SummonedSkeletonEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(SummonedSkeletonEntity::createSummonedSkeletonAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(64)
                    .build(ModEntities.summoned_skeleton);
}
