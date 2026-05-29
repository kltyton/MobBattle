package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Silverfish;

public class InfestationEffect extends MobEffect {
    public InfestationEffect() {
        super(
                MobEffectCategory.HARMFUL,
                0x8B0000 // 暗红色
        );
    }
    @Override
    public void onMobHurt(ServerLevel world, LivingEntity entity, int amplifier, DamageSource source, float amount) {
        Entity attacker = source.getEntity();

        if (attacker instanceof Silverfish || attacker instanceof Endermite) {
            float yawOffset = entity.getRandom().nextBoolean() ? 15.0f : -15.0f;
            entity.setYRot(entity.getYRot() + yawOffset);
            if (entity instanceof ServerPlayer player) {
                player.connection.teleport(player.getX(), player.getY(), player.getZ(),
                        player.getYRot(), player.getXRot());
            }
        }
    }
}
