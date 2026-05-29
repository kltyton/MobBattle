package com.kltyton.mob_battle.entity.drone;

import com.kltyton.mob_battle.entity.drone.goal.FlyFollowOwnerGoal;
import com.kltyton.mob_battle.entity.drone.goal.FlyWanderAroundFarGoal;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.utils.ArmorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class DroneEntity extends TamableAnimal implements RangedAttackMob, FlyingAnimal, GeoEntity {
    public DroneEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoGravity(true);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(PathType.DANGER_OTHER, -10.0F);
        this.setPathfindingMalus(PathType.WALKABLE, -10.0F); // 讨厌可站立方块
    }

    public static AttributeSupplier.Builder createDroneAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 250.0D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 0.1D)
                .add(Attributes.FOLLOW_RANGE, 5.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, world);
        navigation.setCanFloat(false);
        navigation.setCanOpenDoors(true);
        return navigation;
    }
    private int ownerMissingTicks = 0;
    private static final int MAX_OWNER_MISSING_TICKS = 200; // 30秒（20tick/s × 30 = 600）
    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            LivingEntity owner = this.getOwner();
            if (owner instanceof ServerPlayer player && (!DroneManager.isPlayersDrone(this, player) || !ArmorUtil.hasFullArmor(owner, ModMaterial.IRON_GOLD_INSTANCE))) {
                this.discard();
            }
            if (this.tickCount % 20 == 0) this.heal(10.0F);
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
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FlyFollowOwnerGoal(this, 1.5D, 5.0F, 1.0F));
        this.goalSelector.addGoal(4, new FlyWanderAroundFarGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 5.0F));
    }
    @Override
    public void tryToTeleportToOwner() {
        LivingEntity livingEntity = this.getOwner();
        if (livingEntity != null) {
            this.teleportToAroundBlockPos(livingEntity.blockPosition());
        }
    }
    private void teleportToAroundBlockPos(BlockPos pos) {
        for (int i = 0; i < 10; i++) {
            int j = this.random.nextIntBetweenInclusive(-3, 3);
            int k = this.random.nextIntBetweenInclusive(-3, 3);
            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = this.random.nextIntBetweenInclusive(-1, 1);
                if (this.maybeTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k)) {
                    return;
                }
            }
        }
    }
    private boolean maybeTeleportTo(int x, int y, int z) {
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.snapTo(x + 0.5, y + 2, z + 0.5, this.getYRot(), this.getXRot());
            this.navigation.stop();
            return true;
        }
    }
    private boolean canTeleportTo(BlockPos pos) {
        PathType pathNodeType = WalkNodeEvaluator.getPathTypeStatic(this, pos);
        if (pathNodeType != PathType.WALKABLE) {
            return false;
        } else {
            BlockState blockState = this.level().getBlockState(pos.below());
            if (!this.canFlyToOwner() && blockState.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockPos = pos.subtract(this.blockPosition());
                return this.level().noCollision(this, this.getBoundingBox().move(blockPos));
            }
        }
    }
    @Override
    public void aiStep() {
        super.aiStep();
        this.setNoGravity(true); // 强制无重力
    }
    @Override
    public boolean shouldTryTeleportToOwner() {
        LivingEntity livingEntity = this.getOwner();
        return livingEntity != null && this.distanceToSqr(this.getOwner()) >= 64.0;
    }
    @Override
    public boolean isFood(ItemStack stack) {
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
    public boolean isFlying() {
        return !this.onGround();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ANVIL_LAND; // 金属撞击声
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }
    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
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
