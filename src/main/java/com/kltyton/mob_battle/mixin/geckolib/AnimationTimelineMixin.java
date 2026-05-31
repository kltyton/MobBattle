package com.kltyton.mob_battle.mixin.geckolib;

import com.geckolib.animation.state.AnimationTimeline;
import com.geckolib.cache.animation.keyframeevent.KeyFrameData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mixin(value = AnimationTimeline.class, remap = false)
public abstract class AnimationTimelineMixin {
    @Inject(method = "getKeyframesForAnimation", at = @At("HEAD"), cancellable = true)
    private <M extends KeyFrameData> void mobBattle$getKeyframesForAnimation(double startTime, double endTime, M[] markers, CallbackInfoReturnable<List<M>> cir) {
        List<M> validMarkers = new ArrayList<>();

        for (M marker : markers) {
            double markerTime = marker.getTime();
            if (markerTime <= endTime && (markerTime > startTime || startTime == 0.0D)) {
                validMarkers.add(marker);
            }
        }

        validMarkers.sort(Comparator.comparingDouble(KeyFrameData::getTime));
        cir.setReturnValue(validMarkers);
    }
}
