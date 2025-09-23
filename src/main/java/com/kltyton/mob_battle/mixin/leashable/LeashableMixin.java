package com.kltyton.mob_battle.mixin.leashable;

import com.kltyton.mob_battle.accessor.ILead;
import net.minecraft.entity.Leashable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Leashable.class)
public interface LeashableMixin {
    @Redirect(method = {"canBeLeashedTo", "tickLeash"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Leashable;getLeashSnappingDistance()D"))
    private static double getLeashSnappingDistance(Leashable leashable) {
        if (((ILead)leashable).getIsUniversalLeadEnyity()) {
            return Double.MAX_VALUE; // 返回double类型的最大值
        }
        return leashable.getLeashSnappingDistance();
    }
    @Redirect(method = "canBeLeashedTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Leashable;canBeLeashed()Z"))
    private static boolean canBeLeashed(Leashable leashable) {
        if (((ILead)leashable).getIsUniversalLeadEnyity()) {
            return true;
        }
        return leashable.canBeLeashed();
    }
    @Inject(method = "getLeashSnappingDistance", at = @At("RETURN"), cancellable = true)
    default void getLeashSnappingDistance(CallbackInfoReturnable<Double> cir) {
        if (((ILead)this).getIsUniversalLeadEnyity()) {
            cir.setReturnValue(Double.MAX_VALUE); // 返回double类型的最大值
        }
    }
}
