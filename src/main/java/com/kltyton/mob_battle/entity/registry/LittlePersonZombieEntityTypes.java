package com.kltyton.mob_battle.entity.registry;

import com.kltyton.mob_battle.entity.littleperson.giant.LittlePersonGiantEntity;
import com.kltyton.mob_battle.entity.littleperson.king.LittlePersonKingEntity;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.entity.littleperson.zombie.LittlePersonZombieEntity;
import com.kltyton.mob_battle.entity.littleperson.zombie.infected.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/** 僵尸小人的独立类型目录；不进入要求 BaseSkillLittlePersonEntity 强转的旧渲染目录。 */
public final class LittlePersonZombieEntityTypes {
    private static final Map<String, EntityType<? extends LittlePersonMilitiaEntity>> TYPES = new LinkedHashMap<>();
    public static final EntityType<LittlePersonZombieEntity> BASIC = simple("little_person_zombie", LittlePersonZombieEntity.Kind.BASIC);
    public static final EntityType<LittlePersonZombieEntity> CLAW = simple("little_person_claw_zombie", LittlePersonZombieEntity.Kind.CLAW);
    public static final EntityType<LittlePersonZombieEntity> SHIELD = simple("little_person_shield_zombie", LittlePersonZombieEntity.Kind.SHIELD);
    public static final EntityType<LittlePersonZombieEntity> SPRAYER = simple("little_person_sprayer_zombie", LittlePersonZombieEntity.Kind.SPRAYER);
    public static final EntityType<LittlePersonZombieEntity> HEADLESS = simple("little_person_headless_zombie", LittlePersonZombieEntity.Kind.HEADLESS);
    public static final EntityType<LittlePersonZombieEntity> HEAD = simple("little_person_sprayer_zombie_head", LittlePersonZombieEntity.Kind.HEAD);
    public static final EntityType<LittlePersonZombieEntity> ARCHER = simple("zombie_little_person_archer", LittlePersonZombieEntity.Kind.ARCHER);
    public static final EntityType<LittlePersonZombieEntity> SOLDIER = simple("zombie_little_person_militia", LittlePersonZombieEntity.Kind.SOLDIER);
    public static final EntityType<ZombieLittlePersonGuardEntity> GUARD = register("zombie_little_person_guard",
            ZombieLittlePersonGuardEntity::new, ZombieLittlePersonGuardEntity::attributes, 0.9F);
    public static final EntityType<ZombieEliteLittlePersonGuardEntity> ELITE_GUARD = register("zombie_elite_little_person_guard",
            ZombieEliteLittlePersonGuardEntity::new, ZombieEliteLittlePersonGuardEntity::attributes, 0.9F);
    public static final EntityType<ZombieLittlePersonKingEntity> KING = register("zombie_little_person_king",
            ZombieLittlePersonKingEntity::new, LittlePersonKingEntity::createLittlePersonKingAttributes, 0.9F);
    public static final EntityType<ZombieLittlePersonGiantEntity> GIANT = register("zombie_little_person_giant",
            ZombieLittlePersonGiantEntity::new, LittlePersonGiantEntity::createLittlePersonGiantAttributes, 0.8F);

    private LittlePersonZombieEntityTypes() { }

    /** 普通实体目录初始化后、动态刷怪蛋迭代前触发类初始化。 */
    public static void init() { }

    public static Map<String, EntityType<? extends LittlePersonMilitiaEntity>> types() {
        return Map.copyOf(TYPES);
    }

    private static EntityType<LittlePersonZombieEntity> simple(String id, LittlePersonZombieEntity.Kind kind) {
        return register(id, (type, level) -> new LittlePersonZombieEntity(type, level, kind),
                () -> LittlePersonZombieEntity.attributes(kind), kind == LittlePersonZombieEntity.Kind.HEAD ? 0.4F : 0.9F);
    }

    private static <T extends LittlePersonMilitiaEntity> EntityType<T> register(String id,
            EntityType.EntityFactory<T> factory, Supplier<AttributeSupplier.Builder> attributes, float height) {
        EntityType<T> type = EntityRegistrySupport.registerEntityType(id,
                FabricEntityType.Builder.createMob(factory, MobCategory.MONSTER,
                        mob -> mob.defaultAttributes(attributes)).sized(0.6F, height).clientTrackingRange(40), true, false);
        TYPES.put(id, type);
        return type;
    }
}
