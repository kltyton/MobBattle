package com.kltyton.mob_battle.mixin.initgoals;

import com.kltyton.mob_battle.accessor.IPiglinEntity;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBruteEntity.class)
@Implements({
        @Interface(iface = CrossbowUser.class, prefix = "crossbowuser$"),
})
public abstract class PiglinBruteEntityMixin extends AbstractPiglinEntity implements CrossbowUser, RangedAttackMob {

    protected PiglinBruteEntityMixin(EntityType<? extends AbstractPiglinEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private static final TrackedData<Boolean> CHARGING =
            DataTracker.registerData(PiglinBruteEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Unique
    private int mob_battle$bowCooldown = 0;

    @Unique
    private int mob_battle$bowTargetSeeingTicker = 0;

    @Unique
    private int mob_battle$bowRetreatCooldown = 0;

    @Unique
    private static final double BOW_ATTACK_RANGE_SQUARED = 225.0D;

    @Unique
    private static final double BOW_MIN_DISTANCE_SQUARED = 64.0D;

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CHARGING, false);
    }

    @Override
    public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
        return weapon == Items.CROSSBOW || weapon instanceof BowItem;
    }

    @Unique
    public void crossbowuser$setCharging(boolean charging) {
        this.dataTracker.set(CHARGING, charging);
    }

    @Unique
    public void crossbowuser$postShoot() {
        this.despawnCounter = 0;
    }

    @Unique
    public void crossbowuser$shootAt(LivingEntity target, float pullProgress) {
        this.shoot(this, 1.6F);
    }

    @Unique
    public void rangedattackmob$shootAt(LivingEntity target, float pullProgress) {
        ItemStack bowStack = this.getMainHandStack().getItem() instanceof BowItem ? this.getMainHandStack() : this.getOffHandStack();
        if (bowStack.isEmpty()) return;

        ItemStack arrowStack = this.getProjectileType(bowStack);
        if (arrowStack.isEmpty()) {
            arrowStack = new ItemStack(Items.ARROW);
        }

        PersistentProjectileEntity arrow = ProjectileUtil.createArrowProjectile(
                this,
                arrowStack,
                pullProgress,
                bowStack
        );

        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        double dy = target.getBodyY(0.3333333333333333) - arrow.getY() + horizontal * 0.2F;

        if (pullProgress >= 1.0F) {
            arrow.setCritical(true);
        }

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            ProjectileEntity.spawnWithVelocity(
                    arrow,
                    serverWorld,
                    arrowStack,
                    dx,
                    dy,
                    dz,
                    1.6F,
                    14 - serverWorld.getDifficulty().getId() * 4
            );
        }

        this.playSound(
                SoundEvents.ENTITY_SKELETON_SHOOT,
                1.0F,
                1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        this.despawnCounter = 0;
    }

    /**
     * @author Use CROSSBOW
     * @reason kltyton
     */
    @Overwrite
    public void initEquipment(Random random, LocalDifficulty localDifficulty) {
        if (random.nextFloat() < 0.34F) {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
        } else if (random.nextFloat() < 0.67F) {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        } else {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }
    }

    @Inject(method = "getActivity", at = @At("HEAD"), cancellable = true)
    private void mob_battle$getActivity(CallbackInfoReturnable<PiglinActivity> cir) {
        if (this.isAttacking() && this.isHoldingTool()) {
            cir.setReturnValue(PiglinActivity.ATTACKING_WITH_MELEE_WEAPON);
        } else if (this.dataTracker.get(CHARGING)) {
            cir.setReturnValue(PiglinActivity.CROSSBOW_CHARGE);
        } else if (this.isHolding(Items.CROSSBOW) && CrossbowItem.isCharged(this.getWeaponStack())) {
            cir.setReturnValue(PiglinActivity.CROSSBOW_HOLD);
        } else {
            cir.setReturnValue(PiglinActivity.DEFAULT);
        }
    }

    @Inject(method = "mobTick", at = @At("TAIL"))
    private void mob_battle$bowTick(ServerWorld world, CallbackInfo ci) {
        if (!this.isAlive()) return;
        if (!this.mob_battle$isHoldingBow()) return;

        LivingEntity target = this.mob_battle$getBowTarget();
        if (target == null) {
            this.clearActiveItem();
            this.setAttacking(false);
            this.mob_battle$bowCooldown = 0;
            this.mob_battle$bowTargetSeeingTicker = 0;
            this.mob_battle$bowRetreatCooldown = 0;
            return;
        }

        boolean canSee = this.getVisibilityCache().canSee(target);
        boolean wasSeeing = this.mob_battle$bowTargetSeeingTicker > 0;
        if (canSee != wasSeeing) {
            this.mob_battle$bowTargetSeeingTicker = 0;
        }
        if (canSee) {
            this.mob_battle$bowTargetSeeingTicker++;
        } else {
            this.mob_battle$bowTargetSeeingTicker--;
        }

        double distanceSq = this.squaredDistanceTo(target);

        this.getLookControl().lookAt(target, 30.0F, 30.0F);

        this.mob_battle$updateBowMovement(target, distanceSq);

        if (this.isUsingItem()) {
            this.setAttacking(true);
            if (!canSee && this.mob_battle$bowTargetSeeingTicker < -60) {
                this.clearActiveItem();
            } else {
                int useTicks = this.getItemUseTime();
                if (useTicks >= 20) {
                    this.clearActiveItem();
                    this.rangedattackmob$shootAt(target, BowItem.getPullProgress(useTicks));
                    this.mob_battle$bowCooldown = 20;
                    this.setAttacking(false);
                }
            }
        } else {
            this.setAttacking(false);
            if (this.mob_battle$bowCooldown > 0) {
                this.mob_battle$bowCooldown--;
            } else if (this.mob_battle$bowTargetSeeingTicker >= -60) {
                this.setCurrentHand(this.mob_battle$getBowHand());
            }
        }
    }

    @Unique
    private void mob_battle$updateBowMovement(LivingEntity target, double distanceSq) {
        if (distanceSq > BOW_ATTACK_RANGE_SQUARED) {
            this.getNavigation().startMovingTo(target, 1.0D);
            this.mob_battle$bowRetreatCooldown = 0;
        } else if (distanceSq < BOW_MIN_DISTANCE_SQUARED) {
            if (this.mob_battle$bowRetreatCooldown > 0) {
                this.mob_battle$bowRetreatCooldown--;
                return;
            }

            this.mob_battle$bowRetreatCooldown = 5;
            Vec3d retreatPos = NoPenaltyTargeting.findFrom(this, 10, 7, target.getPos());
            if (retreatPos != null) {
                this.getNavigation().startMovingTo(retreatPos.x, retreatPos.y, retreatPos.z, 1.2D);
            }
        } else {
            this.getNavigation().stop();
            this.mob_battle$bowRetreatCooldown = 0;
        }
    }

    @Unique
    private boolean mob_battle$isHoldingBow() {
        return this.getMainHandStack().getItem() instanceof BowItem || this.getOffHandStack().getItem() instanceof BowItem;
    }

    @Unique
    private Hand mob_battle$getBowHand() {
        return this.getMainHandStack().getItem() instanceof BowItem ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }

    @Unique
    private LivingEntity mob_battle$getBowTarget() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            target = ((IPiglinEntity) this).getTargetEntity();
        }

        if (target == null || !target.isAlive() || target.isRemoved() || this.isTeammate(target) || !this.canTarget(target)) {
            ((IPiglinEntity) this).setTargetEntity(null);
            return null;
        }

        return target;
    }
}
