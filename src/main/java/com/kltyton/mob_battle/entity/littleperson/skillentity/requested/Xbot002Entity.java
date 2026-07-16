package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class Xbot002Entity extends RequestedTaskLittlePersonEntity {
    private boolean deathBlastDone;

    public Xbot002Entity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 3);
        this.healPerSecond = 3.0F;
        this.autoSkillRange = 8.0D;
        setCooldownSeconds(10, 10, 20);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(2500.0D, 70.0D, 0.30D, 40.0D, 0.30D)
                .add(Attributes.ARMOR, 26.0D)
                .add(Attributes.SCALE, 1.5)
                .add(Attributes.ARMOR_TOUGHNESS, 20.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                10,
                true,
                false,
                (target, world) -> target instanceof LittlePersonEntity && EntityUtil.isValidCombatTarget(this, target)
        ));
    }

    @Override
    protected void runAttack() {
        damageTarget(70.0F, 0.0F);
        knockTargetLikeIronGolem(1.1D);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> {
                LivingEntity target = this.getTarget();
                boolean validTarget = target != null && EntityUtil.isValidCombatTarget(this, target);
                damageTarget(phase == 2 ? 210.0F : 105.0F, 0.0F);
                if (validTarget && this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.levelEvent(2013, target.getOnPos(), 750);
                }
                knockTargetLikeIronGolem(1.3D);
            }
            case 3 -> {
                damageTarget(100.0F, 0.0F);
                knockTargetLikeIronGolem(1.4D);
                this.addEffect(new MobEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, 3 * 20, 4), this);
            }
            case 4 -> {
                lungeTowardTarget(1.8D, 1.45D);
                startMovingHitbox(24, 80.0F);
                knockTargetLikeIronGolem(1.6D);
            }
            default -> {
            }
        }
    }

    @Override
    public void knockback(double strength, double x, double z) {
    }

    private void knockTargetLikeIronGolem(double strength) {
        LivingEntity target = this.getTarget();
        if (target == null || !EntityUtil.isValidCombatTarget(this, target)) {
            return;
        }
        target.knockback(strength, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
        target.hurtMarked = true;
        this.playSound(SoundEvents.ANVIL_LAND, 1.0F, 0.85F + this.random.nextFloat() * 0.2F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.IRON_GOLEM_REPAIR;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        if (!this.deathBlastDone && this.level() instanceof ServerLevel) {
            this.deathBlastDone = true;
            this.addEffect(new MobEffectInstance(ModEffects.SUPER_SELF_DESTRUCT_ENTRY, 20, 0), this);
            areaDamage(5.0D, 160.0F, 0.0F);
        }
    }
}
