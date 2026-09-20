package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.bullet.GoldenBulletEntity;
import com.kltyton.mob_battle.entity.bullet.GoldenTrailProjectile;
import com.kltyton.mob_battle.entity.bullet.IceArrowEntity;
import com.kltyton.mob_battle.entity.customfireball.MagmaLobsterBigFireballEntity;
import com.kltyton.mob_battle.entity.projectile.ElementalSwordProjectileEntity;
import com.kltyton.mob_battle.entity.silverfish.silverfish.GreenConcreteProjectileEntity;
import com.kltyton.mob_battle.entity.projectile.LittleStoneEntity;
import com.kltyton.mob_battle.entity.projectile.MoneyGunProjectileEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * 独立武器与生物技能使用的特殊弹体类型注册器。
 *
 * <p>字段声明顺序与原 {@code ModEntities} 完全一致。直接注册的实体仍在对应别名首次解析时注册；
 * 仅构造的实体继续由 {@code ModEntities.init()} 按历史顺序写入注册表。</p>
 */
public final class SpecialProjectileEntityTypes {

    private SpecialProjectileEntityTypes() {
    }

    public static final EntityType<MagmaLobsterBigFireballEntity> MAGMA_LOBBER_BIG_FIREBALL = EntityRegistrySupport.registerEntityType(
            "magma_lobber_big_fireball",
            EntityType.Builder.<MagmaLobsterBigFireballEntity>of(MagmaLobsterBigFireballEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4)
                    .updateInterval(10),
            false,
            false
    );
    public static final EntityType<IceArrowEntity> ICE_ARROW = EntityRegistrySupport.registerEntityType(
            "ice_arrow",
            EntityType.Builder.<IceArrowEntity>of(IceArrowEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.5F, 0.5F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20),
            false,
            false
    );
    public static final EntityType<GoldenTrailProjectile> GOLDEN_TRAIL_PROJECTILE = EntityRegistrySupport.registerEntityType(
            "golden_trail_projectile",
            EntityType.Builder.<GoldenTrailProjectile>of(GoldenTrailProjectile::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.5F, 0.5F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20),
            false,
            false
    );
    public static final EntityType<GoldenBulletEntity> GOLDEN_BULLET = EntityRegistrySupport.registerEntityType(
            "golden_bullet",
            EntityType.Builder.<GoldenBulletEntity>of(GoldenBulletEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.35F, 0.35F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20),
            false,
            false
    );
    public static final EntityType<GreenConcreteProjectileEntity> GREEN_CONCRETE_PROJECTILE = EntityRegistrySupport.registerEntityType(
            "green_concrete_projectile",
            EntityType.Builder.<GreenConcreteProjectileEntity>of(GreenConcreteProjectileEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.35F, 0.35F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<LittleStoneEntity> LITTLE_STONE_PROJECTILE = EntityRegistrySupport.registerEntityType(
            "little_stone_projectile",
            EntityType.Builder.<LittleStoneEntity>of(LittleStoneEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.25F, 0.25F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20),
            false,
            false
    );
    public static final EntityType<ElementalSwordProjectileEntity> ELEMENTAL_SWORD_PROJECTILE = EntityRegistrySupport.registerEntityType(
            "elemental_sword_projectile",
            EntityType.Builder.<ElementalSwordProjectileEntity>of(ElementalSwordProjectileEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.35F, 0.35F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20),
            false,
            false
    );
    public static final EntityType<MoneyGunProjectileEntity> MONEY_GUN_PROJECTILE = EntityRegistrySupport.registerEntityType(
            "money_gun_projectile",
            EntityType.Builder.<MoneyGunProjectileEntity>of(MoneyGunProjectileEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
}
