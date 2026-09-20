package com.kltyton.mob_battle.items.gravity;

import com.kltyton.mob_battle.Mob_battle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class AreaGravityFields {
    private AreaGravityFields() {}

    private static final Identifier GRAVITY_FIELD_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "area_gravity_device_field");

    /**
     * 无法读取属性最大值时的兜底强度。
     */
    private static final double FALLBACK_HIGH_GRAVITY_AMOUNT = 1.0D;

    /**
     * 粒子球面参数：13 个纬度环（i=0..12）* 每个环 32 个经度点 = 416 个点。
     * 416 个点分为 4 个相位，每 tick 只发送 104 个点，4 tick 轮转覆盖完整球面，
     * 与旧实现每 tick 全量发送 416 个点的几何位置逐点一致。
     *
     * <p>trade-off：单 tick 瞬时粒子数量降为原来的 1/4，短时观察或高速移动下
     * 球面视觉密度会明显变稀；但 4 tick 内完整覆盖且总发送量不变，重力判定与
     * 可观察影响范围不受影响。折中只在视觉密度与每 tick 粒子包开销之间取舍。
     */
    private static final int PARTICLE_RINGS = 12;
    private static final int PARTICLES_PER_RING = 32;
    private static final int PARTICLE_PHASE_COUNT = 4;
    private static final int PARTICLE_TOTAL_POINTS = (PARTICLE_RINGS + 1) * PARTICLES_PER_RING;

    /**
     * 粒子参数（青蓝 -> 白色过渡）是不可变值对象，全部字段共享同一实例，
     * 避免每个场每 tick 都分配新的 DustColorTransitionOptions。
     */
    private static final DustColorTransitionOptions GRAVITY_FIELD_PARTICLE =
            new DustColorTransitionOptions(0x40CCFF, 0xFFFFFF, 1.3F);

    /**
     * 复用的 LivingEntity 类型测试。
     *
     * <p>26.1.2 的 EntityTypeTest.forClass 每次调用都会创建新的匿名实例，因此把
     * 类型测试提升为共享静态字段，避免每 tick 的 AABB 查询重复分配。
     */
    private static final EntityTypeTest<Entity, LivingEntity> LIVING_ENTITY_TEST =
            EntityTypeTest.forClass(LivingEntity.class);

    /**
     * 活跃重力场表。
     *
     * <p>全部入口（物品使用的服务端回调、ServerLevel 的 END_LEVEL_TICK）都运行在
     * 服务器主线程，不存在并发访问，因此使用 IdentityHashMap（按 ServerLevel 实例
     * 身份）替代无意义的 ConcurrentHashMap，消除无必要的并发开销。
     */
    private static final Map<ServerLevel, List<FieldInstance>> ACTIVE_FIELDS = new IdentityHashMap<>();

    public static void addField(ServerLevel world, LivingEntity owner, Vec3 center, double radius, int durationTicks) {
        ACTIVE_FIELDS.computeIfAbsent(world, w -> new ArrayList<>())
                .add(new FieldInstance(owner.getUUID(), center, radius, world.getGameTime() + durationTicks));
    }

    public static void tickWorld(ServerLevel world) {
        List<FieldInstance> fields = ACTIVE_FIELDS.get(world);
        if (fields == null || fields.isEmpty()) {
            return;
        }

        long now = world.getGameTime();
        Iterator<FieldInstance> iterator = fields.iterator();

        while (iterator.hasNext()) {
            FieldInstance field = iterator.next();

            if (now >= field.expireTime) {
                removeAllInField(world, field);
                iterator.remove();
                continue;
            }

            tickField(world, field);
        }

        if (fields.isEmpty()) {
            ACTIVE_FIELDS.remove(world);
        }
    }

    /**
     * 清空所有活跃场（服务端停止时调用），避免静态表持有已关闭世界的强引用。
     *
     * <p>仅用于 ServerLifecycleEvents.SERVER_STOPPING；服务端实体即将随世界卸载，
     * 瞬态重力 modifier 也随之销毁，无需逐个移除。
     */
    public static void clearAllFields() {
        ACTIVE_FIELDS.clear();
    }

    /**
     * 移除指定世界的全部活跃场（世界卸载时调用）。
     *
     * <p>先卸载场上仍存在的实体上的瞬态重力 modifier，再丢弃该世界的全部场状态并
     * 删除静态表条目，避免静态表跨存档持有旧 ServerLevel 强引用。世界卸载与场清理
     * 都发生在服务器主线程，不会与 tick 路径并发。
     */
    public static void removeWorld(ServerLevel world) {
        List<FieldInstance> fields = ACTIVE_FIELDS.remove(world);
        if (fields == null) {
            return;
        }
        for (FieldInstance field : fields) {
            removeAllInField(world, field);
        }
    }

    private static void tickField(ServerLevel world, FieldInstance field) {
        // AABB、半径平方、类型测试与球内谓词都在字段创建时缓存；查询结果复用字段
        // 私有的 scratch 列表（Level 提供填充式 AABB 查询重载），tick 路径不再新建
        // ArrayList/HashSet/EntityTypeTest/lambda。
        field.scratchEntities.clear();
        world.getEntities(
                LIVING_ENTITY_TEST,
                field.box,
                field.insidePredicate,
                field.scratchEntities,
                Integer.MAX_VALUE
        );
        List<LivingEntity> nearby = field.scratchEntities;

        // 复用字段私有的 scratch 集合，避免每个 tick 新建 HashSet。
        Set<UUID> currentInside = field.scratchInside;
        currentInside.clear();
        for (LivingEntity living : nearby) {
            currentInside.add(living.getUUID());
            applyHighGravity(living);
        }

        // 仅移除已离开字段的实体上的重力 modifier；仍停留在场内的实体不会重复添加
        // （applyHighGravity 内部检查 modifier 是否已存在）。
        Iterator<UUID> affectedIterator = field.affectedEntities.iterator();
        while (affectedIterator.hasNext()) {
            UUID uuid = affectedIterator.next();
            if (!currentInside.contains(uuid)) {
                LivingEntity entity = findEntity(world, uuid);
                if (entity != null) {
                    removeHighGravity(entity);
                }
                affectedIterator.remove();
            }
        }

        field.affectedEntities.addAll(currentInside);

        spawnSphereParticles(world, field);
    }

    private static void removeAllInField(ServerLevel world, FieldInstance field) {
        for (UUID uuid : field.affectedEntities) {
            LivingEntity entity = findEntity(world, uuid);
            if (entity != null) {
                removeHighGravity(entity);
            }
        }
        field.affectedEntities.clear();
    }

    private static LivingEntity findEntity(ServerLevel world, UUID uuid) {
        if (world.getEntity(uuid) instanceof LivingEntity living) {
            return living;
        }
        return null;
    }

    private static void applyHighGravity(LivingEntity entity) {
        AttributeInstance gravity = entity.getAttribute(Attributes.GRAVITY);
        if (gravity == null) {
            return;
        }

        if (gravity.getModifier(GRAVITY_FIELD_MODIFIER_ID) == null) {
            gravity.addTransientModifier(createGravityModifier());
        }
    }

    private static AttributeModifier createGravityModifier() {
        return new AttributeModifier(
                GRAVITY_FIELD_MODIFIER_ID,
                getGravityMaximumValue(),
                AttributeModifier.Operation.ADD_VALUE
        );
    }

    private static double getGravityMaximumValue() {
        if (Attributes.GRAVITY.value() instanceof RangedAttribute rangedAttribute) {
            return rangedAttribute.maxValue;
        }
        return FALLBACK_HIGH_GRAVITY_AMOUNT;
    }

    private static void removeHighGravity(LivingEntity entity) {
        AttributeInstance gravity = entity.getAttribute(Attributes.GRAVITY);
        if (gravity == null) {
            return;
        }

        gravity.removeModifier(GRAVITY_FIELD_MODIFIER_ID);
    }

    /**
     * 按相位发送粒子：本 tick 只发送 104 个点，4 个相位轮转后完整覆盖 416 个
     * 预计算点，几何与旧实现逐点一致。
     */
    private static void spawnSphereParticles(ServerLevel world, FieldInstance field) {
        int phase = field.particlePhase;
        field.particlePhase = (phase + 1) & (PARTICLE_PHASE_COUNT - 1);

        double[] points = field.particlePoints;
        for (int pointIndex = phase; pointIndex < PARTICLE_TOTAL_POINTS; pointIndex += PARTICLE_PHASE_COUNT) {
            int offset = pointIndex * 3;
            world.sendParticles(
                    GRAVITY_FIELD_PARTICLE,
                    points[offset],
                    points[offset + 1],
                    points[offset + 2],
                    1,
                    0.0, 0.0, 0.0,
                    0.0
            );
        }
    }

    /**
     * 预计算球面 416 个点的坐标（x/y/z 交错存放在 double 数组中），仅在字段创建时
     * 分配一次，tick 路径不再做三角函数计算。
     */
    private static double[] buildParticlePoints(Vec3 center, double radius) {
        double[] points = new double[PARTICLE_TOTAL_POINTS * 3];
        int index = 0;
        for (int i = 0; i <= PARTICLE_RINGS; i++) {
            double phi = Math.PI * i / PARTICLE_RINGS;
            double y = radius * Math.cos(phi);
            double ringRadius = radius * Math.sin(phi);

            for (int j = 0; j < PARTICLES_PER_RING; j++) {
                double theta = 2.0 * Math.PI * j / PARTICLES_PER_RING;
                points[index++] = center.x + ringRadius * Math.cos(theta);
                points[index++] = center.y + y;
                points[index++] = center.z + ringRadius * Math.sin(theta);
            }
        }
        return points;
    }

    private static final class FieldInstance {
        private final UUID ownerUuid;
        private final Vec3 center;
        private final double radius;
        private final double radiusSquared;
        private final long expireTime;

        /** 创建时缓存；字段生命周期内中心不变，AABB 恒定。 */
        private final AABB box;
        /** 创建时缓存，避免每 tick 为 getEntitiesOfClass 重新分配 lambda。 */
        private final Predicate<LivingEntity> insidePredicate;
        /** 每 tick 复用的临时实体结果列表，避免 AABB 查询分配新列表。 */
        private final List<LivingEntity> scratchEntities = new ArrayList<>();
        /** 每 tick 复用的临时集合，避免分配。 */
        private final Set<UUID> scratchInside = new HashSet<>();
        /** 当前受重力影响的实体 UUID。 */
        private final Set<UUID> affectedEntities = new HashSet<>();
        /** 预计算的球面点坐标。 */
        private final double[] particlePoints;
        /** 当前粒子相位（0..3）。 */
        private int particlePhase;

        private FieldInstance(UUID ownerUuid, Vec3 center, double radius, long expireTime) {
            this.ownerUuid = ownerUuid;
            this.center = center;
            this.radius = radius;
            this.radiusSquared = radius * radius;
            this.expireTime = expireTime;
            this.box = AABB.ofSize(center, radius * 2.0, radius * 2.0, radius * 2.0);
            this.insidePredicate = entity -> entity.isAlive()
                    && entity.distanceToSqr(center) <= radiusSquared;
            this.particlePoints = buildParticlePoints(center, radius);
        }
    }
}
