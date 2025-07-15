package com.kltyton.mob_battle.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CustomFireballEntity extends FireballEntity {
    private final float damage;
    private final float power;
    private final boolean isExplosive;
    public CustomFireballEntity(World world, LivingEntity owner, float power, boolean createFire, float damage) {
        super(world, owner, Vec3d.ZERO, 0);
        this.power = power;
        this.isExplosive = createFire;
        this.damage = damage;
        this.setNoGravity(true);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult)hitResult;
            Entity entity = entityHitResult.getEntity();
            if (entity.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE) && entity instanceof ProjectileEntity projectileEntity) {
                projectileEntity.deflect(ProjectileDeflection.REDIRECTED, this.getOwner(), this.getOwner(), true);
            }

            this.onEntityHit(entityHitResult);
            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.getPos(), GameEvent.Emitter.of(this, null));
        } else if (type == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult)hitResult;
            this.onBlockHit(blockHitResult);
            BlockPos blockPos = blockHitResult.getBlockPos();
            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Emitter.of(this, this.getWorld().getBlockState(blockPos)));
        }
        if (!this.getWorld().isClient) {
            this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), power, isExplosive, World.ExplosionSourceType.NONE);
            this.discard(); // 移除实体
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Entity entity = entityHitResult.getEntity();
            Entity entity2 = this.getOwner();
            DamageSource damageSource = this.getDamageSources().fireball(this, entity2);
            entity.damage(serverWorld, damageSource, damage);
            EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource);
        }
    }
    // 在 CustomFireballEntity.java 中添加以下方法
/*    @Override
    public void tick() {
        super.tick();

        // 添加小火球的粒子效果（模仿烈焰人火球）
        if (this.getWorld().isClient && this.damage == 5.0f) {
            for (int i = 0; i < 2; i++) {
                this.getWorld().addParticleClient(
                        ParticleTypes.FLAME,
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.1,
                        this.getY() + (this.random.nextDouble() - 0.5) * 0.1,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.1,
                        this.random.nextGaussian() * 0.01,
                        this.random.nextGaussian() * 0.01,
                        this.random.nextGaussian() * 0.01
                );
            }
        }
    }*/
}
