package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.villager.archervillager.ArcherVillager;
import com.kltyton.mob_battle.entity.villager.militia.MilitiaArcherVillager;
import com.kltyton.mob_battle.entity.villager.militia.MilitiaWarriorVillager;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager;
import com.kltyton.mob_battle.entity.villager.trading.EvokerVillagerEntity;
import com.kltyton.mob_battle.entity.villager.trading.PiglinVillagerEntity;
import com.kltyton.mob_battle.entity.villager.trading.WitherSkeletonVillagerEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * 村民变体实体（villager variant 家族）的类型构造领域类。
 *
 * <p>集中保存 4 个村民变体的真实构造表达式，字段声明顺序与原 ModEntities
 * 中的声明顺序完全一致。本类字段使用 {@code .build(ModEntities.xxx)} 仅构造类型，
 * 实际 {@code Registry.register} 由 {@code ModEntities.init()} 按历史时机和顺序执行。</p>
 *
 * <p><b>别名与加载顺序契约：</b>本类必须经由 ModEntities 的别名字段（MILITIA_WARRIOR_VILLAGER
 * 等）首次触发静态初始化；本类不注册、不填充任何 Map，因此不改变注册顺序语义。</p>
 */
public final class VillagerVariantEntityTypes {

    private VillagerVariantEntityTypes() {
    }

    public static final EntityType<MilitiaWarriorVillager> MILITIA_WARRIOR_VILLAGER =
            FabricEntityType.Builder.createMob(MilitiaWarriorVillager::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(MilitiaArcherVillager::createVillagerAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MilitiaWarriorVillager::checkWarriorSpawnRules))
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(16)
                    .build(ModEntities.militia_warrior_villager);
    public static final EntityType<MilitiaArcherVillager> MILITIA_ARCHER_VILLAGER =
            FabricEntityType.Builder.createMob(MilitiaArcherVillager::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(MilitiaArcherVillager::createVillagerAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                            MilitiaArcherVillager::checkSnuffleSpawnRules))
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(16)
                    .build(ModEntities.militia_archer_villager);
    public static final EntityType<WarriorVillager> WARRIOR_VILLAGER =
            FabricEntityType.Builder.createMob(WarriorVillager::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(() -> Villager.createAttributes()
                                    .add(Attributes.ATTACK_DAMAGE, 5.0D)
                                    .add(Attributes.ARMOR, 2.0D))
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WarriorVillager::checkWarriorSpawnRules))
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(16)
                    .build(ModEntities.warrior_villager);
    public static final EntityType<ArcherVillager> ARCHER_VILLAGER =
            FabricEntityType.Builder.createMob(ArcherVillager::new, MobCategory.CREATURE,
                            (mob) -> mob.defaultAttributes(() -> ArcherVillager.createVillagerAttributes()
                                    .add(Attributes.ATTACK_DAMAGE, 3.0D)
                                    .add(Attributes.ARMOR, 1.0D))
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                            ArcherVillager::checkSnuffleSpawnRules))
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(64)
                    .build(ModEntities.archer_villager);
    public static final EntityType<WitherSkeletonVillagerEntity> WITHER_SKELETON_VILLAGER =
            FabricEntityType.Builder.createMob(WitherSkeletonVillagerEntity::new, MobCategory.CREATURE,
                            mob -> mob.defaultAttributes(Villager::createAttributes))
                    .sized(0.7F, 2.4F)
                    .clientTrackingRange(16)
                    .build(ModEntities.wither_skeleton_villager);
    public static final EntityType<PiglinVillagerEntity> PIGLIN_VILLAGER =
            FabricEntityType.Builder.createMob(PiglinVillagerEntity::new, MobCategory.CREATURE,
                            mob -> mob.defaultAttributes(Villager::createAttributes))
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(16)
                    .build(ModEntities.piglin_villager);
    public static final EntityType<EvokerVillagerEntity> EVOKER_VILLAGER =
            FabricEntityType.Builder.createMob(EvokerVillagerEntity::new, MobCategory.CREATURE,
                            mob -> mob.defaultAttributes(Villager::createAttributes))
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(16)
                    .build(ModEntities.evoker_villager);
}
