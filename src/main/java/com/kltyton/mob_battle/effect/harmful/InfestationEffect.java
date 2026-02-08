package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class InfestationEffect extends StatusEffect {
    public InfestationEffect() {
        super(
                StatusEffectCategory.HARMFUL,
                0x8B0000 // 暗红色
        );
    }
    @Override
    public void onEntityDamage(ServerWorld world, LivingEntity entity, int amplifier, DamageSource source, float amount) {
        Entity attacker = source.getAttacker();

        if (attacker instanceof SilverfishEntity || attacker instanceof EndermiteEntity) {
            float yawOffset = entity.getRandom().nextBoolean() ? 15.0f : -15.0f;
            entity.setYaw(entity.getYaw() + yawOffset);
            if (entity instanceof ServerPlayerEntity player) {
                player.networkHandler.requestTeleport(player.getX(), player.getY(), player.getZ(),
                        player.getYaw(), player.getPitch());
            }
        }
    }
}
