package com.kltyton.mob_battle.mixin.world;

import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelReader.class)
public interface WorldViewMixin {
/*    @Inject(method = "getBottomY", at = @At("RETURN"), cancellable = true)
    default void getBottomY(CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == -64) cir.setReturnValue(-256);
    }*/
}
