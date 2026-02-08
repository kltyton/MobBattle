package com.kltyton.mob_battle.entity.littleperson.militia.soldier;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.general.GeneralEntityOnlyOneSkill;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class LittlePersonSoldierEntity extends HostileEntity implements LittlePersonEntity, GeneralEntityOnlyOneSkill<LittlePersonSoldierEntity> {
    public int getCooldownTime() {
        return 10;
    }
    public static final TrackedData<Boolean> IS_CHARGING = DataTracker.registerData(LittlePersonSoldierEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(LittlePersonSoldierEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN = DataTracker.registerData(LittlePersonSoldierEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public LittlePersonSoldierEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setAiDisabled(false);
        this.setHasSkill(false);
        this.setSkillCooldown(getCooldownTime());
    }
    @Override
    public boolean hasSkill() {
        return getDataTracker().get(HAS_SKILL);
    }
    public int getSkillCooldown() {
        return getDataTracker().get(SKILL_COOLDOWN);
    }
    @Override
    public void setHasSkill(boolean hasSkill) {
        getDataTracker().set(HAS_SKILL, hasSkill);
    }
    public void setSkillCooldown(int cooldown) {
        getDataTracker().set(SKILL_COOLDOWN, cooldown);
    }
    // 辅助方法
    public boolean isCharging() { return getDataTracker().get(IS_CHARGING); }
    public void setCharging(boolean charging) { getDataTracker().set(IS_CHARGING, charging); }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
        builder.add(SKILL_COOLDOWN, getCooldownTime());
        builder.add(IS_CHARGING, false); // 注册冲锋状态
    }
    @Override
    public void takeKnockback(double strength, double x, double z) {
        if (!hasSkill() || !this.isAiDisabled()) {
            super.takeKnockback(strength, x, z);
        }
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false)); // 添加僵尸攻击目标
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0)); // 添加远距离游荡目标
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F)); // 添加看向玩家的目标
        this.goalSelector.add(8, new LookAroundGoal(this)); // 添加环顾四周的目标
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, GolemEntity.class, true)); // 添加攻击傀儡目标
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, WarriorVillager.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true)); // 添加主动攻击玩家目标
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MobEntity.class, 5, false, false, (entity, world) -> entity instanceof Monster && !(entity instanceof LittlePersonEntity)));
    }
    public static DefaultAttributeContainer.Builder createLittlePersonMilitiaAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 300.0)
                .add(EntityAttributes.FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.ATTACK_DAMAGE, 30.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.2);
    }
    @Override
    public void heal() {
        this.heal(3.0F);
    }
    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            if (this.age % 20 == 0) this.heal();
            if (canSkill()) {
                performSkill();
            }
            // --- 冲锋核心逻辑 ---
            if (isCharging()) {
                performChargeMovement();
            } else if (!hasSkill()) {
                this.setAiDisabled(false);
                int cd = getSkillCooldown();
                if (cd > 0) setSkillCooldown(cd - 1);
            }
        }
    }
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        return false;
    }
    public void performSkill() {
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.setSkillCooldown(getCooldownTime());
        this.triggerAnim("attack_controller", "attack");
    }
    /**
     * 每帧执行的冲锋位移和伤害检测
     */
    private void performChargeMovement() {
        // 锁定冲锋速度，例如每 Tick 移动 0.5 格 (相当于 10格/秒)
        Vec3d lookDir = this.getRotationVector();
        Vec3d velocity = new Vec3d(lookDir.x, 0, lookDir.z).normalize().multiply(0.5);
        // 执行移动
        this.move(MovementType.SELF, velocity);
        // 伤害检测范围 (稍微比碰撞箱大一点)
        Box damageBox = this.getBoundingBox().expand(0.5, 0, 0.5);
        List<Entity> targets = this.getWorld().getOtherEntities(this, damageBox,
                target -> target instanceof LivingEntity && target.isAlive() && !target.isTeammate(this));

        float damageAmount = (float) this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
        for (Entity target : targets) {
            target.damage((ServerWorld) this.getWorld(), this.getDamageSources().mobAttack(this), damageAmount);
            if (target instanceof LivingEntity livingEntity) {
                livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, 100, 0));
            }
        }
    }
    // 4. 修改 runSkill，只作为启动器
    @Override
    public void runSkill(LittlePersonSoldierEntity entity) {
        if (this.getWorld().isClient) return;

        this.setCharging(true);
    }

    // 5. 修改 stopSkill，作为终点站
    @Override
    public void stopSkill() {
        this.setHasSkill(false);
        this.setCharging(false); // 停止冲锋
        this.setAiDisabled(false); // 恢复 AI
    }
    @Override
    public boolean canSkill() {
        if (!ModSkillEntityType.canSkill(this)) return false;
        return !this.getWorld().isClient() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenPlayAndHold("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenPlayAndHold("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 主控制器：负责所有常规状态
        controllers.add(new AnimationController<>("main_controller", 5, this::mainController));
        controllers.add(new AnimationController<>( "attack_controller", animTest -> {
            if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED) {
                if (this.hasSkill()) {
                    ClientPlayNetworking.send(new SkillPayload("stop", this.getId()));
                }
            }
            return PlayState.STOP;
        })
                .triggerableAnim("attack", ATTACK_ANIM)
                .setCustomInstructionKeyframeHandler(s -> {
                    if ("runAttack;".equals(s.keyframeData().getInstructions())) {
                        ClientPlayNetworking.send(new SkillPayload(
                                "attack", this.getId()
                        ));
                    }
                })
        );
    }
    private PlayState mainController(final AnimationTest<LittlePersonSoldierEntity> event) {
        return event.isMoving() ? event.setAndContinue(WALK_ANIM) : event.setAndContinue(IDLE_ANIM);
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
