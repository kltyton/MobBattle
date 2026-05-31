package com.kltyton.mob_battle.mixin.geckolib;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.AnimationProcessor;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.state.AnimationTimeline;
import com.geckolib.cache.animation.Animation;
import com.geckolib.model.GeoModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = AnimationController.class, remap = false)
public abstract class AnimationControllerMixin {
    @Shadow
    protected double triggeredAnimTime;

    @Redirect(
            method = "initializeNewAnimation",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/geckolib/animation/AnimationController;safetyCheckTickLinearity(DD)D"
            )
    )
    private double mobBattle$startTriggeredAnimationAtFirstRender(AnimationController<?> controller, double currentTick, double triggeredTick) {
        return triggeredTick;
    }

    @Redirect(
            method = "initializeNewAnimation",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/geckolib/animation/state/AnimationTimeline;create(Lcom/geckolib/animation/RawAnimation;Lcom/geckolib/animatable/GeoAnimatable;Lcom/geckolib/model/GeoModel;I)Lcom/geckolib/animation/state/AnimationTimeline;"
            )
    )
    private <T extends GeoAnimatable> AnimationTimeline mobBattle$createTriggeredTimelineWithoutLeadIn(RawAnimation rawAnimation, T animatable, GeoModel<T> model, int transitionTicks) {
        if (this.triggeredAnimTime < 0) {
            return AnimationTimeline.create(rawAnimation, animatable, model, transitionTicks);
        }

        List<RawAnimation.Stage> rawStages = rawAnimation.getAnimationStages();
        List<AnimationTimeline.Stage> stages = new ArrayList<>(rawStages.size() + 1);
        double currentTime = 0;

        for (RawAnimation.Stage rawStage : rawStages) {
            Animation animation = AnimationProcessor.getOrCreateAnimation(rawStage, animatable, model);
            if (animation == null) {
                continue;
            }

            LoopType loopType = rawStage.loopType();
            stages.add(new AnimationTimeline.Stage(currentTime, currentTime + animation.length(), false, animation, loopType));
            currentTime += animation.length();
        }

        if (stages.isEmpty()) {
            return null;
        }

        double resetTransitionTime = transitionTicks / 20.0D;
        if (resetTransitionTime > 0.0D) {
            Animation lastAnimation = stages.getLast().animation();
            stages.add(new AnimationTimeline.Stage(currentTime, currentTime + resetTransitionTime, true, lastAnimation, null));
        }

        return new AnimationTimeline(stages.toArray(new AnimationTimeline.Stage[0]));
    }
}
