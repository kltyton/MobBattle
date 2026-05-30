package com.kltyton.mob_battle.mixin.invisibility;

import com.kltyton.mob_battle.accessor.IModEntityRenderState;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
@Implements(@Interface(iface = IModEntityRenderState.class, prefix = "custom$"))
public abstract class LivingEntityMixin extends Entity implements Attackable, WaypointTransmitter {
    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Unique
    private static final EntityDataAccessor<Boolean> TRUE_INVISIBLE = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Unique
    public void custom$setTrueInvisible(boolean invisible) {
        this.entityData.set(TRUE_INVISIBLE, invisible);
    }

    @Unique
    public boolean custom$isTrueInvisible() {
        return this.entityData.get(TRUE_INVISIBLE);
    }
    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void initCustomDataTracker(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(TRUE_INVISIBLE, false);
    }
    @Inject(method = "updateInvisibilityStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;updateSynchronizedMobEffectParticles()V"))
    protected void updatePotionVisibility(CallbackInfo ci) {
        this.custom$setTrueInvisible(this.hasEffect(ModEffects.TRUE_INVISIBLE_ENTRY));
    }

    @ModifyArg(
            method = "updateInvisibilityStatus",
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setInvisible(Z)V",
                    ordinal = 1
            )
    )
    protected boolean modifyInvisible(boolean invisible) {
        return invisible || this.hasEffect(ModEffects.TRUE_INVISIBLE_ENTRY);
    }
    @ModifyVariable(
            method = "hurtServer",
            at = @At("HEAD"),
            argsOnly = true,
            index = 3
    )
    private float applyBlockEffectReduction(float amount) {
        LivingEntity entity = (LivingEntity) (Object) this;
        // 检查实体是否有格挡效果
        if (entity.hasEffect(ModEffects.BLOCK_ENTRY)) {
            int amplifier = entity.getEffect(ModEffects.BLOCK_ENTRY).getAmplifier();
            int reduction = amplifier + 1;
            amount = Math.max(0, amount - reduction);
        }
        return amount;
    }

    @Shadow
    public abstract AttributeMap getAttributes();

    @Shadow
    public abstract double getAttributeValue(Holder<Attribute> attribute);

    @ModifyVariable(
            method = "hurtServer",
            at = @At("HEAD"),
            argsOnly = true,
            index = 3
    )
    private float modifyDamageArgument(float amount) {
        if (this.getAttributes().hasAttribute(ModEntityAttributes.DAMAGE_REDUCTION)) {
            return (float) (amount * (1 - this.getAttributeValue(ModEntityAttributes.DAMAGE_REDUCTION)));
        }
        return amount;
    }
}
