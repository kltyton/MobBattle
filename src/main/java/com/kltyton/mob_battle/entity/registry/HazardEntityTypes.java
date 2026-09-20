package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.cloud.ModifiedDragonBreathCloud;
import com.kltyton.mob_battle.entity.hazard.PoisonousBeachEntity;
import com.kltyton.mob_battle.entity.meteorite.EnderDragonMeteoriteEntity;
import com.kltyton.mob_battle.entity.shield.ShieldEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * 护盾、危险区域、云雾与陨石等场景实体类型注册器。
 *
 * <p>字段声明顺序与原 {@code ModEntities} 完全一致。直接注册的实体仍在对应别名首次解析时注册；
 * 仅构造的实体继续由 {@code ModEntities.init()} 按历史顺序写入注册表。</p>
 */
public final class HazardEntityTypes {

    private HazardEntityTypes() {
    }

    public static final EntityType<ShieldEntity> SHIELD = EntityRegistrySupport.registerEntityType(
            "shield_force_field",
            EntityType.Builder.of(ShieldEntity::new, MobCategory.MISC)
                    .sized(5.0f, 5.0f) // 保留护盾最终碰撞箱尺寸。
                    .clientTrackingRange(10)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<PoisonousBeachEntity> POISONOUS_BEACH = EntityRegistrySupport.registerEntityType(
            "poisonous_beach",
            EntityType.Builder.of(PoisonousBeachEntity::new, MobCategory.MISC)
                    .sized(1.0f, 0.0f) // 保留危险区域最终碰撞箱尺寸。
                    .clientTrackingRange(10)
                    .updateInterval(1),
            false,
            true
    );
    public static final EntityType<ModifiedDragonBreathCloud> MODIFIED_DRAGON_BREATH_CLOUD = EntityRegistrySupport.registerEntityType(
            "modified_dragon_breath_cloud",
            EntityType.Builder.<ModifiedDragonBreathCloud>of(ModifiedDragonBreathCloud::new, MobCategory.MISC)
                    .noLootTable()
                    .fireImmune()
                    .sized(6.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE),
            false,
            false
    );
    public static final EntityType<EnderDragonMeteoriteEntity> ENDER_DRAGON_METEORITE = EntityRegistrySupport.registerEntityType(
            "ender_dragon_meteorite",
            EntityType.Builder.<EnderDragonMeteoriteEntity>of(EnderDragonMeteoriteEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4)
                    .updateInterval(10),
            false,
            false
    );
}
