package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.hiddeneye.HiddenEyeEntity;
import com.kltyton.mob_battle.entity.min.YoungMinEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * 被动宠物类生物实体（passive creature 家族）的类型构造领域类。
 *
 * <p>集中保存 2 个被动/宠物类实体的真实构造表达式：幼敏与隐眼。字段声明顺序与原
 * ModEntities 中的声明顺序完全一致。本类字段使用 {@code .build(ModEntities.xxx)}
 * 仅构造类型，实际注册由 {@code ModEntities.init()} 按历史时机和顺序执行。</p>
 *
 * <p><b>别名与加载顺序契约：</b>本类必须经由 ModEntities 的别名字段（YOUNG_MIN 等）
 * 首次触发静态初始化；本类不注册、不填充任何 Map，因此不改变注册顺序语义。</p>
 */
public final class PassiveCreatureEntityTypes {

    private PassiveCreatureEntityTypes() {
    }

    public static final EntityType<YoungMinEntity> YOUNG_MIN =
            FabricEntityType.Builder.createMob(YoungMinEntity::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(YoungMinEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 0.8F)
                    .clientTrackingRange(16)
                    .build(ModEntities.young_min);
    public static final EntityType<HiddenEyeEntity> HIDDEN_EYE =
            FabricEntityType.Builder.createMob(HiddenEyeEntity::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(HiddenEyeEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 1.2F)
                    .clientTrackingRange(16)
                    .build(ModEntities.hidden_eye);
}
