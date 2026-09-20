package com.kltyton.mob_battle.event.littleperson.zombie;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.civilian.LittlePersonCivilianEntity;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.entity.littleperson.zombie.ZombieLittlePerson;
import com.kltyton.mob_battle.entity.littleperson.zombie.ZombieLittlePersonSpawning;
import com.kltyton.mob_battle.entity.registry.LittlePersonZombieEntityTypes;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.jetbrains.annotations.Nullable;

/** 新生僵尸概率伴生与小人感染事务；区块重载和刷怪蛋均不重复抽签。 */
public final class ZombieLittlePersonLifecycle {
    private static final String PENDING_SPAWN = "mob_battle:little_person_zombie_roll";

    private ZombieLittlePersonLifecycle() { }

    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof Zombie zombie && zombie.removeTag(PENDING_SPAWN)
                    && zombie.getType() == EntityType.ZOMBIE && zombie.getRandom().nextInt(100) == 0) {
                var additional = ZombieLittlePersonSpawning.chooseVariant(zombie.getRandom())
                        .create(level, EntitySpawnReason.NATURAL);
                if (additional != null) {
                    additional.snapTo(zombie.getX(), zombie.getY(), zombie.getZ(), zombie.getYRot(), 0.0F);
                    level.addFreshEntity(additional);
                }
            }
        });
        // 在原版死亡回调把村民变为 ZombieVillager 前完成单次转化，原实体不掉落装备副本。
        ServerLivingEntityEvents.ALLOW_DEATH.register((victim, source, amount) -> {
            if (!(victim instanceof Mob mob) || victim instanceof ZombieLittlePerson
                    || !(source.getEntity() instanceof Zombie || source.getEntity() instanceof ZombieLittlePerson)) {
                return true;
            }
            EntityType<? extends LittlePersonMilitiaEntity> type = infectedType(mob);
            return type == null || convert(mob, type) == null;
        });
    }

    /** 仅真实生成流程标记一次性抽签；命令、刷怪蛋、分配器、加载及感染不参与。 */
    public static void markSpawn(Mob mob, EntitySpawnReason reason) {
        if (mob.getType() == EntityType.ZOMBIE && eligibleReason(reason)) {
            mob.addTag(PENDING_SPAWN);
        }
    }

    public static boolean eligibleReason(EntitySpawnReason reason) {
        return switch (reason) {
            case NATURAL, CHUNK_GENERATION, SPAWNER, TRIAL_SPAWNER, REINFORCEMENT -> true;
            default -> false;
        };
    }

    /** 只对需求明确列出的职业映射，其他技能小人不会意外丢失职业。 */
    public static @Nullable EntityType<? extends LittlePersonMilitiaEntity> infectedType(Mob source) {
        EntityType<?> type = source.getType();
        if (type == ModEntities.LITTLE_PERSON_ARCHER) return LittlePersonZombieEntityTypes.ARCHER;
        if (type == ModEntities.LITTLE_PERSON_MILITIA) return LittlePersonZombieEntityTypes.SOLDIER;
        if (type == ModEntities.LITTLE_PERSON_GUARD) return LittlePersonZombieEntityTypes.GUARD;
        if (type == ModEntities.ELITE_LITTLE_PERSON_GUARD) return LittlePersonZombieEntityTypes.ELITE_GUARD;
        if (type == ModEntities.LITTLE_PERSON_KING) return LittlePersonZombieEntityTypes.KING;
        if (type == ModEntities.LITTLE_PERSON_GIANT) return LittlePersonZombieEntityTypes.GIANT;
        if (source instanceof LittlePersonCivilianEntity) return LittlePersonZombieEntityTypes.BASIC;
        return null;
    }

    private static <T extends LittlePersonMilitiaEntity> @Nullable T convert(Mob source, EntityType<T> type) {
        return source.convertTo(type, ConversionParams.single(source, true, true), converted -> {
            if (source instanceof LittlePersonCivilianEntity
                    && converted instanceof com.kltyton.mob_battle.entity.littleperson.zombie.LittlePersonZombieEntity zombie) {
                zombie.markCivilianInfection();
            }
            converted.setHealth(converted.getMaxHealth());
            if (source instanceof LittlePersonMilitiaEntity militia && militia.getSummonOwner() != null) {
                converted.setSummonOwner(militia.getSummonOwner());
            }
        });
    }
}
