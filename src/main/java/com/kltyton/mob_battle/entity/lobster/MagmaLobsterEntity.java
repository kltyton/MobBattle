package com.kltyton.mob_battle.entity.lobster;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.customfireball.CustomFireballEntity;
import com.kltyton.mob_battle.entity.customfireball.MagmaLobsterBigFireballEntity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class MagmaLobsterEntity extends LobsterEntity {
    public MagmaLobsterEntity(EntityType<? extends MagmaLobsterEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(net.minecraft.entity.ai.pathing.PathNodeType.LAVA, 0.0F);
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
    protected void initGoals() {
        this.goalSelector.add(0, new RetreatToLavaGoal(this, 1.4D));
        this.goalSelector.add(1, new MagmaWaterWanderGoal(this, 1.0D));
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
    public @Nullable EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        EntityData data = super.initialize(world, difficulty, spawnReason, entityData);
        // 岩浆龙虾固定金色即可；你要做单独贴图的话客户端再按实体类型区分
        this.setVariant(LobsterVariant.GOLD);
        return data;
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (source.isIn(DamageTypeTags.IS_FIRE)) {
            return false;
        }
        return super.damage(world, source, amount);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity mate) {
        return ModEntities.MAGMA_LOBSTER.create(world, SpawnReason.BREEDING);
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

        if (target != null && this.canSkill("attack5") && this.squaredDistanceTo(target) > 9.0D) {
            performSkill("attack5");
            return true;
        }

        String[] skills = {"attack2", "attack3", "attack4"};
        int start = this.random.nextInt(skills.length);

        for (int i = 0; i < skills.length; i++) {
            String skill = skills[(start + i) % skills.length];
            if (canSkill(skill)) {
                performSkill(skill);
                return true;
            }
        }

        if (this.canSkill("attack5")) {
            performSkill("attack5");
            return true;
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
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;

        LivingEntity target = this.getTarget();
        Vec3d look = target != null
                ? target.getEyePos().subtract(this.getEyePos()).normalize()
                : this.getRotationVector().normalize();

        Vec3d spawnPos = this.getEyePos().add(look.multiply(0.8D));

        serverWorld.spawnParticles(
                ParticleTypes.FLAME,
                spawnPos.x, spawnPos.y, spawnPos.z,
                35,
                0.25D, 0.25D, 0.25D,
                0.03D
        );

        serverWorld.spawnParticles(
                ParticleTypes.SMOKE,
                spawnPos.x, spawnPos.y, spawnPos.z,
                15,
                0.2D, 0.2D, 0.2D,
                0.01D
        );

        Vec3d side = look.crossProduct(new Vec3d(0, 1, 0));
        if (side.lengthSquared() < 1.0E-6D) {
            side = new Vec3d(1, 0, 0);
        } else {
            side = side.normalize();
        }

        CustomFireballEntity left = new CustomFireballEntity(
                EntityType.FIREBALL,
                this.getWorld(),
                this,
                0.0F,
                false,
                50.0F
        );
        left.refreshPositionAndAngles(
                spawnPos.x + side.x * 0.35D,
                spawnPos.y,
                spawnPos.z + side.z * 0.35D,
                this.getYaw(),
                this.getPitch()
        );
        left.setVelocity(look.x, look.y, look.z, 1.15F, 2.0F);

        CustomFireballEntity right = new CustomFireballEntity(
                EntityType.FIREBALL,
                this.getWorld(),
                this,
                0.0F,
                false,
                50.0F
        );
        right.refreshPositionAndAngles(
                spawnPos.x - side.x * 0.35D,
                spawnPos.y,
                spawnPos.z - side.z * 0.35D,
                this.getYaw(),
                this.getPitch()
        );
        right.setVelocity(look.x, look.y, look.z, 1.15F, 2.0F);

        MagmaLobsterBigFireballEntity big = new MagmaLobsterBigFireballEntity(
                ModEntities.MAGMA_LOBBER_BIG_FIREBALL,
                this.getWorld(),
                this
        );
        big.refreshPositionAndAngles(
                spawnPos.x,
                spawnPos.y,
                spawnPos.z,
                this.getYaw(),
                this.getPitch()
        );
        big.setVelocity(look.x, look.y, look.z, 0.9F, 1.0F);

        serverWorld.spawnEntity(left);
        serverWorld.spawnEntity(right);
        serverWorld.spawnEntity(big);
    }

    private boolean damageCurrentTargetMagma(float damage, boolean applyWeakness) {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return false;
        if (!(this.getTarget() instanceof LivingEntity target) || !target.isAlive()) return false;

        double reach = (this.getWidth() * 2.0F) * (this.getWidth() * 2.0F) + target.getWidth();
        if (this.squaredDistanceTo(target) > reach + 1.0D) return false;

        boolean hit = target.damage(serverWorld, this.getDamageSources().mobAttack(this), damage);
        if (hit) {
            this.onAttacking(target);
            target.setOnFireFor(4);
            if (applyWeakness) {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 10 * 20, 1), this);
            }
        }
        return hit;
    }

    public static net.minecraft.entity.attribute.DefaultAttributeContainer.Builder createAttributes() {
        return LobsterEntity.createAttributes()
                .add(net.minecraft.entity.attribute.EntityAttributes.MAX_HEALTH, 170.0D)
                .add(net.minecraft.entity.attribute.EntityAttributes.ARMOR, 12.0D)
                .add(net.minecraft.entity.attribute.EntityAttributes.ARMOR_TOUGHNESS, 8.0D);
    }

    protected @Nullable BlockPos findNearbyBroadLava(int horizontalRange, int verticalRange) {
        BlockPos origin = this.getBlockPos();
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;

        for (int x = -horizontalRange; x <= horizontalRange; x++) {
            for (int y = -verticalRange; y <= verticalRange; y++) {
                for (int z = -horizontalRange; z <= horizontalRange; z++) {
                    BlockPos pos = origin.add(x, y, z);

                    if (!this.getWorld().getFluidState(pos).isIn(FluidTags.LAVA)) continue;
                    if (!isBroadLava(pos)) continue;

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

    private boolean isBroadLava(BlockPos center) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos pos = center.add(x, 0, z);

                if (!this.getWorld().getFluidState(pos).isIn(FluidTags.LAVA)) {
                    return false;
                }

                BlockPos up = pos.up();
                if (!(this.getWorld().isAir(up) || this.getWorld().getFluidState(up).isIn(FluidTags.LAVA))) {
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
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (this.lobster.hasSkill()) return false;

            boolean lowHealth = this.lobster.getHealth() < this.lobster.getPanicRetreatHealthThreshold();
            boolean idleOnLand = this.lobster.getTarget() == null
                    && !this.lobster.isTouchingWater()
                    && !this.lobster.isInLava();

            if (!lowHealth && !idleOnLand) return false;

            this.panicMode = lowHealth;
            this.threat = this.lobster.getAttacker();

            if (this.threat == null || !this.threat.isAlive()) {
                this.threat = this.lobster.getTarget();
            }

            this.targetLava = this.lobster.findNearbyBroadLava(16, 6);
            return this.targetLava != null || (this.panicMode && this.threat != null);
        }

        @Override
        public boolean shouldContinue() {
            if (this.panicMode) {
                return this.lobster.isAlive()
                        && this.lobster.getHealth() < this.lobster.getPanicRetreatHealthThreshold()
                        && this.threat != null
                        && this.threat.isAlive();
            }

            return this.targetLava != null
                    && !this.lobster.isInLava()
                    && !this.lobster.getNavigation().isIdle();
        }

        @Override
        public void start() {
            this.lobster.setRetreating(true);
            this.lobster.setPanicRetreating(this.panicMode);
            this.lobster.setTarget(null);
            this.lobster.setAttacker(null);
            this.repathCooldown = 0;

            if (this.targetLava != null) {
                this.lobster.getNavigation().startMovingTo(
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
                    this.lobster.getLookControl().lookAt(this.threat, 30.0F, 30.0F);
                }

                if (--this.repathCooldown <= 0) {
                    this.repathCooldown = 10;

                    Vec3d fleePos = NoPenaltyTargeting.findFrom(
                            this.lobster,
                            12,
                            6,
                            this.threat != null ? this.threat.getPos() : this.lobster.getPos()
                    );

                    if (fleePos != null) {
                        this.lobster.getNavigation().startMovingTo(fleePos.x, fleePos.y, fleePos.z, this.speed + 0.2D);
                    } else if (this.targetLava != null) {
                        this.lobster.getNavigation().startMovingTo(
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

            this.lobster.getNavigation().startMovingTo(
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
