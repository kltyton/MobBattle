package com.kltyton.mob_battle.entity.enhancedwither;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedHashSet;
import java.util.Set;

public class EnhancedWitherSkullEntity extends WitherSkull {
    private static final float EXPLOSION_DAMAGE = 40.0F;
    private static final float DANGEROUS_MAGIC_DAMAGE = 12.0F;
    private static final double DAMAGE_RADIUS = 3.0D;

    public EnhancedWitherSkullEntity(EntityType<? extends WitherSkull> entityType, Level world) {
        super(entityType, world);
    }

    public EnhancedWitherSkullEntity(Level world, LivingEntity owner, Vec3 direction) {
        super(world, owner, direction);
    }

    @Override
    public boolean canHitEntity(Entity entity) {
        if (entity instanceof LivingEntity living && !EntityUtil.isValidSummonCombatTarget(this, this.getOwner(), living)) {
            return false;
        }
        return super.canHitEntity(entity);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        // Full damage is applied once from onHit so direct hits and splash hits share one path.
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }

        Entity owner = this.getOwner();
        LivingEntity livingOwner = owner instanceof LivingEntity living ? living : null;
        Set<LivingEntity> targets = new LinkedHashSet<>();

        if (hitResult instanceof EntityHitResult entityHit
                && entityHit.getEntity() instanceof LivingEntity directTarget
                && EntityUtil.isValidSummonCombatTarget(this, owner, directTarget)) {
            targets.add(directTarget);
        }

        Vec3 center = hitResult.getLocation();
        AABB damageBox = new AABB(
                center.x - DAMAGE_RADIUS, center.y - DAMAGE_RADIUS, center.z - DAMAGE_RADIUS,
                center.x + DAMAGE_RADIUS, center.y + DAMAGE_RADIUS, center.z + DAMAGE_RADIUS
        );
        targets.addAll(world.getEntitiesOfClass(LivingEntity.class, damageBox,
                target -> target.distanceToSqr(center) <= DAMAGE_RADIUS * DAMAGE_RADIUS
                        && EntityUtil.isValidSummonCombatTarget(this, owner, target)));

        for (LivingEntity target : targets) {
            applyEnhancedDamage(world, target, livingOwner);
        }

        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 0.0F, false, Level.ExplosionInteraction.NONE);
        this.discard();
    }

    private void applyEnhancedDamage(ServerLevel world, LivingEntity target, LivingEntity owner) {
        int oldInvulnerableTime = target.invulnerableTime;
        target.invulnerableTime = 0;

        DamageSource explosionSource = this.damageSources().explosion(this, owner);
        boolean hit = target.hurtServer(world, explosionSource, EXPLOSION_DAMAGE);
        if (hit) {
            EnchantmentHelper.doPostAttackEffects(world, target, explosionSource);
        }

        int postExplosionInvulnerableTime = target.invulnerableTime;
        if (this.isDangerous() && target.isAlive()) {
            target.invulnerableTime = 0;
            DamageSource magicSource = this.damageSources().indirectMagic(this, owner);
            boolean magicHit = target.hurtServer(world, magicSource, DANGEROUS_MAGIC_DAMAGE);
            if (magicHit) {
                EnchantmentHelper.doPostAttackEffects(world, target, magicSource);
            }
        }

        target.invulnerableTime = Math.max(target.invulnerableTime, Math.max(oldInvulnerableTime, postExplosionInvulnerableTime));
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
        return max;
    }
}
