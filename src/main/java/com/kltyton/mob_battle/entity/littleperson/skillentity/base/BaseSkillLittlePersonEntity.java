package com.kltyton.mob_battle.entity.littleperson.skillentity.base;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.IronManEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.KeyframedLittlePersonEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class BaseSkillLittlePersonEntity extends LittlePersonMilitiaEntity implements ModSkillEntityType, KeyframedLittlePersonEntity {
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
    private boolean normalAttackKnockbackAllowed;

    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(BaseSkillLittlePersonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_1 = SynchedEntityData.defineId(BaseSkillLittlePersonEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_2 = SynchedEntityData.defineId(BaseSkillLittlePersonEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_3 = SynchedEntityData.defineId(BaseSkillLittlePersonEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_4 = SynchedEntityData.defineId(BaseSkillLittlePersonEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_5 = SynchedEntityData.defineId(BaseSkillLittlePersonEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_6 = SynchedEntityData.defineId(BaseSkillLittlePersonEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_7 = SynchedEntityData.defineId(BaseSkillLittlePersonEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_8 = SynchedEntityData.defineId(BaseSkillLittlePersonEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_9 = SynchedEntityData.defineId(BaseSkillLittlePersonEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_10 = SynchedEntityData.defineId(BaseSkillLittlePersonEntity.class, EntityDataSerializers.INT);
    public BaseSkillLittlePersonEntity(EntityType<? extends Monster> entityType, Level world, int skillCount) {
        super(entityType, world);
        this.setNoAi(false);
        this.setHasSkill(false);
        this.skillCount = skillCount;
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
        builder.define(SKILL_COOLDOWN_1, COOL_DOWN_TIME_1);
        builder.define(SKILL_COOLDOWN_2, COOL_DOWN_TIME_2);
        builder.define(SKILL_COOLDOWN_3, COOL_DOWN_TIME_3);
        builder.define(SKILL_COOLDOWN_4, COOL_DOWN_TIME_4);
        builder.define(SKILL_COOLDOWN_5, COOL_DOWN_TIME_5);
        builder.define(SKILL_COOLDOWN_6, COOL_DOWN_TIME_6);
        builder.define(SKILL_COOLDOWN_7, COOL_DOWN_TIME_7);
        builder.define(SKILL_COOLDOWN_8, COOL_DOWN_TIME_8);
        builder.define(SKILL_COOLDOWN_9, COOL_DOWN_TIME_9);
        builder.define(SKILL_COOLDOWN_10, COOL_DOWN_TIME_10);
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
        return !this.level().isClientSide()
                && !hasSkill()
                && getSkillCooldown(skill) == 0
                && this.getTarget() != null
                && isValidSummonTarget(this.getTarget());
    }
    public boolean hasSkill() {
        return this.entityData.get(HAS_SKILL);
    }
    public void setHasSkill(boolean hasSkill) {
        endDamage = false;
        if (!hasSkill) {
            setNormalAttackKnockbackAllowed(false);
        }
        this.entityData.set(HAS_SKILL, hasSkill);
    }
    public boolean allowsNormalAttackKnockback() {
        return this.normalAttackKnockbackAllowed;
    }
    protected void setNormalAttackKnockbackAllowed(boolean normalAttackKnockbackAllowed) {
        this.normalAttackKnockbackAllowed = normalAttackKnockbackAllowed;
    }
    public void performSkill(String skill, boolean isAfterSkill) {
        setNormalAttackKnockbackAllowed(false);
        this.setHasSkill(true);
        this.setNoAi(true);
        if (isAfterSkill) this.setSkillCooldown(skill);
        this.triggerAnim("skill_controller", skill);
    }
    public void performSkill(String skill) {
        this.performSkill(skill, true);
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
    protected void clearSkillCooldowns() {
        setSkillCooldown1(0);
        setSkillCooldown2(0);
        setSkillCooldown3(0);
        setSkillCooldown4(0);
        setSkillCooldown5(0);
        setSkillCooldown6(0);
        setSkillCooldown7(0);
        setSkillCooldown8(0);
        setSkillCooldown9(0);
        setSkillCooldown10(0);
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
    public int getSkillCooldown4() {
        return this.entityData.get(SKILL_COOLDOWN_4);
    }
    public void setSkillCooldown4(int skillCooldown4) {
        this.entityData.set(SKILL_COOLDOWN_4, skillCooldown4);
    }
    public int getSkillCooldown5() {
        return this.entityData.get(SKILL_COOLDOWN_5);
    }
    public void setSkillCooldown5(int skillCooldown5) {
        this.entityData.set(SKILL_COOLDOWN_5, skillCooldown5);
    }
    public int getSkillCooldown6() {return this.entityData.get(SKILL_COOLDOWN_6);}
    public void setSkillCooldown6(int skillCooldown6) {
        this.entityData.set(SKILL_COOLDOWN_6, skillCooldown6);
    }
    public int getSkillCooldown7() {return this.entityData.get(SKILL_COOLDOWN_7);}
    public void setSkillCooldown7(int skillCooldown7) {
        this.entityData.set(SKILL_COOLDOWN_7, skillCooldown7);
    }
    public int getSkillCooldown8() {return this.entityData.get(SKILL_COOLDOWN_8);}
    public void setSkillCooldown8(int skillCooldown8) {
        this.entityData.set(SKILL_COOLDOWN_8, skillCooldown8);
    }
    public int getSkillCooldown9() {return this.entityData.get(SKILL_COOLDOWN_9);}
    public void setSkillCooldown9(int skillCooldown9) {
        this.entityData.set(SKILL_COOLDOWN_9, skillCooldown9);
    }
    public int getSkillCooldown10() {return this.entityData.get(SKILL_COOLDOWN_10);}
    public void setSkillCooldown10(int skillCooldown10) {
        this.entityData.set(SKILL_COOLDOWN_10, skillCooldown10);
    }
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (target instanceof net.minecraft.world.entity.LivingEntity living && !isValidSummonTarget(living)) {
            return false;
        }
        if (!ModSkillEntityType.canSkill(this)) return false;

        for (int i = this.skillCount; i >= 1; i--) {
            String skillName = "attack" + (i + 1);
            if (canSkill(skillName)) {
                performSkill(skillName);
                return true;
            }
        }
        return super.doHurtTarget(world, target);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.setAggressive(this.getTarget() != null);
            if (this.isDeadOrDying()) {
                this.setNoAi(true);
                this.triggerAnim("skill_controller", "die");
            }
            if (!hasSkill()) {
                this.setNoAi(false);
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
    private void decrementCooldownIfPositive(EntityDataAccessor<Integer> cooldownField) {
        int currentCooldown = this.entityData.get(cooldownField);
        if (currentCooldown > 0) {
            this.entityData.set(cooldownField, currentCooldown - 1);
        }
    }
    @Override
    public void knockback(double strength, double x, double z) {
        if (!hasSkill() || !this.isNoAi() || !this.isDeadOrDying()) {
            super.knockback(strength, x, z);
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
            .setCustomInstructionKeyframeHandler(s -> dispatchSkillKeyframe(s.keyframeData().getInstructions()));
    public AnimationController<?> getSkillController() {
        return this.skillController;
    }

    protected void dispatchSkillKeyframe(String rawInstruction) {
        String instruction = rawInstruction.replaceAll("\\s+", "");
        switch (instruction) {
            case "runDie;" -> ClientPlayNetworking.send(new SkillPayload("die", this.getId()));
            case "runStop;" -> ClientPlayNetworking.send(new SkillPayload("stop", this.getId()));
            case "runStopAi;" -> ClientPlayNetworking.send(new SkillPayload("stop_ai", this.getId()));
            case "runStartAi;" -> ClientPlayNetworking.send(new SkillPayload("start_ai", this.getId()));
            default -> {
                if (instruction.startsWith("runAttack")) {
                    String attack = instruction.substring("run".length());
                    if (attack.endsWith(";")) {
                        attack = attack.substring(0, attack.length() - 1);
                    }
                    if (!attack.isEmpty()) {
                        String payload = Character.toLowerCase(attack.charAt(0)) + attack.substring(1);
                        ClientPlayNetworking.send(new SkillPayload(payload, this.getId()));
                    }
                }
            }
        }
    }

    @Override
    public boolean handleSkillPayload(String skillName) {
        return switch (skillName) {
            case "stop_ai" -> {
                this.setNoAi(true);
                yield true;
            }
            case "start_ai" -> {
                this.setNoAi(false);
                yield true;
            }
            case "die" -> {
                this.deathTime = 400;
                yield true;
            }
            case "stop" -> {
                this.setHasSkill(false);
                this.setNoAi(false);
                yield true;
            }
            default -> skillName.startsWith("attack") && handleAttackPayload(skillName);
        };
    }

    protected boolean handleAttackPayload(String skillName) {
        String suffix = skillName.substring("attack".length());
        if (suffix.isEmpty()) {
            return false;
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

    protected void runSkill(int attack, int phase) {
        if (phase != 0) {
            return;
        }
        switch (attack) {
            case 2 -> runSkill_2(this);
            case 3 -> runSkill_3(this);
            case 4 -> runSkill_4(this);
            case 5 -> runSkill_5(this);
            case 6 -> runSkill_6(this);
            case 7 -> runSkill_7(this);
            case 8 -> runSkill_8(this);
            case 9 -> runSkill_9(this);
            case 10 -> runSkill_10(this);
            case 11 -> runSkill_11(this);
            default -> {
            }
        }
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
    protected void tickDeath() {
        if (this instanceof IronManEntity) {
            this.deathTime++;
            if (this.deathTime >= 400 && !this.level().isClientSide() && !this.isRemoved()) {
                die(this);
                this.level().broadcastEntityEvent(this, EntityEvent.POOF);
                this.remove(Entity.RemovalReason.KILLED);
            }
        } else {
            super.tickDeath();
        }
    }
    public static AttributeSupplier.Builder createAttributes() {
        return LittlePersonEntity.createLittlePersonAttributes()
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0);
    }

    @Override
    public boolean canSkill() {
        return ModSkillEntityType.canSkill(this);
    }
}
