package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.effect.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class SelfDestructEffectEvent {
    public static void init() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            // 检查实体是否有自爆效果
            World world = entity.getWorld();
            if (!world.isClient) {
                if (entity.hasStatusEffect(ModEffects.SELF_DESTRUCT_ENTRY)) explode(entity, (ServerWorld) world, 3.0f, 5.0, 20.0f);
                if (entity.hasStatusEffect(ModEffects.SUPER_SELF_DESTRUCT_ENTRY)) explode(entity, (ServerWorld) world, 6.0f, 10.0, 50.0f);
            }
        });
    }
    public static void explode(LivingEntity entity, ServerWorld world, float power, double radius, float damage) {
        Vec3d pos = entity.getPos();
        world.createExplosion(
                entity,
                null,
                null,
                pos.x, pos.y, pos.z,
                power,
                false,
                World.ExplosionSourceType.NONE
        );
        Box area = new Box(pos.x - radius, pos.y - radius, pos.z - radius,
                pos.x + radius, pos.y + radius, pos.z + radius);
        List<Entity> nearbyEntities = world.getOtherEntities(entity, area);

        for (Entity target : nearbyEntities) {
            if (target instanceof LivingEntity livingTarget) {
                if (livingTarget.isTeammate(entity)) {
                    continue;
                }
                livingTarget.damage(
                        world,
                        world.getDamageSources().explosion(entity, entity),
                        damage
                );
            }
        }
    }
}
