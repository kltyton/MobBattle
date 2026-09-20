package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.blueirongolem.BlueIronGolemEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * 蓝色铁傀儡实体（blue iron golem 家族）的类型构造领域类。
 *
 * <p>集中保存 1 个蓝色铁傀儡的真实构造表达式，字段声明顺序与原 ModEntities
 * 中的声明顺序完全一致。本类字段使用 {@code .build(ModEntities.blue_iron_golem)}
 * 仅构造类型，实际注册由 {@code ModEntities.init()} 按历史时机和顺序执行。</p>
 *
 * <p><b>别名与加载顺序契约：</b>本类必须经由 ModEntities 的别名字段
 * （BLUE_IRON_GOLEM）首次触发静态初始化；本类不注册、不填充任何 Map，
 * 因此不改变注册顺序语义。</p>
 */
public final class BlueIronGolemEntityTypes {

    private BlueIronGolemEntityTypes() {
    }

    public static final EntityType<BlueIronGolemEntity> BLUE_IRON_GOLEM =
            FabricEntityType.Builder.createMob(BlueIronGolemEntity::new, MobCategory.MISC, (mob) -> mob.defaultAttributes(BlueIronGolemEntity::createAttributes)
                            .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type, world, reason, pos, random) -> false))
                    .sized(1.4F, 2.7F)
                    .clientTrackingRange(10)
                    .build(ModEntities.blue_iron_golem);
}
