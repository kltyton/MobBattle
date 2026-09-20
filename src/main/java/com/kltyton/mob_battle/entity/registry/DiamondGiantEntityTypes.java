package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.diamondgiant.DiamondGiantEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.SkillVisualEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

/** 钻石巨人实体类型注册器；通过共享注册支持层接入动态刷怪蛋体系。 */
public final class DiamondGiantEntityTypes {
    private DiamondGiantEntityTypes() {
    }

    public static final EntityType<DiamondGiantEntity> DIAMOND_GIANT = EntityRegistrySupport.registerEntityType(
            "diamond_giant",
            FabricEntityType.Builder.createMob(DiamondGiantEntity::new, MobCategory.MONSTER,
                            mob -> mob.defaultAttributes(DiamondGiantEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                            (type, world, reason, pos, random) -> false))
                    .sized(2.5F, 5.0F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            true,
            false
    );

    /** 钻石巨人 attack2 专属尖刺；复用通用视觉实体实现，但隔离模型、动画和伤害契约。 */
    public static final EntityType<SkillVisualEntity> DIAMOND_GIANT_SPIKE = EntityRegistrySupport.registerEntityType(
            "diamond_giant_spike",
            EntityType.Builder.<SkillVisualEntity>of(SkillVisualEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(4.0F, 3.5F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
}
