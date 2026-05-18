package com.kltyton.mob_battle.entity.witherskeletonking.skill;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class WitherSkullKingEntity extends WitherSkullEntity {
    public float power;
    public WitherSkullKingEntity(EntityType<? extends WitherSkullEntity> entityType, World world, float power) {
        super(entityType, world);
        this.power = power;
    }
    /* -------------- 追加在 WitherSkullEntity 内部 -------------- */
    @Override
    public void tick() {               // 1.21+ 映射名，旧映射叫 baseTick()
        super.tick();                  // 先执行原版运动、碰撞检测

        // 如果已离开主人 25 格，直接消失
        if (!this.getWorld().isClient) {
            Entity owner = this.getOwner();
            if (owner != null && this.squaredDistanceTo(owner) > 25.0 * 25.0) {
                this.discard();
            }
        }
    }
    @Override
    public float getEffectiveExplosionResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
        return max;
    }
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Entity var8 = entityHitResult.getEntity();
            boolean bl;
            if (this.getOwner() instanceof LivingEntity livingEntity) {
                if (this.getOwner().isTeammate(var8)) return;
                DamageSource damageSource = this.getDamageSources().witherSkull(this, livingEntity);
                bl = var8.damage(serverWorld, damageSource, power);
                var8.damage(serverWorld, this.getDamageSources().explosion(this, livingEntity), 160F);
                if (bl) {
                    if (var8.isAlive()) {
                        EnchantmentHelper.onTargetDamaged(serverWorld, var8, damageSource);
                    } else {
                        livingEntity.heal(5.0F);
                    }
                }
            } else {
                bl = var8.damage(serverWorld, this.getDamageSources().magic(), power);
            }

            if (bl && var8 instanceof LivingEntity livingEntityx) {
                int i = 0;
                if (this.getWorld().getDifficulty() == Difficulty.NORMAL) {
                    i = 10;
                } else if (this.getWorld().getDifficulty() == Difficulty.HARD) {
                    i = 40;
                }

                if (i > 0) {
                    livingEntityx.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 20 * i, 1), this.getEffectCause());
                }
            }
        }
    }
    protected void onCollisionBase(HitResult hitResult) {
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
    }
    @Override
    protected void onCollision(HitResult hitResult) {
        this.onCollisionBase(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().createExplosion(
                    this,
                    Explosion.createDamageSource(this.getWorld(), this),
                    new ExplosionBehavior() {
                        @Override
                        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
                            return false;
                        }
                    },
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    1.0F,
                    false,
                    World.ExplosionSourceType.MOB
            );

            this.discard();
        }
    }
}
