package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.enhancedwither.EnhancedWitherEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.summon.DualBladeWitherSkeletonEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.summon.ShieldAxeWitherSkeletonEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.SpawnPlacementTypes;

/**
 * 凋灵阵营实体类型注册器。
 *
 * <p>字段声明顺序与原 {@code ModEntities} 完全一致。直接注册的实体仍在对应别名首次解析时注册；
 * 仅构造的实体继续由 {@code ModEntities.init()} 按历史顺序写入注册表。</p>
 */
public final class WitherFactionEntityTypes {

    private WitherFactionEntityTypes() {
    }

    public static final EntityType<EnhancedWitherEntity> ENHANCED_WITHER =
            FabricEntityType.Builder.createMob(EnhancedWitherEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(EnhancedWitherEntity::createEnhancedWitherAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.9F, 3.5F)
                    .clientTrackingRange(64)
                    .fireImmune()
                    .build(ModEntities.enhanced_wither);
    public static final EntityType<DualBladeWitherSkeletonEntity> DUAL_BLADE_WITHER_SKELETON = EntityRegistrySupport.registerEntityType(
            "dual_blade_wither_skeleton",
            FabricEntityType.Builder.createMob(DualBladeWitherSkeletonEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(DualBladeWitherSkeletonEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.7F, 2.4F)
                    .clientTrackingRange(64)
                    .fireImmune(),
            true,
            false
    );
    public static final EntityType<ShieldAxeWitherSkeletonEntity> SHIELD_AXE_WITHER_SKELETON = EntityRegistrySupport.registerEntityType(
            "shield_axe_wither_skeleton",
            FabricEntityType.Builder.createMob(ShieldAxeWitherSkeletonEntity::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(ShieldAxeWitherSkeletonEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.8F, 2.4F)
                    .clientTrackingRange(64)
                    .fireImmune(),
            true,
            false
    );
}
