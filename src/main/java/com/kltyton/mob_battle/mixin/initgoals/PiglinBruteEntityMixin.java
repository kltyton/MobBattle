package com.kltyton.mob_battle.mixin.initgoals;

import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBruteEntity.class)
@Implements(@Interface(iface = CrossbowUser.class, prefix = "crossbowuser$"))
public abstract class PiglinBruteEntityMixin extends AbstractPiglinEntity implements CrossbowUser {
    public PiglinBruteEntityMixin(EntityType<? extends AbstractPiglinEntity> entityType, World world) {
        super(entityType, world);
    }
    @Unique
    private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(PiglinBruteEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CHARGING, false);
    }
    @Override
    public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
        return weapon == Items.CROSSBOW;
    }
    @Unique
    public void crossbowuser$setCharging(boolean charging) {
        this.dataTracker.set(CHARGING, charging);
    }
    @Unique
    private boolean isCharging() {
        return this.dataTracker.get(CHARGING);
    }
    @Unique
    public void crossbowuser$postShoot() {
        this.despawnCounter = 0;
    }
    @Unique
    public void crossbowuser$shootAt(LivingEntity target, float pullProgress) {
        this.shoot(this, 1.6F);
    }
    /**
     * @author Use CROSSBOW
     * @reason kltyton
     */
    @Overwrite
    public void initEquipment(Random random, LocalDifficulty localDifficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));

        // 添加弓弩生成逻辑（例如 30% 概率）
        if (random.nextFloat() < 0.5F) {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        }
    }
    @Inject(method = "getActivity", at = @At("HEAD"), cancellable = true)
    private void getActivity(CallbackInfoReturnable<PiglinActivity> cir) {
        if (this.isAttacking() && this.isHoldingTool()) {
            cir.setReturnValue(PiglinActivity.ATTACKING_WITH_MELEE_WEAPON);
        } else if (this.isCharging()) {
            cir.setReturnValue(PiglinActivity.CROSSBOW_CHARGE);
        } else {
            cir.setReturnValue(this.isHolding(Items.CROSSBOW) && CrossbowItem.isCharged(this.getWeaponStack()) ? PiglinActivity.CROSSBOW_HOLD : PiglinActivity.DEFAULT);
        }
    }
}
