package com.kltyton.mob_battle.entity.lobster;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.general.GeneralEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class LobsterEntity extends Animal implements GeneralEntity<LobsterEntity> {
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(LobsterEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> RETREATING =
            SynchedEntityData.defineId(LobsterEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PANIC_RETREATING =
            SynchedEntityData.defineId(LobsterEntity.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<Boolean> HAS_SKILL =
            SynchedEntityData.defineId(LobsterEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_1 =
            SynchedEntityData.defineId(LobsterEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_2 =
            SynchedEntityData.defineId(LobsterEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_3 =
            SynchedEntityData.defineId(LobsterEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_4 =
            SynchedEntityData.defineId(LobsterEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_5 =
            SynchedEntityData.defineId(LobsterEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation SWING_ANIM = RawAnimation.begin().thenLoop("swing");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private boolean targetingUnderwater;

    public LobsterEntity(EntityType<? extends LobsterEntity> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new LobsterMoveControl(this);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
        this.setPathfindingMalus(PathType.LAVA, -1.0F);
        this.setHasSkill(false);
    }
    public float getPanicRetreatHealthThreshold() {
        return 10.0F;
    }
    @Override
    public Mob getEntity() {
        return this;
    }

    @Override
    public int getSkillCount() {
        return 3;
    }

    @Override
    public EntityDataAccessor<Boolean> getHasSkillKey() {
        return HAS_SKILL;
    }

    @Override
    public EntityDataAccessor<Integer> getCooldownKey1() {
        return SKILL_COOLDOWN_1;
    }

    @Override
    public EntityDataAccessor<Integer> getCooldownKey2() {
        return SKILL_COOLDOWN_2;
    }

    @Override
    public EntityDataAccessor<Integer> getCooldownKey3() {
        return SKILL_COOLDOWN_3;
    }

    @Override
    public EntityDataAccessor<Integer> getCooldownKey4() {
        return SKILL_COOLDOWN_4;
    }

    @Override
    public EntityDataAccessor<Integer> getCooldownKey5() {
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, LobsterVariant.RED.getId());
        builder.define(RETREATING, false);
        builder.define(PANIC_RETREATING, false);
        entityInitDataTracker(builder);
    }
    public boolean isPanicRetreating() {
        return this.entityData.get(PANIC_RETREATING);
    }

    public void setPanicRetreating(boolean panicRetreating) {
        this.entityData.set(PANIC_RETREATING, panicRetreating);
    }
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new RetreatToWaterGoal(this, 1.35D));
        this.goalSelector.addGoal(1, new WaterWanderGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, stack -> stack.is(Items.ROTTEN_FLESH), false));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Zombie.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Drowned.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new AmphibiousPathNavigation(this, world);
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.isSwimming();
    }

    public boolean isTargetingUnderwater() {
        if (this.targetingUnderwater) {
            return true;
        } else {
            LivingEntity target = this.getTarget();
            return target != null && target.isInWater();
        }
    }

    public void setTargetingUnderwater(boolean targetingUnderwater) {
        this.targetingUnderwater = targetingUnderwater;
    }

    public boolean shouldSinkInWater() {
        return this.isInWater() && this.getTarget() == null && !this.hasSkill() && !this.isRetreating();
    }

    @Override
    public void updateSwimming() {
        if (!this.level().isClientSide) {
            boolean underwaterActive = this.isEffectiveAi()
                    && this.isUnderWater()
                    && (this.isTargetingUnderwater() || this.shouldSinkInWater());
            this.setSwimming(underwaterActive);
        }
    }

    @Override
    public boolean isVisuallySwimming() {
        return this.isSwimming();
    }

    @Override
    public void travel(Vec3 movementInput) {
        if (this.isUnderWater() && (this.isTargetingUnderwater() || this.shouldSinkInWater())) {
            this.moveRelative(0.02F, movementInput);
            this.move(MoverType.SELF, this.getDeltaMovement());

            if (this.shouldSinkInWater()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.85D, 0.9D, 0.85D).add(0.0D, -0.02D, 0.0D));
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            }
        } else {
            super.travel(movementInput);
        }
    }

    @Override
    public void tick() {
        super.tick();
        entityTick();

        if (!this.level().isClientSide) {
            if (this.tickCount % 20 == 0 && this.isAlive() && this.getHealth() < this.getMaxHealth()) {
                this.heal(1.0F);
            }

            if (this.isUnderWater()) {
                if (this.getTarget() != null || this.isRetreating()) {
                    this.setTargetingUnderwater(true);
                } else if (this.getNavigation().isDone()) {
                    this.setTargetingUnderwater(false);
                }
            } else if (!this.isInWater()) {
                this.setTargetingUnderwater(false);
            }
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (this.isPanicRetreating()) {
            return false;
        }
        return doSkill();
    }

    @Override
    public boolean doSkill() {
        if (this.isPanicRetreating()) return false;
        if (!canSkill()) return false;

        String[] skills = {"attack4", "attack3", "attack2"};

        for (String skill : skills) {
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
        if (!(this.level() instanceof ServerLevel serverWorld)) return false;
        if (!(this.getTarget() instanceof LivingEntity target) || !target.isAlive()) return false;
        if (!isTargetInAttackRange(target)) return false;

        boolean hit = target.hurtServer(serverWorld, this.damageSources().mobAttack(this), damage);
        if (hit) {
            this.setLastHurtMob(target);
        }
        return hit;
    }

    private boolean isTargetInAttackRange(LivingEntity target) {
        double reach = (this.getBbWidth() * 2.0F) * (this.getBbWidth() * 2.0F) + target.getBbWidth();
        return this.distanceToSqr(target) <= reach + 1.0D;
    }

    public LobsterVariant getVariant() {
        return LobsterVariant.byId(this.entityData.get(VARIANT));
    }

    public void setVariant(LobsterVariant variant) {
        this.entityData.set(VARIANT, variant.getId());
    }

    public boolean isRetreating() {
        return this.entityData.get(RETREATING);
    }

    public void setRetreating(boolean retreating) {
        this.entityData.set(RETREATING, retreating);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.ROTTEN_FLESH);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob mate) {
        if (mate instanceof LobsterEntity other
                && this.getType() == ModEntities.LOBSTER
                && other.getType() == ModEntities.LOBSTER
                && this.getVariant() == LobsterVariant.GOLD
                && other.getVariant() == LobsterVariant.GOLD
                && world.random.nextFloat() < 0.02F) {
            return ModEntities.MAGMA_LOBSTER.create(world, EntitySpawnReason.BREEDING);
        }

        LobsterEntity child = ModEntities.LOBSTER.create(world, EntitySpawnReason.BREEDING);
        if (child == null) return null;

        LobsterVariant biomeVariant = determineBiomeVariant(world, this.blockPosition(), child.getRandom());
        if (mate instanceof LobsterEntity other && world.random.nextFloat() < 0.5F) {
            child.setVariant(world.random.nextBoolean() ? this.getVariant() : other.getVariant());
        } else {
            child.setVariant(biomeVariant);
        }

        return child;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        SpawnGroupData data = super.finalizeSpawn(world, difficulty, spawnReason, entityData);
        this.setVariant(determineBiomeVariant(world.getLevel(), this.blockPosition(), this.random));
        return data;
    }

    private static LobsterVariant determineBiomeVariant(ServerLevel world, BlockPos pos, net.minecraft.util.RandomSource random) {
        Holder<Biome> biome = world.getBiome(pos);

        if (biome.is(BiomeTags.IS_NETHER)) {
            return LobsterVariant.GOLD;
        }

        if (biome.is(BiomeTags.IS_RIVER)
                || biome.is(Biomes.SWAMP)
                || biome.is(Biomes.MANGROVE_SWAMP)) {
            return LobsterVariant.GRAY;
        }

        if (biome.is(Biomes.SNOWY_PLAINS)
                || biome.is(Biomes.SNOWY_TAIGA)
                || biome.is(Biomes.ICE_SPIKES)
                || biome.is(Biomes.SNOWY_SLOPES)
                || biome.is(Biomes.FROZEN_PEAKS)
                || biome.is(Biomes.JAGGED_PEAKS)
                || biome.is(Biomes.GROVE)
                || biome.is(Biomes.SNOWY_BEACH)
                || biome.is(Biomes.FROZEN_RIVER)) {
            return LobsterVariant.WHITE;
        }

        return random.nextBoolean() ? LobsterVariant.RED : LobsterVariant.BLUE;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        view.putInt("Variant", this.getVariant().getId());
        view.putBoolean("Retreating", this.isRetreating());
        view.putBoolean("PanicRetreating", this.isPanicRetreating());
        view.putBoolean("TargetingUnderwater", this.targetingUnderwater);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        this.setVariant(LobsterVariant.byId(view.getIntOr("Variant", LobsterVariant.RED.getId())));
        this.setRetreating(view.getBooleanOr("Retreating", false));
        this.setPanicRetreating(view.getBooleanOr("PanicRetreating", false));
        this.targetingUnderwater = view.getBooleanOr("TargetingUnderwater", false);
    }

    @Override
    public PlayState mainController(AnimationTest<?> event) {
        if (this.hasSkill()) {
            return PlayState.CONTINUE;
        }

        if (this.isPanicRetreating()) {
            return event.setAndContinue(SWING_ANIM);
        }

        if (this.isInWater() && this.getTarget() == null) {
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

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.2D);
    }

    protected @Nullable BlockPos findNearbyBroadWater(int horizontalRange, int verticalRange) {
        BlockPos origin = this.blockPosition();
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;

        for (int x = -horizontalRange; x <= horizontalRange; x++) {
            for (int y = -verticalRange; y <= verticalRange; y++) {
                for (int z = -horizontalRange; z <= horizontalRange; z++) {
                    BlockPos pos = origin.offset(x, y, z);

                    if (!this.level().getFluidState(pos).is(FluidTags.WATER)) continue;
                    if (!isBroadWater(pos)) continue;

                    double dist = origin.distSqr(pos);
                    if (dist < bestDistance) {
                        bestDistance = dist;
                        best = pos.immutable();
                    }
                }
            }
        }

        return best;
    }

    private boolean isBroadWater(BlockPos center) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos pos = center.offset(x, 0, z);

                if (!this.level().getFluidState(pos).is(FluidTags.WATER)) {
                    return false;
                }

                BlockPos up = pos.above();
                if (!(this.level().isEmptyBlock(up) || this.level().getFluidState(up).is(FluidTags.WATER))) {
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
            if (this.lobster.isTargetingUnderwater() && this.lobster.isInWater()) {
                if ((target != null && target.getY() > this.lobster.getY()) || this.lobster.targetingUnderwater) {
                    this.lobster.setDeltaMovement(this.lobster.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                }

                if (this.operation != Operation.MOVE_TO || this.lobster.getNavigation().isDone()) {
                    this.lobster.setSpeed(0.02F);
                    return;
                }

                double dx = this.wantedX - this.lobster.getX();
                double dy = this.wantedY - this.lobster.getY();
                double dz = this.wantedZ - this.lobster.getZ();
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (dist < 1.0E-6D) {
                    this.lobster.setSpeed(0.0F);
                    return;
                }

                dy /= dist;
                float yaw = (float)(Mth.atan2(dz, dx) * 180.0F / Math.PI) - 90.0F;
                this.lobster.setYRot(this.rotlerp(this.lobster.getYRot(), yaw, 90.0F));
                this.lobster.yBodyRot = this.lobster.getYRot();

                float targetSpeed = (float)(this.speedModifier * this.lobster.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float lerpedSpeed = Mth.lerp(0.125F, this.lobster.getSpeed(), targetSpeed);
                this.lobster.setSpeed(lerpedSpeed);
                this.lobster.setDeltaMovement(this.lobster.getDeltaMovement().add(
                        lerpedSpeed * dx * 0.005D,
                        lerpedSpeed * dy * 0.08D,
                        lerpedSpeed * dz * 0.005D
                ));
            } else {
                if (this.lobster.shouldSinkInWater()) {
                    this.lobster.setSpeed(0.02F);
                    this.lobster.setDeltaMovement(this.lobster.getDeltaMovement().add(0.0D, -0.01D, 0.0D));
                    return;
                }

                if (!this.lobster.onGround()) {
                    this.lobster.setDeltaMovement(this.lobster.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }

                super.tick();
            }
        }
    }

    private static class RetreatToWaterGoal extends Goal {
        private final LobsterEntity lobster;
        private final double speed;
        private BlockPos targetWater;
        private LivingEntity threat;
        private boolean panicMode;
        private int repathCooldown;

        private RetreatToWaterGoal(LobsterEntity lobster, double speed) {
            this.lobster = lobster;
            this.speed = speed;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.lobster.hasSkill()) return false;

            boolean lowHealth = this.lobster.getHealth() < this.lobster.getPanicRetreatHealthThreshold();
            boolean idleOnLand = this.lobster.getTarget() == null && !this.lobster.isInWater();

            if (!lowHealth && !idleOnLand) return false;

            this.panicMode = lowHealth;
            this.threat = this.lobster.getLastHurtByMob();

            if (this.threat == null || !this.threat.isAlive()) {
                this.threat = this.lobster.getTarget();
            }

            this.targetWater = this.lobster.findNearbyBroadWater(16, 6);
            return this.targetWater != null || (this.panicMode && this.threat != null);
        }

        @Override
        public boolean canContinueToUse() {
            if (this.panicMode) {
                return this.lobster.isAlive()
                        && this.lobster.getHealth() < this.lobster.getPanicRetreatHealthThreshold()
                        && this.threat != null
                        && this.threat.isAlive();
            }

            return this.targetWater != null
                    && !this.lobster.isInWater()
                    && !this.lobster.getNavigation().isDone();
        }

        @Override
        public void start() {
            this.lobster.setRetreating(true);
            this.lobster.setPanicRetreating(this.panicMode);
            this.lobster.setTarget(null);
            this.lobster.setLastHurtByMob(null);
            this.lobster.setTargetingUnderwater(true);
            this.repathCooldown = 0;

            if (this.targetWater != null) {
                this.lobster.getNavigation().moveTo(
                        this.targetWater.getX() + 0.5D,
                        this.targetWater.getY() + 0.5D,
                        this.targetWater.getZ() + 0.5D,
                        this.speed
                );
            }
        }

        @Override
        public void tick() {
            if (this.panicMode) {
                if (this.threat != null && this.threat.isAlive()) {
                    this.lobster.getLookControl().setLookAt(this.threat, 30.0F, 30.0F);
                }

                if (--this.repathCooldown <= 0) {
                    this.repathCooldown = 10;

                    Vec3 fleePos = DefaultRandomPos.getPosAway(
                            this.lobster,
                            12,
                            6,
                            this.threat != null ? this.threat.position() : this.lobster.position()
                    );

                    if (fleePos != null) {
                        this.lobster.getNavigation().moveTo(fleePos.x, fleePos.y, fleePos.z, this.speed + 0.2D);
                    } else if (this.targetWater != null) {
                        this.lobster.getNavigation().moveTo(
                                this.targetWater.getX() + 0.5D,
                                this.targetWater.getY() + 0.5D,
                                this.targetWater.getZ() + 0.5D,
                                this.speed
                        );
                    }
                }
                return;
            }

            if (this.targetWater == null) return;

            this.lobster.getNavigation().moveTo(
                    this.targetWater.getX() + 0.5D,
                    this.targetWater.getY() + 0.5D,
                    this.targetWater.getZ() + 0.5D,
                    this.speed
            );
        }

        @Override
        public void stop() {
            this.targetWater = null;
            this.threat = null;
            this.panicMode = false;
            this.repathCooldown = 0;
            this.lobster.setRetreating(false);
            this.lobster.setPanicRetreating(false);
            this.lobster.setTargetingUnderwater(false);
        }
    }

    private static class WaterWanderGoal extends Goal {
        private final LobsterEntity lobster;
        private final double speed;

        private WaterWanderGoal(LobsterEntity lobster, double speed) {
            this.lobster = lobster;
            this.speed = speed;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.lobster.getTarget() == null
                    && !this.lobster.isRetreating()
                    && this.lobster.isInWater()
                    && this.lobster.getRandom().nextInt(40) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.lobster.getNavigation().isDone()
                    && this.lobster.isInWater()
                    && this.lobster.getTarget() == null
                    && !this.lobster.isRetreating();
        }

        @Override
        public void start() {
            Vec3 vec3d = DefaultRandomPos.getPosTowards(
                    this.lobster,
                    6,
                    4,
                    Vec3.atCenterOf(this.lobster.blockPosition().below(2)),
                    (float) (Math.PI / 2)
            );

            if (vec3d != null) {
                this.lobster.setTargetingUnderwater(true);
                this.lobster.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
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
