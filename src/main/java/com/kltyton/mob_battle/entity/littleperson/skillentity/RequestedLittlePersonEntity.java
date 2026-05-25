package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class RequestedLittlePersonEntity extends BaseSkillLittlePersonEntity implements KeyframedLittlePersonEntity {
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("run");
    protected static final RawAnimation BLOCK_1_ANIM = RawAnimation.begin().thenPlay("block_1");

    protected float healPerSecond;
    protected int blockChance;
    protected float blockDamageCap = -1.0F;
    protected String blockAnimation = "block";
    protected int skillDamageReductionTicks;
    protected int movingDamageTicks;
    protected float movingDamage;
    protected double autoSkillRange = 10.0D;
    protected int lifeTicks = -1;

    private final AnimationController<?> requestedSkillController = new AnimationController<>("skill_controller", 5, animTest -> {
        if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED) {
            if (this.hasSkill()) {
                ClientPlayNetworking.send(new SkillPayload("stop", this.getId()));
            }
            if (animTest.isCurrentAnimation(DIE_ANIM)) {
                ClientPlayNetworking.send(new SkillPayload("die", this.getId()));
            }
        }
        return PlayState.STOP;
    })
            .triggerableAnim("attack", ATTACK_ANIM)
            .triggerableAnim("attack2", attackAnimation(2))
            .triggerableAnim("attack3", attackAnimation(3))
            .triggerableAnim("attack4", attackAnimation(4))
            .triggerableAnim("attack5", attackAnimation(5))
            .triggerableAnim("attack6", attackAnimation(6))
            .triggerableAnim("attack7", attackAnimation(7))
            .triggerableAnim("attack8", attackAnimation(8))
            .triggerableAnim("attack9", attackAnimation(9))
            .triggerableAnim("attack10", attackAnimation(10))
            .triggerableAnim("attack11", attackAnimation(11))
            .triggerableAnim("block", BLOCK_ANIM)
            .triggerableAnim("block_1", BLOCK_1_ANIM)
            .triggerableAnim("die", DIE_ANIM)
            .setCustomInstructionKeyframeHandler(s -> dispatchKeyframe(s.keyframeData().getInstructions()));

    protected RequestedLittlePersonEntity(EntityType<? extends HostileEntity> entityType, World world, int skillCount) {
        super(entityType, world, skillCount);
    }

    protected RawAnimation attackAnimation(int attackNumber) {
        return switch (attackNumber) {
            case 2 -> ATTACK_ANIM_2;
            case 3 -> ATTACK_ANIM_3;
            case 4 -> ATTACK_ANIM_4;
            case 5 -> ATTACK_ANIM_5;
            case 6 -> ATTACK_ANIM_6;
            case 7 -> ATTACK_ANIM_7;
            case 8 -> ATTACK_ANIM_8;
            case 9 -> ATTACK_ANIM_9;
            case 10 -> ATTACK_ANIM_10;
            case 11 -> ATTACK_ANIM_11;
            default -> ATTACK_ANIM;
        };
    }

    protected void setCooldownSeconds(int... seconds) {
        if (seconds.length > 0) COOL_DOWN_TIME_1 = seconds[0] * 20;
        if (seconds.length > 1) COOL_DOWN_TIME_2 = seconds[1] * 20;
        if (seconds.length > 2) COOL_DOWN_TIME_3 = seconds[2] * 20;
        if (seconds.length > 3) COOL_DOWN_TIME_4 = seconds[3] * 20;
        if (seconds.length > 4) COOL_DOWN_TIME_5 = seconds[4] * 20;
        if (seconds.length > 5) COOL_DOWN_TIME_6 = seconds[5] * 20;
        if (seconds.length > 6) COOL_DOWN_TIME_7 = seconds[6] * 20;
        if (seconds.length > 7) COOL_DOWN_TIME_8 = seconds[7] * 20;
        if (seconds.length > 8) COOL_DOWN_TIME_9 = seconds[8] * 20;
        if (seconds.length > 9) COOL_DOWN_TIME_10 = seconds[9] * 20;
        clearSkillCooldowns();
    }

    @Override
    public AnimationController<?> getSkillController() {
        return this.requestedSkillController;
    }

    @Override
    public PlayState mainController(final AnimationTest<LittlePersonMilitiaEntity> event) {
        if (this.hasSkill()) {
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            return this.isAttacking() ? event.setAndContinue(RUN_ANIM) : event.setAndContinue(WALK_ANIM);
        }
        return event.setAndContinue(IDLE_ANIM);
    }

    @Override
    public void heal() {
        if (this.healPerSecond > 0.0F) {
            this.heal(this.healPerSecond);
        }
    }

    @Override
    protected void mobTick(ServerWorld world) {
        super.mobTick(world);
        LivingEntity target = this.getTarget();
        if (target != null && isValidSummonTarget(target) && !this.hasSkill() && this.age % 10 == 0) {
            double distance = this.distanceTo(target);
            if (distance <= this.autoSkillRange) {
                this.tryAttack(world, target);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            return;
        }
        if (this.lifeTicks > 0 && --this.lifeTicks <= 0) {
            this.discard();
            return;
        }
        if (this.skillDamageReductionTicks > 0) {
            this.skillDamageReductionTicks--;
        }
        if (this.movingDamageTicks > 0) {
            this.movingDamageTicks--;
            areaDamage(1.25D, this.movingDamage, 0.0F);
        }
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        if (!(target instanceof LivingEntity living) || !isValidSummonTarget(living) || !canSkill()) {
            return false;
        }
        for (int attack = this.skillCount + 1; attack >= 2; attack--) {
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

    protected boolean canUseSkill(String skillName, LivingEntity target) {
        return canSkill(skillName) && this.distanceTo(target) <= skillRange(skillName);
    }

    protected double skillRange(String skillName) {
        return 8.0D;
    }

    protected boolean canUseNormalAttack(LivingEntity target) {
        return this.distanceTo(target) <= 3.0D;
    }

    protected void performNormalAttack() {
        this.setHasSkill(true);
        this.setNormalAttackKnockbackAllowed(true);
        this.setAiDisabled(false);
        this.triggerAnim("skill_controller", "attack");
    }

    @Override
    public boolean handleSkillPayload(String skillName) {
        switch (skillName) {
            case "attack" -> {
                runAttack();
                return true;
            }
            case "stop" -> {
                this.setHasSkill(false);
                this.setAiDisabled(false);
                return true;
            }
            case "stop_ai" -> {
                this.setAiDisabled(true);
                return true;
            }
            case "start_ai" -> {
                this.setAiDisabled(false);
                return true;
            }
            case "die" -> {
                this.deathTime = 400;
                return true;
            }
            default -> {
                if (skillName.startsWith("attack")) {
                    return handleAttackPayload(skillName);
                }
                return false;
            }
        }
    }

    @Override
    protected boolean handleAttackPayload(String skillName) {
        String suffix = skillName.substring("attack".length());
        if (suffix.isEmpty()) {
            runAttack();
            return true;
        }
        String[] parts = suffix.split("_", 2);
        try {
            int attack = Integer.parseInt(parts[0]);
            int phase = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            runSkill(attack, phase);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private void dispatchKeyframe(String rawInstruction) {
        dispatchSkillKeyframe(rawInstruction);
    }

    @Override
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
        runSkill(2, 0);
    }

    @Override
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {
        runSkill(3, 0);
    }

    @Override
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {
        runSkill(4, 0);
    }

    @Override
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {
        runSkill(5, 0);
    }

    @Override
    public void runSkill_6(BaseSkillLittlePersonEntity entity) {
        runSkill(6, 0);
    }

    @Override
    public void runSkill_7(BaseSkillLittlePersonEntity entity) {
        runSkill(7, 0);
    }

    @Override
    public void runSkill_8(BaseSkillLittlePersonEntity entity) {
        runSkill(8, 0);
    }

    @Override
    public void runSkill_9(BaseSkillLittlePersonEntity entity) {
        runSkill(9, 0);
    }

    @Override
    public void runSkill_10(BaseSkillLittlePersonEntity entity) {
        runSkill(10, 0);
    }

    @Override
    public void runSkill_11(BaseSkillLittlePersonEntity entity) {
        runSkill(11, 0);
    }

    protected abstract void runAttack();

    protected abstract void runSkill(int attack, int phase);

    protected void damageTarget(float physicalDamage, float magicDamage) {
        LivingEntity target = this.getTarget();
        if (target == null || !isValidSummonTarget(target) || !(this.getWorld() instanceof ServerWorld)) {
            return;
        }
        damagePhysical(target, physicalDamage);
        damageMagic(target, magicDamage);
    }

    protected void damagePhysical(LivingEntity target, float amount) {
        if (amount <= 0.0F || !isValidSummonTarget(target) || !(this.getWorld() instanceof ServerWorld world)) {
            return;
        }
        target.damage(world, this.getDamageSources().mobAttack(this), amount);
    }

    protected void damageMagic(LivingEntity target, float amount) {
        if (amount <= 0.0F || !isValidSummonTarget(target) || !(this.getWorld() instanceof ServerWorld world)) {
            return;
        }
        target.damage(world, this.getDamageSources().indirectMagic(this, this), amount);
    }

    protected void areaDamage(double radius, float physicalDamage, float magicDamage) {
        for (LivingEntity target : EntityUtil.getNearbyEntity(this, LivingEntity.class, radius, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            if (!isValidSummonTarget(target)) {
                continue;
            }
            damagePhysical(target, physicalDamage);
            damageMagic(target, magicDamage);
        }
    }

    protected void coneDamage(double radius, float arcDegrees, float physicalDamage, float magicDamage) {
        for (LivingEntity target : EntityUtil.getEntitiesInCone(this, LivingEntity.class, radius, arcDegrees, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            if (!isValidSummonTarget(target)) {
                continue;
            }
            damagePhysical(target, physicalDamage);
            damageMagic(target, magicDamage);
        }
    }

    protected void forwardBoxDamage(double length, double width, double height, float physicalDamage, float magicDamage) {
        for (LivingEntity target : getForwardBoxTargets(length, width, height)) {
            damagePhysical(target, physicalDamage);
            damageMagic(target, magicDamage);
        }
    }

    protected List<LivingEntity> getForwardBoxTargets(double length, double width, double height) {
        Vec3d forward = this.getRotationVec(1.0F);
        Vec3d horizontal = new Vec3d(forward.x, 0.0D, forward.z);
        if (horizontal.lengthSquared() < 1.0E-4D) {
            horizontal = Vec3d.fromPolar(0.0F, this.getYaw());
        }
        horizontal = horizontal.normalize();
        Vec3d center = this.getPos().add(horizontal.multiply(length * 0.5D + 0.5D)).add(0.0D, height * 0.5D, 0.0D);
        Box box = new Box(center.x - width * 0.5D, center.y - height * 0.5D, center.z - width * 0.5D,
                center.x + width * 0.5D, center.y + height * 0.5D, center.z + width * 0.5D);
        return new ArrayList<>(this.getWorld().getEntitiesByClass(LivingEntity.class, box,
                this::isValidSummonTarget));
    }

    protected void pullTargetToFront(LivingEntity target, double distance) {
        Vec3d forward = this.getRotationVec(1.0F).normalize();
        Vec3d pos = this.getPos().add(forward.multiply(distance));
        target.requestTeleport(pos.x, this.getY(), pos.z);
    }

    protected void pullNearbyTargets(double radius, double distance) {
        for (LivingEntity target : EntityUtil.getNearbyEntity(this, LivingEntity.class, radius, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            if (!isValidSummonTarget(target)) {
                continue;
            }
            Vec3d direction = this.getPos().subtract(target.getPos());
            Vec3d horizontal = new Vec3d(direction.x, 0.0D, direction.z);
            if (horizontal.lengthSquared() > 1.0E-4D) {
                target.addVelocity(horizontal.normalize().multiply(distance * 0.35D).x, 0.12D, horizontal.normalize().multiply(distance * 0.35D).z);
                target.velocityModified = true;
            }
        }
    }

    protected List<LivingEntity> nearestTargets(double radius, int limit) {
        return EntityUtil.getNearbyEntity(this, LivingEntity.class, radius, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)
                .stream()
                .filter(this::isValidSummonTarget)
                .sorted(Comparator.comparingDouble(this::squaredDistanceTo))
                .limit(limit)
                .toList();
    }

    protected void addPiercing(LivingEntity target, int seconds, int amplifier) {
        target.addStatusEffect(new StatusEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, seconds * 20, amplifier), this);
    }

    protected void addMagicPiercing(LivingEntity target, int seconds, int amplifier) {
        target.addStatusEffect(new StatusEffectInstance(ModEffects.VOID_ARMOR_PIERCING_ENTRY, seconds * 20, amplifier), this);
    }

    @Override
    public boolean blockAttack(@NotNull DamageSource source, float amount) {
        if (this.blockChance <= 0 || isEnvironmentDamage(source)) {
            return false;
        }
        if (this.blockDamageCap > 0.0F && amount > this.blockDamageCap) {
            return false;
        }
        if (this.random.nextInt(100) >= this.blockChance) {
            return false;
        }
        this.playSound(SoundEvents.ITEM_SHIELD_BLOCK.value(), 1.0F, 1.0F);
        this.triggerAnim("attack_controller", this.blockAnimation);
        return true;
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (this.skillDamageReductionTicks > 0) {
            amount *= 0.5F;
        }
        return super.damage(world, source, amount);
    }

    protected boolean isEnvironmentDamage(DamageSource source) {
        return source.isIn(DamageTypeTags.IS_FALL)
                || source.isIn(DamageTypeTags.IS_FIRE)
                || source.isIn(DamageTypeTags.IS_EXPLOSION)
                || source.isIn(DamageTypeTags.IS_DROWNING)
                || source.isIn(DamageTypeTags.IS_FREEZING)
                || source.isIn(DamageTypeTags.IS_LIGHTNING)
                || source.isIn(DamageTypeTags.BURN_FROM_STEPPING)
                || source.isIn(DamageTypeTags.WITCH_RESISTANT_TO);
    }

    protected void startMovingHitbox(int ticks, float damage) {
        this.movingDamageTicks = ticks;
        this.movingDamage = damage;
    }

    protected static DefaultAttributeContainer.Builder createRequestedAttributes(double health, double attackDamage, double speed, double followRange, double damageReduction) {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, health)
                .add(EntityAttributes.ATTACK_DAMAGE, attackDamage)
                .add(EntityAttributes.MOVEMENT_SPEED, speed)
                .add(EntityAttributes.FOLLOW_RANGE, followRange)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, damageReduction);
    }
}
