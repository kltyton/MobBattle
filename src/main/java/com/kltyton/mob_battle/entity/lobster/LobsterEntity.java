package com.kltyton.mob_battle.entity.lobster;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.general.GeneralEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class LobsterEntity extends AnimalEntity implements GeneralEntity<LobsterEntity> {
    private static final TrackedData<Integer> VARIANT =
            DataTracker.registerData(LobsterEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> RETREATING =
            DataTracker.registerData(LobsterEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<Boolean> HAS_SKILL =
            DataTracker.registerData(LobsterEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN_1 =
            DataTracker.registerData(LobsterEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_2 =
            DataTracker.registerData(LobsterEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_3 =
            DataTracker.registerData(LobsterEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_4 =
            DataTracker.registerData(LobsterEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_5 =
            DataTracker.registerData(LobsterEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final RawAnimation SWING_ANIM = RawAnimation.begin().thenLoop("swing");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private boolean targetingUnderwater;

    public LobsterEntity(EntityType<? extends LobsterEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new LobsterMoveControl(this);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 0.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, -1.0F);
        this.setHasSkill(false);
    }

    @Override
    public MobEntity getEntity() {
        return this;
    }

    @Override
    public int getSkillCount() {
        return 3;
    }

    @Override
    public TrackedData<Boolean> getHasSkillKey() {
        return HAS_SKILL;
    }

    @Override
    public TrackedData<Integer> getCooldownKey1() {
        return SKILL_COOLDOWN_1;
    }

    @Override
    public TrackedData<Integer> getCooldownKey2() {
        return SKILL_COOLDOWN_2;
    }

    @Override
    public TrackedData<Integer> getCooldownKey3() {
        return SKILL_COOLDOWN_3;
    }

    @Override
    public TrackedData<Integer> getCooldownKey4() {
        return SKILL_COOLDOWN_4;
    }

    @Override
    public TrackedData<Integer> getCooldownKey5() {
        return SKILL_COOLDOWN_5;
    }

    @Override
    public int getMaxSkillCooldown_1() {
        return 0;
    }

    @Override
    public int getMaxSkillCooldown_2() {
        return 0;
    }

    @Override
    public int getMaxSkillCooldown_3() {
        return 0;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(VARIANT, LobsterVariant.RED.getId());
        builder.add(RETREATING, false);
        entityInitDataTracker(builder);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new RetreatToWaterGoal(this, 1.35D));
        this.goalSelector.add(1, new WaterWanderGoal(this, 1.0D));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(3, new TemptGoal(this, 1.0D, stack -> stack.isOf(Items.ROTTEN_FLESH), false));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.7D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, ZombieEntity.class, true, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, DrownedEntity.class, true, false));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, ZombifiedPiglinEntity.class, true, false));
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new AmphibiousSwimNavigation(this, world);
    }

    @Override
    public boolean isPushedByFluids() {
        return !this.isSwimming();
    }

    public boolean isTargetingUnderwater() {
        if (this.targetingUnderwater) {
            return true;
        } else {
            LivingEntity target = this.getTarget();
            return target != null && target.isTouchingWater();
        }
    }

    public void setTargetingUnderwater(boolean targetingUnderwater) {
        this.targetingUnderwater = targetingUnderwater;
    }

    public boolean shouldSinkInWater() {
        return this.isTouchingWater() && this.getTarget() == null && !this.hasSkill() && !this.isRetreating();
    }

    @Override
    public void updateSwimming() {
        if (!this.getWorld().isClient) {
            boolean underwaterActive = this.canActVoluntarily()
                    && this.isSubmergedInWater()
                    && (this.isTargetingUnderwater() || this.shouldSinkInWater());
            this.setSwimming(underwaterActive);
        }
    }

    @Override
    public boolean isInSwimmingPose() {
        return this.isSwimming();
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.isSubmergedInWater() && (this.isTargetingUnderwater() || this.shouldSinkInWater())) {
            this.updateVelocity(0.02F, movementInput);
            this.move(MovementType.SELF, this.getVelocity());

            if (this.shouldSinkInWater()) {
                this.setVelocity(this.getVelocity().multiply(0.85D, 0.9D, 0.85D).add(0.0D, -0.02D, 0.0D));
            } else {
                this.setVelocity(this.getVelocity().multiply(0.9D));
            }
        } else {
            super.travel(movementInput);
        }
    }

    @Override
    public void tick() {
        super.tick();
        entityTick();

        if (!this.getWorld().isClient) {
            if (this.age % 20 == 0 && this.isAlive() && this.getHealth() < this.getMaxHealth()) {
                this.heal(1.0F);
            }

            if (this.isSubmergedInWater()) {
                if (this.getTarget() != null || this.isRetreating()) {
                    this.setTargetingUnderwater(true);
                } else if (this.getNavigation().isIdle()) {
                    this.setTargetingUnderwater(false);
                }
            } else if (!this.isTouchingWater()) {
                this.setTargetingUnderwater(false);
            }
        }
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        return doSkill();
    }

    @Override
    public boolean doSkill() {
        if (!canSkill()) return false;

        String[] skills = {"attack2", "attack3", "attack4"};
        int start = this.random.nextInt(skills.length);

        for (int i = 0; i < skills.length; i++) {
            String skill = skills[(start + i) % skills.length];
            if (canSkill(skill)) {
                performSkill(skill);
                return true;
            }
        }
        return false;
    }

    @Override
    public void runSkill_2(LobsterEntity entity) {
        damageCurrentTarget(10.0F);
    }

    @Override
    public void runSkill_3(LobsterEntity entity) {
        damageCurrentTarget(12.0F);
    }

    @Override
    public void runSkill_4(LobsterEntity entity) {
        damageCurrentTarget(16.0F);
    }

    private boolean damageCurrentTarget(float damage) {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return false;
        if (!(this.getTarget() instanceof LivingEntity target) || !target.isAlive()) return false;
        if (!isTargetInAttackRange(target)) return false;

        boolean hit = target.damage(serverWorld, this.getDamageSources().mobAttack(this), damage);
        if (hit) {
            this.onAttacking(target);
        }
        return hit;
    }

    private boolean isTargetInAttackRange(LivingEntity target) {
        double reach = (this.getWidth() * 2.0F) * (this.getWidth() * 2.0F) + target.getWidth();
        return this.squaredDistanceTo(target) <= reach + 1.0D;
    }

    public LobsterVariant getVariant() {
        return LobsterVariant.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(LobsterVariant variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    public boolean isRetreating() {
        return this.dataTracker.get(RETREATING);
    }

    public void setRetreating(boolean retreating) {
        this.dataTracker.set(RETREATING, retreating);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Items.ROTTEN_FLESH);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity mate) {
        LobsterEntity child = ModEntities.LOBSTER.create(world, SpawnReason.BREEDING);
        if (child == null) return null;

        LobsterVariant biomeVariant = determineBiomeVariant(world, this.getBlockPos(), child.getRandom());
        if (mate instanceof LobsterEntity other && world.random.nextFloat() < 0.5F) {
            child.setVariant(world.random.nextBoolean() ? this.getVariant() : other.getVariant());
        } else {
            child.setVariant(biomeVariant);
        }

        return child;
    }

    @Override
    public @Nullable EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        EntityData data = super.initialize(world, difficulty, spawnReason, entityData);
        this.setVariant(determineBiomeVariant(world.toServerWorld(), this.getBlockPos(), this.random));
        return data;
    }

    private static LobsterVariant determineBiomeVariant(ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        RegistryEntry<Biome> biome = world.getBiome(pos);

        if (biome.isIn(BiomeTags.IS_NETHER)) {
            return LobsterVariant.GOLD;
        }

        if (biome.isIn(BiomeTags.IS_RIVER)
                || biome.matchesKey(BiomeKeys.SWAMP)
                || biome.matchesKey(BiomeKeys.MANGROVE_SWAMP)) {
            return LobsterVariant.GRAY;
        }

        if (biome.matchesKey(BiomeKeys.SNOWY_PLAINS)
                || biome.matchesKey(BiomeKeys.SNOWY_TAIGA)
                || biome.matchesKey(BiomeKeys.ICE_SPIKES)
                || biome.matchesKey(BiomeKeys.SNOWY_SLOPES)
                || biome.matchesKey(BiomeKeys.FROZEN_PEAKS)
                || biome.matchesKey(BiomeKeys.JAGGED_PEAKS)
                || biome.matchesKey(BiomeKeys.GROVE)
                || biome.matchesKey(BiomeKeys.SNOWY_BEACH)
                || biome.matchesKey(BiomeKeys.FROZEN_RIVER)) {
            return LobsterVariant.WHITE;
        }

        return random.nextBoolean() ? LobsterVariant.RED : LobsterVariant.BLUE;
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putInt("Variant", this.getVariant().getId());
        view.putBoolean("Retreating", this.isRetreating());
        view.putBoolean("TargetingUnderwater", this.targetingUnderwater);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.setVariant(LobsterVariant.byId(view.getInt("Variant", LobsterVariant.RED.getId())));
        this.setRetreating(view.getBoolean("Retreating", false));
        this.targetingUnderwater = view.getBoolean("TargetingUnderwater", false);
    }

    @Override
    public PlayState mainController(AnimationTest<?> event) {
        if (this.hasSkill()) {
            return PlayState.CONTINUE;
        }

        if (this.isRetreating()) {
            return event.setAndContinue(SWING_ANIM);
        }

        if (this.isTouchingWater() && this.getTarget() == null) {
            return event.setAndContinue(IDLE_ANIM);
        }

        return event.isMoving()
                ? event.setAndContinue(WALK_ANIM)
                : event.setAndContinue(IDLE_ANIM);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return AnimalEntity.createAnimalAttributes()
                .add(EntityAttributes.MAX_HEALTH, 50.0D)
                .add(EntityAttributes.ARMOR, 6.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.FOLLOW_RANGE, 24.0D)
                .add(EntityAttributes.ATTACK_KNOCKBACK, 0.2D);
    }

    private @Nullable BlockPos findNearbyBroadWater(int horizontalRange, int verticalRange) {
        BlockPos origin = this.getBlockPos();
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;

        for (int x = -horizontalRange; x <= horizontalRange; x++) {
            for (int y = -verticalRange; y <= verticalRange; y++) {
                for (int z = -horizontalRange; z <= horizontalRange; z++) {
                    BlockPos pos = origin.add(x, y, z);

                    if (!this.getWorld().getFluidState(pos).isIn(FluidTags.WATER)) continue;
                    if (!isBroadWater(pos)) continue;

                    double dist = origin.getSquaredDistance(pos);
                    if (dist < bestDistance) {
                        bestDistance = dist;
                        best = pos.toImmutable();
                    }
                }
            }
        }

        return best;
    }

    private boolean isBroadWater(BlockPos center) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos pos = center.add(x, 0, z);

                if (!this.getWorld().getFluidState(pos).isIn(FluidTags.WATER)) {
                    return false;
                }

                BlockPos up = pos.up();
                if (!(this.getWorld().isAir(up) || this.getWorld().getFluidState(up).isIn(FluidTags.WATER))) {
                    return false;
                }
            }
        }

        return true;
    }

    static class LobsterMoveControl extends MoveControl {
        private final LobsterEntity lobster;

        public LobsterMoveControl(LobsterEntity lobster) {
            super(lobster);
            this.lobster = lobster;
        }

        @Override
        public void tick() {
            LivingEntity target = this.lobster.getTarget();

            if (this.lobster.isTargetingUnderwater() && this.lobster.isTouchingWater()) {
                if ((target != null && target.getY() > this.lobster.getY()) || this.lobster.targetingUnderwater) {
                    this.lobster.setVelocity(this.lobster.getVelocity().add(0.0D, 0.002D, 0.0D));
                }

                if (this.state != State.MOVE_TO || this.lobster.getNavigation().isIdle()) {
                    this.lobster.setMovementSpeed(0.02F);
                    return;
                }

                double dx = this.targetX - this.lobster.getX();
                double dy = this.targetY - this.lobster.getY();
                double dz = this.targetZ - this.lobster.getZ();
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (dist < 1.0E-6D) {
                    this.lobster.setMovementSpeed(0.0F);
                    return;
                }

                dy /= dist;
                float yaw = (float)(MathHelper.atan2(dz, dx) * 180.0F / Math.PI) - 90.0F;
                this.lobster.setYaw(this.wrapDegrees(this.lobster.getYaw(), yaw, 90.0F));
                this.lobster.bodyYaw = this.lobster.getYaw();

                float targetSpeed = (float)(this.speed * this.lobster.getAttributeValue(EntityAttributes.MOVEMENT_SPEED));
                float lerpedSpeed = MathHelper.lerp(0.125F, this.lobster.getMovementSpeed(), targetSpeed);
                this.lobster.setMovementSpeed(lerpedSpeed);
                this.lobster.setVelocity(this.lobster.getVelocity().add(
                        lerpedSpeed * dx * 0.005D,
                        lerpedSpeed * dy * 0.08D,
                        lerpedSpeed * dz * 0.005D
                ));
            } else {
                if (this.lobster.shouldSinkInWater()) {
                    this.lobster.setMovementSpeed(0.02F);
                    this.lobster.setVelocity(this.lobster.getVelocity().add(0.0D, -0.01D, 0.0D));
                    return;
                }

                if (!this.lobster.isOnGround()) {
                    this.lobster.setVelocity(this.lobster.getVelocity().add(0.0D, -0.008D, 0.0D));
                }

                super.tick();
            }
        }
    }

    private static class RetreatToWaterGoal extends Goal {
        private final LobsterEntity lobster;
        private final double speed;
        private BlockPos targetWater;
        private LivingEntity lookTarget;

        private RetreatToWaterGoal(LobsterEntity lobster, double speed) {
            this.lobster = lobster;
            this.speed = speed;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (this.lobster.hasSkill()) return false;

            boolean lowHealth = this.lobster.getHealth() < 10.0F;
            boolean idleOnLand = this.lobster.getTarget() == null && !this.lobster.isTouchingWater();

            if (!lowHealth && !idleOnLand) return false;

            this.targetWater = this.lobster.findNearbyBroadWater(16, 6);
            return this.targetWater != null;
        }

        @Override
        public boolean shouldContinue() {
            return this.targetWater != null
                    && !this.lobster.isTouchingWater()
                    && !this.lobster.getNavigation().isIdle();
        }

        @Override
        public void start() {
            this.lookTarget = this.lobster.getTarget();
            this.lobster.setTarget(null);
            this.lobster.setRetreating(true);
            this.lobster.setTargetingUnderwater(true);
            this.lobster.getNavigation().startMovingTo(
                    this.targetWater.getX() + 0.5D,
                    this.targetWater.getY() + 0.5D,
                    this.targetWater.getZ() + 0.5D,
                    this.speed
            );
        }

        @Override
        public void tick() {
            if (this.targetWater == null) return;

            this.lobster.getNavigation().startMovingTo(
                    this.targetWater.getX() + 0.5D,
                    this.targetWater.getY() + 0.5D,
                    this.targetWater.getZ() + 0.5D,
                    this.speed
            );

            if (this.lookTarget != null && this.lookTarget.isAlive()) {
                this.lobster.getLookControl().lookAt(this.lookTarget, 30.0F, 30.0F);
            }
        }

        @Override
        public void stop() {
            this.targetWater = null;
            this.lookTarget = null;
            this.lobster.setRetreating(false);
            this.lobster.setTargetingUnderwater(false);
        }
    }

    private static class WaterWanderGoal extends Goal {
        private final LobsterEntity lobster;
        private final double speed;

        private WaterWanderGoal(LobsterEntity lobster, double speed) {
            this.lobster = lobster;
            this.speed = speed;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return this.lobster.getTarget() == null
                    && !this.lobster.isRetreating()
                    && this.lobster.isTouchingWater()
                    && this.lobster.getRandom().nextInt(40) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return !this.lobster.getNavigation().isIdle()
                    && this.lobster.isTouchingWater()
                    && this.lobster.getTarget() == null
                    && !this.lobster.isRetreating();
        }

        @Override
        public void start() {
            Vec3d vec3d = NoPenaltyTargeting.findTo(
                    this.lobster,
                    6,
                    4,
                    Vec3d.ofCenter(this.lobster.getBlockPos().down(2)),
                    (float) (Math.PI / 2)
            );

            if (vec3d != null) {
                this.lobster.setTargetingUnderwater(true);
                this.lobster.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
            }
        }

        @Override
        public void stop() {
            if (this.lobster.getTarget() == null && !this.lobster.isRetreating()) {
                this.lobster.setTargetingUnderwater(false);
            }
        }
    }
}