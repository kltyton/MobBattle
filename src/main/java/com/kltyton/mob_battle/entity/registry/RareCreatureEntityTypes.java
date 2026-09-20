package com.kltyton.mob_battle.entity.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import com.kltyton.mob_battle.entity.evoker.SuperEvokerEntity;
import com.kltyton.mob_battle.entity.flowerfairy.FlowerFairyEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.SpawnPlacementTypes;

/**
 * 稀有独立生物实体类型注册器。
 *
 * <p>字段声明顺序与原 {@code ModEntities} 完全一致。直接注册的实体仍在对应别名首次解析时注册；
 * 仅构造的实体继续由 {@code ModEntities.init()} 按历史顺序写入注册表。</p>
 */
public final class RareCreatureEntityTypes {

    private RareCreatureEntityTypes() {
    }

    public static final EntityType<FlowerFairyEntity> FLOWER_FAIRY = EntityRegistrySupport.registerEntityType(
            "flower_fairy",
            FabricEntityType.Builder.createMob(FlowerFairyEntity::new, MobCategory.CREATURE,

                            (mob) -> mob.defaultAttributes(FlowerFairyEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))

                    .sized(0.4F, 0.4F)
                    .eyeHeight(0.3F)
                    .clientTrackingRange(10),
            true,
            true
    );
    public static final EntityType<SuperEvokerEntity> SUPER_EVOKER  = EntityRegistrySupport.registerEntityType(
            "super_evoker",
            FabricEntityType.Builder.createMob(SuperEvokerEntity::new, MobCategory.MONSTER,

            (mob) -> mob.defaultAttributes(SuperEvokerEntity::createSuperEvokerAttributes)
                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))

                    .sized(0.6F, 1.95F)
                    .passengerAttachments(2.0F)
                    .ridingOffset(-0.6F)
                    .clientTrackingRange(8),
            true,
            false
    );
}
