package com.kltyton.mob_battle.items.manager;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class AreaGravityFieldManager {
    private AreaGravityFieldManager() {}

    private static final Identifier GRAVITY_FIELD_MODIFIER_ID =
            Identifier.of(Mob_battle.MOD_ID, "area_gravity_device_field");

    /**
     * “高重力”的强度。
     */
    private static final double HIGH_GRAVITY_AMOUNT = 1.0D;

    private static final EntityAttributeModifier GRAVITY_MODIFIER =
            new EntityAttributeModifier(
                    GRAVITY_FIELD_MODIFIER_ID,
                    HIGH_GRAVITY_AMOUNT,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
            );

    private static final Map<ServerWorld, List<FieldInstance>> ACTIVE_FIELDS = new ConcurrentHashMap<>();

    public static void addField(ServerWorld world, LivingEntity owner, Vec3d center, double radius, int durationTicks) {
        ACTIVE_FIELDS.computeIfAbsent(world, w -> new ArrayList<>())
                .add(new FieldInstance(owner.getUuid(), center, radius, world.getTime() + durationTicks));
    }

    public static void tickWorld(ServerWorld world) {
        List<FieldInstance> fields = ACTIVE_FIELDS.get(world);
        if (fields == null || fields.isEmpty()) {
            return;
        }

        long now = world.getTime();
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

    private static void tickField(ServerWorld world, FieldInstance field) {
        Box box = Box.of(field.center, field.radius * 2.0, field.radius * 2.0, field.radius * 2.0);

        List<LivingEntity> nearby = world.getEntitiesByClass(
                LivingEntity.class,
                box,
                entity -> entity.isAlive() && entity.squaredDistanceTo(field.center) <= field.radius * field.radius
        );

        Set<UUID> currentInside = new HashSet<>();
        for (LivingEntity living : nearby) {
            currentInside.add(living.getUuid());
            applyHighGravity(living);
        }

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

        spawnSphereParticles(world, field.center, field.radius);
    }

    private static void removeAllInField(ServerWorld world, FieldInstance field) {
        for (UUID uuid : field.affectedEntities) {
            LivingEntity entity = findEntity(world, uuid);
            if (entity != null) {
                removeHighGravity(entity);
            }
        }
        field.affectedEntities.clear();
    }

    private static LivingEntity findEntity(ServerWorld world, UUID uuid) {
        if (world.getEntity(uuid) instanceof LivingEntity living) {
            return living;
        }
        return null;
    }

    private static void applyHighGravity(LivingEntity entity) {
        EntityAttributeInstance gravity = entity.getAttributeInstance(EntityAttributes.GRAVITY);
        if (gravity == null) {
            return;
        }

        if (gravity.getModifier(GRAVITY_FIELD_MODIFIER_ID) == null) {
            gravity.addPersistentModifier(GRAVITY_MODIFIER);
        }
    }

    private static void removeHighGravity(LivingEntity entity) {
        EntityAttributeInstance gravity = entity.getAttributeInstance(EntityAttributes.GRAVITY);
        if (gravity == null) {
            return;
        }

        gravity.removeModifier(GRAVITY_FIELD_MODIFIER_ID);
    }

    private static void spawnSphereParticles(ServerWorld world, Vec3d center, double radius) {
        // 青蓝 -> 白色 过渡
        DustColorTransitionParticleEffect particle = new DustColorTransitionParticleEffect(
                0x40CCFF,
                0xFFFFFF,
                1.3F
        );

        int rings = 12;
        int pointsPerRing = 32;

        for (int i = 0; i <= rings; i++) {
            double phi = Math.PI * i / rings;
            double y = radius * Math.cos(phi);
            double ringRadius = radius * Math.sin(phi);

            for (int j = 0; j < pointsPerRing; j++) {
                double theta = 2.0 * Math.PI * j / pointsPerRing;
                double x = ringRadius * Math.cos(theta);
                double z = ringRadius * Math.sin(theta);

                world.spawnParticles(
                        particle,
                        center.x + x,
                        center.y + y,
                        center.z + z,
                        1,
                        0.0, 0.0, 0.0,
                        0.0
                );
            }
        }
    }

    private static final class FieldInstance {
        private final UUID ownerUuid;
        private final Vec3d center;
        private final double radius;
        private final long expireTime;
        private final Set<UUID> affectedEntities = new HashSet<>();

        private FieldInstance(UUID ownerUuid, Vec3d center, double radius, long expireTime) {
            this.ownerUuid = ownerUuid;
            this.center = center;
            this.radius = radius;
            this.expireTime = expireTime;
        }
    }
}
