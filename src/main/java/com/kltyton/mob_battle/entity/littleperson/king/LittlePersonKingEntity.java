package com.kltyton.mob_battle.entity.littleperson.king;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntity;
import com.kltyton.mob_battle.entity.littleperson.king.skill.LittlePersonKingSkill;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.requested.EliteLittlePersonGuardEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.client.animation.gecko.SkillAnimationPlayback;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;

import java.util.List;

public class LittlePersonKingEntity extends LittlePersonMilitiaEntity implements ModSkillEntityType {
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(LittlePersonKingEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_1 = SynchedEntityData.defineId(LittlePersonKingEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_2 = SynchedEntityData.defineId(LittlePersonKingEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_3 = SynchedEntityData.defineId(LittlePersonKingEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> STAGE = SynchedEntityData.defineId(LittlePersonKingEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> IS_VIOLENT = SynchedEntityData.defineId(LittlePersonKingEntity.class, EntityDataSerializers.BOOLEAN);
    private boolean loadingAdditionalData;
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
        builder.define(SKILL_COOLDOWN_3, 60 * 20);
        builder.define(IS_VIOLENT, false);
    }
    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if (this.loadingAdditionalData || this.level().isClientSide()) {
            return;
        }
        float currentHealth = this.getHealth();
        if (currentHealth >= this.getMaxHealth()) {
            this.setIsViolent(false);
            this.setStage(0);
            return;
        }
        int stage = this.getStage();
        if (currentHealth <= 1500.0F && stage < 1) {
            this.setStage(1);
            summonGuards(6);
            stage = 1;
        }
        if (currentHealth <= 1000.0F && stage < 2) {
            this.setStage(2);
            summonGuards(10);
            stage = 2;
        }
        if (currentHealth <= 500.0F && stage < 3) {
            this.setStage(3);
            summonGuards(20);
            setIsViolent(true);
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        this.loadingAdditionalData = true;
        try {
            super.readAdditionalSaveData(input);
        } finally {
            this.loadingAdditionalData = false;
        }
        setStage(Math.max(0, input.getIntOr("SkillStage", getStage())));
        setIsViolent(input.getBooleanOr("IsViolent", isViolent()));
        setSkillCooldown1(Math.max(0, input.getIntOr("SkillCooldown1", getSkillCooldown1())));
        setSkillCooldown2(Math.max(0, input.getIntOr("SkillCooldown2", getSkillCooldown2())));
        setSkillCooldown3(Math.max(0, input.getIntOr("SkillCooldown3", getSkillCooldown3())));
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("SkillStage", getStage());
        output.putBoolean("IsViolent", isViolent());
        output.putInt("SkillCooldown1", getSkillCooldown1());
        output.putInt("SkillCooldown2", getSkillCooldown2());
        output.putInt("SkillCooldown3", getSkillCooldown3());
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
            case "attack4" -> getSkillCooldown3();
            default -> 114514;
        };
    }
    public boolean hasSkill() {
        return this.entityData.get(HAS_SKILL);
    }
    public void setHasSkill(boolean hasSkill) {
        this.entityData.set(HAS_SKILL, hasSkill);
    }
    /**
     * 处理服务端边界校验后的技能指令。
     *
     * <p>保留旧网络分发逻辑中的全部指令字符串与动作，包括技能停止与 AI 开关。
     *
     * @param skillName 兼容现有网络协议的技能字符串
     * @return 指令是否被本实体识别并处理
     */
    @Override
    public boolean handleSkillPayload(String skillName) {
        switch (skillName) {
            case "attack2" -> LittlePersonKingSkill.runSkill_2(this);
            case "attack3" -> LittlePersonKingSkill.runSkill_3(this);
            case "attack4" -> summonEliteGuard();
            case "stop_ai" -> this.setNoAi(true);
            case "start_ai" -> this.setNoAi(false);
            case "stop" -> {
                this.setHasSkill(false);
                this.setNoAi(false);
            }
            default -> {
                return false;
            }
        }
        return true;
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
            case "attack4" -> setSkillCooldown3(60 * 20);
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
    public boolean isViolent() {
        return this.entityData.get(IS_VIOLENT);
    }
    public void setIsViolent(boolean isViolent) {
        this.entityData.set(IS_VIOLENT, isViolent);
    }
    public void heal() {
        this.heal(2.0F);
    }

    /** 皇族原型共用技能时序，由实体族系选择实际召唤目标。 */
    public void summonGuards(int count) {
        LittlePersonKingSkill.summonLittlePersonGuardEntity(this, count);
    }

    /** 大护卫召唤入口，感染型覆写此处以保留原来的技能冷却与触发条件。 */
    public void summonEliteGuard() {
        LittlePersonKingSkill.runSkill_4(this);
    }

    /** 免伤叠加方式不变，族系仅调整最大上限。 */
    protected double damageReductionCap() {
        return 0.96D;
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            List<LittlePersonGuardEntity> guardEntities = LittlePersonKingSkill.getNearbyLittlePersonGuardEntity(this, 50);
            List<EliteLittlePersonGuardEntity> eliteGuardEntities = LittlePersonKingSkill.getNearbyEliteLittlePersonGuardEntity(this, 50);
            double damageReduction = Math.min(damageReductionCap(), guardEntities.size() * 0.2 + eliteGuardEntities.size() * 0.3);
            AttributeInstance attributeInstance = this.getAttribute(ModEntityAttributes.DAMAGE_REDUCTION);
            if (attributeInstance != null) attributeInstance.setBaseValue(damageReduction);

            if (!hasSkill()) {
                this.setNoAi(false);
                // 冷却递减
                decrementCooldownIfPositive(SKILL_COOLDOWN_1);
                decrementCooldownIfPositive(SKILL_COOLDOWN_2);
                decrementCooldownIfPositive(SKILL_COOLDOWN_3);
            }
        }
    }
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        if (canSkill("attack4")) {
            performSkill("attack4");
            return true;
        }
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
    protected static final RawAnimation ATTACK_ANIM_4 = RawAnimation.begin().thenPlay("attack4");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>( "skill_controller", animTest -> {
                    if (SkillAnimationPlayback.consumeFinishedTriggeredAnimation(animTest)) {
                        ClientPlayNetworking.send(new SkillPayload(
                                "stop", this.getId()
                        ));
                    }
                    return SkillAnimationPlayback.playTriggeredAnimationOrStop(animTest);
                })
                        .receiveTriggeredAnimations()
                        .triggerableAnim("attack2", ATTACK_ANIM_2)
                        .triggerableAnim("attack3", ATTACK_ANIM_3)
                        .triggerableAnim("attack4", ATTACK_ANIM_4)
                        .setCustomInstructionKeyframeHandler(s -> {
                            String instruction = s.keyframeData().getInstructions().replaceAll("\\s+", "");
                            if ("attack2".equals(instruction) || "runAttack2;".equals(instruction)) {
                                this.playSound(SoundEvents.ANVIL_LAND, 1.0F, 1.0F);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack2", this.getId()
                                ));
                            }
                            if ("attack3".equals(instruction) || "runAttack3;".equals(instruction)) {
                                this.playSound(SoundEvents.ANVIL_LAND, 1.0F, 1.0F);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack3", this.getId()
                                ));
                            }
                            if ("attack4".equals(instruction) || "runAttack4;".equals(instruction)) {
                                this.playSound(SoundEvents.ANVIL_LAND, 1.0F, 1.0F);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack4", this.getId()
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
