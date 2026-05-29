package com.kltyton.mob_battle.entity.witherskeletonking.skill;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class WitherSkullKingEntity extends WitherSkull {
    public float power;
    public WitherSkullKingEntity(EntityType<? extends WitherSkull> entityType, Level world, float power) {
        super(entityType, world);
        this.power = power;
    }
    /* -------------- 追加在 WitherSkullEntity 内部 -------------- */
    @Override
    public void tick() {               // 1.21+ 映射名，旧映射叫 baseTick()
        super.tick();                  // 先执行原版运动、碰撞检测

        // 如果已离开主人 25 格，直接消失
        if (!this.level().isClientSide) {
            Entity owner = this.getOwner();
            if (owner != null && this.distanceToSqr(owner) > 25.0 * 25.0) {
                this.discard();
            }
        }
    }
    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
        return max;
    }
    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (this.level() instanceof ServerLevel serverWorld) {
            Entity var8 = entityHitResult.getEntity();
            boolean bl;
            if (var8 instanceof LivingEntity living && !EntityUtil.isValidSummonCombatTarget(this, this.getOwner(), living)) {
                return;
            }
            if (this.getOwner() instanceof LivingEntity livingEntity) {
                DamageSource damageSource = this.damageSources().witherSkull(this, livingEntity);
                bl = var8.hurtServer(serverWorld, damageSource, power);
                var8.hurtServer(serverWorld, this.damageSources().explosion(this, livingEntity), 160F);
                if (bl) {
                    if (var8.isAlive()) {
                        EnchantmentHelper.doPostAttackEffects(serverWorld, var8, damageSource);
                    } else {
                        livingEntity.heal(5.0F);
                    }
                }
            } else {
                bl = var8.hurtServer(serverWorld, this.damageSources().magic(), power);
            }

            if (bl && var8 instanceof LivingEntity livingEntityx) {
                livingEntityx.addEffect(new MobEffectInstance(ModEffects.DECAY_ENTRY, 3 * 20, 0), this.getEffectSource());
            }
        }
    }
    protected void onCollisionBase(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult)hitResult;
            Entity entity = entityHitResult.getEntity();
            if (entity instanceof LivingEntity living && !EntityUtil.isValidSummonCombatTarget(this, this.getOwner(), living)) {
                return;
            }
            if (entity.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE) && entity instanceof Projectile projectileEntity) {
                projectileEntity.deflect(ProjectileDeflection.AIM_DEFLECT, this.getOwner(), this.getOwner(), true);
            }

            this.onHitEntity(entityHitResult);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, hitResult.getLocation(), GameEvent.Context.of(this, null));
        } else if (type == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult)hitResult;
            this.onHitBlock(blockHitResult);
            BlockPos blockPos = blockHitResult.getBlockPos();
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Context.of(this, this.level().getBlockState(blockPos)));
        }
    }
    @Override
    protected void onHit(HitResult hitResult) {
        if (hitResult instanceof EntityHitResult entityHitResult
                && entityHitResult.getEntity() instanceof LivingEntity living
                && !EntityUtil.isValidSummonCombatTarget(this, this.getOwner(), living)) {
            return;
        }
        this.onCollisionBase(hitResult);
        if (!this.level().isClientSide) {
            this.level().explode(
                    this,
                    Explosion.getDefaultDamageSource(this.level(), this),
                    new ExplosionDamageCalculator() {
                        @Override
                        public boolean shouldBlockExplode(Explosion explosion, BlockGetter world, BlockPos pos, BlockState state, float power) {
                            return false;
                        }
                    },
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    1.0F,
                    false,
                    Level.ExplosionInteraction.MOB
            );

            this.discard();
        }
    }
}
