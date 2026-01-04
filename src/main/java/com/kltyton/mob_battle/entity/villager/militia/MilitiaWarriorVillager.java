package com.kltyton.mob_battle.entity.villager.militia;

import com.kltyton.mob_battle.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
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

import java.util.List;

// 近战村民
public class MilitiaWarriorVillager extends IronGolemEntity{
    public static DefaultAttributeContainer.Builder createVillagerAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.MAX_HEALTH, 30)
                .add(EntityAttributes.ATTACK_DAMAGE, 10);
    }
    // 添加群体仇恨的检测范围（64格）
    private static final double ALERT_RANGE = 64.0;

    public static final TrackedData<BlockPos> HOME_POS = DataTracker.registerData(MilitiaWarriorVillager.class, TrackedDataHandlerRegistry.BLOCK_POS);
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
        if (!this.getWorld().isClient() && this.age % 20 == 0) {
            this.heal(1f);
            if (!getHomePos().equals(new BlockPos(0, -9999, 0)) && (this.getPos().distanceTo(getHomePos().toCenterPos()) >= 128.0) || !this.getWorld().getBlockState(getHomePos()).isOf(ModBlocks.TARGET_BLOCK)) {
                VillagerEntity villager = EntityType.VILLAGER.create(this.getWorld(), SpawnReason.CONVERSION);
                if (villager != null) {
                    villager.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
                    this.getWorld().spawnEntity(villager);
                    this.setHomePos(new BlockPos(0, -9999, 0));
                    this.discard();
                }
            }
        }
    }
    public MilitiaWarriorVillager(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
        this.getNavigation().setCanSwim(true);
        this.setCanPickUpLoot(true);
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

    public static boolean checkWarriorSpawnRules(EntityType<MilitiaWarriorVillager> warriorVillagerEntityType, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getLocalDifficulty(pos).getGlobalDifficulty() != Difficulty.PEACEFUL;
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this)); // 添加游泳AI
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.9, 32.0F));
        this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6, false));
        this.goalSelector.add(4, new IronGolemWanderAroundGoal(this, 0.6));
        this.goalSelector.add(5, new IronGolemLookGoal(this));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackIronGolemTargetGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MobEntity.class, 5, false, false, (entity, world) -> entity instanceof Monster));
        this.targetSelector.add(4, new UniversalAngerGoal<>(this, false));
    }
    @Override
    protected EntityNavigation createNavigation(World world) {
        return new MobNavigation(this, world); // 允许基础游泳
    }
    // 2. 确保实体可以装备这些物品（可选，通常 MobEntity 已处理）

}
