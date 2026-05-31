package com.kltyton.mob_battle.utils;

import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public final class GeoAnimationUtil {
    private static final double FINISHED_TRIGGERED_TIMELINE = -2.0D;
    private static final Map<AnimationController<?>, RawAnimation> LAST_FINISHED_ANIMATIONS =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<Object, Long> RECENTLY_FINISHED_ANIMATABLES =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final long FINISHED_ANIMATION_BRIDGE_MS = 250L;
    private static long nextAnimationDebugLogMs;

    private GeoAnimationUtil() {
    }

    public static boolean consumeFinishedTriggeredAnimation(AnimationTest<?> animationTest) {
        AnimationController<?> controller = animationTest.controller();
        if (cleanupFinishedResetTransition(controller)) {
            return false;
        }

        if (hasFinishedTriggeredAnimation(controller)) {
            resetFinishedTriggeredAnimation(animationTest, controller);
            return true;
        }

        if (controller.isPlayingTriggeredAnimation()) {
            logAnimationState("playing", animationTest, controller);
        }
        return false;
    }

    public static boolean isLastFinishedAnimation(AnimationTest<?> animationTest, RawAnimation animation) {
        return Objects.equals(LAST_FINISHED_ANIMATIONS.get(animationTest.controller()), animation);
    }

    public static boolean hasRecentlyFinishedTriggeredAnimation(Object animatable) {
        Long finishedAt = RECENTLY_FINISHED_ANIMATABLES.get(animatable);
        return finishedAt != null && System.currentTimeMillis() - finishedAt <= FINISHED_ANIMATION_BRIDGE_MS;
    }

    public static PlayState playTriggeredAnimationOrStop(AnimationTest<?> animationTest) {
        AnimationController<?> controller = animationTest.controller();
        if (cleanupFinishedResetTransition(controller)) {
            return PlayState.STOP;
        }

        if (hasFinishedTriggeredAnimation(controller)) {
            resetFinishedTriggeredAnimation(animationTest, controller);
            return PlayState.STOP;
        }

        boolean pendingTriggeredAnimation = controller.getCurrentRawAnimation() != null
                && controller.getPlayState() == PlayState.CONTINUE
                && controller.getCurrentAnimationPoint() == null;
        if (pendingTriggeredAnimation) {
            logAnimationState("pending", animationTest, controller);
        }
        return controller.isPlayingTriggeredAnimation() || pendingTriggeredAnimation ? PlayState.CONTINUE : PlayState.STOP;
    }

    private static boolean hasFinishedTriggeredAnimation(AnimationController<?> controller) {
        return controller.getCurrentTimelineTime() == FINISHED_TRIGGERED_TIMELINE
                || (controller.isPlayingTriggeredAnimation() && controller.hasAnimationFinished());
    }

    private static boolean cleanupFinishedResetTransition(AnimationController<?> controller) {
        if (controller.getCurrentTimelineTime() != FINISHED_TRIGGERED_TIMELINE) {
            return false;
        }

        RawAnimation currentAnimation = controller.getCurrentRawAnimation();
        if (currentAnimation != null && Objects.equals(LAST_FINISHED_ANIMATIONS.get(controller), currentAnimation)) {
            controller.reset();
            return true;
        }
        return false;
    }

    private static void resetFinishedTriggeredAnimation(AnimationTest<?> animationTest, AnimationController<?> controller) {
        RawAnimation finishedAnimation = controller.getCurrentRawAnimation();
        if (finishedAnimation != null) {
            LAST_FINISHED_ANIMATIONS.put(controller, finishedAnimation);
        }
        RECENTLY_FINISHED_ANIMATABLES.put(animationTest.animatable(), System.currentTimeMillis());
        logAnimationState("finish", animationTest, controller);
        controller.stopTriggeredAnimation();
    }

    private static void logAnimationState(String event, AnimationTest<?> animationTest, AnimationController<?> controller) {
        long now = System.currentTimeMillis();
        if (!"finish".equals(event) && now < nextAnimationDebugLogMs) {
            return;
        }

        if ("playing".equals(event)) {
            nextAnimationDebugLogMs = now + 2000L;
        }

        Object animatable = animationTest.animatable();
        if (animatable instanceof Entity entity) {
            Mob_battle.LOGGER.info(
                    "[MobBattle][Anim] {} controller={} raw={} timeline={} animTime={} finished={} playingTriggered={} entity={} id={} class={} hasNoAi={}",
                    event,
                    controller.getName(),
                    controller.getCurrentRawAnimation(),
                    controller.getCurrentTimelineTime(),
                    controller.getCurrentAnimationTime(),
                    controller.hasAnimationFinished(),
                    controller.isPlayingTriggeredAnimation(),
                    entity.getType(),
                    entity.getId(),
                    entity.getClass().getName(),
                    entity instanceof Mob mob && mob.isNoAi()
            );
            return;
        }

        Mob_battle.LOGGER.info(
                "[MobBattle][Anim] {} controller={} raw={} timeline={} animTime={} finished={} playingTriggered={} animatable={}",
                event,
                controller.getName(),
                controller.getCurrentRawAnimation(),
                controller.getCurrentTimelineTime(),
                controller.getCurrentAnimationTime(),
                controller.hasAnimationFinished(),
                controller.isPlayingTriggeredAnimation(),
                animatable.getClass().getName()
        );
    }
}
