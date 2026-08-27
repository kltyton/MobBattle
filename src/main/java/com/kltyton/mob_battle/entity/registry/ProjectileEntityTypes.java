package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.bullet.BulletEntity;
import com.kltyton.mob_battle.entity.customfireball.CustomSuperBigFireballEntity;
import com.kltyton.mob_battle.entity.firewall.FireWallEntity;
import com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile.MissileEntity;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.ironmanbullet.IronManBulletEntity;
import com.kltyton.mob_battle.entity.meteorite.MeteoriteEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.skill.WitherSkullBulletEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * 弹射物 / 危险区域实体（projectile / hazard 家族）的类型构造领域类。
 *
 * <p>本类集中保存 11 个弹射物与危险区域实体的真实构造表达式：WITHER_SKULL_BULLET_ENTITY、
 * IRON_MAN_BULLET_ENTITY、BULLET_ENTITY、LITTLE_ARROW、STONE_ARROW、POISON_ARROW、
 * SPEAR_BULLET、BIG_CUSTOM_FIREBALL、METEORITE、MISSILE、FIRE_WALL。字段声明顺序与原
 * ModEntities 中的声明顺序完全一致，注册 ID（含 fairewall）、尺寸、分类、客户端追踪范围、
 * 更新频率均与原表达式逐字一致。实际 {@code Registry.register} 仍由
 * {@code ModEntities.init()} 按历史时机和顺序执行。</p>
 *
 * <p><b>别名与加载顺序契约：</b></p>
 * <ul>
 *   <li>ModEntities 中对应的 11 个公共字段已改为同类型、同字段名的源码兼容别名，
 *       实体 ID 与注册语义均与重构前一致；</li>
 *   <li>本类必须经由 ModEntities 的别名字段首次触发静态初始化：此时 ModEntities
 *       的 SPAWN_EGG_ENTITIES / GENERAL_RENDERERS / LITTLE_PERSON_ENTITIES 三个 Map
 *       以及全部 ResourceKey 已完成实例化，随后按本类字段声明顺序依次构造类型；</li>
 *   <li>本类不维护额外的 Map 填充（原实现中这 11 个实体均未加入 SPAWN_EGG_ENTITIES
 *       或 GENERAL_RENDERERS），因此不存在额外的 Map 顺序语义。</li>
 * </ul>
 */
public final class ProjectileEntityTypes {
    private ProjectileEntityTypes() {
    }

    public static final EntityType<WitherSkullBulletEntity> WITHER_SKULL_BULLET_ENTITY =
            EntityType.Builder.<WitherSkullBulletEntity>of(WitherSkullBulletEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ModEntities.wither_skull_bullet);

    public static final EntityType<IronManBulletEntity> IRON_MAN_BULLET_ENTITY =
            EntityType.Builder.<IronManBulletEntity>of(IronManBulletEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ModEntities.iron_man_bullet);
    public static final EntityType<BulletEntity> BULLET_ENTITY =
            EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125F, 0.3125F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(ModEntities.bullet);
    public static final EntityType<LittleArrowEntity> LITTLE_ARROW =
            EntityType.Builder.<LittleArrowEntity>of(LittleArrowEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125F, 0.3125F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(ModEntities.little_arrow);
    public static final EntityType<LittleArrowEntity> STONE_ARROW =
            EntityType.Builder.<LittleArrowEntity>of(LittleArrowEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125F, 0.3125F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(ModEntities.stone_arrow);
    public static final EntityType<LittleArrowEntity> POISON_ARROW =
            EntityType.Builder.<LittleArrowEntity>of(LittleArrowEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125F, 0.3125F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(ModEntities.poison_arrow);
    public static final EntityType<LittleArrowEntity> SPEAR_BULLET =
            EntityType.Builder.<LittleArrowEntity>of(LittleArrowEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.3125F, 0.3125F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(ModEntities.spear_bullet);
    public static final EntityType<CustomSuperBigFireballEntity> BIG_CUSTOM_FIREBALL =
            EntityType.Builder.<CustomSuperBigFireballEntity>of(CustomSuperBigFireballEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ModEntities.bigfireball);
    public static final EntityType<MeteoriteEntity> METEORITE =
            EntityType.Builder.<MeteoriteEntity>of(MeteoriteEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ModEntities.meteorite);
    public static final EntityType<MissileEntity> MISSILE =
            EntityType.Builder.<MissileEntity>of(MissileEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.5f, 0.5f)
                    .eyeHeight(0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ModEntities.missile);
    public static final EntityType<FireWallEntity> FIRE_WALL =
            EntityType.Builder.<FireWallEntity>of(FireWallEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(1f, 3f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ModEntities.firewall);
}
