package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

public class LittlePersonMedicEntity extends RequestedTaskLittlePersonEntity {
    private static final double HEAL_SEARCH_RANGE = 12.0D;
    private static final double HEAL_CAST_RANGE = 3.0D;
    private static final double THREAT_RANGE = 6.0D;
    private LivingEntity queuedHealTarget;

    public LittlePersonMedicEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 0);
        this.healPerSecond = 3.0F;
        this.autoSkillRange = 0.0D;
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(200.0D, 1.0D, 0.50D, 40.0D, 0.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && !this.hasSkill()) {
            LivingEntity threat = findNearbyThreat();
            if (threat != null) {
                fleeFrom(threat);
                return;
            }

            LivingEntity healTarget = findHealTarget();
            if (healTarget == null) {
                this.queuedHealTarget = null;
                return;
            }

            this.queuedHealTarget = healTarget;
            this.setTarget(null);
            double distance = this.distanceTo(healTarget);
            if (distance > HEAL_CAST_RANGE) {
                this.getNavigation().moveTo(healTarget, 1.15D);
            } else if (this.tickCount % 20 == 0) {
                performNormalAttack();
            }
        }
    }

    @Override
    protected void runAttack() {
        LivingEntity healTarget = isHealableAlly(this.queuedHealTarget) ? this.queuedHealTarget : findHealTarget();
        if (healTarget != null) {
            healTarget.heal(1.0F);
        }
        this.queuedHealTarget = null;
    }

    private LivingEntity findHealTarget() {
        return EntityQueries.getNearbyEntity(this, LivingEntity.class, HEAL_SEARCH_RANGE, false, EntityQueries.TeamFilter.ALL).stream()
                .filter(this::isHealableAlly)
                .min(Comparator.comparingDouble(this::distanceToSqr))
                .orElse(null);
    }

    private LivingEntity findNearbyThreat() {
        return EntityQueries.getNearbyEntity(this, LivingEntity.class, THREAT_RANGE, false, EntityQueries.TeamFilter.ALL).stream()
                .filter(this::isValidSummonTarget)
                .min(Comparator.comparingDouble(this::distanceToSqr))
                .orElse(null);
    }

    private void fleeFrom(LivingEntity threat) {
        Vec3 away = this.position().subtract(threat.position());
        Vec3 horizontal = new Vec3(away.x, 0.0D, away.z);
        if (horizontal.lengthSqr() < 1.0E-4D) {
            return;
        }
        horizontal = horizontal.normalize();
        this.setTarget(null);
        this.getNavigation().moveTo(this.getX() + horizontal.x * 8.0D, this.getY(), this.getZ() + horizontal.z * 8.0D, 1.3D);
        this.push(horizontal.x * 0.15D, 0.02D, horizontal.z * 0.15D);
        this.hurtMarked = true;
    }

    private boolean isHealableAlly(LivingEntity entity) {
        if (entity == null || entity == this || entity instanceof LittlePersonMedicEntity || !entity.isAlive()) {
            return false;
        }
        if (entity.getHealth() >= entity.getMaxHealth() || EntityQueries.isValidCombatTarget(this, entity)) {
            return false;
        }
        Entity owner = EntityQueries.getKnownOwner(this);
        return entity.isAlliedTo(this) || this.isAlliedTo(entity) || EntityQueries.isFriendlyToSummon(this, owner, entity);
    }

    @Override
    protected void runSkill(int attack, int phase) {
    }
}
