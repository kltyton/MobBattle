package com.kltyton.mob_battle.items.manager;

import com.kltyton.mob_battle.Mob_battle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class AreaGravityFieldManager {
    private AreaGravityFieldManager() {}

    private static final ResourceLocation GRAVITY_FIELD_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "area_gravity_device_field");

    /**
     * “高重力”的强度。
     */
    private static final double HIGH_GRAVITY_AMOUNT = 1.0D;

    private static final AttributeModifier GRAVITY_MODIFIER =
            new AttributeModifier(
                    GRAVITY_FIELD_MODIFIER_ID,
                    HIGH_GRAVITY_AMOUNT,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            );

    private static final Map<ServerLevel, List<FieldInstance>> ACTIVE_FIELDS = new ConcurrentHashMap<>();

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

    private static void tickField(ServerLevel world, FieldInstance field) {
        AABB box = AABB.ofSize(field.center, field.radius * 2.0, field.radius * 2.0, field.radius * 2.0);

        List<LivingEntity> nearby = world.getEntitiesOfClass(
                LivingEntity.class,
                box,
                entity -> entity.isAlive() && entity.distanceToSqr(field.center) <= field.radius * field.radius
        );

        Set<UUID> currentInside = new HashSet<>();
        for (LivingEntity living : nearby) {
            currentInside.add(living.getUUID());
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
            gravity.addPermanentModifier(GRAVITY_MODIFIER);
        }
    }

    private static void removeHighGravity(LivingEntity entity) {
        AttributeInstance gravity = entity.getAttribute(Attributes.GRAVITY);
        if (gravity == null) {
            return;
        }

        gravity.removeModifier(GRAVITY_FIELD_MODIFIER_ID);
    }

    private static void spawnSphereParticles(ServerLevel world, Vec3 center, double radius) {
        // 青蓝 -> 白色 过渡
        DustColorTransitionOptions particle = new DustColorTransitionOptions(
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

                world.sendParticles(
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
        private final Vec3 center;
        private final double radius;
        private final long expireTime;
        private final Set<UUID> affectedEntities = new HashSet<>();

        private FieldInstance(UUID ownerUuid, Vec3 center, double radius, long expireTime) {
            this.ownerUuid = ownerUuid;
            this.center = center;
            this.radius = radius;
            this.expireTime = expireTime;
        }
    }
}
