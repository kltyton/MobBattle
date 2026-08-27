package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.RequestedLittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.SkillProjectileEntity;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

abstract class RequestedTaskLittlePersonEntity extends RequestedLittlePersonEntity {
    protected RequestedTaskLittlePersonEntity(EntityType<? extends Monster> entityType, Level world, int skillCount) {
        super(entityType, world, skillCount);
    }

    protected static AttributeSupplier.Builder requestedAttributes(double health, double attackDamage, double speed, double followRange, double damageReduction) {
        return createRequestedAttributes(health, attackDamage, speed, followRange, damageReduction);
    }

    protected void dashForward(double strength) {
        Vec3 direction = horizontalLook();
        this.push(direction.x * strength, 0.04D, direction.z * strength);
        this.hurtMarked = true;
    }

    protected void lungeTowardTarget(double strength, double navigationSpeed) {
        LivingEntity target = this.getTarget();
        if (target == null || !isValidSummonTarget(target)) {
            dashForward(strength);
            return;
        }
        Vec3 direction = target.position().subtract(this.position());
        Vec3 horizontal = new Vec3(direction.x, 0.0D, direction.z);
        if (horizontal.lengthSqr() < 1.0E-4D) {
            horizontal = horizontalLook();
        } else {
            horizontal = horizontal.normalize();
        }

        this.setNoAi(false);
        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.getNavigation().moveTo(target, navigationSpeed);
        this.setDeltaMovement(horizontal.x * strength, 0.08D, horizontal.z * strength);
        this.hurtMarked = true;
    }

    protected void knockTargetAway(LivingEntity target, double strength, double y) {
        Vec3 direction = target.position().subtract(this.position());
        Vec3 horizontal = new Vec3(direction.x, 0.0D, direction.z);
        if (horizontal.lengthSqr() < 1.0E-4D) {
            horizontal = horizontalLook();
        } else {
            horizontal = horizontal.normalize();
        }
        target.push(horizontal.x * strength, y, horizontal.z * strength);
        target.hurtMarked = true;
    }

    protected void shootSpearAtTarget(float damage) {
        shootLittleArrowAtTarget(ModEntities.SPEAR_BULLET, damage, 1.6F);
    }

    protected void shootLittleArrowAtTarget(EntityType<LittleArrowEntity> type, float damage, float velocity) {
        LivingEntity target = this.getTarget();
        if (target == null || !isValidSummonTarget(target) || !(this.level() instanceof ServerLevel serverWorld)) {
            return;
        }
        double targetX = target.getX() - this.getX();
        double targetY = target.getEyeY() - this.getEyeY();
        double targetZ = target.getZ() - this.getZ();
        double distance = Math.sqrt(targetX * targetX + targetZ * targetZ);
        targetY += target.getDeltaMovement().y() * distance * 0.25D;

        LittleArrowEntity arrow = new LittleArrowEntity(
                type,
                this.level(),
                this,
                new ItemStack(Items.ARROW),
                null
        );
        arrow.setBaseDamage(applyStrengthAndWeakness(damage));
        arrow.setOwner(this);
        arrow.shoot(targetX, targetY, targetZ, velocity, 0.01F);
        arrow.setTrueDamage(true, false);
        serverWorld.addFreshEntity(arrow);
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    protected void shootSkillProjectileAtTarget(EntityType<SkillProjectileEntity> type, float physicalDamage, float magicDamage,
                                                double speed, int maxAge, boolean pierceEntities, boolean pierceBlocks,
                                                boolean explodeOnHit, double explosionRadius) {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        LivingEntity target = this.getTarget();
        Vec3 direction = target != null && isValidSummonTarget(target)
                ? target.getEyePosition().subtract(this.getEyePosition()).normalize()
                : this.getViewVector(1.0F).normalize();
        spawnSkillProjectile(type, this.getEyePosition().add(direction.scale(0.8D)), direction.scale(speed),
                physicalDamage, magicDamage, maxAge, pierceEntities, pierceBlocks, explodeOnHit, explosionRadius);
    }

    protected void spawnSkillProjectile(EntityType<SkillProjectileEntity> type, Vec3 position, Vec3 velocity,
                                        float physicalDamage, float magicDamage, int maxAge, boolean pierceEntities,
                                        boolean pierceBlocks, boolean explodeOnHit, double explosionRadius) {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        SkillProjectileEntity projectile = type.create(world, EntitySpawnReason.MOB_SUMMONED);
        if (projectile == null) {
            return;
        }
        projectile.configure(this, position, velocity, applyStrengthAndWeakness(physicalDamage), magicDamage,
                pierceEntities, pierceBlocks, explodeOnHit, maxAge);
        projectile.setExplosionRadius(explosionRadius);
        world.addFreshEntity(projectile);
    }

    protected void damagePhysicalNoInvulnerability(LivingEntity target, float amount) {
        if (amount <= 0.0F || !isValidSummonTarget(target) || !(this.level() instanceof ServerLevel world)) {
            return;
        }
        target.invulnerableTime = 0;
        target.hurtServer(world, this.damageSources().mobAttack(this), applyStrengthAndWeakness(amount));
        target.invulnerableTime = 0;
    }

    protected void damageTargetNoInvulnerability(float amount) {
        LivingEntity target = this.getTarget();
        if (target != null) {
            damagePhysicalNoInvulnerability(target, amount);
        }
    }

    protected Iterable<LivingEntity> alliedLittlePersons(double radius, boolean includeSelf) {
        return EntityQueries.getNearbyEntity(this, LivingEntity.class, LittlePersonEntity.class, radius, includeSelf, EntityQueries.TeamFilter.ONLY_TEAM);
    }

    private Vec3 horizontalLook() {
        Vec3 forward = this.getViewVector(1.0F);
        Vec3 horizontal = new Vec3(forward.x, 0.0D, forward.z);
        if (horizontal.lengthSqr() < 1.0E-4D) {
            horizontal = Vec3.directionFromRotation(0.0F, this.getYRot());
        }
        return horizontal.normalize();
    }
}
