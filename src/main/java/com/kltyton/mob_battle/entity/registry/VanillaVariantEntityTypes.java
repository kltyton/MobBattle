package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.ModEntities;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.zombie.Zombie;

/**
 * 原版生物行为变体的类型构造器；类型在兼容门面 init 阶段按历史顺序注册。
 *
 * <p>字段声明顺序与原 {@code ModEntities} 完全一致。直接注册的实体仍在对应别名首次解析时注册；
 * 仅构造的实体继续由 {@code ModEntities.init()} 按历史顺序写入注册表。</p>
 */
public final class VanillaVariantEntityTypes {

    private VanillaVariantEntityTypes() {
    }

    public static final EntityType<Zombie> BOW_ZOMBIE_MOD =
            FabricEntityType.Builder.<Zombie>createMob(Zombie::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(Zombie::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 1.95F)
                    .eyeHeight(1.74F)
                    .passengerAttachments(2.0125F)
                    .ridingOffset(-0.7F)
                    .clientTrackingRange(8)
                    .notInPeaceful()
                    .build(ModEntities.bow_zombie_mod);
    public static final EntityType<PiglinBrute> PIGLIN_BRUTE_SPEAR_MOD =
            FabricEntityType.Builder.<PiglinBrute>createMob(PiglinBrute::new, MobCategory.MONSTER,
                            (mob) -> mob.defaultAttributes(PiglinBrute::createAttributes)
                                    .spawnPlacement(SpawnPlacementTypes.ON_GROUND,
                                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> false))
                    .sized(0.6F, 1.95F)
                    .eyeHeight(1.79F)
                    .passengerAttachments(2.0125F)
                    .ridingOffset(-0.7F)
                    .clientTrackingRange(8)
                    .notInPeaceful()
                    .build(ModEntities.piglin_brute_spear_mod);
}
