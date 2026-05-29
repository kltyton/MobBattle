package com.kltyton.mob_battle.mixin.disarm;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 900)
public abstract class LivingEntityDisarmMixin extends Entity {
    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Shadow
    public abstract void travel(Vec3 movementInput);

    @Shadow
    public abstract @Nullable MobEffectInstance getEffect(Holder<MobEffect> effect);

    public LivingEntityDisarmMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "startUsingItem", at = @At("HEAD"), cancellable = true)
    private void preventItemUse(InteractionHand hand, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!ModSkillEntityType.canSkill(entity)) {
            ci.cancel();
        }
    }
    @Override
    public void turn(double cursorDeltaX, double cursorDeltaY) {
        if (this.hasEffect(ModEffects.ICE_ENTRY)) {
            int amplifier = -1;
            MobEffectInstance effect = this.getEffect(ModEffects.ICE_ENTRY);
            if (effect != null) {
                amplifier = effect.getAmplifier();
            }
            if (amplifier >= 5) return;
        }
        super.turn(cursorDeltaX, cursorDeltaY);
    }
    @Inject(
            method = "travel",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onTravel(Vec3 movementInput, CallbackInfo ci) {
        if (this.hasEffect(ModEffects.ICE_ENTRY) && movementInput != Vec3.ZERO) {
            int amplifier = -1;
            MobEffectInstance effect = this.getEffect(ModEffects.ICE_ENTRY);
            if (effect != null) {
                amplifier = effect.getAmplifier();
            }
            if (amplifier >= 5) {
                this.travel(Vec3.ZERO);
                ci.cancel();
            }
        }
    }
}
