package com.kltyton.mob_battle.entity.lobster;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.customfireball.CustomFireballEntity;
import com.kltyton.mob_battle.entity.customfireball.MagmaLobsterBigFireballEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class MagmaLobsterEntity extends LobsterEntity {
    public MagmaLobsterEntity(EntityType<? extends MagmaLobsterEntity> entityType, Level world) {
        super(entityType, world);
        this.setPathfindingMalus(PathType.LAVA, 0.0F);
    }

    @Override
    public int getSkillCount() {
        return 4;
    }

    @Override
    public int getMaxSkillCooldown_4() {
        return 35 * 20;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new RetreatToLavaGoal(this, 1.4D));
        this.goalSelector.addGoal(1, new MagmaWaterWanderGoal(this, 1.0D));
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
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        SpawnGroupData data = super.finalizeSpawn(world, difficulty, spawnReason, entityData);
        // 岩浆龙虾固定金色即可；你要做单独贴图的话客户端再按实体类型区分
        this.setVariant(LobsterVariant.GOLD);
        return data;
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_FIRE)) {
            return false;
        }
        return super.hurtServer(world, source, amount);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob mate) {
        LobsterEntity child = ModEntities.LOBSTER.create(world, EntitySpawnReason.BREEDING);
        if (child == null) return null;

        child.setVariant(LobsterVariant.GOLD);
        return child;
    }
    @Override
    public float getPanicRetreatHealthThreshold() {
        return 30.0F;
    }
    @Override
    public boolean doSkill() {
        if (this.isPanicRetreating()) return false;
        if (!canSkill()) return false;

        LivingEntity target = this.getTarget();

        if (target != null && this.canSkill("attack5")) {
            performSkill("attack5");
            return true;
        }

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
        damageCurrentTargetMagma(20.0F, false);
    }

    @Override
    public void runSkill_3(LobsterEntity entity) {
        damageCurrentTargetMagma(22.0F, false);
    }

    @Override
    public void runSkill_4(LobsterEntity entity) {
        damageCurrentTargetMagma(25.0F, true);
    }

    @Override
    public void runSkill_5(LobsterEntity entity) {
        if (!(this.level() instanceof ServerLevel serverWorld)) return;

        LivingEntity target = this.getTarget();
        Vec3 look = target != null
                ? target.getEyePosition().subtract(this.getEyePosition()).normalize()
                : this.getLookAngle().normalize();

        Vec3 spawnPos = this.getEyePosition().add(look.scale(0.8D));

        serverWorld.sendParticles(
                ParticleTypes.FLAME,
                spawnPos.x, spawnPos.y, spawnPos.z,
                35,
                0.25D, 0.25D, 0.25D,
                0.03D
        );

        serverWorld.sendParticles(
                ParticleTypes.SMOKE,
                spawnPos.x, spawnPos.y, spawnPos.z,
                15,
                0.2D, 0.2D, 0.2D,
                0.01D
        );

        Vec3 side = look.cross(new Vec3(0, 1, 0));
        if (side.lengthSqr() < 1.0E-6D) {
            side = new Vec3(1, 0, 0);
        } else {
            side = side.normalize();
        }

        CustomFireballEntity left = new CustomFireballEntity(
                EntityType.FIREBALL,
                this.level(),
                this,
                0.0F,
                false,
                50.0F
        );
        left.snapTo(
                spawnPos.x + side.x * 0.35D,
                spawnPos.y,
                spawnPos.z + side.z * 0.35D,
                this.getYRot(),
                this.getXRot()
        );
        left.shoot(look.x, look.y, look.z, 1.15F, 2.0F);

        CustomFireballEntity right = new CustomFireballEntity(
                EntityType.FIREBALL,
                this.level(),
                this,
                0.0F,
                false,
                50.0F
        );
        right.snapTo(
                spawnPos.x - side.x * 0.35D,
                spawnPos.y,
                spawnPos.z - side.z * 0.35D,
                this.getYRot(),
                this.getXRot()
        );
        right.shoot(look.x, look.y, look.z, 1.15F, 2.0F);

        MagmaLobsterBigFireballEntity big = new MagmaLobsterBigFireballEntity(
                ModEntities.MAGMA_LOBBER_BIG_FIREBALL,
                this.level(),
                this
        );
        big.snapTo(
                spawnPos.x,
                spawnPos.y,
                spawnPos.z,
                this.getYRot(),
                this.getXRot()
        );
        big.shoot(look.x, look.y, look.z, 0.9F, 1.0F);

        serverWorld.addFreshEntity(left);
        serverWorld.addFreshEntity(right);
        serverWorld.addFreshEntity(big);
    }

    private boolean damageCurrentTargetMagma(float damage, boolean applyWeakness) {
        if (!(this.level() instanceof ServerLevel serverWorld)) return false;
        if (!(this.getTarget() instanceof LivingEntity target) || !target.isAlive()) return false;

        double reach = (this.getBbWidth() * 2.0F) * (this.getBbWidth() * 2.0F) + target.getBbWidth();
        if (this.distanceToSqr(target) > reach + 1.0D) return false;

        boolean hit = target.hurtServer(serverWorld, this.damageSources().mobAttack(this), damage);
        if (hit) {
            this.setLastHurtMob(target);
            target.igniteForSeconds(4);
            if (applyWeakness) {
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * 20, 1), this);
            }
        }
        return hit;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LobsterEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 170.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 8.0D);
    }

    protected @Nullable BlockPos findNearbyBroadLava(int horizontalRange, int verticalRange) {
        BlockPos origin = this.blockPosition();
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;

        for (int x = -horizontalRange; x <= horizontalRange; x++) {
            for (int y = -verticalRange; y <= verticalRange; y++) {
                for (int z = -horizontalRange; z <= horizontalRange; z++) {
                    BlockPos pos = origin.offset(x, y, z);

                    if (!this.level().getFluidState(pos).is(FluidTags.LAVA)) continue;
                    if (!isBroadLava(pos)) continue;

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

    private boolean isBroadLava(BlockPos center) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos pos = center.offset(x, 0, z);

                if (!this.level().getFluidState(pos).is(FluidTags.LAVA)) {
                    return false;
                }

                BlockPos up = pos.above();
                if (!(this.level().isEmptyBlock(up) || this.level().getFluidState(up).is(FluidTags.LAVA))) {
                    return false;
                }
            }
        }

        return true;
    }

    private static class RetreatToLavaGoal extends Goal {
        private final MagmaLobsterEntity lobster;
        private final double speed;
        private BlockPos targetLava;
        private LivingEntity threat;
        private boolean panicMode;
        private int repathCooldown;

        private RetreatToLavaGoal(MagmaLobsterEntity lobster, double speed) {
            this.lobster = lobster;
            this.speed = speed;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.lobster.hasSkill()) return false;

            boolean lowHealth = this.lobster.getHealth() < this.lobster.getPanicRetreatHealthThreshold();
            boolean idleOnLand = this.lobster.getTarget() == null
                    && !this.lobster.isInWater()
                    && !this.lobster.isInLava();

            if (!lowHealth && !idleOnLand) return false;

            this.panicMode = lowHealth;
            this.threat = this.lobster.getLastHurtByMob();

            if (this.threat == null || !this.threat.isAlive()) {
                this.threat = this.lobster.getTarget();
            }

            this.targetLava = this.lobster.findNearbyBroadLava(16, 6);
            return this.targetLava != null || (this.panicMode && this.threat != null);
        }

        @Override
        public boolean canContinueToUse() {
            if (this.panicMode) {
                return this.lobster.isAlive()
                        && this.lobster.getHealth() < this.lobster.getPanicRetreatHealthThreshold()
                        && this.threat != null
                        && this.threat.isAlive();
            }

            return this.targetLava != null
                    && !this.lobster.isInLava()
                    && !this.lobster.getNavigation().isDone();
        }

        @Override
        public void start() {
            this.lobster.setRetreating(true);
            this.lobster.setPanicRetreating(this.panicMode);
            this.lobster.setTarget(null);
            this.lobster.setLastHurtByMob(null);
            this.repathCooldown = 0;

            if (this.targetLava != null) {
                this.lobster.getNavigation().moveTo(
                        this.targetLava.getX() + 0.5D,
                        this.targetLava.getY() + 0.5D,
                        this.targetLava.getZ() + 0.5D,
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
                    } else if (this.targetLava != null) {
                        this.lobster.getNavigation().moveTo(
                                this.targetLava.getX() + 0.5D,
                                this.targetLava.getY() + 0.5D,
                                this.targetLava.getZ() + 0.5D,
                                this.speed
                        );
                    }
                }
                return;
            }

            if (this.targetLava == null) return;

            this.lobster.getNavigation().moveTo(
                    this.targetLava.getX() + 0.5D,
                    this.targetLava.getY() + 0.5D,
                    this.targetLava.getZ() + 0.5D,
                    this.speed
            );
        }

        @Override
        public void stop() {
            this.targetLava = null;
            this.threat = null;
            this.panicMode = false;
            this.repathCooldown = 0;
            this.lobster.setRetreating(false);
            this.lobster.setPanicRetreating(false);
        }
    }

    private static class MagmaWaterWanderGoal extends Goal {
        private final MagmaLobsterEntity lobster;
        private final double speed;

        private MagmaWaterWanderGoal(MagmaLobsterEntity lobster, double speed) {
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
