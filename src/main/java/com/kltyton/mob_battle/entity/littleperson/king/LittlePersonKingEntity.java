package com.kltyton.mob_battle.entity.littleperson.king;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntity;
import com.kltyton.mob_battle.entity.littleperson.king.skill.LittlePersonKingSkill;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.List;

public class LittlePersonKingEntity extends LittlePersonMilitiaEntity {
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(LittlePersonKingEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_1 = SynchedEntityData.defineId(LittlePersonKingEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_2 = SynchedEntityData.defineId(LittlePersonKingEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> STAGE = SynchedEntityData.defineId(LittlePersonKingEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> IS_VIOLENT = SynchedEntityData.defineId(LittlePersonKingEntity.class, EntityDataSerializers.BOOLEAN);
    public LittlePersonKingEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.setNoAi(false);
        this.setHasSkill(false);
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
        builder.define(STAGE, 0);
        builder.define(SKILL_COOLDOWN_1, 160);
        builder.define(SKILL_COOLDOWN_2, 100);
        builder.define(IS_VIOLENT, false);
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
        if (!canSkill()) return false;
        return !this.level().isClientSide() && !hasSkill() && getSkillCooldown(skill) == 0 && this.getTarget() != null;
    }

    public void performSkill(String skill) {
        this.setHasSkill(true);
        this.setNoAi(true);
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
        return this.entityData.get(HAS_SKILL);
    }
    public void setHasSkill(boolean hasSkill) {
        this.entityData.set(HAS_SKILL, hasSkill);
    }
    public int getStage() {
        return this.entityData.get(STAGE);
    }
    public void setStage(int stage) {
        this.entityData.set(STAGE, stage);
    }
    public void setSkillCooldown(String skill) {
        switch (skill) {
            case "attack2" -> setSkillCooldown1(isViolent() ? 200 : 160);
            case "attack3" -> setSkillCooldown2(100);
        }
    }
    public int getSkillCooldown1() {
        return this.entityData.get(SKILL_COOLDOWN_1);
    }
    public void setSkillCooldown1(int skillCooldown1) {
        this.entityData.set(SKILL_COOLDOWN_1, skillCooldown1);
    }
    public int getSkillCooldown2() {
        return this.entityData.get(SKILL_COOLDOWN_2);
    }
    public void setSkillCooldown2(int skillCooldown2) {
        this.entityData.set(SKILL_COOLDOWN_2, skillCooldown2);
    }
    public boolean isViolent() {
        return this.entityData.get(IS_VIOLENT);
    }
    public void setIsViolent(boolean isViolent) {
        this.entityData.set(IS_VIOLENT, isViolent);
    }
    public void heal() {
        this.heal(2.0F);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            List<LittlePersonGuardEntity> guardEntities = LittlePersonKingSkill.getNearbyLittlePersonGuardEntity(this, 50);
            // 计算减伤：每有一个守卫 +0.2，最高 0.96
            double damageReduction = Math.min(0.96, guardEntities.size() * 0.2);
            AttributeInstance attributeInstance = this.getAttribute(ModEntityAttributes.DAMAGE_REDUCTION);
            if (attributeInstance != null) attributeInstance.setBaseValue(damageReduction);

            if (!hasSkill()) {
                this.setNoAi(false);
                // 冷却递减
                decrementCooldownIfPositive(SKILL_COOLDOWN_1);
                decrementCooldownIfPositive(SKILL_COOLDOWN_2);
            }
        }
    }
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        if (canSkill("attack2")) {
            performSkill("attack2");
            return true;
        }
        if (canSkill("attack3")) {
            performSkill("attack3");
            return true;
        }
        return super.doHurtTarget(world, target);
    }
    private void decrementCooldownIfPositive(EntityDataAccessor<Integer> cooldownField) {
        int currentCooldown = this.entityData.get(cooldownField);
        if (currentCooldown > 0) {
            this.entityData.set(cooldownField, currentCooldown - 1);
        }
    }
    @Override
    public void knockback(double strength, double x, double z) {
        if (!hasSkill() || !this.isNoAi()) {
            super.knockback(strength, x, z);
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
                                this.playSound(SoundEvents.ANVIL_LAND, 1.0F, 1.0F);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack2", this.getId()
                                ));
                            }
                            if ("attack3".equals(s.keyframeData().getInstructions())) {
                                this.playSound(SoundEvents.ANVIL_LAND, 1.0F, 1.0F);
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
    public static AttributeSupplier.Builder createLittlePersonKingAttributes() {
        return LittlePersonEntity.createLittlePersonAttributes()
                .add(Attributes.MAX_HEALTH, 2000.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 55.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0);
    }
}
