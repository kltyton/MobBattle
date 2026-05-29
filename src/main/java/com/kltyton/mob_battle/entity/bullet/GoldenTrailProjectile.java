package com.kltyton.mob_battle.entity.bullet;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.utils.CombatEffectUtil;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class GoldenTrailProjectile extends TrueDamageProjectile {
    private static final EntityDataAccessor<Boolean> STRENGTHEN =
            SynchedEntityData.defineId(GoldenTrailProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BLOOD_STRENGTHEN =
            SynchedEntityData.defineId(GoldenTrailProjectile.class, EntityDataSerializers.BOOLEAN);

    private static final Vector3f GOLD_COLOR = new Vector3f(1.0F, 0.84F, 0.2F);
    private static final Vector3f BLOOD_COLOR = new Vector3f(1.0F, 0.1F, 0.1F);

    public GoldenTrailProjectile(EntityType<? extends GoldenTrailProjectile> entityType, Level world) {
        super(entityType, world);
        this.pickup = Pickup.DISALLOWED;
    }

    public GoldenTrailProjectile(EntityType<? extends GoldenTrailProjectile> entityType,
                                 LivingEntity owner,
                                 Level world,
                                 ItemStack stack,
                                 @Nullable ItemStack shotFrom) {
        super(entityType, owner, world, stack, shotFrom);
        this.pickup = Pickup.DISALLOWED;
    }

    public GoldenTrailProjectile(EntityType<? extends GoldenTrailProjectile> entityType,
                                 double x,
                                 double y,
                                 double z,
                                 Level world,
                                 ItemStack stack,
                                 @Nullable ItemStack weapon) {
        super(entityType, x, y, z, world, stack, weapon);
        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STRENGTHEN, false);
        builder.define(BLOOD_STRENGTHEN, false);
    }

    public void setStrengthen(boolean strengthen) {
        this.entityData.set(STRENGTHEN, strengthen);
    }

    public boolean isStrengthen() {
        return this.entityData.get(STRENGTHEN);
    }

    public void setBloodStrengthen(boolean bloodStrengthen) {
        this.entityData.set(BLOOD_STRENGTHEN, bloodStrengthen);
    }

    public boolean isBloodStrengthen() {
        return this.entityData.get(BLOOD_STRENGTHEN);
    }
    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount >= 200) {
            this.discard();
            return;
        }

        if (this.isBloodStrengthen()) {
            spawnTrail(BLOOD_COLOR, true);
        }
        if (this.isStrengthen()) {
            spawnTrail(GOLD_COLOR, false);
        }
    }

    private void spawnTrail(Vector3f colorVec, boolean blood) {
        Level world = this.level();
        if (!world.isClientSide()) {
            return;
        }

        double vx = this.getDeltaMovement().x;
        double vy = this.getDeltaMovement().y;
        double vz = this.getDeltaMovement().z;

        double baseX = this.getX() - vx * 0.25;
        double baseY = this.getY() - vy * 0.25;
        double baseZ = this.getZ() - vz * 0.25;

        int colorRGB =
                ((int) (colorVec.x() * 255) << 16) |
                        ((int) (colorVec.y() * 255) << 8) |
                        ((int) (colorVec.z() * 255));

        for (int i = 0; i < 4; i++) {
            double ox = (this.random.nextDouble() - 0.5) * 0.12;
            double oy = (this.random.nextDouble() - 0.5) * 0.12;
            double oz = (this.random.nextDouble() - 0.5) * 0.12;

            world.addParticle(
                    new DustParticleOptions(colorRGB, blood ? 1.35F : 1.2F),
                    baseX + ox,
                    baseY + oy,
                    baseZ + oz,
                    -vx * 0.10,
                    -vy * 0.10,
                    -vz * 0.10
            );
        }

        if (blood) {
            if (this.tickCount % 2 == 0) {
                world.addParticle(
                        ParticleTypes.CRIT,
                        baseX, baseY, baseZ,
                        0.0, 0.0, 0.0
                );
            }
        } else {
            if (this.tickCount % 2 == 0) {
                world.addParticle(
                        ParticleTypes.ELECTRIC_SPARK,
                        baseX, baseY, baseZ,
                        0.0, 0.0, 0.0
                );
            }
            if (this.tickCount % 3 == 0) {
                world.addParticle(
                        ParticleTypes.GLOW,
                        baseX, baseY, baseZ,
                        0.0, 0.0, 0.0
                );
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        this.discard();
    }

    @Override
    public void addAdditionalSaveData(ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("Strengthen", this.isStrengthen());
        nbt.putBoolean("BloodStrengthen", this.isBloodStrengthen());
    }

    @Override
    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        this.setStrengthen(nbt.getBooleanOr("Strengthen", false));
        this.setBloodStrengthen(nbt.getBooleanOr("BloodStrengthen", false));
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.TRAIN_BULLET);
    }
    @Override
    public void additionalDamage(Entity entity) {
        if (!this.level().isClientSide && entity instanceof LivingEntity livingEntity) {
            applyStrengthenBulletEffect((ServerLevel) this.level(), livingEntity);
        }
    }
    public void applyStrengthenBulletEffect(ServerLevel world, LivingEntity target) {
        if (this.isStrengthen()) {
            Entity owner = this.getOwner();
            CombatEffectUtil.addPigSpiritMark(target, owner instanceof LivingEntity living ? living : target, 1);
            target.hurtServer(world, target.damageSources().magic(), 10.0F);
        }
    }
}
