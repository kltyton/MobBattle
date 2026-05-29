package com.kltyton.mob_battle.mixin.leashable;

import com.kltyton.mob_battle.accessor.ILead;
import net.minecraft.world.entity.Leashable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Leashable.class)
public interface LeashableMixin {
    @Redirect(method = {"canHaveALeashAttachedTo", "tickLeash"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;leashSnapDistance()D"))
    private static double getLeashSnappingDistance(Leashable leashable) {
        if (((ILead)leashable).getIsUniversalLeadEnyity()) {
            return Double.MAX_VALUE; // 返回double类型的最大值
        }
        return leashable.leashSnapDistance();
    }
    @Redirect(method = "canHaveALeashAttachedTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;canBeLeashed()Z"))
    private static boolean canBeLeashed(Leashable leashable) {
        if (((ILead)leashable).getIsUniversalLeadEnyity()) {
            return true;
        }
        return leashable.canBeLeashed();
    }
    @Inject(method = "leashSnapDistance", at = @At("RETURN"), cancellable = true)
    default void getLeashSnappingDistance(CallbackInfoReturnable<Double> cir) {
        if (((ILead)this).getIsUniversalLeadEnyity()) {
            cir.setReturnValue(Double.MAX_VALUE); // 返回double类型的最大值
        }
    }
}
