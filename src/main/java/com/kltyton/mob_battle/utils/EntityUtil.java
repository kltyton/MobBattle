package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.entity.OwnedSummon;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class EntityUtil {
    public static boolean isCreativeOrSpectator(Entity entity) {
        return entity instanceof Player player && (player.isCreative() || player.isSpectator());
    }

    public static boolean isValidCombatTarget(LivingEntity source, LivingEntity target) {
        return target != null
                && target != source
                && target.isAlive()
                && !isCreativeOrSpectator(target)
                && !target.isAlliedTo(source);
    }

    @Nullable
    public static Entity getSummonOwner(Entity entity) {
        if (entity instanceof OwnedSummon ownedSummon) {
            return ownedSummon.getSummonOwner();
        }
        return null;
    }

    @Nullable
    public static Entity getKnownOwner(Entity entity) {
        Entity summonOwner = getSummonOwner(entity);
        if (summonOwner != null) {
            return summonOwner;
        }
        if (entity instanceof Projectile projectile) {
            return projectile.getOwner();
        }
        if (entity instanceof OwnableEntity tameable) {
            return tameable.getOwner();
        }
        if (entity instanceof TraceableEntity ownable) {
            return ownable.getOwner();
        }
        return null;
    }

    public static boolean isFriendlyToSummon(Entity summon, @Nullable Entity owner, Entity target) {
        if (target == null || target == summon) {
            return true;
        }
        if (target.isAlliedTo(summon) || summon.isAlliedTo(target)) {
            return true;
        }

        Entity resolvedOwner = owner == null ? getKnownOwner(summon) : owner;
        if (resolvedOwner == null) {
            return false;
        }
        if (isSameOrTeammate(target, resolvedOwner)) {
            return true;
        }

        Entity rootOwner = getSummonOwner(resolvedOwner);
        if (rootOwner != null && isSameOrTeammate(target, rootOwner)) {
            return true;
        }

        Entity targetOwner = getKnownOwner(target);
        if (targetOwner == null) {
            return false;
        }
        return isSameOrTeammate(targetOwner, resolvedOwner)
                || rootOwner != null && isSameOrTeammate(targetOwner, rootOwner);
    }

    public static boolean isValidSummonCombatTarget(Entity summon, @Nullable Entity owner, LivingEntity target) {
        return target != null
                && target.isAlive()
                && !isCreativeOrSpectator(target)
                && !isFriendlyToSummon(summon, owner, target);
    }

    public static boolean shouldBlockOwnedSummonDamage(Entity damagingEntity, LivingEntity target) {
        Entity owner = getSummonOwner(damagingEntity);
        if (owner == null && damagingEntity instanceof Projectile projectile) {
            Entity projectileOwner = projectile.getOwner();
            Entity projectileOwnerSummoner = projectileOwner == null ? null : getSummonOwner(projectileOwner);
            return projectileOwnerSummoner != null && isFriendlyToSummon(projectileOwner, projectileOwnerSummoner, target);
        }
        if (owner == null) {
            owner = getKnownOwner(damagingEntity);
        }
        return owner != null && isFriendlyToSummon(damagingEntity, owner, target);
    }

    private static boolean isSameOrTeammate(Entity target, Entity owner) {
        return target == owner || target.isAlliedTo(owner) || owner.isAlliedTo(target);
    }

    /**
     * 将实体 targetA 加入到实体 sourceB 所在的队伍中
     * @param targetA 要加入队伍的实体
     * @param sourceB 参考队伍的实体
     */
    public static void joinSameTeam(Entity targetA, Entity sourceB) {
        // 1. 获取参考实体 B 的队伍
        Team abstractTeam = sourceB.getTeam();

        // 只有当 B 确实在一个队伍中时才执行操作
        if (abstractTeam instanceof PlayerTeam team) {
            Scoreboard scoreboard = sourceB.level().getScoreboard();

            // 2. 获取实体 A 的标识符
            // 在原版记分板中，玩家使用名称，而实体使用 UUID 字符串
            String entryA = targetA.getStringUUID();

            // 3. 将 A 添加到队伍中
            // 该方法会自动将 A 从之前的队伍中移除
            scoreboard.addPlayerToTeam(entryA, team);
        }
    }



    public enum TeamFilter {
        ALL,             // 所有人
        EXCLUDE_TEAM,    // 排除队友
        ONLY_TEAM        // 只看队友
    }

    /**
     * 获取指定实体周围指定范围内的某类实体的数量（球形范围，精确距离）
     *
     * @param center      中心实体
     * @param clazz       要统计的实体类（例如 LivingEntity.class）
     * @param filterClass 额外筛选类（例如 HostileEntity.class），实体必须是此类的实例才会计数。
     *                    传入 Object.class 表示不进行额外筛选（等同于无筛选）。
     * @param radius      范围半径（单位：格）
     * @param includeSelf 是否包含中心实体自身（如果是同一类的话）
     * @param teamFilter           队伍筛选
     * @param <T>         实体类型
     * @return 符合条件的实体数量
     */
    public static <T extends LivingEntity> int getNearbyEntityCount(LivingEntity center, Class<T> clazz, Class<?> filterClass, double radius, boolean includeSelf, TeamFilter teamFilter) {
        List<T> nearbyEntities = getNearbyEntity(center, clazz, filterClass, radius, includeSelf, teamFilter);
        if (nearbyEntities == null) return 0;
        else return nearbyEntities.size();
    }

    /**
     * 获取指定实体周围指定范围内的某类实体的集合（球形范围，精确距离）
     *
     * @param center               中心实体
     * @param clazz                要统计的实体类
     * @param filterClass          额外筛选类
     * @param radius               范围半径
     * @param includeSelf          是否包含中心实体自身
     * @param teamFilter           队伍筛选
     * @param <T>                  实体类型
     * @return 符合条件的实体列表
     */
    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, Class<?> filterClass, double radius, boolean includeSelf, TeamFilter teamFilter) {
        return getNearbyEntity(center, clazz, filterClass, radius, includeSelf, teamFilter, null, null);
    }

    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, Class<?> filterClass, AABB box, boolean includeSelf, TeamFilter teamFilter, TargetingConditions targetPredicate) {
        return getNearbyEntity(center, clazz, filterClass,1, box, includeSelf, teamFilter, null, targetPredicate);
    }

    /**
     * 获取指定实体周围指定范围内的某类实体的集合（球形范围，精确距离）
     *
     * @param center               中心实体
     * @param clazz                要统计的实体类
     * @param radius               范围半径
     * @param includeSelf          是否包含中心实体自身
     * @param teamFilter           队伍筛选
     * @param <T>                  实体类型
     * @return 符合条件的实体列表
     */
    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, double radius, boolean includeSelf, TeamFilter teamFilter) {
        return getNearbyEntity(center, clazz, Object.class, radius, includeSelf, teamFilter);
    }

    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, double radius, boolean includeSelf, TeamFilter teamFilter, TargetingConditions targetPredicate) {
        return getNearbyEntity(center, clazz, Object.class, radius, includeSelf, teamFilter, null, targetPredicate);
    }

    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, double radius, boolean includeSelf, TeamFilter teamFilter, Predicate<T> extraPredicate) {
        return getNearbyEntity(center, clazz, Object.class, radius, includeSelf, teamFilter, extraPredicate, null);
    }
    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, Class<?> filterClass, double radius, boolean includeSelf, TeamFilter teamFilter, Predicate<T> extraPredicate, TargetingConditions targetPredicate) {
        return EntityUtil.getNearbyEntity(center, clazz, filterClass, radius, null, includeSelf, teamFilter, extraPredicate, targetPredicate);
    }
    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, Class<?> filterClass, double radius, AABB box, boolean includeSelf, TeamFilter teamFilter, Predicate<T> extraPredicate, TargetingConditions targetPredicate) {
        Level world = center.level();
        if (world.isClientSide()) {
            return List.of();
        }
        ServerLevel serverWorld = (ServerLevel) world;
        double radiusSq = radius * radius;
        if (box == null) box = center.getBoundingBox().inflate(radius);
        Predicate<T> predicate = entity -> {
            if (!entity.isAlive()) return false;
            if (isCreativeOrSpectator(entity)) return false;
            if (!includeSelf && entity == center) return false;
            if (!filterClass.isInstance(entity)) return false;
            boolean isTeammate = entity.isAlliedTo(center);
            if (teamFilter == TeamFilter.EXCLUDE_TEAM && isTeammate) return false;
            if (teamFilter == TeamFilter.ONLY_TEAM && !isTeammate) return false;
            return entity.distanceToSqr(center) <= radiusSq;
        };

        if (extraPredicate != null) predicate = predicate.and(extraPredicate);
        List<T> finalEntities = world.getEntitiesOfClass(clazz, box, predicate);
        if (targetPredicate != null) {
            finalEntities.removeIf(entity -> !targetPredicate.test(serverWorld, center, entity));
        }

        return finalEntities;
    }
    /**
     * 获取范围内最近的合法实体 (排除自身)
     */
    @Nullable
    public static <T extends LivingEntity> T getClosestNearbyEntity(LivingEntity center, Class<T> clazz, double radius, TeamFilter teamFilter) {
        return getClosestNearbyEntity(center, clazz, radius, teamFilter, null, null);
    }
    @Nullable
    public static <T extends LivingEntity> T getClosestNearbyEntity(LivingEntity center, Class<T> clazz, double radius, TeamFilter teamFilter, Predicate<T> extraPredicate, TargetingConditions targetPredicate) {
        List<T> entities = getNearbyEntity(center, clazz, Object.class, radius, false, teamFilter, extraPredicate, targetPredicate);
        T closest = null;
        double minDistanceSq = -1.0;

        for (T entity : entities) {
            double distSq = entity.distanceToSqr(center);
            if (minDistanceSq == -1.0 || distSq < minDistanceSq) {
                minDistanceSq = distSq;
                closest = entity;
            }
        }
        return closest;
    }

    /**
     * 获取视线范围内（扇形/锥形区域）的实体
     * 适用于横扫攻击或定向抓取
     */
    public static <T extends LivingEntity> List<T> getEntitiesInCone(LivingEntity center, Class<T> clazz, double radius, float arcDegrees, TeamFilter teamFilter) {
        return getEntitiesInCone(center, clazz, radius, arcDegrees, teamFilter, null, null);
    }
    public static <T extends LivingEntity> List<T> getEntitiesInCone(LivingEntity center, Class<T> clazz, double radius, float arcDegrees, TeamFilter teamFilter, Predicate<T> extraPredicate, TargetingConditions targetPredicate) {
        Vec3 lookDir = center.getViewVector(1.0F);
        Predicate<T> basePredicate = entity -> {
            Vec3 toEntity = entity.position().subtract(center.position()).normalize();
            double dotProduct = lookDir.dot(toEntity);
            double angle = Math.acos(dotProduct) * (180.0 / Math.PI);
            return angle <= (arcDegrees / 2.0);
        };
        return getNearbyEntity(center, clazz, Object.class, radius, false, teamFilter, extraPredicate == null ? basePredicate : basePredicate.and(extraPredicate), targetPredicate);
    }
    /**
     * 重载版本：默认不包含自身，可自定义范围
     */
    public static <T extends LivingEntity> int getNearbyEntityCount(LivingEntity center, Class<T> clazz, double radius) {
        return getNearbyEntityCount(center, clazz, Object.class, radius, false, TeamFilter.ONLY_TEAM);
    }
    public static <T extends LivingEntity> int getNearbyEntityCount(LivingEntity center, Class<T> clazz, Class<?> filterClass, double radius) {
        return getNearbyEntityCount(center, clazz, filterClass, radius, false, TeamFilter.ONLY_TEAM);
    }
    /**
     * 在指定中心位置附近寻找安全的实体生成位置（避免卡墙、浮空、无实体冲突）
     *
     * @param world          服务端世界
     * @param entityTemplate 已创建但尚未生成的实体模板（用于获取 boundingBox 和尺寸）
     * @param center         中心位置（通常是目标玩家的位置）
     * @param horizontalRange 水平搜索范围（单位：格）
     * @param verticalRange   垂直搜索范围（单位：格，通常较小）
     * @param maxAttempts     最大随机尝试次数
     * @param requireGround   是否要求底部有固体方块支撑（true=避免生成在空中直接掉落）
     * @return Optional<Vec3d> 安全位置，如果未找到返回 empty（调用方可自行 fallback）
     */
    public static Optional<Vec3> findSafeSpawnPosition(
            ServerLevel world,
            Entity entityTemplate,
            Vec3 center,
            double horizontalRange,
            double verticalRange,
            int maxAttempts,
            boolean requireGround) {

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // 随机偏移
            double offsetX = (world.random.nextDouble() - 0.5) * horizontalRange * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * horizontalRange * 2;
            double offsetY = (world.random.nextDouble() - 0.5) * verticalRange * 2; // 垂直范围通常较小

            Vec3 candidatePos = center.add(offsetX, offsetY, offsetZ);

            // 临时设置位置以获取正确的 boundingBox
            entityTemplate.setPos(candidatePos);
            AABB boundingBox = entityTemplate.getBoundingBox();

            // 检查底部是否有支撑
            boolean hasGround = true;
            if (requireGround) {
                BlockPos bottomPos = BlockPos.containing(candidatePos.x, boundingBox.minY - 0.01, candidatePos.z);
                hasGround = world.getBlockState(bottomPos).isRedstoneConductor(world, bottomPos);
            }

            // 空间空、无固体方块碰撞、无其他实体占用
            if (hasGround
                    && world.noCollision(entityTemplate, boundingBox) // 更严格的检查（考虑实体碰撞掩码）
                    && world.getEntities(entityTemplate, boundingBox).isEmpty()) {

                return Optional.of(candidatePos);
            }
        }

        return Optional.empty();
    }

    /**
     * 重载：默认参数（水平6格，垂直±2格，尝试50次，要求地面支撑）
     */
    public static Optional<Vec3> findSafeSpawnPosition(ServerLevel world, Entity entityTemplate, Vec3 center) {
        return findSafeSpawnPosition(world, entityTemplate, center, 6.0, 2.0, 50, true);
    }

    /**
     * 重载：不要求地面支撑（飞行实体或允许短暂掉落）
     */
    public static Optional<Vec3> findSafeSpawnPositionNoGround(ServerLevel world, Entity entityTemplate, Vec3 center) {
        return findSafeSpawnPosition(world, entityTemplate, center, 6.0, 2.0, 50, false);
    }

}
