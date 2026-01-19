package com.kltyton.mob_battle.entity.villager.militia;

import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.entity.bullet.ITrueDamageProjectile;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
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

import java.util.List;
import java.util.UUID;

// 远程村民
public class MilitiaArcherVillager extends SnowGolemEntity implements Angerable {
    public static final TrackedData<BlockPos> HOME_POS = DataTracker.registerData(MilitiaArcherVillager.class, TrackedDataHandlerRegistry.BLOCK_POS);
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
            if (!getHomePos().equals(new BlockPos(0, -9999, 0)) && !this.getWorld().getBlockState(getHomePos()).isOf(ModBlocks.TARGET_BLOCK)) {
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
    @Nullable
    private UUID angryAt;
    private int angerTime;
    public static final UniformIntProvider ANGER_TIME_RANGE;
    private static final double ALERT_RANGE = 64.0;
    public MilitiaArcherVillager(EntityType<? extends SnowGolemEntity> entityType, World world) {
        super(entityType, world);
        this.getNavigation().setCanSwim(true);
        this.setCanPickUpLoot(true);
    }
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this)); // 添加游泳AI
        this.targetSelector.add(2, new RevengeGoal(this));
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
    public static boolean checkSnuffleSpawnRules(EntityType<MilitiaArcherVillager> snuffle, WorldAccess world, SpawnReason spawnType, BlockPos pos, Random random) {
        return world.getLocalDifficulty(pos).getGlobalDifficulty() != Difficulty.PEACEFUL;
    }
    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        double targetX = target.getX() - this.getX();
        double targetY = target.getEyeY() - (double)1.1F;
        double targetZ = target.getZ() - this.getZ();
        double distance = Math.sqrt(targetX * targetX + targetZ * targetZ) * (double)0.2F;
        World world = this.getWorld();

        if (world instanceof ServerWorld serverWorld) {
            // 创建箭实体
            ArrowEntity arrowEntity = new ArrowEntity(world, this, new ItemStack(Items.ARROW), this.getMainHandStack().getItem() == Items.BOW ? this.getMainHandStack() : null);
            ((ITrueDamageProjectile) arrowEntity).setTrueDamage(true, false);
            ItemStack itemStack = this.getWeaponStack();
            float f = (float) this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
            System.out.println("Arrow Damage: " + f);
            f = EnchantmentHelper.getDamage(serverWorld, itemStack, target, this.getDamageSources().arrow(arrowEntity, this), f);
            System.out.println("Arrow Damage: " + f);
            f += itemStack.getItem().getBonusAttackDamage(target, f, this.getDamageSources().arrow(arrowEntity, this));
            System.out.println("Arrow Damage: " + f);
            // 设置箭的伤害
            arrowEntity.setDamage(f);
            arrowEntity.setOwner(this);
            double adjustedY = targetY + distance * 0.5; // 调整箭的发射高度
            arrowEntity.setVelocity(targetX, adjustedY - arrowEntity.getY(), targetZ, 1.6F, 0.1F); // 调整速度和轨迹
            // 发射箭
            serverWorld.spawnEntity(arrowEntity);
        }

        // 播放攻击音效
        this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
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
    public static DefaultAttributeContainer.Builder createVillagerAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.MAX_HEALTH, 30)
                .add(EntityAttributes.ATTACK_DAMAGE, 15);
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
    @Override
    protected EntityNavigation createNavigation(World world) {
        return new MobNavigation(this, world); // 允许基础游泳
    }
}

