package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.summon.presence.LivingBlockEntity;
import com.kltyton.mob_battle.entity.summon.presence.PresenceOrbitBlockEntity;
import com.kltyton.mob_battle.entity.summon.presence.PresenceBlockProjectile;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/** “有”套装的临时环绕物、投射物与持久化召唤物；不进入自然生成或刷怪蛋列表。 */
public final class PresenceEntityTypes {
    public static final EntityType<PresenceOrbitBlockEntity> ORBIT = EntityRegistrySupport.registerEntityType(
            "presence_orbit_block", EntityType.Builder.<PresenceOrbitBlockEntity>of(PresenceOrbitBlockEntity::new, MobCategory.MISC)
                    .sized(0.45F, 0.45F).noLootTable().noSave().clientTrackingRange(8).updateInterval(1), false, false);
    public static final EntityType<PresenceBlockProjectile> PROJECTILE = EntityRegistrySupport.registerEntityType(
            "presence_block_projectile", EntityType.Builder.<PresenceBlockProjectile>of(PresenceBlockProjectile::new, MobCategory.MISC)
                    .sized(0.45F, 0.45F).noLootTable().noSave().clientTrackingRange(8).updateInterval(1), false, false);
    public static final EntityType<LivingBlockEntity> LIVING_BLOCK = EntityRegistrySupport.registerEntityType(
            "living_block", EntityType.Builder.<LivingBlockEntity>of(LivingBlockEntity::new, MobCategory.CREATURE)
                    .sized(0.9F, 0.9F).noLootTable().clientTrackingRange(8).updateInterval(2), false, false);

    private PresenceEntityTypes() {}

    /** 由实体注册阶段调用，避免在物品构造过程中提前初始化召唤物属性。 */
    public static void init() {
        FabricDefaultAttributeRegistry.register(LIVING_BLOCK, LivingBlockEntity.createAttributes());
    }
}
