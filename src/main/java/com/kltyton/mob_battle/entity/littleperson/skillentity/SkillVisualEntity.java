package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SkillVisualEntity extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(SkillVisualEntity.class, EntityDataSerializers.INT);
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlayAndHold("attack");
    private static final RawAnimation ATTACK_1_ANIM = RawAnimation.begin().thenPlayAndHold("attack1");
    private static final RawAnimation ATTACK_2_ANIM = RawAnimation.begin().thenPlayAndHold("attack2");
    private static final RawAnimation ATTACK_3_ANIM = RawAnimation.begin().thenPlayAndHold("attack3");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity owner;
    private float damage;
    private int damageAge = -1;
    private int maxAge = 30;
    private double radius = 1.0D;

    public SkillVisualEntity(EntityType<? extends SkillVisualEntity> entityType, Level world) {
        super(entityType, world);
    }

    public SkillVisualEntity configure(LivingEntity owner, float damage, int damageAge, int maxAge, double radius, int variant) {
        this.owner = owner;
        this.damage = damage;
        this.damageAge = damageAge;
        this.maxAge = maxAge;
        this.radius = radius;
        this.entityData.set(VARIANT, variant);
        return this;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(VARIANT, 0);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (this.damageAge >= 0 && this.tickCount == this.damageAge) {
                damageNearby();
            }
            if (this.tickCount > this.maxAge) {
                this.discard();
            }
        }
    }

    private void damageNearby() {
        if (!(this.level() instanceof ServerLevel world) || this.owner == null || this.damage <= 0.0F) {
            return;
        }
        AABB box = this.getBoundingBox().inflate(this.radius);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, box,
                living -> EntityUtil.isValidSummonCombatTarget(this, this.owner, living))) {
            target.hurtServer(world, this.owner.damageSources().mobAttack(this.owner), this.damage);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 5, state -> {
            int variant = this.entityData.get(VARIANT);
            if (variant == 1) {
                return state.setAndContinue(ATTACK_1_ANIM);
            }
            if (variant == 2) {
                return state.setAndContinue(ATTACK_2_ANIM);
            }
            if (variant == 3) {
                return state.setAndContinue(ATTACK_3_ANIM);
            }
            return state.setAndContinue(ATTACK_ANIM);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
