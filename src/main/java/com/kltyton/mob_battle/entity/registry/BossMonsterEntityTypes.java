package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import com.kltyton.mob_battle.entity.irongolem.hulkbuster.HulkbusterEntity;
import com.kltyton.mob_battle.entity.vindicatorgeneral.VindicatorGeneralEntity;
import com.kltyton.mob_battle.entity.voidcell.VoidCellEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.entity.xunsheng.XunShengEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * 自定义首领/精英怪物实体（boss monster 家族）的类型构造领域类。
 *
 * <p>集中保存 6 个首领级自定义怪物的真实构造表达式：虚空细胞、寻圣、深渊生物、
 * 凋灵骷髅王、灾厄将军、浩克破坏者。字段声明顺序与原 ModEntities 中的声明顺序完全一致。
 * 本类字段使用 {@code .build(ModEntities.xxx)} 仅构造类型，实际注册由
 * {@code ModEntities.init()} 按历史时机和顺序执行。</p>
 *
 * <p><b>别名与加载顺序契约：</b>本类必须经由 ModEntities 的别名字段（VOID_CELL 等）
 * 首次触发静态初始化；本类不注册、不填充任何 Map，因此不改变注册顺序语义。</p>
 */
public final class BossMonsterEntityTypes {

    private BossMonsterEntityTypes() {
    }

    public static final EntityType<VoidCellEntity> VOID_CELL =
            FabricEntityType.Builder.createMob(VoidCellEntity::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(VoidCellEntity::createVoidCellAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.6F)
                    .clientTrackingRange(16)
                    .build(ModEntities.void_cell);

    public static final EntityType<XunShengEntity> XUN_SHENG =
            FabricEntityType.Builder.createMob(XunShengEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(XunShengEntity::addAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    // 保留原寻圣实体碰撞箱：宽 0.6、高 1.95。
                    .sized(0.6F, 1.95F)
                    // 保留原 50 格客户端追踪范围。
                    .clientTrackingRange(50)
                    .build(ModEntities.xun_sheng);
    public static final EntityType<DeepCreatureEntity> DEEP_CREATURE =
            FabricEntityType.Builder.createMob(DeepCreatureEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(DeepCreatureEntity::createDeepCreatureAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(3.6F, 6.95F)
                    .fireImmune()
                    .clientTrackingRange(64)
                    .build(ModEntities.deep_creature);
    public static final EntityType<WitherSkeletonKingEntity> WITHER_SKELETON_KING =
            FabricEntityType.Builder.createMob(WitherSkeletonKingEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(WitherSkeletonKingEntity::addAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(1.0F, 3.15F)
                    .clientTrackingRange(64)
                    .fireImmune()
                    .build(ModEntities.wither_skeleton_king);
    public static final EntityType<VindicatorGeneralEntity> VINDICATOR_GENERAL =
            FabricEntityType.Builder.createMob(VindicatorGeneralEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(VindicatorGeneralEntity::addAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(1.0F, 3.15F)
                    .clientTrackingRange(64)
                    .fireImmune()
                    .build(ModEntities.vindicator_general);
    public static final EntityType<HulkbusterEntity> HULKBUSTER =
            FabricEntityType.Builder.createMob(HulkbusterEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(HulkbusterEntity::addAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(1.0F, 3.15F)
                    .clientTrackingRange(64)
                    .build(ModEntities.hulkbuster);
}
