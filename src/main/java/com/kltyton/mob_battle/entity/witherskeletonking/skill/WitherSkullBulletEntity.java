package com.kltyton.mob_battle.entity.witherskeletonking.skill;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/** 复用原版追踪弹的运动与存档，保留凋零骷髅王的重锁目标和复合伤害。 */
public class WitherSkullBulletEntity extends ShulkerBullet {
    public WitherSkullBulletEntity(EntityType<? extends WitherSkullBulletEntity> entityType, Level world) {
        super(entityType, world);
    }

    public WitherSkullBulletEntity(Level world, LivingEntity owner, Entity target, Direction.Axis axis) {
        this(ModEntities.WITHER_SKULL_BULLET_ENTITY, world);
        setOwner(owner);
        Vec3 origin = owner.getBoundingBox().getCenter();
        snapTo(origin.x, origin.y, origin.z, getYRot(), getXRot());
        this.finalTarget = EntityReference.of(target);
        this.currentMoveDirection = Direction.UP;
        this.selectNextMoveDirection(axis, target);
    }

    @Override
    public void tick() {
        if (!level().isClientSide()) {
            Entity target = EntityReference.getEntity(this.finalTarget, level());
            if (target == null || !target.isAlive()
                    || (target instanceof Player player && (player.isSpectator() || player.isCreative()))) {
                Entity replacement = findReplacementTarget();
                this.finalTarget = EntityReference.of(replacement);
                if (replacement != null) {
                    this.selectNextMoveDirection(this.currentMoveDirection == null ? null : this.currentMoveDirection.getAxis(), replacement);
                }
            }
        }
        super.tick();
    }

    /** 只为凋零骷髅王发射的锁定头重选目标；没有合法候选时交给原版下坠。 */
    private @Nullable Entity findReplacementTarget() {
        if (!(getOwner() instanceof WitherSkeletonKingEntity king)) {
            return null;
        }
        return EntityQueries.getClosestNearbyEntity(king, LivingEntity.class, 64.0D, EntityQueries.TeamFilter.EXCLUDE_TEAM);
    }

    @Override
    public boolean canHitEntity(Entity entity) {
        if (entity instanceof LivingEntity living && !EntityQueries.isValidSummonCombatTarget(this, getOwner(), living)) {
            return false;
        }
        return super.canHitEntity(entity);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        // 三次伤害保留原有顺序和无敌帧裁决，不调用原版实体命中的伤害与漂浮逻辑。
        Entity victim = result.getEntity();
        Entity owner = getOwner();
        if (victim instanceof LivingEntity living && !EntityQueries.isValidSummonCombatTarget(this, owner, living)) {
            return;
        }
        LivingEntity attacker = owner instanceof LivingEntity living ? living : null;
        DamageSource explosion = damageSources().explosion(this, attacker);
        DamageSource projectile = damageSources().mobProjectile(this, attacker);
        DamageSource magic = damageSources().indirectMagic(this, attacker);
        boolean damaged = victim.hurtOrSimulate(explosion, 200.0F);
        victim.hurtOrSimulate(projectile, 100.0F);
        victim.hurtOrSimulate(magic, 30.0F);
        if (damaged) {
            if (level() instanceof ServerLevel serverWorld) {
                EnchantmentHelper.doPostAttackEffects(serverWorld, victim, explosion);
                EnchantmentHelper.doPostAttackEffects(serverWorld, victim, projectile);
                EnchantmentHelper.doPostAttackEffects(serverWorld, victim, magic);
            }
            if (victim instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 60, 4), owner != null ? owner : this);
                living.addEffect(new MobEffectInstance(ModEffects.DECAY_ENTRY, 3 * 20, 0), owner != null ? owner : this);
            }
        }
    }
}
