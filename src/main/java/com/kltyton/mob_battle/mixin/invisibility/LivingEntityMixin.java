package com.kltyton.mob_battle.mixin.invisibility;

import com.kltyton.mob_battle.accessor.IModEntityRenderState;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.waypoint.ServerWaypoint;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
@Implements(@Interface(iface = IModEntityRenderState.class, prefix = "custom$"))
public abstract class LivingEntityMixin extends Entity implements Attackable, ServerWaypoint {
    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Unique
    private static final TrackedData<Boolean> TRUE_INVISIBLE = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    public void custom$setTrueInvisible(boolean invisible) {
        this.dataTracker.set(TRUE_INVISIBLE, invisible);
    }

    @Unique
    public boolean custom$isTrueInvisible() {
        return this.dataTracker.get(TRUE_INVISIBLE);
    }
    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initCustomDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(TRUE_INVISIBLE, false);
    }
    @Inject(method = "updatePotionVisibility", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updatePotionSwirls()V"))
    protected void updatePotionVisibility(CallbackInfo ci) {
        this.custom$setTrueInvisible(this.hasStatusEffect(ModEffects.TRUE_INVISIBLE_ENTRY));
    }

    @ModifyArg(
            method = "updatePotionVisibility",
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setInvisible(Z)V",
                    ordinal = 1
            )
    )
    protected boolean modifyInvisible(boolean invisible) {
        return invisible || this.hasStatusEffect(ModEffects.TRUE_INVISIBLE_ENTRY);
    }
    @ModifyVariable(
            method = "damage",
            at = @At("HEAD"),
            argsOnly = true,
            index = 3
    )
    private float applyBlockEffectReduction(float amount) {
        LivingEntity entity = (LivingEntity) (Object) this;
        // 检查实体是否有格挡效果
        if (entity.hasStatusEffect(ModEffects.BLOCK_ENTRY)) {
            int amplifier = entity.getStatusEffect(ModEffects.BLOCK_ENTRY).getAmplifier();
            int reduction = amplifier + 1;
            amount = Math.max(0, amount - reduction);
        }
        return amount;
    }

    @Shadow
    public abstract AttributeContainer getAttributes();

    @Shadow
    public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    @ModifyVariable(
            method = "damage",
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
