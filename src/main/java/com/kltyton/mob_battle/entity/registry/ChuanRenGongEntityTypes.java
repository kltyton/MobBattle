package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.chuanrengong.ChuanRenGongEntity;
import com.kltyton.mob_battle.entity.chuanrengong.ChuanRenGongLargeProjectileEntity;
import com.kltyton.mob_battle.entity.chuanrengong.ChuanRenGongSmallProjectileEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

/** 传仁工及其两种弹体的独立类型注册器；不扩展共享 ModEntities 门面。 */
public final class ChuanRenGongEntityTypes {
    private ChuanRenGongEntityTypes() {
    }

    public static final EntityType<ChuanRenGongEntity> CHUAN_REN_GONG = EntityRegistrySupport.registerEntityType(
            "chuan_ren_gong",
            FabricEntityType.Builder.createMob(ChuanRenGongEntity::new, MobCategory.MONSTER,
                            mob -> mob.defaultAttributes(ChuanRenGongEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                            (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
                    .notInPeaceful(),
            true,
            false
    );

    public static final EntityType<ChuanRenGongSmallProjectileEntity> SMALL_PROJECTILE = EntityRegistrySupport.registerEntityType(
            "chuan_ren_gong_small_projectile",
            EntityType.Builder.<ChuanRenGongSmallProjectileEntity>of(ChuanRenGongSmallProjectileEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.15F, 0.15F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );

    public static final EntityType<ChuanRenGongLargeProjectileEntity> LARGE_PROJECTILE = EntityRegistrySupport.registerEntityType(
            "chuan_ren_gong_large_projectile",
            EntityType.Builder.<ChuanRenGongLargeProjectileEntity>of(ChuanRenGongLargeProjectileEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );

    /** 由 bootstrap 显式调用，确保刷怪蛋遍历前完成类型和目录注册。 */
    public static void init() {
    }
}
