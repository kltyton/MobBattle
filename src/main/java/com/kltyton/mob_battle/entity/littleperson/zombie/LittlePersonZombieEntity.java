package com.kltyton.mob_battle.entity.littleperson.zombie;

import com.kltyton.mob_battle.client.animation.gecko.SkillAnimationPlayback;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.entity.littleperson.zombie.ai.ZombieLittlePersonTargets;
import com.kltyton.mob_battle.entity.registry.LittlePersonZombieEntityTypes;
import com.kltyton.mob_battle.entity.silverfish.silverfish.GreenConcreteProjectileEntity;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * 基础、爪牙、盾牌、喷射、分裂体和感染民兵的小人僵尸。
 * 特殊攻击由服务端有限 tick 状态机结算，客户端动画不能重复触发伤害。
 */
public final class LittlePersonZombieEntity extends LittlePersonMilitiaEntity
        implements ZombieLittlePerson, RangedAttackMob {
    /** 同一行为骨架下的固定实体类型配置；变种不是可由客户端修改的实体数据。 */
    public enum Kind {
        BASIC(20, 2, 0), CLAW(30, 7, 0), SHIELD(50, 2, 0), SPRAYER(20, 5, 0),
        HEADLESS(20, 5, 0), HEAD(10, 5, 0), ARCHER(15, 2, 1), SOLDIER(20, 3, 1);

        private final double health;
        private final double damage;
        private final float healing;

        Kind(double health, double damage, float healing) {
            this.health = health;
            this.damage = damage;
            this.healing = healing;
        }

        public boolean ranged() {
            return this == SPRAYER || this == HEAD;
        }
    }

    private static final EntityDataAccessor<Boolean> SPECIAL_ACTIVE = SynchedEntityData.defineId(
            LittlePersonZombieEntity.class, EntityDataSerializers.BOOLEAN);
    private final Kind kind;
    private int specialCooldown;
    private int specialTicks;
    private boolean splitComplete;
    private boolean infectedCivilian;

    public LittlePersonZombieEntity(EntityType<? extends Monster> type, Level level, Kind kind) {
        super(type, level);
        this.kind = kind;
        if (kind.ranged()) {
            this.goalSelector.removeAllGoals(goal -> goal instanceof MeleeAttackGoal);
            this.goalSelector.addGoal(2, new RangedBowAttackGoal<>(this, 1.0D, 30, 16.0F));
            // 复用原版骷髅走位 Goal 的持弓准入；渲染器不绘制该 AI 用物品，也不会掉落。
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        }
    }

    public static AttributeSupplier.Builder attributes(Kind kind) {
        return LittlePersonEntity.createLittlePersonAttributes()
                .add(Attributes.MAX_HEALTH, kind.health)
                .add(Attributes.ATTACK_DAMAGE, kind.damage)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.0D);
    }

    public Kind kind() {
        return this.kind;
    }

    /** 感染平民沿用基础型身份，但获得感染职业每秒 1 点的恢复能力。 */
    public void markCivilianInfection() {
        this.infectedCivilian = this.kind == Kind.BASIC;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        ZombieLittlePersonTargets.install(this, this.targetSelector);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPECIAL_ACTIVE, false);
    }

    @Override
    public void heal() {
        if (this.infectedCivilian) {
            this.heal(1.0F);
        } else if (this.kind != null && this.kind.healing > 0.0F) {
            this.heal(this.kind.healing);
        }
    }

    @Override
    public boolean hasSkill() {
        return this.entityData.get(SPECIAL_ACTIVE);
    }

    @Override
    public void setHasSkill(boolean active) {
        this.entityData.set(SPECIAL_ACTIVE, active);
        if (!active) {
            this.specialTicks = 0;
        }
    }

    @Override
    public boolean handleSkillPayload(String command) {
        return false;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        if (this.hasSkill() || this.kind.ranged()) {
            return false;
        }
        if (this.kind == Kind.CLAW && this.specialCooldown == 0
                && target instanceof LivingEntity living && isValidSummonTarget(living) && canSkill()) {
            this.setTarget(living);
            startSpecialAttack();
            return true;
        }
        return super.doHurtTarget(level, target);
    }

    private void startSpecialAttack() {
        this.specialCooldown = this.kind == Kind.CLAW ? 80 : 60;
        this.specialTicks = 16;
        this.setHasSkill(true);
        this.getNavigation().stop();
        this.triggerAnim("zombie_special", "attack2");
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level) || !this.isAlive()) {
            return;
        }
        if (this.specialCooldown > 0) {
            this.specialCooldown--;
        }
        LivingEntity target = this.getTarget();
        if (this.specialTicks > 0) {
            this.getNavigation().stop();
            this.specialTicks--;
            if (this.specialTicks == 8 && canSkill() && target != null && isValidSummonTarget(target)
                    && this.distanceToSqr(target) <= (this.kind == Kind.CLAW ? 4.0D : 1.0D)) {
                target.hurtServer(level, this.damageSources().mobAttack(this), this.kind == Kind.CLAW ? 14.0F : 10.0F);
            }
            if (this.specialTicks == 0) {
                this.setHasSkill(false);
            }
        } else if (this.kind == Kind.SPRAYER && !this.isNoAi() && canSkill() && this.specialCooldown == 0
                && target != null && isValidSummonTarget(target) && this.distanceToSqr(target) <= 1.0D) {
            startSpecialAttack();
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        if (!this.kind.ranged() || this.hasSkill() || !canSkill() || !isValidSummonTarget(target)
                || !(this.level() instanceof ServerLevel level)) {
            return;
        }
        GreenConcreteProjectileEntity projectile = new GreenConcreteProjectileEntity(level, this);
        Vec3 direction = target.getBoundingBox().getCenter().subtract(this.getEyePosition()).normalize();
        projectile.setPos(this.getEyePosition());
        projectile.setBaseDamage(5.0D);
        projectile.setTrueDamage(true, false);
        projectile.setNoGravity(true);
        projectile.shoot(direction.x, direction.y, direction.z, 1.0F, 0.0F);
        level.addFreshEntity(projectile);
        this.triggerAnim("attack_controller", "attack");
        this.playSound(SoundEvents.SLIME_SQUISH_SMALL, 0.8F, 0.7F);
    }

    @Override
    public int blockProbability() {
        return this.kind == Kind.SHIELD ? 20 : 0;
    }

    @Override
    public float maxBlockDamage() {
        return this.kind == Kind.SHIELD ? 20.0F : 0.0F;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (this.dead && this.kind == Kind.SPRAYER && !this.splitComplete
                && this.level() instanceof ServerLevel level) {
            this.splitComplete = true;
            spawnSplit(level, LittlePersonZombieEntityTypes.HEADLESS, -0.25D, 0.0D);
            spawnSplit(level, LittlePersonZombieEntityTypes.HEAD, 0.25D, 0.6D);
        }
    }

    private void spawnSplit(ServerLevel level, EntityType<LittlePersonZombieEntity> type, double xOffset, double yOffset) {
        LittlePersonZombieEntity child = type.create(level, EntitySpawnReason.TRIGGERED);
        if (child == null) {
            return;
        }
        child.snapTo(this.getX() + xOffset, this.getY() + yOffset, this.getZ(), this.getYRot(), 0.0F);
        EntityQueries.joinSameTeam(child, this);
        if (this.getSummonOwner() != null) {
            child.setSummonOwner(this.getSummonOwner());
        }
        level.addFreshEntity(child);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("ZombieSpecialCooldown", this.specialCooldown);
        output.putBoolean("ZombieSplitComplete", this.splitComplete);
        output.putBoolean("ZombieInfectedCivilian", this.infectedCivilian);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.specialCooldown = Math.max(0, input.getIntOr("ZombieSpecialCooldown", 0));
        this.splitComplete = input.getBooleanOr("ZombieSplitComplete", false);
        this.infectedCivilian = this.kind == Kind.BASIC && input.getBooleanOr("ZombieInfectedCivilian", false);
        this.setHasSkill(false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>("zombie_special", SkillAnimationPlayback::playTriggeredAnimationOrStop)
                .receiveTriggeredAnimations()
                .triggerableAnim("attack2", RawAnimation.begin().thenPlay("attack2")));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }
}
