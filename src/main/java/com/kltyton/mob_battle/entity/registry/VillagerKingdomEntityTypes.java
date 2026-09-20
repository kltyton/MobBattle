package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.irongolem.VillagerIronGolemEntity;
import com.kltyton.mob_battle.entity.villager.villagerking.VillagerKingEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * 村民王国护卫实体（villager kingdom 家族）的类型构造领域类。
 *
 * <p>集中保存 2 个村民王国相关实体的真实构造表达式：村民铁傀儡与村民国王。
 * 字段声明顺序与原 ModEntities 中的声明顺序完全一致。本类字段使用
 * {@code .build(ModEntities.xxx)} 仅构造类型，实际注册由 {@code ModEntities.init()}
 * 按历史时机和顺序执行。</p>
 *
 * <p><b>别名与加载顺序契约：</b>本类必须经由 ModEntities 的别名字段
 * （VILLAGER_IRON_GOLEM_ENTITY 等）首次触发静态初始化；本类不注册、不填充任何 Map，
 * 因此不改变注册顺序语义。</p>
 */
public final class VillagerKingdomEntityTypes {

    private VillagerKingdomEntityTypes() {
    }

    public static final EntityType<VillagerIronGolemEntity> VILLAGER_IRON_GOLEM_ENTITY =
            FabricEntityType.Builder.createMob(VillagerIronGolemEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(VillagerIronGolemEntity::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(1.4F, 2.7F)
                    .clientTrackingRange(10)
                    .build(ModEntities.villager_iron_golem);
    public static final EntityType<VillagerKingEntity> VILLAGER_KING_ENTITY =
            FabricEntityType.Builder.createMob(VillagerKingEntity::new, MobCategory.MISC,
                            (mob) -> mob.defaultAttributes(VillagerKingEntity::createVillagerKingAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(1.4F, 3.25F)
                    .build(ModEntities.villager_king);
}
