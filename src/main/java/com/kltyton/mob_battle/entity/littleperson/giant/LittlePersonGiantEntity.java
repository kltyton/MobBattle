package com.kltyton.mob_battle.entity.littleperson.giant;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class LittlePersonGiantEntity extends LittlePersonMilitiaEntity {
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(LittlePersonGiantEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_1 = SynchedEntityData.defineId(LittlePersonGiantEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_2 = SynchedEntityData.defineId(LittlePersonGiantEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_3 = SynchedEntityData.defineId(LittlePersonGiantEntity.class, EntityDataSerializers.INT);
    public LittlePersonGiantEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.setNoAi(false);
        this.setHasSkill(false);
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
        builder.define(SKILL_COOLDOWN_1, 600);
        builder.define(SKILL_COOLDOWN_2, 300);
        builder.define(SKILL_COOLDOWN_3, 500);
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
    public boolean hasSkill() {
        return this.entityData.get(HAS_SKILL);
    }
    public void setHasSkill(boolean hasSkill) {
        this.entityData.set(HAS_SKILL, hasSkill);
    }
    public int getSkillCooldown(String skill) {
        return switch (skill) {
            case "attack2" -> getSkillCooldown1();
            case "attack3" -> getSkillCooldown2();
            case "attack4" -> getSkillCooldown3();
            default -> 114514;
        };
    }
    public void setSkillCooldown(String skill) {
        switch (skill) {
            case "attack2" -> setSkillCooldown1(600);
            case "attack3" -> setSkillCooldown2(300);
            case "attack4" -> setSkillCooldown3(500);
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
    public int getSkillCooldown3() {
        return this.entityData.get(SKILL_COOLDOWN_3);
    }
    public void setSkillCooldown3(int skillCooldown3) {
        this.entityData.set(SKILL_COOLDOWN_3, skillCooldown3);
    }
    @Override
    public void heal() {
        this.heal(10.0F);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (!hasSkill()) {
                this.setNoAi(false);
                // 冷却递减
                decrementCooldownIfPositive(SKILL_COOLDOWN_1);
                decrementCooldownIfPositive(SKILL_COOLDOWN_2);
                decrementCooldownIfPositive(SKILL_COOLDOWN_3);
                if (this.getTarget() != null && this.distanceToSqr(this.getTarget()) <= 20.0D * 20.0D && canSkill("attack4")) {
                    performSkill("attack4");
                }
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
        if (canSkill("attack4")) {
            performSkill("attack4");
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
    @Override
    public boolean blockAttack(@NotNull DamageSource source, float amount) {
        return false;
    }
    protected static final RawAnimation ATTACK_ANIM_2 = RawAnimation.begin().thenPlay("attack2");
    protected static final RawAnimation ATTACK_ANIM_3 = RawAnimation.begin().thenPlay("attack3");
    protected static final RawAnimation ATTACK_ANIM_4 = RawAnimation.begin().thenPlay("attack4");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>( "skill_controller",animTest -> {
            if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED && this.hasSkill()) {
                ClientPlayNetworking.send(new SkillPayload(
                        "stop", this.getId()
                ));
            }
            return PlayState.STOP;
        })
                .triggerableAnim("attack2", ATTACK_ANIM_2)
                .triggerableAnim("attack3", ATTACK_ANIM_3)
                .triggerableAnim("attack4", ATTACK_ANIM_4)
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
                    if ("attack4".equals(s.keyframeData().getInstructions())) {
                        this.playSound(SoundEvents.ANVIL_LAND, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "attack4", this.getId()
                        ));
                    }
                })
        );
    }
    public static AttributeSupplier.Builder createLittlePersonGiantAttributes() {
        return LittlePersonEntity.createLittlePersonAttributes()
                .add(Attributes.MAX_HEALTH, 1300.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 50.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0);
    }
}
