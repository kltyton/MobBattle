package com.kltyton.mob_battle.mixin.initgoals;

import com.kltyton.mob_battle.accessor.IPiglinEntity;
import com.kltyton.mob_battle.accessor.IPiglinBruteSpearMode;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.ai.PiglinBruteSpearUseGoal;
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
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
public abstract class PiglinBruteEntityMixin extends AbstractPiglin implements CrossbowAttackMob, RangedAttackMob, IPiglinBruteSpearMode {

    protected PiglinBruteEntityMixin(EntityType<? extends AbstractPiglin> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mob_battle$addSpearUseGoal(EntityType<? extends PiglinBrute> entityType, Level world, CallbackInfo ci) {
        this.goalSelector.addGoal(
                1,
                new PiglinBruteSpearUseGoal((PiglinBrute) (Object) this, 1.0D, 1.0D, 10.0F, 2.0F)
        );
        if (entityType == ModEntities.PIGLIN_BRUTE_SPEAR_MOD) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SPEAR));
            this.mobBattle$setSpearAttackMode(SPEAR_MODE_USE);
        }
    }

    @Unique
    private static final EntityDataAccessor<Boolean> CHARGING =
            SynchedEntityData.defineId(PiglinBrute.class, EntityDataSerializers.BOOLEAN);

    @Unique
    private static final EntityDataAccessor<Integer> SPEAR_ATTACK_MODE =
            SynchedEntityData.defineId(PiglinBrute.class, EntityDataSerializers.INT);

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
        builder.define(SPEAR_ATTACK_MODE, SPEAR_MODE_NONE);
    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem weapon) {
        return false;
    }

    @Override
    public boolean canUseNonMeleeWeapon(ItemStack item) {
        return item.has(DataComponents.KINETIC_WEAPON) && this.mobBattle$usesSpearAsItem();
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
        if (this.getType() == ModEntities.PIGLIN_BRUTE_SPEAR_MOD) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SPEAR));
            this.mobBattle$setSpearAttackMode(SPEAR_MODE_USE);
            return;
        }

        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
        this.mobBattle$setSpearAttackMode(SPEAR_MODE_NONE);
    }

    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private void mob_battle$getActivity(CallbackInfoReturnable<PiglinArmPose> cir) {
        if (this.getMainHandItem().has(DataComponents.KINETIC_WEAPON)) {
            cir.setReturnValue(PiglinArmPose.DEFAULT);
        } else if (this.isAggressive() && this.isHoldingMeleeWeapon()) {
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
        LivingEntity rememberedTarget = ((IPiglinEntity) this).getTargetEntity();
        if (rememberedTarget != null && rememberedTarget.isAlive() && !rememberedTarget.isRemoved()
                && !this.isAlliedTo(rememberedTarget) && this.canAttack(rememberedTarget)) {
            if (this.getTarget() != rememberedTarget) {
                this.setTarget(rememberedTarget);
            }
        } else if (this.getTarget() != null) {
            this.setTarget(null);
        }
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
        return false;
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

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt(SPEAR_ATTACK_MODE_KEY, this.mobBattle$getSpearAttackMode());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        int mode = input.getIntOr(SPEAR_ATTACK_MODE_KEY, SPEAR_MODE_NONE);
        if (this.getType() == ModEntities.PIGLIN_BRUTE_SPEAR_MOD) {
            mode = SPEAR_MODE_USE;
        } else if (mode != SPEAR_MODE_USE) {
            mode = SPEAR_MODE_NONE;
        }
        this.mobBattle$setSpearAttackMode(mode);
        if ((mode == SPEAR_MODE_USE || input.getBooleanOr(FORCE_GOLDEN_SPEAR_KEY, false))
                && !this.getMainHandItem().is(Items.GOLDEN_SPEAR)) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SPEAR));
        }
    }

    @Override
    public int mobBattle$getSpearAttackMode() {
        return this.entityData.get(SPEAR_ATTACK_MODE);
    }

    @Override
    public void mobBattle$setSpearAttackMode(int mode) {
        this.entityData.set(SPEAR_ATTACK_MODE, mode);
    }
}
