package com.kltyton.mob_battle.mixin.disarm;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 900)
public abstract class LivingEntityDisarmMixin extends Entity {
    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Shadow
    public abstract void travel(Vec3d movementInput);

    public LivingEntityDisarmMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "setCurrentHand", at = @At("HEAD"), cancellable = true)
    private void preventItemUse(Hand hand, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!ModSkillEntityType.canSkill(entity)) {
            ci.cancel();
        }
    }
    @Override
    public void changeLookDirection(double cursorDeltaX, double cursorDeltaY) {
        if (this.hasStatusEffect(ModEffects.ICE_ENTRY)) {
            return;
        }
        super.changeLookDirection(cursorDeltaX, cursorDeltaY);
    }
    @Inject(
            method = "travel",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onTravel(Vec3d movementInput, CallbackInfo ci) {
        if (this.hasStatusEffect(ModEffects.ICE_ENTRY) && movementInput != Vec3d.ZERO) {
            this.travel(Vec3d.ZERO);
            ci.cancel();
        }
    }
}
