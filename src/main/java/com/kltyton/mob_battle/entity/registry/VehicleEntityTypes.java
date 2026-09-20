package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.vehicle.obsidianboat.ObsidianBoatEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/** 载具领域实体类型；客户端渲染与物品注册由各自侧的入口负责。 */
public final class VehicleEntityTypes {
    public static final EntityType<ObsidianBoatEntity> OBSIDIAN_BOAT = EntityRegistrySupport.registerEntityType(
            "obsidian_boat",
            EntityType.Builder.<ObsidianBoatEntity>of(ObsidianBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .fireImmune(),
            false,
            false
    );

    private VehicleEntityTypes() {
    }
}
