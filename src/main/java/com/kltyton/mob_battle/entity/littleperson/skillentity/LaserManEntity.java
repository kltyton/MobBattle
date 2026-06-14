package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class LaserManEntity extends RequestedLittlePersonEntity implements RangedAttackMob {
    private static final double MELEE_SWITCH_RANGE = 4.0D;
    private static final double RANGED_ATTACK_RANGE = 16.0D;
    private static final double RANGED_PREFERRED_RANGE = 8.0D;

    public LaserManEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 6);
        this.healPerSecond = 3.0F;
        this.blockChance = 20;
        this.blockDamageCap = 100.0F;
        this.autoSkillRange = MELEE_SWITCH_RANGE;
        setCooldownSeconds(5, 25, 10, 10, 15, 2);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new LaserManMeleeGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new LaserManRangedGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, 10, true, false, this::canTargetAsSummon));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, WarriorVillager.class, 10, true, false, this::canTargetAsSummon));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::canTargetAsSummon));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false,
                (entity, world) -> entity instanceof Enemy && !(entity instanceof LittlePersonEntity) && canTargetAsSummon(entity, world)));
    }

    private boolean canTargetAsSummon(LivingEntity target, ServerLevel world) {
        return isValidSummonTarget(target);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return createRequestedAttributes(4500.0D, 90.0D, 0.5D, 40.0D, 0.25D);
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!(target instanceof LivingEntity living) || !isValidSummonTarget(living) || !canSkill()) {
            return false;
        }
        if (isMeleeModeTarget(living)) {
            for (int attack : new int[]{6, 5, 4, 3, 2}) {
                String skillName = "attack" + attack;
                if (canUseSkill(skillName, living)) {
                    performSkill(skillName);
                    return true;
                }
            }
            if (canUseNormalAttack(living)) {
                performNormalAttack();
                return true;
            }
            return false;
        }
        if (canUseSkill("attack7", living)) {
            performSkill("attack7");
            return true;
        }
        return false;
    }

    @Override
    protected boolean canUseSkill(String skillName, LivingEntity target) {
        if (("attack2".equals(skillName) || "attack3".equals(skillName) || "attack4".equals(skillName)
                || "attack5".equals(skillName) || "attack6".equals(skillName)) && !isMeleeModeTarget(target)) {
            return false;
        }
        if ("attack7".equals(skillName) && isMeleeModeTarget(target)) {
            return false;
        }
        return super.canUseSkill(skillName, target);
    }

    @Override
    protected double skillRange(String skillName) {
        return switch (skillName) {
            case "attack7" -> RANGED_ATTACK_RANGE;
            default -> MELEE_SWITCH_RANGE;
        };
    }

    @Override
    protected boolean canUseNormalAttack(LivingEntity target) {
        return !this.hasSkill() && isMeleeModeTarget(target);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        if (target == null || !isValidSummonTarget(target) || !canUseSkill("attack7", target)) {
            return;
        }

        performSkill("attack7");
    }

    @Override
    protected void runAttack() {
        damageTarget(90.0F, 0.0F);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> damageTarget(100.0F, 0.0F);
            case 3 -> coneDamage(3.0D, 100.0F, 90.0F, 0.0F);
            case 4 -> damageTarget(150.0F, 0.0F);
            case 5 -> {
                this.setNoAi(false);
                startMovingHitbox(30, 100.0F);
            }
            case 6 -> areaDamage(3.0D, 80.0F, 0.0F);
            case 7 -> shootLaser();
            default -> {
            }
        }
    }

    private void shootLaser() {
        LivingEntity target = this.getTarget();
        if (target == null || !(this.level() instanceof ServerLevel world)) {
            return;
        }
        SkillProjectileEntity projectile = ModEntities.LASER.create(world, EntitySpawnReason.MOB_SUMMONED);
        if (projectile == null) {
            return;
        }
        Vec3 start = this.getEyePosition().add(this.getViewVector(1.0F).scale(0.8D));
        Vec3 velocity = target.getEyePosition().subtract(start).normalize().scale(1.4D);
        projectile.configure(this, start, velocity, 85.0F, 0.0F, false, false, false, 60);
        world.addFreshEntity(projectile);
        this.playSound(SoundEvents.BLAZE_SHOOT, 0.8F, 1.2F);
    }

    private boolean isMeleeModeTarget(@Nullable LivingEntity target) {
        return target != null && this.distanceToSqr(target) <= MELEE_SWITCH_RANGE * MELEE_SWITCH_RANGE;
    }

    private boolean isRangedModeTarget(@Nullable LivingEntity target) {
        return target != null
                && isValidSummonTarget(target)
                && this.distanceToSqr(target) > MELEE_SWITCH_RANGE * MELEE_SWITCH_RANGE;
    }

    private static class LaserManMeleeGoal extends MeleeAttackGoal {
        private final LaserManEntity mob;

        private LaserManMeleeGoal(LaserManEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(mob, speedModifier, followingTargetEvenIfNotSeen);
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            return this.mob.isMeleeModeTarget(this.mob.getTarget()) && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return this.mob.isMeleeModeTarget(this.mob.getTarget()) && super.canContinueToUse();
        }
    }

    private static class LaserManRangedGoal extends Goal {
        private final LaserManEntity mob;
        private final double speedModifier;
        private final int attackIntervalTicks;
        private int attackTime = -1;
        private int updatePathDelay;

        private LaserManRangedGoal(LaserManEntity mob, double speedModifier, int attackIntervalTicks) {
            this.mob = mob;
            this.speedModifier = speedModifier;
            this.attackIntervalTicks = attackIntervalTicks;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.mob.isRangedModeTarget(this.mob.getTarget()) && !this.mob.hasSkill();
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void start() {
            this.attackTime = 0;
            this.updatePathDelay = 0;
            this.mob.setAggressive(true);
        }

        @Override
        public void stop() {
            this.mob.setAggressive(false);
            this.mob.getNavigation().stop();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (!this.mob.isRangedModeTarget(target)) {
                this.mob.getNavigation().stop();
                return;
            }

            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            double distanceSqr = this.mob.distanceToSqr(target);
            boolean canSee = this.mob.getSensing().hasLineOfSight(target);

            if (this.updatePathDelay-- <= 0) {
                updateMovement(target, distanceSqr, canSee);
                this.updatePathDelay = 5 + this.mob.getRandom().nextInt(6);
            }

            if (this.attackTime > 0) {
                this.attackTime--;
            }

            if (this.attackTime <= 0 && canSee && distanceSqr <= RANGED_ATTACK_RANGE * RANGED_ATTACK_RANGE) {
                this.mob.performRangedAttack(target, 1.0F);
                this.attackTime = this.attackIntervalTicks;
            }
        }

        private void updateMovement(LivingEntity target, double distanceSqr, boolean canSee) {
            if (!canSee || distanceSqr > RANGED_ATTACK_RANGE * RANGED_ATTACK_RANGE) {
                this.mob.getNavigation().moveTo(target, this.speedModifier);
                return;
            }

            if (distanceSqr < RANGED_PREFERRED_RANGE * RANGED_PREFERRED_RANGE) {
                moveAwayFrom(target);
                return;
            }

            this.mob.getNavigation().stop();
        }

        private void moveAwayFrom(LivingEntity target) {
            Vec3 away = this.mob.position().subtract(target.position());
            Vec3 horizontal = new Vec3(away.x, 0.0D, away.z);

            if (horizontal.lengthSqr() < 1.0E-4D) {
                horizontal = Vec3.directionFromRotation(0.0F, this.mob.getYRot());
            } else {
                horizontal = horizontal.normalize();
            }

            Vec3 destination = this.mob.position().add(horizontal.scale(4.0D));
            this.mob.getNavigation().moveTo(destination.x, this.mob.getY(), destination.z, this.speedModifier);
        }
    }
}
