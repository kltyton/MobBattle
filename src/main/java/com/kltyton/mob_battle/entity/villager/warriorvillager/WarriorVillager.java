package com.kltyton.mob_battle.entity.villager.warriorvillager;

import com.kltyton.mob_battle.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

// 近战村民
public class WarriorVillager extends IronGolemEntity implements GeoEntity {
    // 添加群体仇恨的检测范围（64格）
    private static final double ALERT_RANGE = 64.0;

    public static final TrackedData<BlockPos> HOME_POS = DataTracker.registerData(WarriorVillager.class, TrackedDataHandlerRegistry.BLOCK_POS);
    public BlockPos getHomePos() {
        return this.dataTracker.get(HOME_POS);
    }
    public void setHomePos(BlockPos pos) {
        this.dataTracker.set(HOME_POS, pos);
    }
    @Override
    public void writeData(WriteView view) {
        super.writeData(view);
        BlockPos homePos = this.getHomePos();
        if (homePos != null) {
            view.put("HomePos", BlockPos.CODEC, homePos);
        }
    }

    @Override
    public void readData(ReadView view) {
        super.readData(view);
        BlockPos homePos = this.getHomePos();
        setHomePos(view.read("HomePos", BlockPos.CODEC).orElse(new BlockPos(0, -9999, 0)));
    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HOME_POS, new BlockPos(0, -9999, 0));
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient() && this.age % 20 == 0
                && !getHomePos().equals(new BlockPos(0, -9999, 0))
                && (this.getPos().distanceTo(getHomePos().toCenterPos()) >= 128.0)
                || !this.getWorld().getBlockState(getHomePos()).isOf(ModBlocks.SCARECROW_BLOCK)){
            VillagerEntity villager = EntityType.VILLAGER.create(this.getWorld(), SpawnReason.CONVERSION);
            if (villager != null) {
                villager.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
                this.getWorld().spawnEntity(villager);
                this.setHomePos(new BlockPos(0, -9999, 0));
                this.discard();
            }
        }
    }
    public WarriorVillager(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
        this.getNavigation().setCanSwim(true);
    }
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        this.attackTicksLeft = 10;
        world.sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
        float f = this.getAttackDamage();
        float g = (int)f > 0 ? f / 2.0F + this.random.nextInt((int)f) : f;
        DamageSource damageSource = this.getDamageSources().mobAttack(this);
        boolean bl = target.damage(world, damageSource, g);
        if (bl) {
            this.triggerAnim("attack_controller", "attack");
            // 获取目标的击退抗性
            double d = target instanceof LivingEntity livingEntity ?
                    livingEntity.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE) : 0.0;
            // 计算实际击退效果（击退抗性会减少击飞力度）
            double e = Math.max(0.0, 1.0 - d);
            // 设置目标的速度，实现击飞效果（y轴方向增加速度）
            target.setVelocity(target.getVelocity().add(0.1 * e, 0.0, 0.1 * e));
            EnchantmentHelper.onTargetDamaged(world, target, damageSource);
        }
        return bl;
    }
    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        boolean bl = super.damage(world, source, amount);
        if (bl && !world.isClient) {
            Entity attacker = source.getAttacker();
            // 确保攻击者是有效生物且不是铁傀儡
            if (attacker instanceof LivingEntity) {
                this.alertOthers((LivingEntity)attacker);
            }
        }
        return bl;
    }
    private void alertOthers(LivingEntity attacker) {
        // 获取64格范围内所有铁傀儡
        List<IronGolemEntity> golems = this.getWorld().getEntitiesByClass(
                IronGolemEntity.class,
                this.getBoundingBox().expand(ALERT_RANGE),
                golem -> golem != this && golem.isAlive()
        );

        for (IronGolemEntity golem : golems) {
            // 跳过玩家创建的且攻击者是玩家的铁傀儡
            if (attacker instanceof GolemEntity) {
                continue;
            }

            // 设置仇恨目标和愤怒时间
            golem.setAngryAt(attacker.getUuid());
            golem.setAngerTime(ANGER_TIME_RANGE.get(this.random));

            // 立即更新目标选择
            if (golem.getTarget() != attacker) {
                golem.setTarget(attacker);
            }
        }
    }
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }

    public static boolean checkWarriorSpawnRules(EntityType<WarriorVillager> warriorVillagerEntityType, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getLocalDifficulty(pos).getGlobalDifficulty() != Difficulty.PEACEFUL;
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
    private PlayState animationController(final AnimationTest<WarriorVillager> state) {
        if (state.isMoving()) {
            // 移动状态时播放行走动画
            return state.setAndContinue(WALK_ANIM);
        } else {
            // 空闲状态时播放待机动画
            return state.setAndContinue(IDEA_ANIM);
        }
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this)); // 添加游泳AI
    }
    @Override
    protected EntityNavigation createNavigation(World world) {
        return new MobNavigation(this, world); // 允许基础游泳
    }

}
