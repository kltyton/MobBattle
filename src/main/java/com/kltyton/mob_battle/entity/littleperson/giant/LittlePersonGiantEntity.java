package com.kltyton.mob_battle.entity.littleperson.giant;

import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
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
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class LittlePersonGiantEntity extends LittlePersonMilitiaEntity {
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(LittlePersonGiantEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN_1 = DataTracker.registerData(LittlePersonGiantEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_2 = DataTracker.registerData(LittlePersonGiantEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_3 = DataTracker.registerData(LittlePersonGiantEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public LittlePersonGiantEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setAiDisabled(false);
        this.setHasSkill(false);
    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
        builder.add(SKILL_COOLDOWN_1, 600);
        builder.add(SKILL_COOLDOWN_2, 300);
        builder.add(SKILL_COOLDOWN_3, 500);
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
    public boolean hasSkill() {
        return this.dataTracker.get(HAS_SKILL);
    }
    public void setHasSkill(boolean hasSkill) {
        this.dataTracker.set(HAS_SKILL, hasSkill);
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
    public int getSkillCooldown3() {
        return this.dataTracker.get(SKILL_COOLDOWN_3);
    }
    public void setSkillCooldown3(int skillCooldown3) {
        this.dataTracker.set(SKILL_COOLDOWN_3, skillCooldown3);
    }
    @Override
    public void heal() {
        this.heal(50.0F);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (!hasSkill()) {
                this.setAiDisabled(false);
                // 冷却递减
                decrementCooldownIfPositive(SKILL_COOLDOWN_1);
                decrementCooldownIfPositive(SKILL_COOLDOWN_2);
                decrementCooldownIfPositive(SKILL_COOLDOWN_3);
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
        if (canSkill("attack4")) {
            performSkill("attack4");
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
    @Override
    public boolean blockAttack(DamageSource source, float amount) {
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
                    if ("attack4".equals(s.keyframeData().getInstructions())) {
                        this.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "attack4", this.getId()
                        ));
                    }
                })
        );
    }
    public static DefaultAttributeContainer.Builder createLittlePersonGiantAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 1300.0)
                .add(EntityAttributes.FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.ATTACK_DAMAGE, 50.0);
    }
}
