package com.kltyton.mob_battle.entity.littleperson.skillentity.base;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.IronManEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
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

public class BaseSkillLittlePersonEntity extends LittlePersonMilitiaEntity implements ModSkillEntityType {
    public boolean endDamage = false;
    public int skillCount = 0;
    public int COOL_DOWN_TIME_1 = -1;
    public int COOL_DOWN_TIME_2 = -1;
    public int COOL_DOWN_TIME_3 = -1;
    public int COOL_DOWN_TIME_4 = -1;
    public int COOL_DOWN_TIME_5 = -1;
    public int COOL_DOWN_TIME_6 = -1;
    public int COOL_DOWN_TIME_7 = -1;
    public int COOL_DOWN_TIME_8 = -1;
    public int COOL_DOWN_TIME_9 = -1;
    public int COOL_DOWN_TIME_10 = -1;

    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN_1 = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_2 = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_3 = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_4 = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_5 = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_6 = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_7 = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_8 = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_9 = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_10 = DataTracker.registerData(BaseSkillLittlePersonEntity.class, TrackedDataHandlerRegistry.INTEGER);
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
        builder.add(SKILL_COOLDOWN_6, COOL_DOWN_TIME_6);
        builder.add(SKILL_COOLDOWN_7, COOL_DOWN_TIME_7);
        builder.add(SKILL_COOLDOWN_8, COOL_DOWN_TIME_8);
        builder.add(SKILL_COOLDOWN_9, COOL_DOWN_TIME_9);
        builder.add(SKILL_COOLDOWN_10, COOL_DOWN_TIME_10);
    }
    public void init() {
        setSkillCooldown("attack2");
        setSkillCooldown("attack3");
        setSkillCooldown("attack4");
        setSkillCooldown("attack5");
        setSkillCooldown("attack6");
        setSkillCooldown("attack7");
        setSkillCooldown("attack8");
        setSkillCooldown("attack9");
        setSkillCooldown("attack10");
        setSkillCooldown("attack11");
    }
    public boolean canSkill(String skill) {
        if (!canSkill()) return false;
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
            case "attack7" -> getSkillCooldown6();
            case "attack8" -> getSkillCooldown7();
            case "attack9" -> getSkillCooldown8();
            case "attack10" -> getSkillCooldown9();
            case "attack11" -> getSkillCooldown10();
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
            case 6 -> COOL_DOWN_TIME_6;
            case 7 -> COOL_DOWN_TIME_7;
            case 8 -> COOL_DOWN_TIME_8;
            case 9 -> COOL_DOWN_TIME_9;
            case 10 -> COOL_DOWN_TIME_10;
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
            case "attack7" -> setSkillCooldown6(cooldown);
            case "attack8" -> setSkillCooldown7(cooldown);
            case "attack9" -> setSkillCooldown8(cooldown);
            case "attack10" -> setSkillCooldown9(cooldown);
            case "attack11" -> setSkillCooldown10(cooldown);
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
    public int getSkillCooldown6() {return this.dataTracker.get(SKILL_COOLDOWN_6);}
    public void setSkillCooldown6(int skillCooldown6) {
        this.dataTracker.set(SKILL_COOLDOWN_6, skillCooldown6);
    }
    public int getSkillCooldown7() {return this.dataTracker.get(SKILL_COOLDOWN_7);}
    public void setSkillCooldown7(int skillCooldown7) {
        this.dataTracker.set(SKILL_COOLDOWN_7, skillCooldown7);
    }
    public int getSkillCooldown8() {return this.dataTracker.get(SKILL_COOLDOWN_8);}
    public void setSkillCooldown8(int skillCooldown8) {
        this.dataTracker.set(SKILL_COOLDOWN_8, skillCooldown8);
    }
    public int getSkillCooldown9() {return this.dataTracker.get(SKILL_COOLDOWN_9);}
    public void setSkillCooldown9(int skillCooldown9) {
        this.dataTracker.set(SKILL_COOLDOWN_9, skillCooldown9);
    }
    public int getSkillCooldown10() {return this.dataTracker.get(SKILL_COOLDOWN_10);}
    public void setSkillCooldown10(int skillCooldown10) {
        this.dataTracker.set(SKILL_COOLDOWN_10, skillCooldown10);
    }
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;

        for (int i = this.skillCount; i >= 1; i--) {
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
                decrementCooldownIfPositive(SKILL_COOLDOWN_6);
                decrementCooldownIfPositive(SKILL_COOLDOWN_7);
                decrementCooldownIfPositive(SKILL_COOLDOWN_8);
                decrementCooldownIfPositive(SKILL_COOLDOWN_9);
                decrementCooldownIfPositive(SKILL_COOLDOWN_10);
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
    protected static final RawAnimation ATTACK_ANIM_7 = RawAnimation.begin().thenPlay("attack7");
    protected static final RawAnimation ATTACK_ANIM_8 = RawAnimation.begin().thenPlay("attack8");
    protected static final RawAnimation ATTACK_ANIM_9 = RawAnimation.begin().thenPlay("attack9");
    protected static final RawAnimation ATTACK_ANIM_10 = RawAnimation.begin().thenPlay("attack10");
    protected static final RawAnimation ATTACK_ANIM_11 = RawAnimation.begin().thenPlay("attack11");
    protected static final RawAnimation DIE_ANIM = RawAnimation.begin().thenPlay("die");
    public AnimationController<?> skillController = new AnimationController<>( "skill_controller", animTest -> {
        if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED) {
            if (this.hasSkill()) ClientPlayNetworking.send(new SkillPayload("stop", this.getId()));
            if (animTest.isCurrentAnimation(DIE_ANIM) && this instanceof IronManEntity) {
                this.deathTime = 400;
                ClientPlayNetworking.send(new SkillPayload(
                        "die", this.getId()
                ));
            }
        }
        return PlayState.STOP;
    })
            .triggerableAnim("attack2", ATTACK_ANIM_2)
            .triggerableAnim("attack3", ATTACK_ANIM_3)
            .triggerableAnim("attack4", ATTACK_ANIM_4)
            .triggerableAnim("attack5", ATTACK_ANIM_5)
            .triggerableAnim("attack6", ATTACK_ANIM_6)
            .triggerableAnim("attack7", ATTACK_ANIM_7)
            .triggerableAnim("attack8", ATTACK_ANIM_8)
            .triggerableAnim("attack9", ATTACK_ANIM_9)
            .triggerableAnim("attack10", ATTACK_ANIM_10)
            .triggerableAnim("attack11", ATTACK_ANIM_11)
            .triggerableAnim("die", DIE_ANIM)
            .setCustomInstructionKeyframeHandler(s -> {
                if ("runAttack2;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack2", this.getId()
                    ));
                }
                if ("runAttack3;".equals(s.keyframeData().getInstructions())) {
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
                if ("runAttack7;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack7", this.getId()
                    ));
                }
                if ("runAttack8;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack8", this.getId()
                    ));
                }
                if ("runAttack9;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack9", this.getId()
                    ));
                }
                if ("runAttack10;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack10", this.getId()
                    ));
                }
                if ("runAttack11;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "attack11", this.getId()
                    ));
                }
                if ("runDie;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "die", this.getId()
                    ));
                }
                if ("runStop;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "stop", this.getId()
                    ));
                }
                if ("runStopAi;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "stop_ai", this.getId()
                    ));
                }
                if ("runStartAi;".equals(s.keyframeData().getInstructions())) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "start_ai", this.getId()
                    ));
                }
            });
    public AnimationController<?> getSkillController() {
        return this.skillController;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(getSkillController());
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
    public void runSkill_7(BaseSkillLittlePersonEntity entity) {

    }
    public void runSkill_8(BaseSkillLittlePersonEntity entity) {

    }
    public void runSkill_9(BaseSkillLittlePersonEntity entity) {

    }
    public void runSkill_10(BaseSkillLittlePersonEntity entity) {

    }
    public void runSkill_11(BaseSkillLittlePersonEntity entity) {

    }
    public void die(BaseSkillLittlePersonEntity entity) {

    }
    protected void updatePostDeath() {
        if (this instanceof IronManEntity) {
            this.deathTime++;
            if (this.deathTime >= 400 && !this.getWorld().isClient() && !this.isRemoved()) {
                die(this);
                this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_DEATH_PARTICLES);
                this.remove(Entity.RemovalReason.KILLED);
            }
        } else {
            super.updatePostDeath();
        }
    }
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.FOLLOW_RANGE, 40.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0);
    }

    @Override
    public boolean canSkill() {
        return ModSkillEntityType.canSkill(this);
    }
}
