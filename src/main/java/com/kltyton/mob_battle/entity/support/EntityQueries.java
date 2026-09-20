package com.kltyton.mob_battle.entity.support;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.OwnedSummon;
import com.kltyton.mob_battle.event.team.TeamFightManager;
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
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class EntityQueries {
    public static boolean isCreativeOrSpectator(Entity entity) {
        return entity instanceof Player player && (player.isCreative() || player.isSpectator());
    }

    public static boolean isValidCombatTarget(LivingEntity source, LivingEntity target) {
        if (target == null || target == source || !target.isAlive() || isCreativeOrSpectator(target)) {
            return false;
        }
        if (TeamFightManager.areForcedOpponents(source, target)) {
            return true;
        }
        Entity targetOwner = getKnownOwner(target);
        if (targetOwner instanceof Player && isLittlePersonFamily(source)) {
            return false;
        }
        return !target.isAlliedTo(source) && !source.isAlliedTo(target);
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

    /**
     * 返回实体已知的玩家 owner；无 owner 或 owner 不是玩家时返回 {@code null}。
     * 技能 Payload 只对这个明确的玩家归属施加 owner 限制，保持无 owner 自然怪的
     * tracking 兼容行为不变。
     */
    @Nullable
    public static Player getKnownPlayerOwner(Entity entity) {
        Entity owner = getKnownOwner(entity);
        return owner instanceof Player player ? player : null;
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
        if (areTeammates(target, resolvedOwner)) {
            return true;
        }

        Entity rootOwner = getSummonOwner(resolvedOwner);
        if (rootOwner != null && areTeammates(target, rootOwner)) {
            return true;
        }

        Entity targetOwner = getKnownOwner(target);
        if (targetOwner == null) {
            return false;
        }
        return areTeammates(targetOwner, resolvedOwner)
                || rootOwner != null && areTeammates(targetOwner, rootOwner);
    }

    public static boolean isValidSummonCombatTarget(Entity summon, @Nullable Entity owner, LivingEntity target) {
        if (target == null || target == summon || target == owner
                || !target.isAlive() || isCreativeOrSpectator(target)) {
            return false;
        }
        if (summon instanceof LivingEntity livingSummon && TeamFightManager.areForcedOpponents(livingSummon, target)) {
            return true;
        }
        if (owner instanceof LivingEntity livingOwner && TeamFightManager.areForcedOpponents(livingOwner, target)) {
            return true;
        }
        return !isFriendlyToSummon(summon, owner, target);
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

    /**
     * 无队伍潜影贝的原版子弹命中其所有者是潜影贝复制机制所需的唯一自击例外。
     */
    public static boolean isUnteamedShulkerSelfHit(Projectile projectile, Entity owner, Entity target) {
        return projectile instanceof ShulkerBullet
                && owner == target
                && owner instanceof Shulker
                && owner.getTeam() == null;
    }

    public static boolean areTeammates(Entity first, Entity second) {
        return first == second || first.isAlliedTo(second) || second.isAlliedTo(first);
    }

    private static boolean isLittlePersonFamily(Entity entity) {
        String className = entity.getClass().getName();
        return className.contains(".entity.littleperson.") && !className.endsWith(".Xbot002Entity");
    }

    /**
     * 将 {@code targetA} 加入 {@code sourceB} 当前所在的记分板队伍。
     *
     * @param targetA 需要加入队伍的实体
     * @param sourceB 提供目标队伍的参考实体
     */
    public static void joinSameTeam(Entity targetA, Entity sourceB) {
        // 读取参考实体当前所属队伍。
        Team abstractTeam = sourceB.getTeam();

        // 仅在参考实体属于真实 PlayerTeam 时修改记分板。
        if (abstractTeam instanceof PlayerTeam team) {
            Scoreboard scoreboard = sourceB.level().getScoreboard();

            // 实体使用 UUID 字符串作为记分板条目。
            // 这样可避免实体显示名变化导致队伍成员失联。
            String entryA = targetA.getStringUUID();

            // addPlayerToTeam 会自动从旧队伍移除该条目。
            // 因此无需先显式调用 removePlayerFromTeam。
            scoreboard.addPlayerToTeam(entryA, team);
        }
    }



    public enum TeamFilter {
        ALL,             // 不按队伍关系过滤
        EXCLUDE_TEAM,    // 排除队友
        ONLY_TEAM        // 仅保留队友
    }

    /**
     * 统计球形范围内符合类型、队伍和附加类别约束的存活实体。
     *
     * @param center 中心实体
     * @param clazz 需要统计的实体类型
     * @param filterClass 额外类别约束；传入 {@code Object.class} 表示不限制
     * @param radius 球形检索半径，单位为方块
     * @param includeSelf 是否允许结果包含中心实体
     * @param teamFilter 队伍关系过滤方式
     * @param <T> 实体类型
     * @return 满足全部约束的实体数量
     */
    public static <T extends LivingEntity> int getNearbyEntityCount(LivingEntity center, Class<T> clazz, Class<?> filterClass, double radius, boolean includeSelf, TeamFilter teamFilter) {
        List<T> nearbyEntities = getNearbyEntity(center, clazz, filterClass, radius, includeSelf, teamFilter);
        if (nearbyEntities == null) return 0;
        else return nearbyEntities.size();
    }

    /**
     * 查询球形范围内符合类型、队伍和附加类别约束的存活实体。
     *
     * @param center 中心实体
     * @param clazz 需要返回的实体类型
     * @param filterClass 额外类别约束
     * @param radius 球形检索半径
     * @param includeSelf 是否允许结果包含中心实体
     * @param teamFilter 队伍关系过滤方式
     * @param <T> 实体类型
     * @return 满足全部约束的实体列表
     */
    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, Class<?> filterClass, double radius, boolean includeSelf, TeamFilter teamFilter) {
        return getNearbyEntity(center, clazz, filterClass, radius, includeSelf, teamFilter, null, null);
    }

    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, Class<?> filterClass, AABB box, boolean includeSelf, TeamFilter teamFilter, TargetingConditions targetPredicate) {
        return getNearbyEntity(center, clazz, filterClass,1, box, includeSelf, teamFilter, null, targetPredicate);
    }

    /**
     * 查询球形范围内指定类型的存活实体，不附加类别约束。
     *
     * @param center 中心实体
     * @param clazz 需要返回的实体类型
     * @param radius 球形检索半径
     * @param includeSelf 是否允许结果包含中心实体
     * @param teamFilter 队伍关系过滤方式
     * @param <T> 实体类型
     * @return 满足全部约束的实体列表
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
        return EntityQueries.getNearbyEntity(center, clazz, filterClass, radius, null, includeSelf, teamFilter, extraPredicate, targetPredicate);
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
            boolean isTeammate = areTeammates(entity, center);
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
     * 返回范围内最近的合法实体，默认排除中心实体。
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
     * 查询视线方向扇形区域内的实体，适用于横扫攻击或定向抓取。
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
     * 统计默认范围内的同队实体；结果不包含中心实体。
     */
    public static <T extends LivingEntity> int getNearbyEntityCount(LivingEntity center, Class<T> clazz, double radius) {
        return getNearbyEntityCount(center, clazz, Object.class, radius, false, TeamFilter.ONLY_TEAM);
    }
    public static <T extends LivingEntity> int getNearbyEntityCount(LivingEntity center, Class<T> clazz, Class<?> filterClass, double radius) {
        return getNearbyEntityCount(center, clazz, filterClass, radius, false, TeamFilter.ONLY_TEAM);
    }
    /**
     * 在中心点附近寻找不会卡墙、悬空或与其他实体重叠的生成位置。
     *
     * @param world 服务端世界
     * @param entityTemplate 已创建但尚未生成的实体模板，用于计算碰撞箱
     * @param center 搜索中心位置
     * @param horizontalRange 水平搜索半径，单位为方块
     * @param verticalRange 垂直搜索半径，单位为方块
     * @param maxAttempts 最大随机尝试次数
     * @param requireGround 是否要求碰撞箱底部存在可支撑方块
     * @return 找到时返回安全位置，否则返回 {@link Optional#empty()}
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
            // 生成本次随机偏移。
            double offsetX = (world.getRandom().nextDouble() - 0.5) * horizontalRange * 2;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * horizontalRange * 2;
            double offsetY = (world.getRandom().nextDouble() - 0.5) * verticalRange * 2; // 垂直范围通常较小。

            Vec3 candidatePos = center.add(offsetX, offsetY, offsetZ);

            // 临时设置模板位置，以得到该候选点对应的碰撞箱。
            entityTemplate.setPos(candidatePos);
            AABB boundingBox = entityTemplate.getBoundingBox();

            // 按调用方要求检查碰撞箱底部是否存在支撑。
            boolean hasGround = true;
            if (requireGround) {
                BlockPos bottomPos = BlockPos.containing(candidatePos.x, boundingBox.minY - 0.01, candidatePos.z);
                hasGround = world.getBlockState(bottomPos).isRedstoneConductor(world, bottomPos);
            }

            // 同时验证方块碰撞与其他实体占用。
            if (hasGround
                    && world.noCollision(entityTemplate, boundingBox) // 检查模板实体自身的碰撞掩码。
                    && world.getEntities(entityTemplate, boundingBox).isEmpty()) {

                return Optional.of(candidatePos);
            }
        }

        return Optional.empty();
    }

    /**
     * 使用默认参数搜索安全位置：水平 6 格、垂直 2 格、最多 50 次，并要求地面支撑。
     */
    public static Optional<Vec3> findSafeSpawnPosition(ServerLevel world, Entity entityTemplate, Vec3 center) {
        return findSafeSpawnPosition(world, entityTemplate, center, 6.0, 2.0, 50, true);
    }

    /**
     * 使用默认范围搜索无需地面支撑的位置，适用于飞行实体或允许短暂下落的实体。
     */
    public static Optional<Vec3> findSafeSpawnPositionNoGround(ServerLevel world, Entity entityTemplate, Vec3 center) {
        return findSafeSpawnPosition(world, entityTemplate, center, 6.0, 2.0, 50, false);
    }

}
