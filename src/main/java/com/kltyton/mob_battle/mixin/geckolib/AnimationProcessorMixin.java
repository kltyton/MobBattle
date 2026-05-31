package com.kltyton.mob_battle.mixin.geckolib;

import com.geckolib.animation.AnimationProcessor;
import com.geckolib.animation.state.AnimationPoint;
import com.geckolib.animation.state.BoneSnapshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = AnimationProcessor.class, remap = false)
public abstract class AnimationProcessorMixin {
    @Redirect(
            method = "findResetPointValue",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/geckolib/animation/AnimationProcessor;getSnapshotResetTarget(Lcom/geckolib/animation/state/BoneSnapshot;Lcom/geckolib/animation/state/AnimationPoint$Transform;Lcom/geckolib/animation/state/AnimationPoint$Axis;Z)F"
            )
    )
    private static float mobBattle$getUnderlyingControllerTarget(BoneSnapshot snapshot, AnimationPoint.Transform transform, AnimationPoint.Axis axis, boolean additive) {
        return switch (transform) {
            case SCALE -> switch (axis) {
                case X -> snapshot.getScaleX();
                case Y -> snapshot.getScaleY();
                case Z -> snapshot.getScaleZ();
            };
            case ROTATION -> switch (axis) {
                case X -> snapshot.getRotX();
                case Y -> snapshot.getRotY();
                case Z -> snapshot.getRotZ();
            };
            case TRANSLATION -> switch (axis) {
                case X -> snapshot.getTranslateX();
                case Y -> snapshot.getTranslateY();
                case Z -> snapshot.getTranslateZ();
            };
        };
    }
}
