package com.kltyton.mob_battle.entity.general;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Mob;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public interface GeneralEntity<T extends Mob> extends ModSkillEntityType, GeoEntity {
    Mob getEntity();
    int getSkillCount();
    EntityDataAccessor<Boolean> getHasSkillKey();
    EntityDataAccessor<Integer> getCooldownKey1();
    EntityDataAccessor<Integer> getCooldownKey2();
    EntityDataAccessor<Integer> getCooldownKey3();
    EntityDataAccessor<Integer> getCooldownKey4();
    EntityDataAccessor<Integer> getCooldownKey5();
    default int getSkillCooldown1() {
        return getEntity().getEntityData().get(getCooldownKey1());
    }
    default void setSkillCooldown1(int skillCooldown1) {
        getEntity().getEntityData().set(getCooldownKey1(), skillCooldown1);
    }
    default int getSkillCooldown2() {
        return getEntity().getEntityData().get(getCooldownKey2());
    }
    default void setSkillCooldown2(int skillCooldown2) {
        getEntity().getEntityData().set(getCooldownKey2(), skillCooldown2);
    }
    default int getSkillCooldown3() {
        return getEntity().getEntityData().get(getCooldownKey3());
    }
    default void setSkillCooldown3(int skillCooldown3) {
        getEntity().getEntityData().set(getCooldownKey3(), skillCooldown3);
    }
    default int getSkillCooldown4() {
        return getEntity().getEntityData().get(getCooldownKey4());
    }
    default void setSkillCooldown4(int skillCooldown4) {
        getEntity().getEntityData().set(getCooldownKey4(), skillCooldown4);
    }
    default int getSkillCooldown5() {
        return getEntity().getEntityData().get(getCooldownKey5());
    }
    default void setSkillCooldown5(int skillCooldown5) {
        getEntity().getEntityData().set(getCooldownKey5(), skillCooldown5);
    }
    default int getMaxSkillCooldown_1() {
        return -1;
    }
    default int getMaxSkillCooldown_2() {
        return -1;
    }
    default int getMaxSkillCooldown_3() {
        return -1;
    }
    default int getMaxSkillCooldown_4() {
        return -1;
    }
    default int getMaxSkillCooldown_5() {
        return -1;
    }
    default void setSkillCooldown(String skill) {
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
    default int getSkillCooldown(String skill) {
        return switch (skill) {
            case "attack2" -> getSkillCooldown1();
            case "attack3" -> getSkillCooldown2();
            case "attack4" -> getSkillCooldown3();
            case "attack5" -> getSkillCooldown4();
            case "attack6" -> getSkillCooldown5();
            default -> 114514;
        };
    }
    default int getMaxCooldownForSkill(int skillIndex) {
        return switch (skillIndex) {
            case 1 -> getMaxSkillCooldown_1();
            case 2 -> getMaxSkillCooldown_2();
            case 3 -> getMaxSkillCooldown_3();
            case 4 -> getMaxSkillCooldown_4();
            case 5 -> getMaxSkillCooldown_5();
            default -> 114514;
        };
    }
    default void entityInitDataTracker(SynchedEntityData.Builder builder) {
        builder.define(getHasSkillKey(), false);
        builder.define(getCooldownKey1(), getMaxCooldownForSkill(1));
        builder.define(getCooldownKey2(), getMaxCooldownForSkill(2));
        builder.define(getCooldownKey3(), getMaxCooldownForSkill(3));
        builder.define(getCooldownKey4(), getMaxCooldownForSkill(4));
        builder.define(getCooldownKey5(), getMaxCooldownForSkill(5));
    }
    default boolean hasSkill() {
        return getEntity().getEntityData().get(getHasSkillKey());
    }
    default void setHasSkill(boolean hasSkill) {
        getEntity().getEntityData().set(getHasSkillKey(), hasSkill);
    }
    default boolean canSkill(String skill) {
        if (!canSkill()) return false;
        return !getEntity().level().isClientSide() && !hasSkill() && getSkillCooldown(skill) == 0 && getEntity().getTarget() != null;
    }
    default void performSkill(String skill) {
        this.setHasSkill(true);
        getEntity().setNoAi(true);
        this.setSkillCooldown(skill);
        this.triggerAnim("skill_controller", skill);
    }
    @Override
    default boolean canSkill() {
        return ModSkillEntityType.canSkill(getEntity());
    }
    default void entityTick() {
        if (!getEntity().level().isClientSide) {
            if (!hasSkill()) {
                getEntity().setNoAi(false);
                // 冷却递减
                decrementCooldownIfPositive(getCooldownKey1());
                decrementCooldownIfPositive(getCooldownKey2());
                decrementCooldownIfPositive(getCooldownKey3());
                decrementCooldownIfPositive(getCooldownKey4());
                decrementCooldownIfPositive(getCooldownKey5());
            }
        }
    }
    default boolean doSkill() {
        if (!canSkill()) return false;
        for (int i = getSkillCount(); i >= 1; i--) {
            String skillName = "attack" + (i + 1);
            if (canSkill(skillName)) {
                performSkill(skillName);
                return true;
            }
        }
        this.triggerAnim("skill_controller", "attack");
        return false;
    }
    default void decrementCooldownIfPositive(EntityDataAccessor<Integer> cooldownField) {
        int currentCooldown = getEntity().getEntityData().get(cooldownField);
        if (currentCooldown > 0) {
            getEntity().getEntityData().set(cooldownField, currentCooldown - 1);
        }
    }
    RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    RawAnimation ATTACK_ANIM_2 = RawAnimation.begin().thenPlay("attack2");
    RawAnimation ATTACK_ANIM_3 = RawAnimation.begin().thenPlay("attack3");
    RawAnimation ATTACK_ANIM_4 = RawAnimation.begin().thenPlay("attack4");
    RawAnimation ATTACK_ANIM_5 = RawAnimation.begin().thenPlay("attack5");
    RawAnimation ATTACK_ANIM_6 = RawAnimation.begin().thenPlay("attack6");
    RawAnimation ATTACK_ANIM_7 = RawAnimation.begin().thenPlay("attack7");
    default void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 5, this::mainController));
        controllers.add(new AnimationController<>( "skill_controller", 5,animTest -> {
                    if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED) {
                        if (this.hasSkill()) ClientPlayNetworking.send(new SkillPayload("stop", getEntity().getId()));
                    }
                    return PlayState.STOP;
                })
                        .triggerableAnim("attack", ATTACK_ANIM)
                        .triggerableAnim("attack2", ATTACK_ANIM_2)
                        .triggerableAnim("attack3", ATTACK_ANIM_3)
                        .triggerableAnim("attack4", ATTACK_ANIM_4)
                        .triggerableAnim("attack5", ATTACK_ANIM_5)
                        .triggerableAnim("attack6", ATTACK_ANIM_6)
                        .triggerableAnim("attack7", ATTACK_ANIM_7)
                        .setCustomInstructionKeyframeHandler(s -> {
                            if ("runAttack2;".equals(s.keyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack2", getEntity().getId()
                                ));
                            }
                            if ("runAttack3;".equals(s.keyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack3", getEntity().getId()
                                ));
                            }
                            if ("runAttack4;".equals(s.keyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack4", getEntity().getId()
                                ));
                            }
                            if ("runAttack5;".equals(s.keyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack5", getEntity().getId()
                                ));
                            }
                            if ("runAttack6;".equals(s.keyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack6", getEntity().getId()
                                ));
                            }
                            if ("runAttack7;".equals(s.keyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack7", getEntity().getId()
                                ));
                            }
                        })
        );
    }
    default PlayState mainController(AnimationTest<?> event) {
        if (this.hasSkill()) {
            return PlayState.CONTINUE;
        }
        return event.isMoving() ? event.setAndContinue(WALK_ANIM) : event.setAndContinue(IDLE_ANIM);
    }
    default void runSkill_2(T entity) {
    }
    default void stopSkill_2(T entity) {
    }
    default void runSkill_1(T entity) {
    }
    default void runSkill_1_1(T entity) {
        runSkill_1(entity);
    }
    default void runSkill_1_2(T entity) {
        runSkill_1(entity);
    }
    default void runSkill_1_3(T entity) {
        runSkill_1(entity);
    }
    default void runSkill_3(T entity) {
    }
    default void runSkill_3_1(T entity) {
        runSkill_3(entity);
    }
    default void runSkill_3_2(T entity) {
        runSkill_3(entity);
    }
    default void runSkill_4(T entity) {
    }
    default void stopSkill_4(T entity) {
    }
    default void runSkill_5(T entity) {
    }
    default void runSkill_6(T entity) {
    }
    default void runSkill_7(T entity) {
    }
    default void runSkill_7_1(T entity) {
        runSkill_7(entity);
    }
    default void runBuff(T entity) {
    }
}
