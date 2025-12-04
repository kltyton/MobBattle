package com.kltyton.mob_battle.entity.drone;

import com.kltyton.mob_battle.entity.drone.goal.FlyFollowOwnerGoal;
import com.kltyton.mob_battle.entity.drone.goal.FlyWanderAroundFarGoal;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class DroneEntity extends TameableEntity implements RangedAttackMob, Flutterer, GeoEntity {
    public DroneEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.setNoGravity(true);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_OTHER, -10.0F);
        this.setPathfindingPenalty(PathNodeType.WALKABLE, -10.0F); // 讨厌可站立方块
    }

    public static DefaultAttributeContainer.Builder createDroneAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 500.0D)
                .add(EntityAttributes.FLYING_SPEED, 0.6D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.ATTACK_DAMAGE, 0.1D)
                .add(EntityAttributes.FOLLOW_RANGE, 5.0D);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation navigation = new BirdNavigation(this, world);
        navigation.setCanSwim(false);
        navigation.setCanOpenDoors(true);
        return navigation;
    }
    private int ownerMissingTicks = 0;
    private static final int MAX_OWNER_MISSING_TICKS = 200; // 30秒（20tick/s × 30 = 600）
    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            LivingEntity owner = this.getOwner();
            if (this.age % 20 == 0) this.heal(10.0F);
            if (owner == null || owner.isRemoved() || !owner.isAlive()) {
                this.ownerMissingTicks++;
                if (this.ownerMissingTicks >= MAX_OWNER_MISSING_TICKS) {
                    this.discard();
                }
            } else {
                this.ownerMissingTicks = 0;
            }
        }
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));

        this.goalSelector.add(1, new SitGoal(this));
        this.goalSelector.add(2, new FlyFollowOwnerGoal(this, 1.5D, 5.0F, 1.0F));
        this.goalSelector.add(4, new FlyWanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 5.0F));
    }
    @Override
    public void tryTeleportToOwner() {
        LivingEntity livingEntity = this.getOwner();
        if (livingEntity != null) {
            this.tryTeleportNear(livingEntity.getBlockPos());
        }
    }
    private void tryTeleportNear(BlockPos pos) {
        for (int i = 0; i < 10; i++) {
            int j = this.random.nextBetween(-3, 3);
            int k = this.random.nextBetween(-3, 3);
            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = this.random.nextBetween(-1, 1);
                if (this.tryTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k)) {
                    return;
                }
            }
        }
    }
    private boolean tryTeleportTo(int x, int y, int z) {
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.refreshPositionAndAngles(x + 0.5, y + 2, z + 0.5, this.getYaw(), this.getPitch());
            this.navigation.stop();
            return true;
        }
    }
    private boolean canTeleportTo(BlockPos pos) {
        PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(this, pos);
        if (pathNodeType != PathNodeType.WALKABLE) {
            return false;
        } else {
            BlockState blockState = this.getWorld().getBlockState(pos.down());
            if (!this.canTeleportOntoLeaves() && blockState.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockPos = pos.subtract(this.getBlockPos());
                return this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset(blockPos));
            }
        }
    }
    @Override
    public void tickMovement() {
        super.tickMovement();
        this.setNoGravity(true); // 强制无重力
    }
    @Override
    public boolean shouldTryTeleportToOwner() {
        LivingEntity livingEntity = this.getOwner();
        return livingEntity != null && this.squaredDistanceTo(this.getOwner()) >= 64.0;
    }
    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

/*    // 5. 交互与驯服逻辑
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!this.isTamed() && itemStack.isOf(Items.IRON_INGOT)) {
            if (!this.getWorld().isClient) {
                this.setTamedBy(player);
                this.getWorld().sendEntityStatus(this, (byte)7); // 播放爱心粒子
            }
            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }*/

    @Override
    public boolean isInAir() {
        return !this.isOnGround();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_ANVIL_LAND; // 金属撞击声
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
    }
    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 5, this::animationController));
        controllers.add(new AnimationController<>( "attack_controller",animTest -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK_ANIM));
    }
    protected PlayState animationController(final AnimationTest<DroneEntity> state) {
        return state.setAndContinue(IDEA_ANIM);
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}