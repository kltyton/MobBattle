package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.explosion.EffectExplosionBehavior;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class SelfDestructEffectEvent {
    public static void init() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            // 检查实体是否有自爆效果
            Level world = entity.level();
            if (!world.isClientSide) {
                if (entity.hasEffect(ModEffects.SELF_DESTRUCT_ENTRY)) explode(entity, (ServerLevel) world, 3.0f, 5.0, 20.0f);
                if (entity.hasEffect(ModEffects.SUPER_SELF_DESTRUCT_ENTRY)) explode(entity, (ServerLevel) world, 6.0f, 10.0, 50.0f);
            }
        });
    }
    public static void explode(LivingEntity entity, ServerLevel world, float power, double radius, float damage) {
        Vec3 pos = entity.position();
        world.explode(
                entity,
                null,
                new EffectExplosionBehavior(entity),
                pos.x, pos.y, pos.z,
                power,
                false,
                Level.ExplosionInteraction.NONE
        );
        AABB area = new AABB(pos.x - radius, pos.y - radius, pos.z - radius,
                pos.x + radius, pos.y + radius, pos.z + radius);
        List<Entity> nearbyEntities = world.getEntities(entity, area);

        for (Entity target : nearbyEntities) {
            if (target instanceof LivingEntity livingTarget) {
                if (livingTarget.isAlliedTo(entity)) {
                    continue;
                }
                livingTarget.hurtServer(
                        world,
                        world.damageSources().explosion(entity, entity),
                        damage
                );
            }
        }
    }
}
