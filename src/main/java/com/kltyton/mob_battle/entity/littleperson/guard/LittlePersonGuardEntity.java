package com.kltyton.mob_battle.entity.littleperson.guard;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
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
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class LittlePersonGuardEntity extends LittlePersonMilitiaEntity {
    public LittlePersonGuardEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setAiDisabled(false);
        this.setHasSkill(false);
    }
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(LittlePersonGuardEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN = DataTracker.registerData(LittlePersonGuardEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> LIFE = DataTracker.registerData(LittlePersonGuardEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
        builder.add(SKILL_COOLDOWN, 700);
        builder.add(LIFE, -1);
    }
    public boolean canSkill() {
        return !this.getWorld().isClient() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    public void performSkill() {
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.setSkillCooldown(700);
        this.triggerAnim("skill_controller", "attack2");
    }
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        if (canSkill()) {
            performSkill();
            return true;
        }
        return super.tryAttack(world, target);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (!hasSkill()) {
                this.setAiDisabled(false);
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
    public void takeKnockback(double strength, double x, double z) {
        if (!hasSkill() || !this.isAiDisabled()) {
            super.takeKnockback(strength, x, z);
        }
    }
    public boolean hasSkill() {
        return this.dataTracker.get(HAS_SKILL);
    }
    public void setHasSkill(boolean hasSkill) {
        this.dataTracker.set(HAS_SKILL, hasSkill);
    }
    public int getSkillCooldown() {
        return this.dataTracker.get(SKILL_COOLDOWN);
    }
    public void setSkillCooldown(int skillCooldown) {
        this.dataTracker.set(SKILL_COOLDOWN, skillCooldown);
    }
    public int getLife() {
        return this.dataTracker.get(LIFE);
    }
    public void setLife(int life) {
        this.dataTracker.set(LIFE, life);
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
                                this.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack2", this.getId()
                                ));
                            }
                        })
        );
    }
    public static DefaultAttributeContainer.Builder createLittlePersonGuardAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 120.0)
                .add(EntityAttributes.FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.4)
                .add(EntityAttributes.ATTACK_DAMAGE, 50.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.8);
    }
    @Override
    public boolean blockAttack(DamageSource source, float amount) {
        // 检查伤害是否来自实体直接攻击
        if (source.getSource() instanceof Entity &&
                !source.isIn(DamageTypeTags.IS_FALL) &&
                !source.isIn(DamageTypeTags.IS_FIRE) &&
                !source.isIn(DamageTypeTags.IS_EXPLOSION) &&
                !source.isIn(DamageTypeTags.IS_DROWNING) &&
                !source.isIn(DamageTypeTags.IS_FREEZING) &&
                !source.isIn(DamageTypeTags.IS_LIGHTNING) &&
                !source.isIn(DamageTypeTags.BURN_FROM_STEPPING) &&
                !source.isIn(DamageTypeTags.WITCH_RESISTANT_TO) &&
                amount <= 80.0F) {

            // 20%概率免疫伤害
            if (this.random.nextInt(100) < 30) {
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK.value(), 1.0F, 1.0F);
                this.triggerAnim("attack_controller", "block");
                return true;
            }
        }
        return false;
    }
}
