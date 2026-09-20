package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.roughwhitezetsu.RoughWhiteZetsuEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.levelgen.Heightmap;

/** 粗糙白绝类型注册器；实体构造只复用原版 Zombie 行为。 */
public final class RoughWhiteZetsuEntityTypes {
    private RoughWhiteZetsuEntityTypes() {
    }

    public static final EntityType<RoughWhiteZetsuEntity> ROUGH_WHITE_ZETSU = EntityRegistrySupport.registerEntityType(
            "rough_white_zetsu",
            FabricEntityType.Builder.createMob(RoughWhiteZetsuEntity::new, MobCategory.MONSTER,
                            mob -> mob.defaultAttributes(Zombie::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                            (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 1.95F)
                    .eyeHeight(1.74F)
                    .passengerAttachments(2.0125F)
                    .ridingOffset(-0.7F)
                    .clientTrackingRange(8)
                    .notInPeaceful(),
            true,
            false
    );

    /** 由 bootstrap 显式调用，确保刷怪蛋遍历前完成类型和目录注册。 */
    public static void init() {
    }
}
