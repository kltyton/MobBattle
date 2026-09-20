package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.littleperson.icesoldier.IceSoldierEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

/** 冰兵实体类型的独立注册入口，不污染共享 ModEntities 小人目录。 */
public final class IceSoldierEntityTypes {
    public static final EntityType<IceSoldierEntity> ICE_SOLDIER = EntityRegistrySupport.registerEntityType(
            "ice_soldier",
            FabricEntityType.Builder.createMob(
                            IceSoldierEntity::new,
                            MobCategory.MISC,
                            mob -> mob.defaultAttributes(IceSoldierEntity::createIceSoldierAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                            (type, level, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40),
            false,
            false);

    private IceSoldierEntityTypes() {
    }

    public static void init() {
        // 通过调用触发静态注册，保留独立入口供主引导和测试使用。
    }
}
