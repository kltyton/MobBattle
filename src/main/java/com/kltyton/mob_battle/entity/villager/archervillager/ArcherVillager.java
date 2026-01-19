package com.kltyton.mob_battle.entity.villager.archervillager;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.UniversalAngerGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

// 远程村民
public class ArcherVillager extends SnowGolemEntity implements Angerable, GeoEntity {
    public static DefaultAttributeContainer.Builder createVillagerAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.FOLLOW_RANGE, 64.0);
    }
    @Nullable
    private UUID angryAt;
    private int angerTime;
    public static final UniformIntProvider ANGER_TIME_RANGE;
    private static final double ALERT_RANGE = 64.0;
    public ArcherVillager(EntityType<? extends SnowGolemEntity> entityType, World world) {
        super(entityType, world);
    }
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this)); // 添加游泳AI
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, net.minecraft.entity.mob.PhantomEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(2, new UniversalAngerGoal<>(this, false));
    }
    @Override
    public boolean hurtByWater() {
        return false;
    }
    @Override
    public boolean isShearable() {
        return false;
    }
    @Override
    public boolean hasPumpkin() {
        return false;
    }
    @Override
    public void setHasPumpkin(boolean hasPumpkin) {
        super.setHasPumpkin(false);
    }
    @Override
    public ActionResult interactMob (PlayerEntity player, Hand hand) {
        return ActionResult.PASS;
    }
    public static boolean checkSnuffleSpawnRules(EntityType<ArcherVillager> snuffle, WorldAccess world, SpawnReason spawnType, BlockPos pos, Random random) {
        return world.getLocalDifficulty(pos).getGlobalDifficulty() != Difficulty.PEACEFUL;
    }
    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        World world = this.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) return;

        this.triggerAnim("attack_controller", "attack");

        // 1. 创建箭实体
        ItemStack arrowStack = new ItemStack(Items.ARROW);
        ArrowEntity arrowEntity = new ArrowEntity(world, this, arrowStack, null);

        // 【重要】设置箭的初始位置为实体的眼睛高度（或略低一点），防止从脚下发出
        // 对于巨型实体，建议根据模型手动指定偏移量
        arrowEntity.setPosition(this.getX(), this.getEyeY() - 0.5D, this.getZ());

        // 2. 设置伤害
        arrowEntity.setDamage(this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE));

        // 3. 计算向量
        double dX = target.getX() - this.getX();
        // 【优化】瞄准目标的躯干中心（getBodyY(0.5)），而不是眼睛，防止近距离过高
        double dY = target.getBodyY(0.5D) - arrowEntity.getY();
        double dZ = target.getZ() - this.getZ();
        double distance = Math.sqrt(dX * dX + dZ * dZ);

        // 4. 动态调整速度
        float velocity = 2.5F;

        // 【核心修复】重力补偿算法
        // 1.6F 速度下默认补偿通常是 distance * 0.2
        // 速度越快，下坠越少。公式大致为：基础补偿 * (1.6 / 当前速度)^2
        // 这里我们简单化：因为你速度是 2.5，系数应该显著缩小，建议尝试 0.05F - 0.08F
        float gravityCorrection = (float)distance * 0.06F;

        // 5. 设置速度
        arrowEntity.setVelocity(dX, dY + (double)gravityCorrection, dZ, velocity, 0.0F);

        // 6. 发射与音效
        serverWorld.spawnEntity(arrowEntity);
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }
    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        boolean bl = super.damage(world, source, amount);
/*        if (bl && !world.isClient) {
            Entity attacker = source.getAttacker();
            if (attacker instanceof LivingEntity) {
                this.alertOthers((LivingEntity)attacker);
            }
        }*/
        return bl;
    }
    private void alertOthers(LivingEntity attacker) {
        // 获取64格范围内所有铁傀儡
        List<SnowGolemEntity> golems = this.getWorld().getEntitiesByClass(
                SnowGolemEntity.class,
                this.getBoundingBox().expand(ALERT_RANGE),
                golem -> golem != this && golem.isAlive()
        );

        for (SnowGolemEntity golem : golems) {
            // 跳过玩家创建的且攻击者是玩家的铁傀儡
            if (attacker instanceof GolemEntity) {
                continue;
            }

            // 设置仇恨目标和愤怒时间
            this.setAngryAt(attacker.getUuid());
            this.setAngerTime(ANGER_TIME_RANGE.get(this.random));

            // 立即更新目标选择
            if (golem.getTarget() != attacker) {
                golem.setTarget(attacker);
            }
        }
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    @Override
    public int getAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Override
    public @Nullable UUID getAngryAt() {
        return this.angryAt;
    }
    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }
    static {
        ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>("main_controller", 5 ,this::animationController));
        controllerRegistrar.add(new AnimationController<>( "attack_controller",animTest -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK_ANIM));
    }
    private PlayState animationController(final AnimationTest<ArcherVillager> state) {
        if (state.isMoving()) {
            // 移动状态时播放行走动画
            return state.setAndContinue(WALK_ANIM);
        } else {
            // 空闲状态时播放待机动画
            return state.setAndContinue(IDEA_ANIM);
        }
    }
    @Override
    protected EntityNavigation createNavigation(World world) {
        return new MobNavigation(this, world); // 允许基础游泳
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
