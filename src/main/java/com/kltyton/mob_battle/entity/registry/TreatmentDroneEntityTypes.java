package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.drone.treatmentdrone.TreatmentDroneEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * 治疗无人机实体（treatment drone 家族）的类型构造领域类。
 *
 * <p>集中保存 1 个治疗无人机的真实构造表达式，字段声明顺序与原 ModEntities
 * 中的声明顺序完全一致。本类字段使用 {@code .build(ModEntities.treatment_drone)}
 * 仅构造类型，实际注册由 {@code ModEntities.init()} 按历史时机和顺序执行。</p>
 *
 * <p><b>别名与加载顺序契约：</b>本类必须经由 ModEntities 的别名字段
 * （TREATMENT_DRONE）首次触发静态初始化；本类不注册、不填充任何 Map，
 * 因此不改变注册顺序语义。</p>
 */
public final class TreatmentDroneEntityTypes {

    private TreatmentDroneEntityTypes() {
    }

    public static final EntityType<TreatmentDroneEntity> TREATMENT_DRONE =
            FabricEntityType.Builder.createMob(TreatmentDroneEntity::new, MobCategory.MISC,
                    (mob) -> mob.defaultAttributes(TreatmentDroneEntity::createDroneAttributes)
                            .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
            .sized(0.7F, 0.3F)
            .clientTrackingRange(5)
            .build(ModEntities.treatment_drone);
}
