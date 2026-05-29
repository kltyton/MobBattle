package com.kltyton.mob_battle.entity.meteorite;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.customfireball.CustomFireballEntity;
import com.kltyton.mob_battle.sounds.ModSounds;
import com.kltyton.mob_battle.utils.EntityUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class MeteoriteEntity extends CustomFireballEntity {
    public MeteoriteEntity(EntityType<? extends CustomFireballEntity> entityType, Level world) {
        super(entityType, world);
    }

    public MeteoriteEntity(EntityType<? extends CustomFireballEntity> entityType, Level world, LivingEntity owner, float power, boolean createFire, float damage) {
        super(entityType, world, owner, power, createFire, damage);
    }
    @Override
    protected boolean shouldBurn() {
        return false;
    }
    @Nullable
    @Override
    protected ParticleOptions getTrailParticle() {
        return null;
    }
    @Override
    protected void onHit(HitResult hitResult) {
        if (!this.level().isClientSide) {
            this.explodeAndApplyEffects();
            this.discard(); // 陨石落地消失
        }
    }
    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
    }
    protected void explodeAndApplyEffects() {
        Level world = this.level();
        // 1. 播放音效
        world.playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.METEORITE_SOUND_EVENT_REFERENCE, SoundSource.BLOCKS, 4.0f, 0.5f);
        if (world instanceof ServerLevel serverWorld) {
            // 2. 确定伤害范围
            double radius = 25.0;
            AABB box = this.getBoundingBox().inflate(radius);
            List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, box);

            Entity owner = this.getOwner();

            for (LivingEntity target : targets) {
                // 3. 队友免伤判断
                if (!EntityUtil.isValidSummonCombatTarget(this, owner, target)) {
                    continue;
                }
                target.addEffect(new MobEffectInstance(ModEffects.STUN_ENTRY, 100, 0));

                DamageSource physicalSource = world.damageSources().fireball(this, owner);
                DamageSource magicSource = world.damageSources().indirectMagic(this, owner);

                target.hurtServer(serverWorld, physicalSource, 50.0f);
                target.hurtServer(serverWorld, magicSource, 50.0f);
            }
            serverWorld.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(), this.getY(), this.getZ(), 3, 1.0, 1.0, 1.0, 0.1);
        }
    }
}
