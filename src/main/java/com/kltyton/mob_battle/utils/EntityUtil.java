package com.kltyton.mob_battle.utils;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Predicate;

public class EntityUtil {
    /**
     * 获取指定实体周围指定范围内的某类实体的数量（球形范围，精确距离）
     *
     * @param center      中心实体
     * @param clazz       要统计的实体类（例如 LivingEntity.class）
     * @param filterClass 额外筛选类（例如 HostileEntity.class），实体必须是此类的实例才会计数。
     *                    传入 Object.class 表示不进行额外筛选（等同于无筛选）。
     * @param radius      范围半径（单位：格）
     * @param includeSelf 是否包含中心实体自身（如果是同一类的话）
     * @param <T>         实体类型
     * @return 符合条件的实体数量
     */
    public static <T extends Entity> int getNearbyEntityCount(Entity center, Class<T> clazz, Class<?> filterClass, double radius, boolean includeSelf) {
        World world = center.getWorld();

        if (world.isClient()) {
            return 0;
        }

        double radiusSq = radius * radius;
        Box box = center.getBoundingBox().expand(radius);

        Predicate<T> predicate = entity -> {
            if (!entity.isAlive()) return false;
            if (!includeSelf && entity == center) return false;
            if (!filterClass.isInstance(entity)) return false;
            return entity.squaredDistanceTo(center) <= radiusSq;
        };

        return world.getEntitiesByClass(clazz, box, predicate).size();
    }

    /**
     * 重载版本：默认不包含自身，可自定义范围
     */
    public static <T extends Entity> int getNearbyEntityCount(Entity center, Class<T> clazz, double radius) {
        return getNearbyEntityCount(center, clazz, Object.class, radius, false);
    }
    public static <T extends Entity> int getNearbyEntityCount(Entity center, Class<T> clazz, Class<?> filterClass, double radius) {
        return getNearbyEntityCount(center, clazz, filterClass, radius, false);
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
    public static Optional<Vec3d> findSafeSpawnPosition(
            ServerWorld world,
            Entity entityTemplate,
            Vec3d center,
            double horizontalRange,
            double verticalRange,
            int maxAttempts,
            boolean requireGround) {

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // 随机偏移
            double offsetX = (world.random.nextDouble() - 0.5) * horizontalRange * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * horizontalRange * 2;
            double offsetY = (world.random.nextDouble() - 0.5) * verticalRange * 2; // 垂直范围通常较小

            Vec3d candidatePos = center.add(offsetX, offsetY, offsetZ);

            // 临时设置位置以获取正确的 boundingBox
            entityTemplate.setPosition(candidatePos);
            Box boundingBox = entityTemplate.getBoundingBox();

            // 检查底部是否有支撑
            boolean hasGround = true;
            if (requireGround) {
                BlockPos bottomPos = BlockPos.ofFloored(candidatePos.x, boundingBox.minY - 0.01, candidatePos.z);
                hasGround = world.getBlockState(bottomPos).isSolidBlock(world, bottomPos);
            }

            // 空间空、无固体方块碰撞、无其他实体占用
            if (hasGround
                    && world.isSpaceEmpty(entityTemplate, boundingBox) // 更严格的检查（考虑实体碰撞掩码）
                    && world.getOtherEntities(entityTemplate, boundingBox).isEmpty()) {

                return Optional.of(candidatePos);
            }
        }

        return Optional.empty();
    }

    /**
     * 重载：默认参数（水平6格，垂直±2格，尝试50次，要求地面支撑）
     */
    public static Optional<Vec3d> findSafeSpawnPosition(ServerWorld world, Entity entityTemplate, Vec3d center) {
        return findSafeSpawnPosition(world, entityTemplate, center, 6.0, 2.0, 50, true);
    }

    /**
     * 重载：不要求地面支撑（飞行实体或允许短暂掉落）
     */
    public static Optional<Vec3d> findSafeSpawnPositionNoGround(ServerWorld world, Entity entityTemplate, Vec3d center) {
        return findSafeSpawnPosition(world, entityTemplate, center, 6.0, 2.0, 50, false);
    }

}
