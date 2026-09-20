package com.kltyton.mob_battle.entity.littleperson.zombie.infected;

import com.kltyton.mob_battle.client.animation.gecko.SkillAnimationPlayback;
import com.kltyton.mob_battle.entity.littleperson.giant.LittlePersonGiantEntity;
import com.kltyton.mob_battle.entity.littleperson.zombie.ZombieLittlePerson;
import com.kltyton.mob_battle.entity.littleperson.zombie.ZombieLittlePersonSpawning;
import com.kltyton.mob_battle.entity.littleperson.zombie.ai.ZombieLittlePersonTargets;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** 感染巨人保留原型战斗数值；每秒回复 3 点生命，并以 10 秒冷却召唤一个随机变种。 */
public final class ZombieLittlePersonGiantEntity extends LittlePersonGiantEntity implements ZombieLittlePerson {
    private int summonCooldown;
    private int summonTicks;

    public ZombieLittlePersonGiantEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    public void heal() { this.heal(3.0F); }

    @Override
    public boolean hasSkill() {
        return this.summonTicks > 0 || super.hasSkill();
    }

    @Override
    public boolean handleSkillPayload(String command) {
        return this.summonTicks == 0 && super.handleSkillPayload(command);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide() || !this.isAlive()) {
            return;
        }
        if (this.summonCooldown > 0) {
            this.summonCooldown--;
        }
        if (this.summonTicks > 0) {
            if (--this.summonTicks == 10 && this.canSkill()) {
                ZombieLittlePersonSpawning.summon(ZombieLittlePersonSpawning.chooseVariant(this.random),
                        this, this.position().add(this.getViewVector(1.0F).scale(2.0D)));
            }
            if (this.summonTicks == 0) {
                this.setNoAi(false);
            }
        } else if (!this.hasSkill() && !this.isNoAi() && this.canSkill() && this.summonCooldown == 0
                && this.getTarget() != null && isValidSummonTarget(this.getTarget())
                && this.distanceToSqr(this.getTarget()) <= 32.0D * 32.0D) {
            this.summonCooldown = 200;
            this.summonTicks = 20;
            this.setNoAi(true);
            this.triggerAnim("zombie_summon", "summon");
        }
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("ZombieSummonCooldown", this.summonCooldown);
        output.putBoolean("ZombieSummonActive", this.summonTicks > 0);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.summonCooldown = Math.max(0, input.getIntOr("ZombieSummonCooldown", 0));
        this.summonTicks = 0;
        if (input.getBooleanOr("ZombieSummonActive", false)) {
            this.setNoAi(false);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        ZombieLittlePersonTargets.install(this, this.targetSelector);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>("zombie_summon", SkillAnimationPlayback::playTriggeredAnimationOrStop)
                .receiveTriggeredAnimations().triggerableAnim("summon", RawAnimation.begin().thenPlay("summon")));
    }

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.ZOMBIE_AMBIENT; }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.ZOMBIE_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.ZOMBIE_DEATH; }
}
