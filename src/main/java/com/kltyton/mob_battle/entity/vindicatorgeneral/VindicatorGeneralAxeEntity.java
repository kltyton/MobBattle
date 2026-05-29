package com.kltyton.mob_battle.entity.vindicatorgeneral;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VindicatorGeneralAxeEntity extends Projectile implements GeoEntity {
    private static final RawAnimation FLY_ANIM = RawAnimation.begin().thenLoop("fly");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final ItemStack unusedDisplayStack = new ItemStack(Items.IRON_AXE);

    private boolean returning;
    private int ownerId = -1;

    public VindicatorGeneralAxeEntity(EntityType<? extends VindicatorGeneralAxeEntity> entityType, Level world) {
        super(entityType, world);
        this.noPhysics = false;
        this.setNoGravity(true);
    }

    public void configure(VindicatorGeneralEntity owner, Vec3 position, Vec3 velocity) {
        this.setOwner(owner);
        this.ownerId = owner.getId();
        this.setPos(position);
        this.setDeltaMovement(velocity);
        this.hurtMarked = true;
        this.noPhysics = false;
        this.setNoGravity(true);
    }

    public ItemStack getDisplayStack() {
        return this.unusedDisplayStack;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            return;
        }

        Entity owner = getOwnerEntity();
        if (this.tickCount > 200 || owner == null && this.tickCount > 20) {
            finishRecovery();
            return;
        }

        if (this.returning && owner != null) {
            this.noPhysics = true;
            Vec3 toOwner = owner.getEyePosition().subtract(this.position());
            if (toOwner.lengthSqr() < 2.25D) {
                finishRecovery();
                return;
            }
            this.setDeltaMovement(toOwner.normalize().scale(1.8D));
            this.hurtMarked = true;
        }

        this.move(MoverType.SELF, this.getDeltaMovement());

        if (!this.returning) {
            if (this.horizontalCollision || this.verticalCollision || touchesSolidBlock()) {
                startReturning();
                return;
            }
            hitNearbyTargets(owner);
        } else if (owner != null && this.distanceToSqr(owner) < 2.25D) {
            finishRecovery();
        }
    }

    private boolean touchesSolidBlock() {
        return this.level().getBlockState(this.blockPosition()).isRedstoneConductor(this.level(), this.blockPosition());
    }

    private void hitNearbyTargets(Entity owner) {
        if (!(owner instanceof VindicatorGeneralEntity vindicatorGeneral) || !(this.level() instanceof ServerLevel world)) {
            return;
        }
        AABB box = this.getBoundingBox().inflate(0.85D);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, box,
                living -> EntityUtil.isValidCombatTarget(vindicatorGeneral, living))) {
            target.hurtServer(world, this.damageSources().mobProjectile(this, vindicatorGeneral), 280.0F);
            startReturning();
            return;
        }
    }

    private void startReturning() {
        this.returning = true;
        this.noPhysics = true;
        this.hurtMarked = true;
    }

    private Entity getOwnerEntity() {
        Entity owner = this.getOwner();
        if (owner != null) {
            return owner;
        }
        if (this.ownerId >= 0 && this.level() instanceof ServerLevel world) {
            return world.getEntity(this.ownerId);
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
