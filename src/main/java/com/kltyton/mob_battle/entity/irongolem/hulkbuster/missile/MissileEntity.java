package com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile;

import com.kltyton.mob_battle.entity.customfireball.CustomFireballEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MissileEntity extends CustomFireballEntity {
    public MissileEntity(EntityType<? extends CustomFireballEntity> entityType, World world) {
        super(entityType, world);
    }

    public MissileEntity(EntityType<? extends CustomFireballEntity> entityType, World world, LivingEntity owner, float power, boolean createFire, float damage) {
        super(entityType, world, owner, power, createFire, damage);
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
            this.discard();
        }
    }
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
    }
    // 在 MissileEntity 类中加入
    @Override
    public void tick() {
        super.tick();
        Vec3d vec3d = this.getVelocity();
        if (vec3d.lengthSquared() > 0.01) {
            this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * (180f / Math.PI)));
            this.setPitch((float)(MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * (180f / Math.PI)));
        }
    }
    private void explodeAndApplyEffects() {
        World world = this.getWorld();
        world.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        if (world instanceof ServerWorld serverWorld) {
            double radius = 10.0;
            Box box = this.getBoundingBox().expand(radius);
            List<LivingEntity> targets = world.getNonSpectatingEntities(LivingEntity.class, box);
            Entity owner = this.getOwner();
            for (LivingEntity target : targets) {
                if (target.isTeammate(owner) || target == owner) {
                    continue;
                }
                DamageSource physicalSource = world.getDamageSources().fireball(this, owner);
                target.damage(serverWorld, physicalSource, 400.0f);
            }
            serverWorld.spawnParticles(ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(), this.getY(), this.getZ(), 3, 1.0, 1.0, 1.0, 0.1);
        }
    }
}

