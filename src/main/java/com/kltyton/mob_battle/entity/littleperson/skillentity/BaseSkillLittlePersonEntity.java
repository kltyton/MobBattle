package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class BaseSkillLittlePersonEntity extends LittlePersonMilitiaEntity {
    public boolean endDamage = false;
    public int skillCount = 0;
    public int COOL_DOWN_TIME_1 = 200;
    public int COOL_DOWN_TIME_2 = 200;
    public int COOL_DOWN_TIME_3 = 200;
    public int COOL_DOWN_TIME_4 = 200;
    public int COOL_DOWN_TIME_5 = 200;

    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN_1 = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_2 = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_3 = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_4 = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_5 = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public BaseSkillLittlePersonEntity(EntityType<? extends HostileEntity> entityType, World world, int skillCount) {
        super(entityType, world);
        this.setAiDisabled(false);
        this.setHasSkill(false);
        this.skillCount = skillCount;
    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
        builder.add(SKILL_COOLDOWN_1, COOL_DOWN_TIME_1);
        builder.add(SKILL_COOLDOWN_2, COOL_DOWN_TIME_2);
        builder.add(SKILL_COOLDOWN_3, COOL_DOWN_TIME_3);
        builder.add(SKILL_COOLDOWN_4, COOL_DOWN_TIME_4);
        builder.add(SKILL_COOLDOWN_5, COOL_DOWN_TIME_5);
    }
    public void init() {
        setSkillCooldown("attack2");
        setSkillCooldown("attack3");
        setSkillCooldown("attack4");
        setSkillCooldown("attack5");
        setSkillCooldown("attack6");
    }
    public boolean canSkill(String skill) {
        return !this.getWorld().isClient() && !hasSkill() && getSkillCooldown(skill) == 0 && this.getTarget() != null;
    }
    public boolean hasSkill() {
        return this.dataTracker.get(HAS_SKILL);
    }
    public void setHasSkill(boolean hasSkill) {
        endDamage = false;
        this.dataTracker.set(HAS_SKILL, hasSkill);
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
            case "attack4" -> getSkillCooldown3();
            case "attack5" -> getSkillCooldown4();
            case "attack6" -> getSkillCooldown5();
            default -> 114514;
        };
    }
    protected int getMaxCooldownForSkill(int skillIndex) {
        return switch (skillIndex) {
            case 1 -> COOL_DOWN_TIME_1;
            case 2 -> COOL_DOWN_TIME_2;
            case 3 -> COOL_DOWN_TIME_3;
            case 4 -> COOL_DOWN_TIME_4;
            case 5 -> COOL_DOWN_TIME_5;
            default -> 114514;
        };
    }

    public void setSkillCooldown(String skill) {
        int index = Integer.parseInt(skill.replace("attack", "")) - 1;
        int cooldown = getMaxCooldownForSkill(index);
        switch (skill) {
            case "attack2" -> setSkillCooldown1(cooldown);
            case "attack3" -> setSkillCooldown2(cooldown);
            case "attack4" -> setSkillCooldown3(cooldown);
            case "attack5" -> setSkillCooldown4(cooldown);
            case "attack6" -> setSkillCooldown5(cooldown);
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
    public int getSkillCooldown4() {
        return this.dataTracker.get(SKILL_COOLDOWN_4);
    }
    public void setSkillCooldown4(int skillCooldown4) {
        this.dataTracker.set(SKILL_COOLDOWN_4, skillCooldown4);
    }
    public int getSkillCooldown5() {
        return this.dataTracker.get(SKILL_COOLDOWN_5);
    }
    public void setSkillCooldown5(int skillCooldown5) {
        this.dataTracker.set(SKILL_COOLDOWN_5, skillCooldown5);
    }
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        for (int i = 1; i <= this.skillCount; i++) {
            String skillName = "attack" + (i + 1);
            if (canSkill(skillName)) {
                performSkill(skillName);
                return true;
            }
        }
        return super.tryAttack(world, target);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (this.isDead()) {
                this.setAiDisabled(true);
                this.triggerAnim("skill_controller", "die");
            }
            if (!hasSkill()) {
                this.setAiDisabled(false);
                // 冷却递减
                decrementCooldownIfPositive(SKILL_COOLDOWN_1);
                decrementCooldownIfPositive(SKILL_COOLDOWN_2);
                decrementCooldownIfPositive(SKILL_COOLDOWN_3);
                decrementCooldownIfPositive(SKILL_COOLDOWN_4);
                decrementCooldownIfPositive(SKILL_COOLDOWN_5);
            }
        }
    }
    private void decrementCooldownIfPositive(TrackedData<Integer> cooldownField) {
        int currentCooldown = this.dataTracker.get(cooldownField);
        if (currentCooldown > 0) {
            this.dataTracker.set(cooldownField, currentCooldown - 1);
        }
    }
    @Override
    public void takeKnockback(double strength, double x, double z) {
        if (!hasSkill() || !this.isAiDisabled() || !this.isDead()) {
            super.takeKnockback(strength, x, z);
        }
    }
    protected static final RawAnimation ATTACK_ANIM_2 = RawAnimation.begin().thenPlay("attack2");
    protected static final RawAnimation ATTACK_ANIM_3 = RawAnimation.begin().thenPlay("attack3");
    protected static final RawAnimation ATTACK_ANIM_4 = RawAnimation.begin().thenPlay("attack4");
    protected static final RawAnimation ATTACK_ANIM_5 = RawAnimation.begin().thenPlay("attack5");
    protected static final RawAnimation ATTACK_ANIM_6 = RawAnimation.begin().thenPlay("attack6");
    protected static final RawAnimation DIE_ANIM = RawAnimation.begin().thenPlay("die");
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
                        .triggerableAnim("attack4", ATTACK_ANIM_4)
                        .triggerableAnim("attack5", ATTACK_ANIM_5)
                        .triggerableAnim("attack6", ATTACK_ANIM_6)
                        .triggerableAnim("die", DIE_ANIM)
                        .setCustomInstructionKeyframeHandler(s -> {
                            if ("runAttack2;".equals(s.keyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack2", this.getId()
                                ));
                            }
                            if ("runAttack3".equals(s.keyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack3", this.getId()
                                ));
                            }
                            if ("runAttack4;".equals(s.keyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack4", this.getId()
                                ));
                            }
                            if ("runAttack5;".equals(s.keyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack5", this.getId()
                                ));
                            }
                            if ("runAttack6;".equals(s.keyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack6", this.getId()
                                ));
                            }
                        })
        );
    }
    public void runSkill_2(BaseSkillLittlePersonEntity entity) {
    }
    public void runSkill_3(BaseSkillLittlePersonEntity entity) {

    }
    public void runSkill_4(BaseSkillLittlePersonEntity entity) {

    }
    public void runSkill_5(BaseSkillLittlePersonEntity entity) {

    }
    public void runSkill_6(BaseSkillLittlePersonEntity entity) {

    }
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0);
    }
}
