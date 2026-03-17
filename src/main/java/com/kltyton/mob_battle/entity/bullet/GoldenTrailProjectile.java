package com.kltyton.mob_battle.entity.bullet;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class GoldenTrailProjectile extends TrueDamageProjectile {
    private static final TrackedData<Boolean> STRENGTHEN =
            DataTracker.registerData(GoldenTrailProjectile.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> BLOOD_STRENGTHEN =
            DataTracker.registerData(GoldenTrailProjectile.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final Vector3f GOLD_COLOR = new Vector3f(1.0F, 0.84F, 0.2F);
    private static final Vector3f BLOOD_COLOR = new Vector3f(1.0F, 0.1F, 0.1F);

    public GoldenTrailProjectile(EntityType<? extends GoldenTrailProjectile> entityType, World world) {
        super(entityType, world);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public GoldenTrailProjectile(EntityType<? extends GoldenTrailProjectile> entityType,
                                 LivingEntity owner,
                                 World world,
                                 ItemStack stack,
                                 @Nullable ItemStack shotFrom) {
        super(entityType, owner, world, stack, shotFrom);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public GoldenTrailProjectile(EntityType<? extends GoldenTrailProjectile> entityType,
                                 double x,
                                 double y,
                                 double z,
                                 World world,
                                 ItemStack stack,
                                 @Nullable ItemStack weapon) {
        super(entityType, x, y, z, world, stack, weapon);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(STRENGTHEN, false);
        builder.add(BLOOD_STRENGTHEN, false);
    }

    public void setStrengthen(boolean strengthen) {
        this.dataTracker.set(STRENGTHEN, strengthen);
    }

    public boolean isStrengthen() {
        return this.dataTracker.get(STRENGTHEN);
    }

    public void setBloodStrengthen(boolean bloodStrengthen) {
        this.dataTracker.set(BLOOD_STRENGTHEN, bloodStrengthen);
    }

    public boolean isBloodStrengthen() {
        return this.dataTracker.get(BLOOD_STRENGTHEN);
    }
    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age >= 200) {
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
        World world = this.getWorld();
        if (!world.isClient()) {
            return;
        }

        double vx = this.getVelocity().x;
        double vy = this.getVelocity().y;
        double vz = this.getVelocity().z;

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

            world.addParticleClient(
                    new DustParticleEffect(colorRGB, blood ? 1.35F : 1.2F),
                    baseX + ox,
                    baseY + oy,
                    baseZ + oz,
                    -vx * 0.10,
                    -vy * 0.10,
                    -vz * 0.10
            );
        }

        if (blood) {
            if (this.age % 2 == 0) {
                world.addParticleClient(
                        ParticleTypes.CRIT,
                        baseX, baseY, baseZ,
                        0.0, 0.0, 0.0
                );
            }
        } else {
            if (this.age % 2 == 0) {
                world.addParticleClient(
                        ParticleTypes.ELECTRIC_SPARK,
                        baseX, baseY, baseZ,
                        0.0, 0.0, 0.0
                );
            }
            if (this.age % 3 == 0) {
                world.addParticleClient(
                        ParticleTypes.GLOW,
                        baseX, baseY, baseZ,
                        0.0, 0.0, 0.0
                );
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        this.discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.discard();
    }

    @Override
    public void writeCustomData(WriteView nbt) {
        super.writeCustomData(nbt);
        nbt.putBoolean("Strengthen", this.isStrengthen());
        nbt.putBoolean("BloodStrengthen", this.isBloodStrengthen());
    }

    @Override
    public void readCustomData(ReadView nbt) {
        super.readCustomData(nbt);
        this.setStrengthen(nbt.getBoolean("Strengthen", false));
        this.setBloodStrengthen(nbt.getBoolean("BloodStrengthen", false));
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.TRAIN_BULLET);
    }
    @Override
    public void additionalDamage(Entity entity) {
        if (!this.getWorld().isClient && entity instanceof LivingEntity livingEntity) {
            applyStrengthenBulletEffect((ServerWorld) this.getWorld(), livingEntity);
        }
    }
    public void applyStrengthenBulletEffect(ServerWorld world, LivingEntity target) {
        if (this.isStrengthen()) {
            StatusEffectInstance current = target.getStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
            int amplifier = 0;
            if (current != null) {
                amplifier = Math.min(current.getAmplifier() + 1, 79);
            }
            target.addStatusEffect(new StatusEffectInstance(ModEffects.PIG_SPIRIT_MARK_ENTRY, 160, amplifier));
            target.damage(world, target.getDamageSources().magic(), 10.0F);
        }
    }
}