package com.kltyton.mob_battle.entity.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import com.kltyton.mob_battle.entity.skull.mage.NewSkullMageEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.SpawnPlacementTypes;

/**
 * 骷髅体系附属实体类型注册器。
 *
 * <p>字段声明顺序与原 {@code ModEntities} 完全一致。直接注册的实体仍在对应别名首次解析时注册；
 * 仅构造的实体继续由 {@code ModEntities.init()} 按历史顺序写入注册表。</p>
 */
public final class SkullSupportEntityTypes {

    private SkullSupportEntityTypes() {
    }

    public static final EntityType<NewSkullMageEntity> NEW_SKULL_MAGE = EntityRegistrySupport.registerEntityType(
            "new_skull_mage",
            FabricEntityType.Builder.createMob(
                            NewSkullMageEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(NewSkullMageEntity::createNewSkullMageAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.8F, 1.55F)
                    .clientTrackingRange(40),
            true,
            false
    );
}
