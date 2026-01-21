package com.kltyton.mob_battle.entity.littleperson.king;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntity;
import com.kltyton.mob_battle.entity.littleperson.king.skill.LittlePersonKingSkill;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.List;

public class LittlePersonKingEntity extends LittlePersonMilitiaEntity {
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(LittlePersonKingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN_1 = DataTracker.registerData(LittlePersonKingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_2 = DataTracker.registerData(LittlePersonKingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> STAGE = DataTracker.registerData(LittlePersonKingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> IS_VIOLENT = DataTracker.registerData(LittlePersonKingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public LittlePersonKingEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setAiDisabled(false);
        this.setHasSkill(false);
    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
        builder.add(STAGE, 0);
        builder.add(SKILL_COOLDOWN_1, 160);
        builder.add(SKILL_COOLDOWN_2, 100);
        builder.add(IS_VIOLENT, false);
    }
    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if (health == this.getMaxHealth()) {
            this.setIsViolent(false);
            this.setStage(0);
        }
        int stage = this.getStage();
        switch ((int) health) {
            case 1500:
                if (stage < 1) {
                    this.setStage(1);
                    LittlePersonKingSkill.summonLittlePersonGuardEntity(this, 6);
                }
                break;
            case 1000:
                if (stage < 2) {
                    this.setStage(2);
                    LittlePersonKingSkill.summonLittlePersonGuardEntity(this, 10);
                }
                break;
            case 500:
                if (stage < 3) {
                    this.setStage(3);
                    LittlePersonKingSkill.summonLittlePersonGuardEntity(this, 20);
                    setIsViolent(true);
                }
                break;
            default:
                break;
        }
    }
    public boolean canSkill(String skill) {
        return !this.getWorld().isClient() && !hasSkill() && getSkillCooldown(skill) == 0 && this.getTarget() != null;
    }
    public void performSkill(String skill) {
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.setSkillCooldown(skill);
        this.triggerAnim("skill_controller", skill);
    }
    public int getSkillCooldown(String skill) {
        return switch (skill) {
            case "attack2" -> getSkillCooldown1();
            case "attack3" -> getSkillCooldown2();
            default -> 114514;
        };
    }
    public boolean hasSkill() {
        return this.dataTracker.get(HAS_SKILL);
    }
    public void setHasSkill(boolean hasSkill) {
        this.dataTracker.set(HAS_SKILL, hasSkill);
    }
    public int getStage() {
        return this.dataTracker.get(STAGE);
    }
    public void setStage(int stage) {
        this.dataTracker.set(STAGE, stage);
    }
    public void setSkillCooldown(String skill) {
        switch (skill) {
            case "attack2" -> setSkillCooldown1(isViolent() ? 200 : 160);
            case "attack3" -> setSkillCooldown2(100);
        }
    }
    public int getSkillCooldown1() {
        return this.dataTracker.get(SKILL_COOLDOWN_1);
    }
    public void setSkillCooldown1(int skillCooldown1) {
        this.dataTracker.set(SKILL_COOLDOWN_1, skillCooldown1);
    }
    public int getSkillCooldown2() {
        return this.dataTracker.get(SKILL_COOLDOWN_2);
    }
    public void setSkillCooldown2(int skillCooldown2) {
        this.dataTracker.set(SKILL_COOLDOWN_2, skillCooldown2);
    }
    public boolean isViolent() {
        return this.dataTracker.get(IS_VIOLENT);
    }
    public void setIsViolent(boolean isViolent) {
        this.dataTracker.set(IS_VIOLENT, isViolent);
    }
    public void heal() {
        this.heal(2.0F);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            List<LittlePersonGuardEntity> guardEntities = LittlePersonKingSkill.getNearbyLittlePersonGuardEntity(this, 50);
            // 计算减伤：每有一个守卫 +0.2，最高 0.96
            double damageReduction = Math.min(0.96, guardEntities.size() * 0.2);
            EntityAttributeInstance attributeInstance = this.getAttributeInstance(ModEntityAttributes.DAMAGE_REDUCTION);
            if (attributeInstance != null) attributeInstance.setBaseValue(damageReduction);

            if (!hasSkill()) {
                this.setAiDisabled(false);
                // 冷却递减
                decrementCooldownIfPositive(SKILL_COOLDOWN_1);
                decrementCooldownIfPositive(SKILL_COOLDOWN_2);
            }
        }
    }
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        if (canSkill("attack2")) {
            performSkill("attack2");
            return true;
        }
        if (canSkill("attack3")) {
            performSkill("attack3");
            return true;
        }
        return super.tryAttack(world, target);
    }
    private void decrementCooldownIfPositive(TrackedData<Integer> cooldownField) {
        int currentCooldown = this.dataTracker.get(cooldownField);
        if (currentCooldown > 0) {
            this.dataTracker.set(cooldownField, currentCooldown - 1);
        }
    }
    @Override
    public void takeKnockback(double strength, double x, double z) {
        if (!hasSkill() || !this.isAiDisabled()) {
            super.takeKnockback(strength, x, z);
        }
    }
    protected static final RawAnimation ATTACK_ANIM_2 = RawAnimation.begin().thenPlay("attack2");
    protected static final RawAnimation ATTACK_ANIM_3 = RawAnimation.begin().thenPlay("attack3");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>( "skill_controller", animTest -> {
                    if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED && this.hasSkill()) {
                        ClientPlayNetworking.send(new SkillPayload(
                                "stop", this.getId()
                        ));
                    }
                    return PlayState.STOP;
                })
                        .triggerableAnim("attack2", ATTACK_ANIM_2)
                        .triggerableAnim("attack3", ATTACK_ANIM_3)
                        .setCustomInstructionKeyframeHandler(s -> {
                            if ("attack2".equals(s.keyframeData().getInstructions())) {
                                this.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack2", this.getId()
                                ));
                            }
                            if ("attack3".equals(s.keyframeData().getInstructions())) {
                                this.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack3", this.getId()
                                ));
                            }
                        })
        );
    }
    public boolean blockAttack(@NotNull DamageSource source, float amount) {
        return false;
    }
    public static DefaultAttributeContainer.Builder createLittlePersonKingAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 2000.0)
                .add(EntityAttributes.FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.ATTACK_DAMAGE, 55.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0);
    }
}
