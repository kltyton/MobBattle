package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.cbot.CbotSnowballEntity;
import com.kltyton.mob_battle.entity.cbot.SnowmanIceBlockEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.SkillProjectileEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.SkillVisualEntity;
import com.kltyton.mob_battle.entity.vindicatorgeneral.VindicatorGeneralAxeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * 小人族技能弹体与可视效果实体类型注册器。
 *
 * <p>字段声明顺序与原 {@code ModEntities} 完全一致。直接注册的实体仍在对应别名首次解析时注册；
 * 仅构造的实体继续由 {@code ModEntities.init()} 按历史顺序写入注册表。</p>
 */
public final class LittlePersonSkillEffectEntityTypes {

    private LittlePersonSkillEffectEntityTypes() {
    }

    public static final EntityType<SkillProjectileEntity> LASER = EntityRegistrySupport.registerEntityType(
            "laser",
            EntityType.Builder.<SkillProjectileEntity>of(SkillProjectileEntity::new, MobCategory.MISC)
                    .sized(0.35F, 0.35F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SkillProjectileEntity> SEVEN_HARVEST_BULLET = EntityRegistrySupport.registerEntityType(
            "seven_harvest_bullet",
            EntityType.Builder.<SkillProjectileEntity>of(SkillProjectileEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.35F, 0.35F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SkillProjectileEntity> SEVEN_HARVEST_EXPLOSIVE_BULLET = EntityRegistrySupport.registerEntityType(
            "seven_harvest_explosive_bullet",
            EntityType.Builder.<SkillProjectileEntity>of(SkillProjectileEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.45F, 0.45F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SkillProjectileEntity> KNIFE_PROJECTILE = EntityRegistrySupport.registerEntityType(
            "knife_projectile",
            EntityType.Builder.<SkillProjectileEntity>of(SkillProjectileEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.35F, 0.35F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SkillProjectileEntity> SKELETON_HEAD_PROJECTILE = EntityRegistrySupport.registerEntityType(
            "skeleton_head_projectile",
            EntityType.Builder.<SkillProjectileEntity>of(SkillProjectileEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.8F, 0.8F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SkillProjectileEntity> BLOOD_SWORD_ENERGY = EntityRegistrySupport.registerEntityType(
            "blood_sword_energy",
            EntityType.Builder.<SkillProjectileEntity>of(SkillProjectileEntity::new, MobCategory.MISC)
                    .sized(0.8F, 0.8F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SkillProjectileEntity> ICE_SWORD_ENERGY = EntityRegistrySupport.registerEntityType(
            "ice_sword_energy",
            EntityType.Builder.<SkillProjectileEntity>of(SkillProjectileEntity::new, MobCategory.MISC)
                    .sized(0.8F, 0.8F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SkillProjectileEntity> ICE_BOMB = EntityRegistrySupport.registerEntityType(
            "ice_bomb",
            EntityType.Builder.<SkillProjectileEntity>of(SkillProjectileEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SnowmanIceBlockEntity> SNOWMAN_ICE_BLOCK = EntityRegistrySupport.registerEntityType(
            "snowman_ice_block",
            EntityType.Builder.<SnowmanIceBlockEntity>of(SnowmanIceBlockEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.9F, 0.9F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<CbotSnowballEntity> CBOT_SNOWBALL = EntityRegistrySupport.registerEntityType(
            "cbot_snowball",
            EntityType.Builder.<CbotSnowballEntity>of(CbotSnowballEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<VindicatorGeneralAxeEntity> VINDICATOR_GENERAL_AXE = EntityRegistrySupport.registerEntityType(
            "vindicator_general_axe",
            EntityType.Builder.<VindicatorGeneralAxeEntity>of(VindicatorGeneralAxeEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.6F, 0.6F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SkillVisualEntity> ICE_FANGS = EntityRegistrySupport.registerEntityType(
            "ice_fangs",
            EntityType.Builder.<SkillVisualEntity>of(SkillVisualEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
    public static final EntityType<SkillVisualEntity> NINJA_CLONE = EntityRegistrySupport.registerEntityType(
            "ninja_clone",
            EntityType.Builder.<SkillVisualEntity>of(SkillVisualEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(64)
                    .updateInterval(1),
            false,
            false
    );
}
