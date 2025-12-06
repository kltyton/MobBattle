package com.kltyton.mob_battle.entity.littleperson.militia;

import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class LittlePersonMilitiaEntity extends HostileEntity implements LittlePersonEntity {

    public LittlePersonMilitiaEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false)); // 添加僵尸攻击目标
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0)); // 添加远距离游荡目标
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F)); // 添加看向玩家的目标
        this.goalSelector.add(8, new LookAroundGoal(this)); // 添加环顾四周的目标
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true)); // 添加主动攻击玩家目标
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MobEntity.class, 5, false, false, (entity, world) -> entity instanceof Monster && !(entity instanceof LittlePersonEntity)));
    }
    public static DefaultAttributeContainer.Builder createLittlePersonMilitiaAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 100.0)
                .add(EntityAttributes.FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.ATTACK_DAMAGE, 20.0);
    }
    public void heal() {
        this.heal(1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (this.age % 20 == 0) this.heal();
        }
    }
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        this.triggerAnim("attack_controller", "attack");
        return super.tryAttack(world, target);
    }
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
                amount <= 35.0F) {

            // 20%概率免疫伤害
            if (this.random.nextInt(100) < 20) {
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK.value(), 1.0F, 1.0F);
                this.triggerAnim("attack_controller", "block");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (blockAttack(source, amount)) return false;
        return super.damage(world, source, amount);
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation BLOCK_ANIM = RawAnimation.begin().thenPlay("block");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 主控制器：负责所有常规状态
        controllers.add(new AnimationController<>("main_controller", 5, this::mainController));
        controllers.add(new AnimationController<>( "attack_controller",animTest -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK_ANIM)
                .triggerableAnim("block", BLOCK_ANIM));
    }
    private PlayState mainController(final AnimationTest<LittlePersonMilitiaEntity> event) {
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
