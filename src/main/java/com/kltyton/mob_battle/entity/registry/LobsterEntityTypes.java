package com.kltyton.mob_battle.entity.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import com.kltyton.mob_battle.entity.lobster.LobsterEntity;
import com.kltyton.mob_battle.entity.lobster.MagmaLobsterEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.SpawnPlacementTypes;

/**
 * 龙虾生物实体类型注册器。
 *
 * <p>字段声明顺序与原 {@code ModEntities} 完全一致。直接注册的实体仍在对应别名首次解析时注册；
 * 仅构造的实体继续由 {@code ModEntities.init()} 按历史顺序写入注册表。</p>
 */
public final class LobsterEntityTypes {

    private LobsterEntityTypes() {
    }

    public static final EntityType<LobsterEntity> LOBSTER = EntityRegistrySupport.registerEntityType(
            "lobster_entity",
            FabricEntityType.Builder.createMob(LobsterEntity::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(LobsterEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                            (type, world, reason, pos, random) -> false))
                    .sized(2F, 0.7F)
                    .eyeHeight(0.25F)
                    .clientTrackingRange(8),
            true,
            false
    );
    public static final EntityType<MagmaLobsterEntity> MAGMA_LOBSTER = EntityRegistrySupport.registerEntityType(
            "magma_lobster_entity",
            FabricEntityType.Builder.createMob(MagmaLobsterEntity::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(MagmaLobsterEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                            (type, world, reason, pos, random) -> false))
                    .sized(2F, 0.7F)
                    .eyeHeight(0.25F)
                    .clientTrackingRange(8)
                    .fireImmune(),
            true,
            false
    );
}
