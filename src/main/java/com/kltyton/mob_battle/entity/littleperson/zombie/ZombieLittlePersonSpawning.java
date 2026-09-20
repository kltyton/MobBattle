package com.kltyton.mob_battle.entity.littleperson.zombie;

import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.entity.registry.LittlePersonZombieEntityTypes;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/** 小人僵尸的固定概率池与服务端召唤边界，不改变专用刷怪蛋指定的实体类型。 */
public final class ZombieLittlePersonSpawning {
    private ZombieLittlePersonSpawning() {
    }

    /** 六个等概率槽中三个属于基础型，其余三个变种各占一个槽。 */
    public static EntityType<LittlePersonZombieEntity> chooseVariant(RandomSource random) {
        return switch (random.nextInt(6)) {
            case 3 -> LittlePersonZombieEntityTypes.CLAW;
            case 4 -> LittlePersonZombieEntityTypes.SHIELD;
            case 5 -> LittlePersonZombieEntityTypes.SPRAYER;
            default -> LittlePersonZombieEntityTypes.BASIC;
        };
    }

    /** 创建具有明确召唤者的实体，先确定安全位置和队伍，再加入服务端世界。 */
    public static <T extends LittlePersonMilitiaEntity> @Nullable T summon(
            EntityType<T> type, LivingEntity owner, Vec3 position) {
        if (!(owner.level() instanceof ServerLevel level)) {
            return null;
        }
        T summoned = type.create(level, EntitySpawnReason.MOB_SUMMONED);
        if (summoned == null) {
            return null;
        }
        summoned.snapTo(position.x, position.y, position.z, owner.getYRot(), 0.0F);
        summoned.setPos(EntityQueries.findSafeSpawnPosition(level, summoned, position).orElse(position));
        summoned.setSummonOwner(owner);
        if (owner instanceof Mob mob && mob.getTarget() != null
                && EntityQueries.isValidSummonCombatTarget(summoned, owner, mob.getTarget())) {
            summoned.setTarget(mob.getTarget());
        }
        return level.addFreshEntity(summoned) ? summoned : null;
    }
}
