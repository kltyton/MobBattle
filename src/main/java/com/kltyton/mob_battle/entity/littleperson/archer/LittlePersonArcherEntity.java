package com.kltyton.mob_battle.entity.littleperson.archer;

import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntity;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

public class LittlePersonArcherEntity extends HostileEntity implements LittlePersonEntity, RangedAttackMob {

    public LittlePersonArcherEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0D, 20, 6.0F));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0)); // 添加远距离游荡目标
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F)); // 添加看向玩家的目标
        this.goalSelector.add(8, new LookAroundGoal(this)); // 添加环顾四周的目标
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true)); // 添加主动攻击玩家目标
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MobEntity.class, 5, false, false, (entity, world) -> entity instanceof Monster && !(entity instanceof LittlePersonEntity)));
    }
    public static DefaultAttributeContainer.Builder createLittlePersonArcherAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 100.0)
                .add(EntityAttributes.FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.ATTACK_DAMAGE, 30.0);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (this.age % 20 == 0) this.heal(1.0F);
        }
    }
    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
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
                return false;
            }
        }

        // 如果不满足条件或者未触发免疫，则正常处理伤害
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

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        double targetX = target.getX() - this.getX();
        double targetY = target.getEyeY() - this.getEyeY(); // 更精确的高度计算
        double targetZ = target.getZ() - this.getZ();
        double distance = Math.sqrt(targetX * targetX + targetZ * targetZ);

        // 预测目标移动（提高准确性）
        targetY += target.getVelocity().getY() * distance * 0.25; // 重力补偿

        World world = this.getWorld();

        if (world instanceof ServerWorld serverWorld) {
            this.triggerAnim("attack_controller", "attack");
            // 创建箭实体
            LittleArrowEntity arrowEntity = new LittleArrowEntity(world, this, new ItemStack(Items.ARROW), this.getMainHandStack().getItem() == Items.BOW ? this.getMainHandStack() : null);
            // 设置箭的伤害
            arrowEntity.setDamage(this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE));
            arrowEntity.setOwner(this);

            // 减小散布参数以提高精度（从0.1F改为0.01F）
            arrowEntity.setVelocity(targetX, targetY, targetZ, 1.6F, 0.01F);
            arrowEntity.setTrueDamage(true);
            // 发射箭
            serverWorld.spawnEntity(arrowEntity);
        }

        // 播放攻击音效
        this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

}
