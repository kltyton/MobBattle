package com.kltyton.mob_battle.entity.meteorite;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.customfireball.CustomFireballEntity;
import com.kltyton.mob_battle.sounds.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MeteoriteEntity extends CustomFireballEntity {
    public MeteoriteEntity(EntityType<? extends CustomFireballEntity> entityType, World world) {
        super(entityType, world);
    }

    public MeteoriteEntity(EntityType<? extends CustomFireballEntity> entityType, World world, LivingEntity owner, float power, boolean createFire, float damage) {
        super(entityType, world, owner, power, createFire, damage);
    }

    public MeteoriteEntity(World world, LivingEntity owner, float power, boolean createFire, float damage) {
        super(world, owner, power, createFire, damage);
    }
    @Override
    protected boolean isBurning() {
        return false;
    }
    @Nullable
    @Override
    protected ParticleEffect getParticleType() {
        return null;
    }
    @Override
    protected void onCollision(HitResult hitResult) {
        if (!this.getWorld().isClient) {
            this.explodeAndApplyEffects();
            this.discard(); // 陨石落地消失
        }
    }
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
    }
    private void explodeAndApplyEffects() {
        World world = this.getWorld();
        // 1. 播放音效
        world.playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.METEORITE_SOUND_EVENT_REFERENCE, SoundCategory.BLOCKS, 4.0f, 0.5f);
        if (world instanceof ServerWorld serverWorld) {
            // 2. 确定伤害范围
            double radius = 25.0;
            Box box = this.getBoundingBox().expand(radius);
            List<LivingEntity> targets = world.getNonSpectatingEntities(LivingEntity.class, box);

            Entity owner = this.getOwner();

            for (LivingEntity target : targets) {
                // 3. 队友免伤判断
                if (target.isTeammate(owner) || target == owner) {
                    continue;
                }
                target.addStatusEffect(new StatusEffectInstance(ModEffects.STUN_ENTRY, 100, 0));

                DamageSource physicalSource = world.getDamageSources().fireball(this, owner);
                DamageSource magicSource = world.getDamageSources().indirectMagic(this, owner);

                target.damage(serverWorld, physicalSource, 50.0f);
                target.damage(serverWorld, magicSource, 50.0f);
            }
            serverWorld.spawnParticles(ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(), this.getY(), this.getZ(), 3, 1.0, 1.0, 1.0, 0.1);
        }
    }
}
