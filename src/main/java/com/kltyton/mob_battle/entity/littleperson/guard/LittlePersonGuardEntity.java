package com.kltyton.mob_battle.entity.littleperson.guard;

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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class LittlePersonGuardEntity extends LittlePersonMilitiaEntity {
    public LittlePersonGuardEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.setNoAi(false);
        this.setHasSkill(false);
    }
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(LittlePersonGuardEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN = SynchedEntityData.defineId(LittlePersonGuardEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LIFE = SynchedEntityData.defineId(LittlePersonGuardEntity.class, EntityDataSerializers.INT);
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
        builder.define(SKILL_COOLDOWN, 700);
        builder.define(LIFE, -1);
    }
    public boolean canSkillAttack() {
        if (!ModSkillEntityType.canSkill(this)) return false;
        return !this.level().isClientSide() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    public void performSkill() {
        this.setHasSkill(true);
        this.setNoAi(true);
        this.setSkillCooldown(700);
        this.triggerAnim("skill_controller", "attack2");
    }
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (target instanceof net.minecraft.world.entity.LivingEntity living && !isValidSummonTarget(living)) {
            return false;
        }
        if (!ModSkillEntityType.canSkill(this)) return false;
        if (canSkillAttack()) {
            performSkill();
            return true;
        }
        return super.doHurtTarget(world, target);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (!hasSkill()) {
                this.setNoAi(false);
                int currentCooldown = this.getSkillCooldown();
                if (currentCooldown > 0) {
                    this.setSkillCooldown(currentCooldown - 1);
                }
                int currentLife = this.getLife();
                if (currentLife > 0) {
                    this.setLife(currentLife - 1);
                } else if (currentLife == 0) {
                    this.discard();
                }
            }
        }
    }
    @Override
    public void knockback(double strength, double x, double z) {
        if (!hasSkill() || !this.isNoAi()) {
            super.knockback(strength, x, z);
        }
    }
    public boolean hasSkill() {
        return this.entityData.get(HAS_SKILL);
    }
    public void setHasSkill(boolean hasSkill) {
        this.entityData.set(HAS_SKILL, hasSkill);
    }
    public int getSkillCooldown() {
        return this.entityData.get(SKILL_COOLDOWN);
    }
    public void setSkillCooldown(int skillCooldown) {
        this.entityData.set(SKILL_COOLDOWN, skillCooldown);
    }
    public int getLife() {
        return this.entityData.get(LIFE);
    }
    public void setLife(int life) {
        this.entityData.set(LIFE, life);
    }
    protected static final RawAnimation ATTACK_ANIM_2 = RawAnimation.begin().thenPlay("attack2");
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
                        .setCustomInstructionKeyframeHandler(s -> {
                            if ("attack2".equals(s.keyframeData().getInstructions())) {
                                this.playSound(SoundEvents.ANVIL_LAND, 1.0F, 1.0F);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack2", this.getId()
                                ));
                            }
                        })
        );
    }
    public static AttributeSupplier.Builder createLittlePersonGuardAttributes() {
        return LittlePersonEntity.createLittlePersonAttributes()
                .add(Attributes.MAX_HEALTH, 120.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.ATTACK_DAMAGE, 50.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.8);
    }
    public int blockProbability() {
        return 30;
    }
    public float maxBlockDamage() {
        return 80f;
    }
}
