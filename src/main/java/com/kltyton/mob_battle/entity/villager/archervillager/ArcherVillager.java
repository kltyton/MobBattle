package com.kltyton.mob_battle.entity.villager.archervillager;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.ai.goal.GeneralProtectionVillagerGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
public class ArcherVillager extends SnowGolem implements NeutralMob, GeoEntity {
    public static AttributeSupplier.Builder createVillagerAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }
    @Nullable
    private UUID angryAt;
    private int angerTime;
    public static final UniformInt ANGER_TIME_RANGE;
    private static final double ALERT_RANGE = 64.0;
    public ArcherVillager(EntityType<? extends SnowGolem> entityType, Level world) {
        super(entityType, world);
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this)); // 添加游泳AI
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new GeneralProtectionVillagerGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.monster.Phantom.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(2, new ResetUniversalAngerTargetGoal<>(this, false));
    }
    @Override
    public boolean isSensitiveToWater() {
        return false;
    }
    @Override
    public boolean readyForShearing() {
        return false;
    }
    @Override
    public boolean hasPumpkin() {
        return false;
    }
    @Override
    public void setPumpkin(boolean hasPumpkin) {
        super.setPumpkin(false);
    }
    @Override
    public InteractionResult mobInteract (Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }
    public static boolean checkSnuffleSpawnRules(EntityType<ArcherVillager> snuffle, LevelAccessor world, EntitySpawnReason spawnType, BlockPos pos, RandomSource random) {
        return world.getCurrentDifficultyAt(pos).getDifficulty() != Difficulty.PEACEFUL;
    }
    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        if (!ModSkillEntityType.canSkill(this)) return;
        Level world = this.level();
        if (!(world instanceof ServerLevel serverWorld)) return;

        this.triggerAnim("attack_controller", "attack");

        // 1. 创建箭实体
        ItemStack arrowStack = new ItemStack(Items.ARROW);
        Arrow arrowEntity = new Arrow(world, this, arrowStack, null);

        // 【重要】设置箭的初始位置为实体的眼睛高度（或略低一点），防止从脚下发出
        // 对于巨型实体，建议根据模型手动指定偏移量
        arrowEntity.setPos(this.getX(), this.getEyeY() - 0.5D, this.getZ());

        // 2. 设置伤害
        arrowEntity.setBaseDamage(this.getAttributeValue(Attributes.ATTACK_DAMAGE));

        // 3. 计算向量
        double dX = target.getX() - this.getX();
        // 【优化】瞄准目标的躯干中心（getBodyY(0.5)），而不是眼睛，防止近距离过高
        double dY = target.getY(0.5D) - arrowEntity.getY();
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
        arrowEntity.shoot(dX, dY + (double)gravityCorrection, dZ, velocity, 0.0F);

        // 6. 发射与音效
        serverWorld.addFreshEntity(arrowEntity);
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }
    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        boolean bl = super.hurtServer(world, source, amount);
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
        List<SnowGolem> golems = this.level().getEntitiesOfClass(
                SnowGolem.class,
                this.getBoundingBox().inflate(ALERT_RANGE),
                golem -> golem != this && golem.isAlive()
        );

        for (SnowGolem golem : golems) {
            // 跳过玩家创建的且攻击者是玩家的铁傀儡
            if (attacker instanceof AbstractGolem) {
                continue;
            }

            // 设置仇恨目标和愤怒时间
            this.setPersistentAngerTarget(attacker.getUUID());
            this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));

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
        return SoundEvents.VILLAGER_DEATH;
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Override
    public @Nullable UUID getPersistentAngerTarget() {
        return this.angryAt;
    }
    @Override
    public void setPersistentAngerTarget(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));
    }
    static {
        ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(20, 39);
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
    protected PathNavigation createNavigation(Level world) {
        return new GroundPathNavigation(this, world); // 允许基础游泳
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
