package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SkillVisualEntity extends Entity implements GeoEntity {
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(SkillVisualEntity.class, TrackedDataHandlerRegistry.INTEGER);
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

    public SkillVisualEntity(EntityType<? extends SkillVisualEntity> entityType, World world) {
        super(entityType, world);
    }

    public SkillVisualEntity configure(LivingEntity owner, float damage, int damageAge, int maxAge, double radius, int variant) {
        this.owner = owner;
        this.damage = damage;
        this.damageAge = damageAge;
        this.maxAge = maxAge;
        this.radius = radius;
        this.dataTracker.set(VARIANT, variant);
        return this;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(VARIANT, 0);
    }

    @Override
    protected void readCustomData(ReadView view) {
    }

    @Override
    protected void writeCustomData(WriteView view) {
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            if (this.damageAge >= 0 && this.age == this.damageAge) {
                damageNearby();
            }
            if (this.age > this.maxAge) {
                this.discard();
            }
        }
    }

    private void damageNearby() {
        if (!(this.getWorld() instanceof ServerWorld world) || this.owner == null || this.damage <= 0.0F) {
            return;
        }
        Box box = this.getBoundingBox().expand(this.radius);
        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, box,
                living -> EntityUtil.isValidSummonCombatTarget(this, this.owner, living))) {
            target.timeUntilRegen = 0;
            target.damage(world, this.owner.getDamageSources().mobAttack(this.owner), this.damage);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 5, state -> {
            int variant = this.dataTracker.get(VARIANT);
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
