package com.kltyton.mob_battle.entity.littleperson.icesoldier;

import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import com.kltyton.mob_battle.client.animation.gecko.SkillAnimationPlayback;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.OwnedSummon;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 冰刀召唤的无传送小人冰兵。
 *
 * <p>主人目标同步在服务端执行，并复用统一的召唤物队伍判定；普通攻击单独清零目标
 * 的无敌帧，避免影响其他来源的伤害语义。</p>
 */
public final class IceSoldierEntity extends Monster implements LittlePersonEntity, OwnedSummon {
    public static final float BASIC_ATTACK_DAMAGE = 8.0F;
    public static final int MAX_HEALTH = 200;
    public static final int MAX_LIFETIME_TICKS = 800;

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");

    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> SUMMON_OWNER =
            SynchedEntityData.defineId(IceSoldierEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int remainingLifetimeTicks = MAX_LIFETIME_TICKS;

    public IceSoldierEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SUMMON_OWNER, Optional.empty());
    }

    @Override
    public boolean canSkill() {
        return false;
    }

    public static AttributeSupplier.Builder createIceSoldierAttributes() {
        return LittlePersonEntity.createLittlePersonAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, BASIC_ATTACK_DAMAGE)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (--this.remainingLifetimeTicks <= 0) {
                this.discard();
                return;
            }
            updateOwnerCombatTarget();
        }
    }

    private void updateOwnerCombatTarget() {
        LivingEntity owner = this.getSummonOwner();
        if (owner == null || !owner.isAlive()) {
            clearOwnerCombatState();
            return;
        }

        LivingEntity target = owner instanceof Mob mob ? mob.getTarget() : null;
        if (target == null) {
            target = owner.getLastHurtMob();
        }
        if (target == null) {
            target = owner.getLastHurtByMob();
        }

        if (target != null && isValidSummonTarget(target)) {
            this.setTarget(target);
        } else {
            this.setTarget(null);
        }

        if (this.getTarget() == null && this.distanceToSqr(owner) > 16.0D * 16.0D) {
            this.getNavigation().moveTo(owner, 1.1D);
        }
    }

    public void setSummonOwner(@Nullable LivingEntity owner) {
        this.entityData.set(SUMMON_OWNER, Optional.ofNullable(owner).map(EntityReference::of));
        if (owner != null) {
            EntityQueries.joinSameTeam(this, owner);
        } else {
            clearOwnerCombatState();
        }
    }

    @Override
    @Nullable
    public LivingEntity getSummonOwner() {
        EntityReference<LivingEntity> ownerReference = this.entityData.get(SUMMON_OWNER).orElse(null);
        if (ownerReference == null) {
            return null;
        }

        LivingEntity owner = ownerReference.getEntity(this.level(), LivingEntity.class);
        if (owner == null) {
            this.entityData.set(SUMMON_OWNER, Optional.empty());
            clearOwnerCombatState();
            return null;
        }
        if (!this.level().isClientSide()) {
            EntityQueries.joinSameTeam(this, owner);
        }
        return owner;
    }

    public boolean isValidSummonTarget(LivingEntity target) {
        LivingEntity owner = this.getSummonOwner();
        return owner != null && owner.isAlive()
                && EntityQueries.isValidSummonCombatTarget(this, owner, target);
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        if (!(target instanceof LivingEntity livingTarget)
                || !isValidSummonTarget(livingTarget)
                || !livingTarget.isAlive()) {
            return false;
        }

        livingTarget.invulnerableTime = 0;
        boolean hit = livingTarget.hurtServer(level, this.damageSources().mobAttack(this), BASIC_ATTACK_DAMAGE);
        if (hit) {
            livingTarget.invulnerableTime = 0;
            this.setLastHurtMob(livingTarget);
            this.triggerAnim("attack_controller", "attack");
            this.playAttackSound();
        }
        return hit;
    }

    private void clearOwnerCombatState() {
        if (!this.level().isClientSide()) {
            this.setTarget(null);
            this.getNavigation().stop();
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        EntityReference<LivingEntity> owner = this.entityData.get(SUMMON_OWNER).orElse(null);
        if (owner != null) {
            EntityReference.store(owner, output, "SummonOwner");
        }
        output.putInt("RemainingLifetime", this.remainingLifetimeTicks);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        EntityReference<LivingEntity> owner =
                EntityReference.readWithOldOwnerConversion(input, "SummonOwner", this.level());
        this.entityData.set(SUMMON_OWNER, Optional.ofNullable(owner));
        this.remainingLifetimeTicks = Math.max(0,
                Math.min(MAX_LIFETIME_TICKS, input.getIntOr("RemainingLifetime", MAX_LIFETIME_TICKS)));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", state -> {
            if (state.isMoving()) {
                return state.setAndContinue(WALK_ANIM);
            }
            return state.setAndContinue(IDLE_ANIM);
        }));
        controllers.add(new AnimationController<>("attack_controller", SkillAnimationPlayback::playTriggeredAnimationOrStop)
                .receiveTriggeredAnimations()
                .triggerableAnim("attack", ATTACK_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
