package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.archer.LittlePersonArcherEntity;
import com.kltyton.mob_battle.entity.littleperson.civilian.LittlePersonCivilianEntity;
import com.kltyton.mob_battle.entity.littleperson.civilian.LittlePersonWorkerEntity;
import com.kltyton.mob_battle.entity.littleperson.giant.LittlePersonGiantEntity;
import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntity;
import com.kltyton.mob_battle.entity.littleperson.king.LittlePersonKingEntity;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * 小人族基础生命周期与职业实体类型构造器。
 *
 * <p>字段声明顺序与原 {@code ModEntities} 完全一致。直接注册的实体仍在对应别名首次解析时注册；
 * 仅构造的实体继续由 {@code ModEntities.init()} 按历史顺序写入注册表。</p>
 */
public final class LittlePersonBaseEntityTypes {

    private LittlePersonBaseEntityTypes() {
    }

    public static final EntityType<LittlePersonCivilianEntity> LITTLE_PERSON_CIVILIAN =
            FabricEntityType.Builder.createMob(LittlePersonCivilianEntity::new, MobCategory.MISC,
                    (mob) -> mob.defaultAttributes(LittlePersonCivilianEntity::createLittlePersonCivilianAttributes)
                            .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(0.6F, 0.9F)
            .clientTrackingRange(40)
            .build(ModEntities.little_person_civilian);
    public static final EntityType<LittlePersonWorkerEntity> LITTLE_PERSON_WORKER = EntityRegistrySupport.registerEntityType(
            "little_person_worker",
            FabricEntityType.Builder.createMob(LittlePersonWorkerEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonCivilianEntity::createLittlePersonCivilianAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40),
            true,
            false
    );
    public static final EntityType<LittlePersonMilitiaEntity> LITTLE_PERSON_MILITIA =
            FabricEntityType.Builder.createMob(LittlePersonMilitiaEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonMilitiaEntity::createLittlePersonMilitiaAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
                    .build(ModEntities.little_person_militia);
    public static final EntityType<LittlePersonArcherEntity> LITTLE_PERSON_ARCHER =
            FabricEntityType.Builder.createMob(LittlePersonArcherEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonArcherEntity::createLittlePersonArcherAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
                    .build(ModEntities.little_person_archer);
    public static final EntityType<LittlePersonGiantEntity> LITTLE_PERSON_GIANT =
            FabricEntityType.Builder.createMob(LittlePersonGiantEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonGiantEntity::createLittlePersonGiantAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.8F)
                    .clientTrackingRange(40)
                    .build(ModEntities.little_person_giant);
    public static final EntityType<LittlePersonGuardEntity> LITTLE_PERSON_GUARD =
            FabricEntityType.Builder.createMob(LittlePersonGuardEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonGuardEntity::createLittlePersonGuardAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
                    .build(ModEntities.little_person_guard);
    public static final EntityType<LittlePersonKingEntity> LITTLE_PERSON_KING = FabricEntityType.Builder.createMob(
            LittlePersonKingEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(LittlePersonKingEntity::createLittlePersonKingAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(40)
                    .build(ModEntities.little_person_king);
}
