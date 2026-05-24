package com.kltyton.mob_battle.entity.vindicatorgeneral;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VindicatorGeneralAxeEntity extends ProjectileEntity implements GeoEntity {
    private static final RawAnimation FLY_ANIM = RawAnimation.begin().thenLoop("fly");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final ItemStack unusedDisplayStack = new ItemStack(Items.IRON_AXE);

    private boolean returning;
    private int ownerId = -1;

    public VindicatorGeneralAxeEntity(EntityType<? extends VindicatorGeneralAxeEntity> entityType, World world) {
        super(entityType, world);
        this.noClip = false;
        this.setNoGravity(true);
    }

    public void configure(VindicatorGeneralEntity owner, Vec3d position, Vec3d velocity) {
        this.setOwner(owner);
        this.ownerId = owner.getId();
        this.setPosition(position);
        this.setVelocity(velocity);
        this.velocityModified = true;
        this.noClip = false;
        this.setNoGravity(true);
    }

    public ItemStack getDisplayStack() {
        return this.unusedDisplayStack;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    protected void readCustomData(ReadView view) {
    }

    @Override
    protected void writeCustomData(WriteView view) {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            return;
        }

        Entity owner = getOwnerEntity();
        if (this.age > 200 || owner == null && this.age > 20) {
            finishRecovery();
            return;
        }

        if (this.returning && owner != null) {
            this.noClip = true;
            Vec3d toOwner = owner.getEyePos().subtract(this.getPos());
            if (toOwner.lengthSquared() < 2.25D) {
                finishRecovery();
                return;
            }
            this.setVelocity(toOwner.normalize().multiply(1.8D));
            this.velocityModified = true;
        }

        this.move(MovementType.SELF, this.getVelocity());

        if (!this.returning) {
            if (this.horizontalCollision || this.verticalCollision || touchesSolidBlock()) {
                startReturning();
                return;
            }
            hitNearbyTargets(owner);
        } else if (owner != null && this.squaredDistanceTo(owner) < 2.25D) {
            finishRecovery();
        }
    }

    private boolean touchesSolidBlock() {
        return this.getWorld().getBlockState(this.getBlockPos()).isSolidBlock(this.getWorld(), this.getBlockPos());
    }

    private void hitNearbyTargets(Entity owner) {
        if (!(owner instanceof VindicatorGeneralEntity vindicatorGeneral) || !(this.getWorld() instanceof ServerWorld world)) {
            return;
        }
        Box box = this.getBoundingBox().expand(0.85D);
        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, box,
                living -> EntityUtil.isValidCombatTarget(vindicatorGeneral, living))) {
            target.timeUntilRegen = 0;
            target.damage(world, this.getDamageSources().mobProjectile(this, vindicatorGeneral), 280.0F);
            startReturning();
            return;
        }
    }

    private void startReturning() {
        this.returning = true;
        this.noClip = true;
        this.velocityModified = true;
    }

    private Entity getOwnerEntity() {
        Entity owner = this.getOwner();
        if (owner != null) {
            return owner;
        }
        if (this.ownerId >= 0 && this.getWorld() instanceof ServerWorld world) {
            return world.getEntityById(this.ownerId);
        }
        return null;
    }

    private void finishRecovery() {
        Entity owner = getOwnerEntity();
        if (owner instanceof VindicatorGeneralEntity vindicatorGeneral) {
            vindicatorGeneral.startAxeRecovery();
        }
        this.discard();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 0, state -> state.setAndContinue(FLY_ANIM)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
