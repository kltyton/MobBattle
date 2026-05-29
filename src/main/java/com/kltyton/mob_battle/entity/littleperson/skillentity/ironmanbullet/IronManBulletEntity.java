package com.kltyton.mob_battle.entity.littleperson.skillentity.ironmanbullet;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IronManBulletEntity extends Projectile {
    @Nullable
    private EntityReference<Entity> target;
    @Nullable
    private Direction direction;
    private int stepCount;
    private double targetX;
    private double targetY;
    private double targetZ;

    public IronManBulletEntity(EntityType<? extends IronManBulletEntity> entityType, Level world) {
        super(entityType, world);
        this.noPhysics = true;
    }

    public IronManBulletEntity(Level world, LivingEntity owner, Entity target, Direction.Axis axis) {
        this(ModEntities.IRON_MAN_BULLET_ENTITY, world);
        this.setOwner(owner);
        Vec3 vec3d = owner.getBoundingBox().getCenter();
        this.snapTo(vec3d.x, vec3d.y, vec3d.z, this.getYRot(), this.getXRot());
        this.target = new EntityReference<>(target);
        this.direction = Direction.UP;
        this.changeTargetDirection(axis, target);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        if (this.target != null) {
            view.store("Target", UUIDUtil.CODEC, this.target.getUUID());
        }

        view.storeNullable("Dir", Direction.LEGACY_ID_CODEC, this.direction);
        view.putInt("Steps", this.stepCount);
        view.putDouble("TXD", this.targetX);
        view.putDouble("TYD", this.targetY);
        view.putDouble("TZD", this.targetZ);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        this.stepCount = view.getIntOr("Steps", 0);
        this.targetX = view.getDoubleOr("TXD", 0.0);
        this.targetY = view.getDoubleOr("TYD", 0.0);
        this.targetZ = view.getDoubleOr("TZD", 0.0);
        this.direction = view.read("Dir", Direction.LEGACY_ID_CODEC).orElse(null);
        this.target = EntityReference.read(view, "Target");
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Nullable
    private Direction getProjectileDirection() {
        return this.direction;
    }

    private void setDirection(@Nullable Direction direction) {
        this.direction = direction;
    }

    private void changeTargetDirection(@Nullable Direction.Axis axis, @Nullable Entity target) {
        double d = 0.5;
        BlockPos blockPos;
        if (target == null) {
            blockPos = this.blockPosition().below();
        } else {
            d = target.getBbHeight() * 0.5;
            blockPos = BlockPos.containing(target.getX(), target.getY() + d, target.getZ());
        }

        double e = blockPos.getX() + 0.5;
        double f = blockPos.getY() + d;
        double g = blockPos.getZ() + 0.5;
        Direction direction = null;
        if (!blockPos.closerToCenterThan(this.position(), 2.0)) {
            BlockPos blockPos2 = this.blockPosition();
            List<Direction> list = Lists.newArrayList();
            if (axis != Direction.Axis.X) {
                if (blockPos2.getX() < blockPos.getX() && this.level().isEmptyBlock(blockPos2.east())) {
                    list.add(Direction.EAST);
                } else if (blockPos2.getX() > blockPos.getX() && this.level().isEmptyBlock(blockPos2.west())) {
                    list.add(Direction.WEST);
                }
            }

            if (axis != Direction.Axis.Y) {
                if (blockPos2.getY() < blockPos.getY() && this.level().isEmptyBlock(blockPos2.above())) {
                    list.add(Direction.UP);
                } else if (blockPos2.getY() > blockPos.getY() && this.level().isEmptyBlock(blockPos2.below())) {
                    list.add(Direction.DOWN);
                }
            }

            if (axis != Direction.Axis.Z) {
                if (blockPos2.getZ() < blockPos.getZ() && this.level().isEmptyBlock(blockPos2.south())) {
                    list.add(Direction.SOUTH);
                } else if (blockPos2.getZ() > blockPos.getZ() && this.level().isEmptyBlock(blockPos2.north())) {
                    list.add(Direction.NORTH);
                }
            }

            direction = Direction.getRandom(this.random);
            if (list.isEmpty()) {
                for (int i = 5; !this.level().isEmptyBlock(blockPos2.relative(direction)) && i > 0; i--) {
                    direction = Direction.getRandom(this.random);
                }
            } else {
                direction = list.get(this.random.nextInt(list.size()));
            }

            e = this.getX() + direction.getStepX();
            f = this.getY() + direction.getStepY();
            g = this.getZ() + direction.getStepZ();
        }

        this.setDirection(direction);
        double h = e - this.getX();
        double j = f - this.getY();
        double k = g - this.getZ();
        double l = Math.sqrt(h * h + j * j + k * k);
        if (l == 0.0) {
            this.targetX = 0.0;
            this.targetY = 0.0;
            this.targetZ = 0.0;
        } else {
            this.targetX = h / l * 0.15;
            this.targetY = j / l * 0.15;
            this.targetZ = k / l * 0.15;
        }

        this.hasImpulse = true;
        this.stepCount = 10 + this.random.nextInt(5) * 10;
    }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public void tick() {
        super.tick();
        Entity entity = null;
        if (!this.level().isClientSide() && this.target != null) {
            entity = EntityReference.get(this.target, this.level(), Entity.class);
        }
        HitResult hitResult = null;
        if (!this.level().isClientSide) {
            // 2. 如果解析出来的 entity 为空，清理引用
            if (entity == null) {
                this.target = null;
            }

            // 3. 目标不存在或死亡时的逻辑
            if (entity == null || !entity.isAlive() || (entity instanceof Player p && (p.isSpectator() || p.isCreative()))) {
                this.applyGravity();
            } else {
                this.targetX = Mth.clamp(this.targetX * 1.025, -1.0, 1.0);
                this.targetY = Mth.clamp(this.targetY * 1.025, -1.0, 1.0);
                this.targetZ = Mth.clamp(this.targetZ * 1.025, -1.0, 1.0);
                Vec3 vec3d = this.getDeltaMovement();
                this.setDeltaMovement(vec3d.add((this.targetX - vec3d.x) * 0.2, (this.targetY - vec3d.y) * 0.2, (this.targetZ - vec3d.z) * 0.2));
            }

            hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        }

        Vec3 vec3d = this.getDeltaMovement();
        this.setPos(this.position().add(vec3d));
        this.applyEffectsFromBlocks();
        if (this.portalProcess != null && this.portalProcess.isInsidePortalThisTick()) {
            this.handlePortal();
        }

        if (hitResult != null && this.isAlive() && hitResult.getType() != HitResult.Type.MISS) {
            this.hitTargetOrDeflectSelf(hitResult);
        }

        ProjectileUtil.rotateTowardsMovement(this, 0.5F);
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.END_ROD, this.getX() - vec3d.x, this.getY() - vec3d.y + 0.15, this.getZ() - vec3d.z, 0.0, 0.0, 0.0);
        } else if (entity != null) {
            if (this.stepCount > 0) {
                this.stepCount--;
                if (this.stepCount == 0) {
                    this.changeTargetDirection(this.direction == null ? null : this.direction.getAxis(), entity);
                }
            }

            if (this.direction != null) {
                BlockPos blockPos = this.blockPosition();
                Direction.Axis axis = this.direction.getAxis();
                if (this.level().loadedAndEntityCanStandOn(blockPos.relative(this.direction), this)) {
                    this.changeTargetDirection(axis, entity);
                } else {
                    BlockPos blockPos2 = entity.blockPosition();
                    if (axis == Direction.Axis.X && blockPos.getX() == blockPos2.getX()
                            || axis == Direction.Axis.Z && blockPos.getZ() == blockPos2.getZ()
                            || axis == Direction.Axis.Y && blockPos.getY() == blockPos2.getY()) {
                        this.changeTargetDirection(axis, entity);
                    }
                }
            }
        }
    }

    @Override
    protected boolean isAffectedByBlocks() {
        return !this.isRemoved();
    }

    @Override
    public boolean canHitEntity(Entity entity) {
        if (entity instanceof LivingEntity living && !EntityUtil.isValidSummonCombatTarget(this, this.getOwner(), living)) {
            return false;
        }
        return super.canHitEntity(entity) && !entity.noPhysics;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 16384.0;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    /* 命中实体 → 伤害 + 漂浮效果 */
    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity victim   = result.getEntity();
        Entity owner    = this.getOwner();
        if (victim instanceof LivingEntity living && !EntityUtil.isValidSummonCombatTarget(this, owner, living)) {
            return;
        }
        LivingEntity attacker = owner instanceof LivingEntity ? (LivingEntity) owner : null;
        DamageSource indirectMagicSrc = this.damageSources().indirectMagic(this, attacker);
        boolean damaged = victim.hurtOrSimulate(indirectMagicSrc, 15.0F);
        if (damaged) {
            if (this.level() instanceof ServerLevel sw)
                EnchantmentHelper.doPostAttackEffects(sw, victim, indirectMagicSrc);
            if (victim instanceof LivingEntity lv) {
                lv.addEffect(
                        new MobEffectInstance(MobEffects.WEAKNESS,  5 * 20, 7),
                        MoreObjects.firstNonNull(owner, this));
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        ((ServerLevel)this.level()).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.2, 0.2, 0.2, 0.0);
        this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0F, 1.0F);
    }

    private void destroy() {
        this.discard();
        this.level().gameEvent(GameEvent.ENTITY_DAMAGE, this.position(), GameEvent.Context.of(this));
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        this.destroy();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurtClient(DamageSource source) {
        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
        world.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2, 0.2, 0.2, 0.0);
        this.destroy();
        return true;
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        double d = packet.getXa();
        double e = packet.getYa();
        double f = packet.getZa();
        this.setDeltaMovement(d, e, f);
    }
}
