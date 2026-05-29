package com.kltyton.mob_battle.mixin.initgoals;

import com.kltyton.mob_battle.accessor.IPiglinEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBrute.class)
@Implements({
        @Interface(iface = CrossbowAttackMob.class, prefix = "crossbowuser$"),
})
public abstract class PiglinBruteEntityMixin extends AbstractPiglin implements CrossbowAttackMob, RangedAttackMob {

    protected PiglinBruteEntityMixin(EntityType<? extends AbstractPiglin> entityType, Level world) {
        super(entityType, world);
    }

    @Unique
    private static final EntityDataAccessor<Boolean> CHARGING =
            SynchedEntityData.defineId(PiglinBrute.class, EntityDataSerializers.BOOLEAN);

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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHARGING, false);
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem weapon) {
        return weapon == Items.CROSSBOW || weapon instanceof BowItem;
    }

    @Unique
    public void crossbowuser$setChargingCrossbow(boolean charging) {
        this.entityData.set(CHARGING, charging);
    }

    @Unique
    public void crossbowuser$onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Unique
    public void crossbowuser$performRangedAttack(LivingEntity target, float pullProgress) {
        this.performCrossbowAttack(this, 1.6F);
    }

    @Unique
    public void rangedattackmob$shootAt(LivingEntity target, float pullProgress) {
        ItemStack bowStack = this.getMainHandItem().getItem() instanceof BowItem ? this.getMainHandItem() : this.getOffhandItem();
        if (bowStack.isEmpty()) return;

        ItemStack arrowStack = this.getProjectile(bowStack);
        if (arrowStack.isEmpty()) {
            arrowStack = new ItemStack(Items.ARROW);
        }

        AbstractArrow arrow = ProjectileUtil.getMobArrow(
                this,
                arrowStack,
                pullProgress,
                bowStack
        );

        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        double dy = target.getY(0.3333333333333333) - arrow.getY() + horizontal * 0.2F;

        if (pullProgress >= 1.0F) {
            arrow.setCritArrow(true);
        }

        if (this.level() instanceof ServerLevel serverWorld) {
            Projectile.spawnProjectileUsingShoot(
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
                SoundEvents.SKELETON_SHOOT,
                1.0F,
                1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        this.noActionTime = 0;
    }

    /**
     * @author Use CROSSBOW
     * @reason kltyton
     */
    @Overwrite
    public void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance localDifficulty) {
        if (random.nextFloat() < 0.34F) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
        } else if (random.nextFloat() < 0.67F) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }
    }

    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private void mob_battle$getActivity(CallbackInfoReturnable<PiglinArmPose> cir) {
        if (this.isAggressive() && this.isHoldingMeleeWeapon()) {
            cir.setReturnValue(PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON);
        } else if (this.entityData.get(CHARGING)) {
            cir.setReturnValue(PiglinArmPose.CROSSBOW_CHARGE);
        } else if (this.isHolding(Items.CROSSBOW) && CrossbowItem.isCharged(this.getWeaponItem())) {
            cir.setReturnValue(PiglinArmPose.CROSSBOW_HOLD);
        } else {
            cir.setReturnValue(PiglinArmPose.DEFAULT);
        }
    }

    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void mob_battle$bowTick(ServerLevel world, CallbackInfo ci) {
        if (!this.isAlive()) return;
        if (!this.mob_battle$isHoldingBow()) return;

        LivingEntity target = this.mob_battle$getBowTarget();
        if (target == null) {
            this.stopUsingItem();
            this.setAggressive(false);
            this.mob_battle$bowCooldown = 0;
            this.mob_battle$bowTargetSeeingTicker = 0;
            this.mob_battle$bowRetreatCooldown = 0;
            return;
        }

        boolean canSee = this.getSensing().hasLineOfSight(target);
        boolean wasSeeing = this.mob_battle$bowTargetSeeingTicker > 0;
        if (canSee != wasSeeing) {
            this.mob_battle$bowTargetSeeingTicker = 0;
        }
        if (canSee) {
            this.mob_battle$bowTargetSeeingTicker++;
        } else {
            this.mob_battle$bowTargetSeeingTicker--;
        }

        double distanceSq = this.distanceToSqr(target);

        this.getLookControl().setLookAt(target, 30.0F, 30.0F);

        this.mob_battle$updateBowMovement(target, distanceSq);

        if (this.isUsingItem()) {
            this.setAggressive(true);
            if (!canSee && this.mob_battle$bowTargetSeeingTicker < -60) {
                this.stopUsingItem();
            } else {
                int useTicks = this.getTicksUsingItem();
                if (useTicks >= 20) {
                    this.stopUsingItem();
                    this.rangedattackmob$shootAt(target, BowItem.getPowerForTime(useTicks));
                    this.mob_battle$bowCooldown = 20;
                    this.setAggressive(false);
                }
            }
        } else {
            this.setAggressive(false);
            if (this.mob_battle$bowCooldown > 0) {
                this.mob_battle$bowCooldown--;
            } else if (this.mob_battle$bowTargetSeeingTicker >= -60) {
                this.startUsingItem(this.mob_battle$getBowHand());
            }
        }
    }

    @Unique
    private void mob_battle$updateBowMovement(LivingEntity target, double distanceSq) {
        if (distanceSq > BOW_ATTACK_RANGE_SQUARED) {
            this.getNavigation().moveTo(target, 1.0D);
            this.mob_battle$bowRetreatCooldown = 0;
        } else if (distanceSq < BOW_MIN_DISTANCE_SQUARED) {
            if (this.mob_battle$bowRetreatCooldown > 0) {
                this.mob_battle$bowRetreatCooldown--;
                return;
            }

            this.mob_battle$bowRetreatCooldown = 5;
            Vec3 retreatPos = DefaultRandomPos.getPosAway(this, 10, 7, target.position());
            if (retreatPos != null) {
                this.getNavigation().moveTo(retreatPos.x, retreatPos.y, retreatPos.z, 1.2D);
            }
        } else {
            this.getNavigation().stop();
            this.mob_battle$bowRetreatCooldown = 0;
        }
    }

    @Unique
    private boolean mob_battle$isHoldingBow() {
        return this.getMainHandItem().getItem() instanceof BowItem || this.getOffhandItem().getItem() instanceof BowItem;
    }

    @Unique
    private InteractionHand mob_battle$getBowHand() {
        return this.getMainHandItem().getItem() instanceof BowItem ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    @Unique
    private LivingEntity mob_battle$getBowTarget() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            target = ((IPiglinEntity) this).getTargetEntity();
        }

        if (target == null || !target.isAlive() || target.isRemoved() || this.isAlliedTo(target) || !this.canAttack(target)) {
            ((IPiglinEntity) this).setTargetEntity(null);
            return null;
        }

        return target;
    }
}
